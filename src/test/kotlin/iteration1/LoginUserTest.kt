package iteration1

import BaseTest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import models.admin.createUser.CreateUserRequest
import models.authentication.LoginUserRequest
import models.authentication.LoginUserResponse
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import steps.AdminSteps

@DisplayName("Check user login creation")
class LoginUserTest : BaseTest() {
    private val adminSteps = AdminSteps()

    @Test
    @DisplayName("Admin user can login")
    fun adminCanGenerateAuthTokenTest() {
        val adminLoginRequest = LoginUserRequest("admin", "admin")
        val adminLoginResponse = ValidatedCrudRequester<LoginUserResponse>(
            unAuthSpec(),
            requestReturnOk(),
            LOGIN
        ).post(adminLoginRequest)
        adminLoginRequest.shouldMatchResponse(adminLoginResponse)
    }

    @Test
    @DisplayName("User can login")
    fun userCanGenerateAuthTokenTest() {
        val createUserRequest = generate<CreateUserRequest>()
        //создание пользователя
        adminSteps.createUser(createUserRequest)
        //получение авторизации
        val userLoginRequest = LoginUserRequest(createUserRequest.username, createUserRequest.password)
        CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).assertThat()
            .header(AUTHORIZATION, notNullValue())
    }
}