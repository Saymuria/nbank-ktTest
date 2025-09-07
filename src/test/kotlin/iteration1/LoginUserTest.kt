package iteration1

import generators.RandomData.Companion.getUserName
import generators.RandomData.Companion.getUserPassword
import models.CreateUserRequest
import models.LoginUserRequest
import models.UserRole
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import requests.AdminCreateUserRequester
import requests.LoginUserRequester
import specs.RequestSpecs
import specs.ResponseSpec

class LoginUserTest : BaseTest(){

    //Название теста должно совпадать с требованием, которое он проверяет
    @Test
    fun adminCanGenerateAuthTokenTest() {
        val userRequest = LoginUserRequest("admin", "admin")
        LoginUserRequester(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk()).post(userRequest)
    }

    @Test
    fun userCanGenerateAuthTokenTest() {
        val userData = CreateUserRequest(getUserName(), getUserPassword(), UserRole.USER.toString())
        //создание пользователя
        AdminCreateUserRequester(RequestSpecs.adminAuthSpec(), ResponseSpec.entityWasCreated()).post(userData)
        //получение авторизации
        LoginUserRequester(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk()).post(userData)
            .assertThat()
            .header(AUTHORIZATION, notNullValue())
    }
}