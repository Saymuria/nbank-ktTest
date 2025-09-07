package requests

import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

class AdminCreateUserRequester(
    requestSpecification: RequestSpecification,
    responseSpecification: ResponseSpecification
) : Request(requestSpecification, responseSpecification) {

    override fun post(model: BaseModel?): ValidatableResponse {
        return given()
            .spec(requestSpecification)
            .body( model)
            .post("/api/v1/admin/users")
            .then()
            .assertThat()
            .spec(responseSpecification)
    }
}