package dsl

import framework.skeleton.Endpoint
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import io.restassured.http.Method
import io.restassured.http.Method.DELETE
import io.restassured.http.Method.GET
import io.restassured.http.Method.POST
import io.restassured.http.Method.PUT
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

inline fun <reified T : BaseModel> Endpoint.validatedRequest(
    auth: () -> RequestSpecification = { unAuthSpec() },
    response: () -> ResponseSpecification = { requestReturnOk() },
    method: Method,
    requestBody: BaseModel? = null,
    id: Long? = null
): T {
    val requester = ValidatedCrudRequester<T>(auth(), response(), this)
    return when (method) {
        GET -> requester.get(id)
        POST -> requester.post(requestBody)
        PUT -> requester.update(id, requestBody)
        DELETE -> requester.delete(id)
        else -> throw IllegalArgumentException("Unsupported method: $method")
    }
}

fun Endpoint.request(
    auth: () -> RequestSpecification = { unAuthSpec() },
    method: Method,
    requestBody: BaseModel? = null,
    id: Long? = null,
    response: () -> ResponseSpecification = { requestReturnOk() },
) {
    val requester = CrudRequester(auth(), response(), this)
        .let {
            when (method) {
                GET -> it.get(id)
                POST -> it.post(requestBody)
                PUT -> it.update(id, requestBody)
                DELETE -> it.delete(id)
                else -> throw IllegalArgumentException("Unsupported method: $method")
            }
        }
}