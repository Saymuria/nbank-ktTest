package framework.skeleton.requesters

import framework.skeleton.Endpoint
import framework.skeleton.HttpRequest
import framework.skeleton.interfaces.CrudEndpointInterface
import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

class CrudRequester(
    requestSpecification: RequestSpecification,
    responseSpecification: ResponseSpecification, endpoint: Endpoint
) : HttpRequest(requestSpecification, responseSpecification, endpoint), CrudEndpointInterface {

    override fun post(model: BaseModel?): ValidatableResponse {
        val body = model ?: ""
        return given()
            .spec(requestSpecification)
            .body(body)
            .post(endpoint.url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun get(id: Long?): ValidatableResponse {
        val url = if (id != null) {
            String.format(endpoint.url, id)
        } else {
            endpoint.url
        }
        return given()
            .spec(requestSpecification)
            .get(url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun update(id: Long?, model: BaseModel?): ValidatableResponse {
        val body = model ?: ""
        val url = if (id != null) {
            String.format(endpoint.url, id)
        } else {
            endpoint.url
        }
        return given()
            .spec(requestSpecification)
            .body(body)
            .put(url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun delete(id: Long?): ValidatableResponse {
        val url = if (id != null) {
            String.format(endpoint.url, id)
        } else {
            endpoint.url
        }
        return given()
            .spec(requestSpecification)
            .delete(url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

}