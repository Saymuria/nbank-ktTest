package requests

import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel

abstract class Request {
    protected val requestSpecification: RequestSpecification
    protected val responseSpecification: ResponseSpecification

    constructor(requestSpecification: RequestSpecification, responseSpecification: ResponseSpecification) {
        this.requestSpecification = requestSpecification
        this.responseSpecification = responseSpecification
    }

    abstract fun post(model: BaseModel?) : ValidatableResponse
}
