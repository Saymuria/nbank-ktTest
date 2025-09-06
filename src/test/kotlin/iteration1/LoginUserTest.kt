package iteration1

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.HttpStatus.SC_CREATED
import org.apache.http.HttpStatus.SC_OK
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class LoginUserTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupRestAssured() {
            RestAssured.filters(
                listOf(
                    RequestLoggingFilter(), ResponseLoggingFilter()
                )
            )
        }
    }

    //Название теста должно совпадать с требованием, которое он проверяет
    @Test
    fun adminCanGenerateAuthTokenTest() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                """
                    {
                    "username": "admin",
                    "password": "admin"
                    }
                """
            )
            .post("http://localhost:4111/api/v1/auth/login")
            .then()
            .assertThat()
            .statusCode(SC_OK)
            .header("Authorization", "Basic YWRtaW46YWRtaW4=")
    }

    @Test
    fun userCanGenerateAuthTokenTest() {
        //создание пользователя
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .body(
                """
                    {
                        "username": "sasha1393123",
                        "password": "verysTRongPassword33$",
                        "role": "USER"
                    }"""
            )
            .post("http://localhost:4111/api/v1/admin/users")
            .then()
            .assertThat()
            .statusCode(SC_CREATED)

        //получение авторизации
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                """
                    {
                    "username": "sasha1393123", 
                    "password": "verysTRongPassword33$"
                    }
                """
            )
            .post("http://localhost:4111/api/v1/auth/login")
            .then()
            .assertThat()
            .statusCode(SC_OK)
            .header("Authorization", Matchers.notNullValue())
    }
}