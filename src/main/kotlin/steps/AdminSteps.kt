package steps

import framework.skeleton.Endpoint
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs
import framework.specs.ResponseSpec
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse


class AdminSteps {

    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        return ValidatedCrudRequester<CreateUserResponse>(
            RequestSpecs.adminAuthSpec(),
            ResponseSpec.entityWasCreated(),
            Endpoint.CREATE_USER,
        ).post(createUserRequest)
    }
}