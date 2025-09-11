package iteration1

import generators.RandomData
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import models.CreateUserRequest
import models.LoginUserRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.HttpStatus.SC_OK
import org.junit.jupiter.api.Test
import requests.skeleton.Endpoint
import requests.skeleton.requesters.CrudRequester
import specs.RequestSpecs
import specs.ResponseSpec

class CreateAccountTest {

    @Test
    fun userCanAccountTest() {
        val userRequest = CreateUserRequest(
            username = RandomData.getUserName(),
            password = RandomData.getUserPassword(),
            role = "USER"
        )
        val userAuthToken = LoginUserRequest(username = userRequest.username, password = userRequest.password)
        //создание пользователя
        CrudRequester(RequestSpecs.adminAuthSpec(), ResponseSpec.entityWasCreated(), Endpoint.ADMIN_USER).post(
            userRequest
        )
        //получение авторизации
        val userAuthHeader =
            CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpec.requestReturnOk(), Endpoint.LOGIN).post(userAuthToken)
                .extract().header(AUTHORIZATION)

        CrudRequester(
            RequestSpecs.authAsUser(userRequest.username, userRequest.password),
            ResponseSpec.entityWasCreated(), Endpoint.ACCOUNTS
        ).post(null)
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