package iteration1

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.HttpStatus.SC_BAD_REQUEST
import org.apache.http.HttpStatus.SC_CREATED
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CreateUserTest {

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

        @JvmStatic
        fun userInvalidData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("   ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("ab", "verysTRongPassword33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abshdfkdofdfjdfd", "verysTRongPassword33$", "USER", "Username must be between 3 and 15 characters"),
                Arguments.of("   ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("   ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("   ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
            )
        }
    }

    @Test
    fun adminCanCreateUserWithCorrectData() {
        //создание пользователя
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .body(
                """
                    {
                        "username": "sasha11238",
                        "password": "verysTRongPassword33$",
                        "role": "USER"
                    }"""
            )
            .post("http://localhost:4111/api/v1/admin/users")
            .then()
            .assertThat()
            .statusCode(SC_CREATED)
            .body("username", Matchers.equalTo("sasha11238"))
            .body("password", Matchers.not(Matchers.equalTo("verysTRongPassword33$")))
            .body("role", Matchers.equalTo("USER"))
    }


    @ParameterizedTest
    @MethodSource("userInvalidData")
    fun adminCanCreateUserWithInvalidData(
        username: String,
        password: String,
        role: String,
        keyError: String,
        valueError: String
    ) {
        val requestBody = String.format(
            """{
                        "username": "%s",
                        "password": "%s",
                        "role": "%s"
                    }""", username, password, role
        )
        //создание пользователя
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .body(requestBody)
            .post("http://localhost:4111/api/v1/admin/users")
            .then()
            .assertThat()
            .statusCode(SC_BAD_REQUEST)
            .body(keyError, Matchers.equalTo(valueError))
    }
}