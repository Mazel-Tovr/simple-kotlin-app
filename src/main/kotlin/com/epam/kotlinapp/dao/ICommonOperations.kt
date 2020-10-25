package com.epam.kotlinapp.dao

import com.epam.kotlinapp.model.Entity

interface ICommonOperations<T : Entity> {
    fun create(entity: T)
    fun getEntity(id: Long):T?
    fun getAll():List<T>
    fun update(entity: T)
    fun delete(entity: T)
    fun delete(id: Long)
}