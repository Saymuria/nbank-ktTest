package iteration2

import BaseTest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.GET_CUSTOMER_PROFILE
import framework.skeleton.Endpoint.UPDATE_CUSTOMER_PROFILE
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import models.admin.createUser.CreateUserRequest
import models.customer.GetCustomerProfileResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import steps.AdminSteps
import steps.UserSteps
import java.util.stream.Stream

@DisplayName("Check user name editing")
class EditProfileTest : BaseTest() {
    private val adminSteps = AdminSteps()
    private val userSteps = UserSteps()

//Не понятно какие валидации для name, но разделила бы так
    companion object {
        @JvmStatic
        fun validNames(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("A"), //минимальная длина
                Arguments.of("John"),
                Arguments.of("Анна"), //кириллица
                Arguments.of("John Doe"), //с пробелом
                Arguments.of("O'Connor"), //с апострофом
                Arguments.of("a".repeat(10)) //максимальная длина
            )
        }

        @JvmStatic
        fun invalidNames(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("   "), //только пробелы
                Arguments.of("a".repeat(11)), //слишком длинное
                Arguments.of("John@Doe"), //запрещенный символ
                Arguments.of("12345") //только цифры
            )
        }
    }

    @Test
    @DisplayName("User can set name to profile")
    fun userCanSetName() {
        val user = generate<CreateUserRequest>()
        adminSteps.createUser(user)
        userSteps.createAccount(user.username, user.password)
        val username = generate<UpdateCustomerProfileRequest>()
        val updateCustomerProfileResponse = ValidatedCrudRequester<UpdateCustomerProfileResponse>(
            authAsUser(user.username, user.password),
            requestReturnOk(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, username)
        val getCustomerProfileResponse = ValidatedCrudRequester<GetCustomerProfileResponse>(
            authAsUser(user.username, user.password),
            requestReturnOk(),
            GET_CUSTOMER_PROFILE
        ).get(null)
        softly.assertThat(updateCustomerProfileResponse.message).isEqualTo("Profile updated successfully")
        updateCustomerProfileResponse.shouldMatchResponse(softly, getCustomerProfileResponse)
    }

    @Test
    @DisplayName("User can change name in profile")
    fun userCanChangeUserName() {
        val user = generate<CreateUserRequest>()
        adminSteps.createUser(user)
        userSteps.createAccount(user.username, user.password)
        val setCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        userSteps.updateName(user.username, user.password, setCustomerProfileRequest)
        val updateCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        CrudRequester(
            authAsUser(user.username, user.password),
            requestReturnOk(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, updateCustomerProfileRequest)
    }

    @ParameterizedTest
    @MethodSource("validNames")
    fun userCanSetValidName(validName: String) {
        val user = generate<CreateUserRequest>()
        adminSteps.createUser(user)
        userSteps.createAccount(user.username, user.password)
        val updateCustomerProfileRequest = UpdateCustomerProfileRequest(validName)
        CrudRequester(
            authAsUser(user.username, user.password),
            requestReturnOk(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, updateCustomerProfileRequest)
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    fun userCannotSetInvalidName(invalidName: String) {
        val user = generate<CreateUserRequest>()
        adminSteps.createUser(user)
        userSteps.createAccount(user.username, user.password)
        val updateCustomerProfileRequest = UpdateCustomerProfileRequest(invalidName)
        CrudRequester(
            authAsUser(user.username, user.password),
            requestReturnsBadRequest(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, updateCustomerProfileRequest)
    }
}