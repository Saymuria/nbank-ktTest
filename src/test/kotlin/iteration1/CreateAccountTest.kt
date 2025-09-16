package iteration1

import BaseTest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.CREATE_ACCOUNT
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import models.accounts.createAccount.CreateAccountResponse
import models.admin.createUser.CreateUserRequest
import models.customer.GetCustomerAccountsResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import steps.AdminSteps

@DisplayName("Check user account creation")
class CreateAccountTest : BaseTest() {
    private val adminSteps = AdminSteps()

    @Test
    @DisplayName("Positive test: user can create account")
    fun userCanAccountTest() {
        //createUser
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        //create account
        val createAccountResponse = ValidatedCrudRequester<CreateAccountResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            entityWasCreated(), CREATE_ACCOUNT
        ).post(null)
        //check account creation
        val getAccountResponse = ValidatedCrudRequester<GetCustomerAccountsResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            GET_CUSTOMER_ACCOUNTS
        ).get(null)
        createAccountResponse.shouldMatchResponse(getAccountResponse)
    }
}