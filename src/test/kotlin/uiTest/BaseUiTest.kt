package uiTest

import apiTest.BaseTest
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.logevents.SelenideLogger
import common.extensions.AdminSessionExtension
import common.extensions.UserSessionExtension
import io.qameta.allure.selenide.AllureSelenide
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(AdminSessionExtension::class)
@ExtendWith(UserSessionExtension::class)
abstract class BaseUiTest : BaseTest() {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpSelenoid() {
            Configuration.remote = configs.Config.getProperty("uiRemote")
            Configuration.baseUrl = configs.Config.getProperty("uiBaseUrl")
            Configuration.browser = configs.Config.getProperty("browser")
            Configuration.browserSize = configs.Config.getProperty("browserSize")
            SelenideLogger.addListener("AllureSelenide", AllureSelenide())
            Configuration.browserCapabilities.setCapability(
                "selenoid:options", mapOf("enableVNC" to true, "enableLog" to true)
            )
        }
    }
    @AfterEach
    fun cleanup() {
        try {
            Selenide.clearBrowserLocalStorage()
        } finally {
            Selenide.closeWebDriver()
        }
    }
}