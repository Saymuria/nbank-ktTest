package uiTest

import apiTest.BaseTest
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.executeJavaScript
import dsl.TestUser
import framework.specs.RequestSpecs.Companion.getUserAuthHeader
import models.authentication.LoginUserRequest
import org.junit.jupiter.api.BeforeAll

abstract class BaseUiTest : BaseTest() {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpSelenoid() {
            Configuration.remote = configs.Config.getProperty("uiRemote")
            Configuration.baseUrl = configs.Config.getProperty("uiBaseUrl")
            Configuration.browser = configs.Config.getProperty("browser")
            Configuration.browserSize = configs.Config.getProperty("browserSize")
            Configuration.browserCapabilities.setCapability(
                "selenoid:options", mapOf("enableVNC" to true, "enableLog" to true)
            )
        }
    }

    fun authorizeAsUser(username: String, password: String) {
        Selenide.open("/")
        val authHeader = getUserAuthHeader(username, password)
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
    }

    fun authorizeAsUser(loginUserRequest: LoginUserRequest) {
        authorizeAsUser(loginUserRequest.username, loginUserRequest.password)
    }

    fun TestUser.authorizeAsUser() {
        authorizeAsUser(username, originalPassword)
    }

}