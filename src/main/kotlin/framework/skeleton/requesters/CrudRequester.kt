package framework.skeleton.requesters

import framework.skeleton.Endpoint
import framework.skeleton.HttpRequest
import framework.skeleton.interfaces.CrudEndpointInterface
import hellpers.step
import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

class CrudRequester(
    requestSpecification: RequestSpecification,
    responseSpecification: ResponseSpecification, endpoint: Endpoint
) : HttpRequest(requestSpecification, responseSpecification, endpoint), CrudEndpointInterface {

    override fun post(model: BaseModel?): ValidatableResponse = step("Отправляем POST запрос ${endpoint.url}") {
        val body = model ?: ""
        given()
            .spec(requestSpecification)
            .body(body)
            .post(endpoint.url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun get(id: Long?): ValidatableResponse = step("Отправляем GET запрос ${endpoint.url} id=$id") {
        val url = if (id != null) {
            String.format(endpoint.url, id)
        } else {
            endpoint.url
        }
        given()
            .spec(requestSpecification)
            .get(url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun update(id: Long?, model: BaseModel?): ValidatableResponse =
        step("Отправляем PUT запрос ${endpoint.url} id=$id") {
            val body = model ?: ""
            val url = if (id != null) {
                String.format(endpoint.url, id)
            } else {
                endpoint.url
            }
            given()
                .spec(requestSpecification)
                .body(body)
                .put(url)
                .then()
                .assertThat()
                .spec(responseSpecification)
        }

    override fun delete(id: Long?): ValidatableResponse =
        step("Отправляем DELETE запрос ${endpoint.url} id=$id") {
        val url = if (id != null) {
            String.format(endpoint.url, id)
        } else {
            endpoint.url
        }
        given()
            .spec(requestSpecification)
            .delete(url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

}