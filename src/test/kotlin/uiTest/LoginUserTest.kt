package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import dsl.createUser
import models.authentication.LoginUserRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class LoginUserTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpSelenoid() {
            Configuration.remote = "http://localhost:4444/wd/hub"
            Configuration.baseUrl = "http://192.168.0.4:3000"
            Configuration.browser = "chrome"
            Configuration.browserSize = "1920x1080"
            Configuration.browserCapabilities.setCapability(
                "selenoid:options", mapOf("enableVNC" to true, "enableLog" to true)
            )
        }
    }

    @Test
    fun adminCanLoginWithCorrectDataTest() {
        val admin = LoginUserRequest("admin", "admin")
        Selenide.open("/login")
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.password)
        `$`("button").click()
        `$`(Selectors.byText("Admin Panel")).shouldBe(Condition.visible)
    }

    @Test
    fun userCanLoginWithCorrectDataTest() {
        val user = createUser()
        Selenide.open("/login")
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.originalPassword)
        `$`("button").click()
        `$`(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"))
    }
}