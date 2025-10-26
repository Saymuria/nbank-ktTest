package steps

import dsl.validatedRequest
import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.GET_ALL_USER
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs
import framework.specs.RequestSpecs.Companion.adminAuthSpec
import framework.specs.ResponseSpec
import io.restassured.http.Method.GET
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse


class AdminSteps {
    companion object{
        fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
            return ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminAuthSpec(),
                ResponseSpec.entityWasCreated(),
                Endpoint.CREATE_USER,
            ).post(createUserRequest)
        }

        fun getAllUsers(): GetAllUserResponse {
            return GET_ALL_USER.validatedRequest<GetAllUserResponse>(
                auth = { adminAuthSpec() },
                method = GET
            )
        }
    }

    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        return ValidatedCrudRequester<CreateUserResponse>(
            RequestSpecs.adminAuthSpec(),
            ResponseSpec.entityWasCreated(),
            Endpoint.CREATE_USER,
        ).post(createUserRequest)
    }

    fun getAllUsers(): GetAllUserResponse {
        return GET_ALL_USER.validatedRequest<GetAllUserResponse>(
            auth = { adminAuthSpec() },
            method = GET
        )
    }
}