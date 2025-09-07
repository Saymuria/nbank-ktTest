package requests

import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

class CreateAccountRequester(requestSpecification: RequestSpecification, responseSpecification: ResponseSpecification) :
    Request(requestSpecification, responseSpecification) {

    override fun post(model: BaseModel?): ValidatableResponse {
        return given()
            .spec(requestSpecification)
            .post("/api/v1/accounts")
            .then()
            .assertThat()
            .spec(responseSpecification)
    }
}