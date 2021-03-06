package com.epam.kotlinapp.crud.endpoints

import com.epam.kotlinapp.Model
import com.epam.kotlinapp.crud.business.ICommonServices
import com.epam.kotlinapp.crud.endpoints.roots.*
import com.epam.kotlinapp.crud.exceptions.DataException
import com.epam.kotlinapp.crud.exceptions.ProductGroupNotFoundException
import com.epam.kotlinapp.crud.listener.Event.*
import com.epam.kotlinapp.crud.listener.IObserver
import com.epam.kotlinapp.crud.model.ProductGroup
import de.nielsfalk.ktor.swagger.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


private val productGroupExample = mapOf(
    "id" to 0,
    "groupName" to "some group",
)


@KtorExperimentalLocationsAPI
fun Route.productGroupController(productGroupService: ICommonServices<ProductGroup>, observer: IObserver) {


    get<ApiRoot.ProductGroups>("all".responds(ok<Model<ProductGroup>>())) {
        observer.onEvent(READ, "Getting all product groups")
        call.respond(HttpStatusCode.OK, productGroupService.getAll())
    }

    get<ApiRoot.ProductGroups.CurrentProductGroup>(
        "find".responds(
            ok<ProductGroup>(),
            notFound()
        )
    )
    {
        val id: Long = call.parameters["id"].let { param ->
            if (param == null) {
                call.respond(HttpStatusCode.BadRequest, "Id isn't present")
                return@get
            } else {
                param.toLong()
            }
        }
        kotlin.runCatching { productGroupService.getEntity(id) }
            .onSuccess { product ->
                observer.onEvent(READ, "Getting product with id = $id")
                call.respond(HttpStatusCode.OK, product)
            }
            .onFailure { exception ->
                when (exception) {
                    is ProductGroupNotFoundException -> {
                        observer.onEvent(READ, "Trying to get product by id but ${exception.message}")
                        call.respond(HttpStatusCode.BadRequest, "Trying to get product by id but ${exception.message}")
                    }
                    else -> {
                        observer.onEvent(DELETE, "Server exception ${exception.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Server exception ${exception.message}")
                    }
                }
            }
    }
    post<ApiRoot.ProductGroups, ProductGroup>(
        "create"
            .description("Add new user to date base")
            .examples(
                example("Group", productGroupExample, summary = "super group")
            )
            .responds(
                created<ProductGroup>(
                    example("Group", productGroupExample)
                )
            )
    ) { _, entity: ProductGroup ->

        kotlin.runCatching { productGroupService.create(entity) }
            .onSuccess { product ->
                observer.onEvent(CREATE, "Creating new product group $product")
                call.respond(HttpStatusCode.Created, product)
            }
            .onFailure { exception ->
                when (exception) {
                    is DataException -> {
                        observer.onEvent(CREATE, "Product group wasn't created ${exception.message}")
                        call.respond(HttpStatusCode.BadRequest, "Product group wasn't created ${exception.message}")
                    }
                    else -> {
                        observer.onEvent(DELETE, "Server exception ${exception.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Server exception ${exception.message}")
                    }
                }
            }
    }

    delete<ApiRoot.ProductGroups.CurrentProductGroup>(
        "delete".responds(
            ok<Unit>(),
            notFound()
        )
    ) {

        val id: Long = call.parameters["id"].let { param ->
            if (param == null) {
                call.respond(HttpStatusCode.BadRequest, "Id isn't present")
                return@delete
            } else {
                param.toLong()
            }
        }

        kotlin.runCatching { productGroupService.delete(id) }
            .onSuccess {
                observer.onEvent(DELETE, "Product group was deleted")
                call.respond(HttpStatusCode.OK, "Product group successfully removed")
            }
            .onFailure { exception ->
                when (exception) {
                    is DataException -> {
                        observer.onEvent(DELETE, "Product group wasn't deleted ${exception.message}")
                        call.respond(HttpStatusCode.BadRequest, "Product group wasn't deleted ${exception.message}")
                    }
                    else -> {
                        observer.onEvent(DELETE, "Server exception ${exception.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Server exception ${exception.message}")
                    }
                }

            }
    }
    put<ApiRoot.ProductGroups.CurrentProductGroup, ProductGroup>(
        "update"
            .description("Update product group in db (by his id)")
            .examples(
                example("Group", productGroupExample, summary = "super group")
            ).responds(
                ok<ProductGroup>(),
                notFound()
            )
    ) { _, productGroup: ProductGroup ->
        productGroup.id = call.parameters["id"].let { param ->
            if (param == null) {
                call.respond(HttpStatusCode.BadRequest, "Id isn't present")
                return@put
            } else {
                param.toLong()
            }
        }
        kotlin.runCatching { productGroupService.update(productGroup) }
            .onSuccess {
                observer.onEvent(UPDATE, "Product group was edited")
                call.respond(HttpStatusCode.OK, productGroup)
            }
            .onFailure { exception ->
                when (exception) {
                    is DataException -> {
                        observer.onEvent(UPDATE, "Product group wasn't edited ${exception.message}")
                        call.respond(HttpStatusCode.BadRequest, "Product group wasn't edited ${exception.message}")
                    }
                    else -> {
                        observer.onEvent(DELETE, "Server exception ${exception.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Server exception ${exception.message}")
                    }
                }
            }
    }
}
