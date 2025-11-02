package apiTest.iteration1

import apiTest.BaseTest
import dsl.request
import dsl.validatedRequest
import entities.UserRole
import entities.UserRole.USER
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.CREATE_USER
import framework.skeleton.Endpoint.GET_ALL_USER
import framework.specs.RequestSpecs.Companion.adminAuthSpec
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequestWithError
import framework.utils.generate
import io.restassured.http.Method.GET
import io.restassured.http.Method.POST
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

private const val INVALID_PASSWORD_ERROR =
    "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"
private const val INVALID_USERNAME_LENGTH_ERROR = "Username must be between 3 and 15 characters"
private const val INVALID_USERNAME_CHARACTERS_ERROR = "Username must contain only letters, digits, dashes, underscores, and dots"
private const val USERNAME = "username"
private const val PASSWORD = "password"
private const val STRONG_PASSWORD = "verysTRongPassword33$"

@DisplayName("Check user creation")
class CreateUserTest : BaseTest() {

    companion object {
        @JvmStatic
        private fun userInvalidData(): Stream<Arguments> {
            return Stream.of(
                of("   ", STRONG_PASSWORD, USERNAME, listOf("Username cannot be blank", INVALID_USERNAME_CHARACTERS_ERROR)),
                of("ab", STRONG_PASSWORD, USERNAME, listOf(INVALID_USERNAME_LENGTH_ERROR)),
                of("abshdfkdofdfjdfd", STRONG_PASSWORD, USERNAME, listOf(INVALID_USERNAME_LENGTH_ERROR)),
                of(
                    "!@#$%^&*()+=",
                    "verysTRongPassword33$",
                    USERNAME,listOf(INVALID_USERNAME_CHARACTERS_ERROR)
                ),
                of("dkdgld8304_1", "   ", PASSWORD, listOf("Password cannot be blank", INVALID_PASSWORD_ERROR)),
                of("dkdgld8304_2", "Djf2@4o", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
                of("validuser_1", "Nodigit!", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
                of("validuser_2", "nouppercase1!", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
                of("validuser_3", "NOLOWERCASE1!", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
                of("validuser_4", "NoSpecialChar1", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
                of("validuser_5", "With space 1!", PASSWORD, listOf(INVALID_PASSWORD_ERROR)),
            )
        }
    }

    @Test
    @DisplayName("Positive test: admin can create user with valid data")
    fun adminCanCreateUserWithCorrectData() {
        val createUserRequest = generate<CreateUserRequest>()
        val createUserResponse = CREATE_USER.validatedRequest<CreateUserResponse>(
            auth = { adminAuthSpec() },
            response = { entityWasCreated() },
            requestBody = createUserRequest,
            method = POST
        )
        createUserRequest shouldMatchResponse createUserResponse

        val allUsers = GET_ALL_USER.validatedRequest<GetAllUserResponse>(
            auth = { adminAuthSpec() },
            method = GET
        )
        //проверка
    }

    @ParameterizedTest
    @MethodSource("userInvalidData")
    @DisplayName("Negative test: admin cannot create user with invalid data")
    fun adminCanNotCreateUserWithInvalidData(
        username: String,
        password: String,
        keyError: String,
        valueErrors: List<String>
    ) {
        val createUserRequest = CreateUserRequest(username = username, password = password, role = USER)
        CREATE_USER.request(
            auth = { adminAuthSpec() },
            response = { requestReturnsBadRequestWithError(keyError, valueErrors) },
            requestBody = createUserRequest,
            method = POST
        )
    }

    //может админ и может создавать, я бы уточнила
    @Test
    @Disabled("Тест временно отключен")
    @DisplayName("Negative test: admin cannot create admin-user")
    fun adminCannotCreateAdminUser() {
        val createUserRequest = generate<CreateUserRequest>(mapOf("role" to UserRole.ADMIN))
        CREATE_USER.request(
            auth = { adminAuthSpec() },
            response = { requestReturnsBadRequest() },
            requestBody = createUserRequest,
            method = POST
        )
    }
}