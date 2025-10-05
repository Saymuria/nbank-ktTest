package iteration1

import BaseTest
import dsl.createUser
import dsl.request
import dsl.validatedRequest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import io.restassured.http.Method
import io.restassured.http.Method.POST
import models.authentication.LoginUserRequest
import models.authentication.LoginUserResponse
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Check user login creation")
class LoginUserTest : BaseTest() {

    @Test
    @DisplayName("Admin user can login")
    fun adminCanGenerateAuthTokenTest() {
        val adminLoginRequest = LoginUserRequest("admin", "admin")
        val adminLoginResponse = LOGIN.validatedRequest<LoginUserResponse>(
            auth = { unAuthSpec() },
            requestBody = adminLoginRequest,
            method = POST
        )
        adminLoginRequest shouldMatchResponse adminLoginResponse
    }

    @Test
    @DisplayName("User can login")
    fun userCanGenerateAuthTokenTest() {
        val user = createUser()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).assertThat()
            .header(AUTHORIZATION, notNullValue())
    }
}