package iteration1

import BaseTest
import entities.UserRole
import entities.UserRole.USER
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.CREATE_USER
import framework.skeleton.Endpoint.GET_ALL_USER
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.adminAuthSpec
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequestWithError
import framework.utils.generate
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

private const val INVALID_PASSWORD_ERROR =
    "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"
private const val INVALID_USERNAME_ERROR = "Username must be between 3 and 15 characters"
private const val USERNAME = "username"
private const val PASSWORD = "password"
private const val STRONG_PASSWORD = "verysTRongPassword33$"

@DisplayName("Check user creation")
class CreateUserTest : BaseTest() {

    companion object {
        @JvmStatic
        private fun userInvalidData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("   ", STRONG_PASSWORD, USERNAME, "Username cannot be blank"),
                Arguments.of("ab", STRONG_PASSWORD, USERNAME, INVALID_USERNAME_ERROR),
                Arguments.of("abshdfkdofdfjdfd", STRONG_PASSWORD, USERNAME, INVALID_USERNAME_ERROR),
                Arguments.of(
                    "!@#$%^&*()+=",
                    "verysTRongPassword33$",
                    USERNAME,
                    "Username must contain only letters, digits, dashes, underscores, and dots"
                ),
                Arguments.of("dkdgld8304_1", "   ", PASSWORD, "Password cannot be blank"),
                Arguments.of("dkdgld8304_2", "Djf2@4o", PASSWORD, INVALID_PASSWORD_ERROR),
                Arguments.of("validuser_1", "Nodigit!", PASSWORD, INVALID_PASSWORD_ERROR),
                Arguments.of("validuser_2", "nouppercase1!", PASSWORD, INVALID_PASSWORD_ERROR),
                Arguments.of("validuser_3", "NOLOWERCASE1!", PASSWORD, INVALID_PASSWORD_ERROR),
                Arguments.of("validuser_4", "NoSpecialChar1", PASSWORD, INVALID_PASSWORD_ERROR),
                Arguments.of("validuser_5", "With space 1!", PASSWORD, INVALID_PASSWORD_ERROR),
            )
        }
    }

    @Test
    @DisplayName("Positive test: admin can create user with valid data")
    fun adminCanCreateUserWithCorrectData() {
        val createUserRequest = generate<CreateUserRequest>()
        //создание пользователя
        val createUserResponse = ValidatedCrudRequester<CreateUserResponse>(
            adminAuthSpec(),
            entityWasCreated(),
            CREATE_USER,
        ).post(createUserRequest)
        createUserRequest.shouldMatchResponse(createUserResponse)
        ValidatedCrudRequester<GetAllUserResponse>(
            adminAuthSpec(),
            requestReturnOk(),
            GET_ALL_USER,
        ).get(null)
    }

    @ParameterizedTest
    @MethodSource("userInvalidData")
    @DisplayName("Negative test: admin cannot create user with invalid data")
    fun adminCanCreateUserWithInvalidData(
        username: String,
        password: String,
        keyError: String,
        valueError: String
    ) {
        val createUserRequest = CreateUserRequest(username = username, password = password, role = USER)
        CrudRequester(
            adminAuthSpec(),
            requestReturnsBadRequestWithError(keyError, valueError), CREATE_USER
        ).post(createUserRequest)
    }

    //может админ и может создавать, я бы уточнила
    @Test
    @DisplayName("Negative test: admin cannot create admin-user")
    fun adminCanCreateUserWithInvalidData() {
        val createUserRequest = generate<CreateUserRequest>(mapOf("role" to UserRole.ADMIN))
        CrudRequester(
            adminAuthSpec(),
            requestReturnsBadRequest(), CREATE_USER
        ).post(createUserRequest)
    }
}