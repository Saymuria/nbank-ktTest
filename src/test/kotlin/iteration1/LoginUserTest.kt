package iteration1

import generators.RandomData.Companion.getUserName
import generators.RandomData.Companion.getUserPassword
import models.CreateUserRequest
import models.CreateUserResponse
import models.LoginUserRequest
import models.LoginUserResponse
import entities.UserRole
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import requests.skeleton.Endpoint
import requests.skeleton.requesters.CrudRequester
import requests.skeleton.requesters.ValidatedCrudRequester
import specs.RequestSpecs
import specs.ResponseSpec

class LoginUserTest : BaseTest(){

    //Название теста должно совпадать с требованием, которое он проверяет
    @Test
    fun adminCanGenerateAuthTokenTest() {
        val userRequest = LoginUserRequest("admin", "admin")
        ValidatedCrudRequester<LoginUserResponse>(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk(), Endpoint.LOGIN).post(userRequest)
    }

    @Test
    fun userCanGenerateAuthTokenTest() {
        val userData = CreateUserRequest(getUserName(), getUserPassword(), UserRole.USER.toString())
        //создание пользователя
        ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(), ResponseSpec.entityWasCreated(), Endpoint.ADMIN_USER).post(userData)
        //получение авторизации
        CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk(), Endpoint.LOGIN).post(userData)
            .assertThat()
            .header(AUTHORIZATION, notNullValue())
    }
}