package com.epam.kotlinapp.crud.business

import com.epam.kotlinapp.crud.dao.*
import com.epam.kotlinapp.crud.dao.nosql.*
import com.epam.kotlinapp.crud.exceptions.*
import com.epam.kotlinapp.crud.model.*
import kotlinx.collections.immutable.*
import org.slf4j.*

class UserService(private val userOperations: ICommonOperations<User>) : ICommonServices<User> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun create(entity: User): User {

        return kotlin.runCatching { userOperations.create(entity) }
            .onSuccess { logger.info("User added") }
            .onFailure { logger.error(it.message) }
            .getOrElse { throw DataException("User couldn't be created") }
    }

    override fun getEntity(id: Long): User {

        val user: User? = kotlin.runCatching { userOperations.getEntity(id) }
            .onSuccess { logger.info("Getting user") }
            .onFailure { logger.error(it.message) }
            .getOrNull()

        return user ?: throw UserNotFoundException("User with id = $id couldn't found")
    }

    override fun getAll(): ImmutableList<User> {
        return kotlin.runCatching { userOperations.getAll() }
            .onSuccess { logger.info("Getting users") }
            .onFailure { logger.error(it.message) }
            .getOrDefault(persistentListOf())
    }

    override fun update(entity: User) {
        if (entity.id == -1L)
            throw DataException("User id can't be empty")
        kotlin.runCatching { userOperations.update(entity) }
            .onSuccess { logger.info("User updated") }
            .onFailure {
                logger.error(it.message)
                throw DataException("User wasn't updated")
            }
    }

    override fun delete(id: Long) {
        kotlin.runCatching { userOperations.delete(id) }
            .onSuccess { logger.info("User deleted") }
            .onFailure {
                logger.error(it.message)
                throw DataException("User wasn't deleted")
            }
    }


}
