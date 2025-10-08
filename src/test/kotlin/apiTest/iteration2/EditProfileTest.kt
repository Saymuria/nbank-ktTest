package apiTest.iteration2

import apiTest.BaseTest
import dsl.*
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.GET_CUSTOMER_PROFILE
import framework.skeleton.Endpoint.UPDATE_CUSTOMER_PROFILE
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import io.restassured.http.Method.GET
import io.restassured.http.Method.PUT
import models.customer.GetCustomerProfileResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("Check user name editing")
class EditProfileTest : BaseTest() {

    //Не понятно какие валидации для name, но разделила бы так
    companion object {
        @JvmStatic
        fun validNames(): Stream<Arguments> {
            return Stream.of(
                of("A"), //минимальная длина
                of("John"),
                of("Анна"), //кириллица
                of("John Doe"), //с пробелом
                of("O'Connor"), //с апострофом
                of("a".repeat(10)) //максимальная длина
            )
        }

        @JvmStatic
        fun invalidNames(): Stream<Arguments> {
            return Stream.of(
                of("   "), //только пробелы
                of("a".repeat(11)), //слишком длинное
                of("John@Doe"), //запрещенный символ
                of("12345") //только цифры
            )
        }
    }

    @Test
    @DisplayName("User can set name to profile")
    fun userCanSetName() {
        val (user, account) = createUserWithAccount()
        val updateCustomerProfileResponse = user.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        )
        check(softly) {
            updateCustomerProfileResponse.message shouldBe "Profile updated successfully"
            updateCustomerProfileResponse shouldMatch getCustomerProfileResponse
        }
    }

    @Test
    @DisplayName("User can change name in profile")
    fun userCanChangeUserName() {
        val (user, account) = createUserWithAccount()
        val updateCustomerProfileResponse = user.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val changeCustomerProfileResponse = user.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name ?: throw NullPointerException("Name is null")
        check(softly) {
            changeCustomerProfileResponse.customer.name?.let { updateCustomerProfileResponse.customer.name?.shouldNotBe(it) }
            changeCustomerProfileResponse.customer.name?.shouldBe(getCustomerProfileResponse)
        }
    }

    @ParameterizedTest
    @MethodSource("validNames")
    fun userCanSetValidName(validName: String) {
        val (user, account) = createUserWithAccount()
        val updateCustomerProfileResponse = user.updateProfileName(UpdateCustomerProfileRequest(validName))
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name ?: throw NullPointerException("Name is null")
        updateCustomerProfileResponse shouldMatchResponse getCustomerProfileResponse
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    fun userCannotSetInvalidName(invalidName: String) {
        val (user, account) = createUserWithAccount()
        val updateCustomerProfileRequest = UpdateCustomerProfileRequest(invalidName)
        UPDATE_CUSTOMER_PROFILE.request(
            auth = { authAsUser(user.username, user.originalPassword) },
            response = { requestReturnsBadRequest() },
            method = PUT,
            requestBody = updateCustomerProfileRequest
        )
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name
        check(softly) {
            getCustomerProfileResponse?.let { user.username shouldNotBe  it }
        }
    }
}