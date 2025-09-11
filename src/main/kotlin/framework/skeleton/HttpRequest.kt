package framework.skeleton

import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification

abstract class HttpRequest {
    protected val requestSpecification: RequestSpecification
    protected val responseSpecification: ResponseSpecification
    protected val endpoint : Endpoint

    constructor(
        requestSpecification: RequestSpecification,
        responseSpecification: ResponseSpecification,
        endpoint: Endpoint
    ) {
        this.requestSpecification = requestSpecification
        this.responseSpecification = responseSpecification
        this.endpoint = endpoint
    }
}