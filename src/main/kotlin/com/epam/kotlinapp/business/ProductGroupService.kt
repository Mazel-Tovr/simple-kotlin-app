package com.epam.kotlinapp.business

import com.epam.kotlinapp.dao.ICommonOperations
import com.epam.kotlinapp.dao.ProductGroupOperations
import com.epam.kotlinapp.exceptions.DataException
import com.epam.kotlinapp.exceptions.ProductGroupNotFoundException
import com.epam.kotlinapp.model.ProductGroup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

object ProductGroupService : ICommonServices<ProductGroup> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val productGroupOperations: ICommonOperations<ProductGroup> = ProductGroupOperations

    override fun create(entity: ProductGroup) {
        try {
            productGroupOperations.create(entity)
            logger.info("User added")
        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
    }

    override fun getEntity(id: Long): ProductGroup {
        var productGroup: ProductGroup? = null;
        try {
            productGroup = productGroupOperations.getEntity(id);
            return productGroup ?: throw ProductGroupNotFoundException("ProductGroup with id = $id couldn't found")

        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
        productGroup.let { return it!! }
    }

    override fun getAll(): List<ProductGroup> {
        var list: List<ProductGroup> = emptyList();
        try {
            list = productGroupOperations.getAll();
            return list.ifEmpty { throw ProductGroupNotFoundException("ProductGroups couldn't be found") }
        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
        return list
    }

    override fun update(entity: ProductGroup) {
        try {
            if (entity.id == null)
                throw DataException("Product group id can't be empty")
            else productGroupOperations.update(entity)
        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
    }

    override fun delete(entity: ProductGroup) {
        try {
            productGroupOperations.update(entity)
        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
    }

    override fun delete(id: Long) {
        try {
            productGroupOperations.delete(id)
        } catch (ex: SQLException) {
            logger.error(ex.message)
        }
    }
}