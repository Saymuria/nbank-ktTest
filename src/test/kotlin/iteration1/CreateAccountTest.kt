package iteration1

import generators.RandomData
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import models.CreateUserRequest
import models.LoginUserRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.HttpStatus.SC_OK
import org.junit.jupiter.api.Test
import requests.AdminCreateUserRequester
import requests.CreateAccountRequester
import requests.LoginUserRequester
import specs.RequestSpecs
import specs.ResponseSpec

class CreateAccountTest {

    @Test
    fun userCanAccountTest() {
        val userRequest = CreateUserRequest(username = RandomData.getUserName(), password = RandomData.getUserPassword(), role = "USER")
        val userAuthToken = LoginUserRequest(username = userRequest.username, password = userRequest.password)
//создание пользователя
        AdminCreateUserRequester(RequestSpecs.adminAuthSpec(), ResponseSpec.entityWasCreated()).post(userRequest)
        //получение авторизации
        val userAuthHeader = LoginUserRequester(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk()).post(userAuthToken)
            .extract()
            .header(AUTHORIZATION)

        CreateAccountRequester(RequestSpecs.authAsUser(userRequest.username,userRequest.password), ResponseSpec.entityWasCreated()).post(null)
        //check account creation
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header(AUTHORIZATION, userAuthHeader)
            .get("http://localhost:4111/api/v1/customer/accounts")
            .then()
            .assertThat()
            .statusCode(SC_OK)
    }
}