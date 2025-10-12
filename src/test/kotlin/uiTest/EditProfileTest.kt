package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import dsl.createUser
import dsl.updateProfileName
import dsl.validatedRequest
import framework.skeleton.Endpoint.GET_CUSTOMER_PROFILE
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import io.restassured.http.Method.GET
import models.authentication.LoginUserRequest
import models.customer.GetCustomerProfileResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class EditProfileTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpSelenoid() {
            Configuration.remote = "http://localhost:4444/wd/hub"
            Configuration.baseUrl = "http://192.168.0.5:3000"
            Configuration.browser = "chrome"
            Configuration.browserSize = "1920x1080"
            Configuration.browserCapabilities.setCapability(
                "selenoid:options", mapOf("enableVNC" to true, "enableLog" to true)
            )
        }
    }

    @Test
    fun userCanSetNameTest() {
        val user = createUser()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        val name = generate<UpdateCustomerProfileRequest>()
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byClassName("user-name")).click()
        `$`(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).click()
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(name.name)
        `$`(Selectors.byText("\uD83D\uDCBE Save Changes")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("✅ Name updated successfully!")
        alert.accept()
        `$`(Selectors.byText("\uD83C\uDFE0 Home")).click()
        `$`(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, ${name.name}!"))
        Selenide.refresh()
        `$`(Selectors.byClassName("user-name")).shouldBe(Condition.visible).shouldHave(Condition.text(name.name))
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name
        assertThat(getCustomerProfileResponse).isEqualTo(name.name)
    }

    @Test
    fun userCannotSetInvalidNameTest() {
        val user = createUser()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        val name = generate<UpdateCustomerProfileRequest>(mapOf("name" to "12345"))
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byClassName("user-name")).click()
        `$`(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).click()
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(name.name)
        `$`(Selectors.byText("\uD83D\uDCBE Save Changes")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Name should be valid")
        alert.accept()
        `$`(Selectors.byText("\uD83C\uDFE0 Home")).click()
        `$`(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"))
        Selenide.refresh()
        `$`(Selectors.byClassName("user-name")).shouldBe(Condition.visible).shouldHave(Condition.text("noname"))
        GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name.isNullOrEmpty()
    }

    @Test
    fun userCanChangeNameTest() {
        val user = createUser()
        val updateCustomerProfileRequst = generate<UpdateCustomerProfileRequest>()
        user.updateProfileName(updateCustomerProfileRequst)
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        val nawName = generate<UpdateCustomerProfileRequest>()
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byClassName("user-name")).click()
        `$`(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).value == updateCustomerProfileRequst.name
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).clear()
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(nawName.name)
        `$`(Selectors.byText("\uD83D\uDCBE Save Changes")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("✅ Name updated successfully!")
        alert.accept()
        `$`(Selectors.byText("\uD83C\uDFE0 Home")).click()
        `$`(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, ${nawName.name}!"))
        Selenide.refresh()
        `$`(Selectors.byClassName("user-name")).shouldBe(Condition.visible).shouldHave(Condition.text(nawName.name))
        val getCustomerProfileResponse = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).name
        assertThat(getCustomerProfileResponse).isEqualTo(nawName.name)
    }

    @Test
    fun userCannotSubmitEmptyEditNameFormTest() {
        val user = createUser()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byClassName("user-name")).click()
        `$`(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible)
        `$`(Selectors.byText("\uD83D\uDCBE Save Changes")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("❌ Please enter a valid name.")
    }


    @Test
    fun userCannotSetSameNameTest() {
        val user = createUser()
        val updateCustomerProfileRequst = generate<UpdateCustomerProfileRequest>()
        user.updateProfileName(updateCustomerProfileRequst)
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byClassName("user-name")).click()
        `$`(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter new name")).value == updateCustomerProfileRequst.name
        `$`(Selectors.byText("\uD83D\uDCBE Save Changes")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("⚠️ New name is the same as the current one.")
    }
}