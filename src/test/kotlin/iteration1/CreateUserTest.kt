package iteration1

import generators.RandomData
import models.CreateUserRequest
import models.CreateUserResponse
import entities.UserRole
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import requests.skeleton.Endpoint
import requests.skeleton.requesters.CrudRequester
import requests.skeleton.requesters.ValidatedCrudRequester
import specs.RequestSpecs
import specs.ResponseSpec
import java.util.stream.Stream

class CreateUserTest : BaseTest() {

    companion object {

        @JvmStatic
        fun userInvalidData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("   ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("ab", "verysTRongPassword33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abshdfkdofdfjdfd", "verysTRongPassword33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("!@#$%^&*()+=", "verysTRongPassword33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("dkdgld8304_1", "   ", "USER", "password", "Password cannot be blank"),
                Arguments.of("dkdgld8304_2", "Djf2@4o", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
            )
        }
    }

    @Test
    fun adminCanCreateUserWithCorrectData() {
        val createUserRequest = CreateUserRequest(username = RandomData.getUserName(), password = RandomData.getUserPassword(), role = UserRole.USER.toString())
        //создание пользователя
        val createUserResponse = ValidatedCrudRequester<CreateUserResponse>(
            RequestSpecs.adminAuthSpec(),
            ResponseSpec.entityWasCreated(),
            Endpoint.ADMIN_USER,
        ).post(createUserRequest)
        softly.assertThat(createUserRequest.username).isEqualTo(createUserResponse.username)
        softly.assertThat(createUserRequest.password).isNotEqualTo(createUserResponse.password)
        softly.assertThat(createUserRequest.role).isEqualTo(createUserResponse.role)
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
        val createUserRequest = CreateUserRequest(username = username, password = password, role = role)
        CrudRequester(RequestSpecs.adminAuthSpec(),
            ResponseSpec.requestReturnsBadRequest(keyError, valueError), Endpoint.ADMIN_USER
        ).post(createUserRequest)
    }
}