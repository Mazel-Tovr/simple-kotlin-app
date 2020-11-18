package com.epam.kotlinapp.crud.dao.nosql

import com.epam.kotlinapp.crud.dao.*
import com.epam.kotlinapp.crud.model.*
import jetbrains.exodus.entitystore.Entity
import kotlinx.collections.immutable.*
import kotlinx.collections.immutable.adapters.*
import kotlinx.dnq.*
import kotlinx.dnq.query.*

object ProductOperationImpl : ICommonOperations<Product> {
    val conn = CommonStore.entityStore

    override fun create(entity: Product) = conn.transactional {
        entity.toXdProduct().toProduct()
    }

    override fun getEntity(id: Long): Product? = conn.transactional(readonly = true) {
        XdProduct.all().asSequence().firstOrNull { it.entityId.localId == id }?.toProduct()
    }

    override fun getAll(): ImmutableList<Product> = conn.transactional(readonly = true) {
        XdProduct.all().toList().map { it.toProduct() }.let { ImmutableListAdapter(it) }
    }

    override fun update(entity: Product): Unit = conn.transactional {
        XdProduct.all().asSequence().first { it.entityId.localId == entity.id }
            .update {
                productName = entity.productName
                price = entity.price
                productDescription = entity.description
                groupId = entity.groupId
            }
    }

    override fun delete(id: Long): Unit = conn.transactional {
        XdProduct.all().asSequence().firstOrNull { it.entityId.localId == id }?.delete()
    }
}


private fun Product.toXdProduct() = XdProduct.new {
    productName = this@toXdProduct.productName
    price = this@toXdProduct.price
    productDescription = this@toXdProduct.description
    groupId = this@toXdProduct.groupId
    userId = this@toXdProduct.userId
}


private fun Entity.toProduct() = Product(
    id.localId,
    getProperty("productName") as String,
    getProperty("price") as Int,
    getProperty("productDescription") as String,
    getProperty("group") as Long,
    getProperty("user") as Long
)


private fun XdProduct.toProduct() = Product(entityId.localId, productName, price, productDescription, groupId, userId)

private fun XdProduct.update(builder: XdProduct.() -> Unit): XdProduct = apply { builder() }


class XdProduct(entity: Entity) : XdEntity(entity) {
    companion object : XdNaturalEntityType<XdProduct>()

    var productName by xdRequiredStringProp()
    var price by xdRequiredIntProp()
    var productDescription by xdRequiredStringProp()
    var groupId by xdRequiredLongProp()//xdLink0_1(XdProductGroup)
    var userId by xdRequiredLongProp()//xdLink0_1(XdUser)
}