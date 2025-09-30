package framework.skeleton.requesters

import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import models.BaseModel
import framework.skeleton.Endpoint
import framework.skeleton.HttpRequest
import framework.skeleton.interfaces.CrudEndpointInterface

class ValidatedCrudRequester<T : BaseModel>(
    requestSpecification: RequestSpecification,
    responseSpecification: ResponseSpecification,
    endpoint: Endpoint
) : HttpRequest(requestSpecification, responseSpecification, endpoint), CrudEndpointInterface {

    private val crudRequester = CrudRequester(requestSpecification, responseSpecification, endpoint)

    override fun post(model: BaseModel?): T {
        return crudRequester.post(model)
            .extract()
            .`as`(endpoint.responseModel.java) as T
    }


    override fun get(id: Long?): T {
        return crudRequester.get(id)
            .extract()
            .`as`(endpoint.responseModel.java) as T
    }

    override fun update(id: Long?, model: BaseModel?): T {
        return crudRequester.update(id, model)
            .extract()
            .`as`(endpoint.responseModel.java) as T
    }

    override fun delete(id: Long?): T {
        return crudRequester.delete(id)
            .extract()
            .`as`(endpoint.responseModel.java) as T
    }

}