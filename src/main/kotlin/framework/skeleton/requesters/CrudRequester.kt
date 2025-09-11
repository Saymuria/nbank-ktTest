package framework.skeleton.requesters

import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel
import framework.skeleton.Endpoint
import framework.skeleton.HttpRequest
import framework.skeleton.interfaces.CrudEndpointInterface

class CrudRequester(requestSpecification: RequestSpecification,
                    responseSpecification: ResponseSpecification, endpoint: Endpoint
) : HttpRequest(requestSpecification, responseSpecification, endpoint), CrudEndpointInterface {

    override fun post(model: BaseModel?) : ValidatableResponse {
        val body = if (model == null) "" else model
        return given()
            .spec(requestSpecification)
            .body( body)
            .post(endpoint.url)
            .then()
            .assertThat()
            .spec(responseSpecification)
    }

    override fun get(id: Long): ValidatableResponse {
        TODO("Not yet implemented")
    }

    override fun update(id: Long, model: BaseModel): ValidatableResponse {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

}