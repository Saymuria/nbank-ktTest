package iteration1

import BaseTest
import dsl.createUser
import dsl.validatedRequest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.CREATE_ACCOUNT
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.entityWasCreated
import io.restassured.http.Method
import io.restassured.http.Method.GET
import io.restassured.http.Method.POST
import models.accounts.createAccount.CreateAccountResponse
import models.customer.GetCustomerAccountsResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Check user account creation")
class CreateAccountTest : BaseTest() {

    @Test
    @DisplayName("Positive test: user can create account")
    fun userCanAccountTest() {
        val user = createUser()
        val createAccountResponse = CREATE_ACCOUNT.validatedRequest<CreateAccountResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            response = { entityWasCreated() },
            method = POST
        )
        val getAccountResponse = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        )
        createAccountResponse shouldMatchResponse getAccountResponse
    }
}