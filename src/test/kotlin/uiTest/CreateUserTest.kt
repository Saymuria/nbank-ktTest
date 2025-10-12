package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.switchTo
import dsl.validatedRequest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.GET_ALL_USER
import framework.specs.RequestSpecs.Companion.adminAuthSpec
import framework.utils.generate
import io.restassured.http.Method.GET
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.authentication.LoginUserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CreateUserTest {
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
    fun adminCanCreateUser() {
        //Шаг 1 админ залогинился в банке
        val admin = LoginUserRequest("admin", "admin")
        Selenide.open("/login")
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.password)
        `$`("button").click()
        `$`(Selectors.byText("Admin Panel")).shouldBe(Condition.visible)
        //Шаг 2 админ создает юзера в банке
        val newUser = generate<CreateUserRequest>()
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.password)
        `$`(Selectors.byText("Add User")).click()
        // шаг 3 проверка, что aлерт User created successfully
        val alert = switchTo().alert()

        assertEquals(alert.text, "✅ User created successfully!")
        alert.accept()

        //Шаг 4 проверка, что юзер отображается на UI
        val allUsersFromDashboard = `$`(Selectors.byText("All Users")).parent().findAll("li")
        allUsersFromDashboard.findBy(Condition.exactText(newUser.username + "\nUSER")).shouldBe(Condition.visible)
        //Шаг 5 проверка, что юзера создан на API
        val user = GET_ALL_USER.validatedRequest<GetAllUserResponse>(
            auth = { adminAuthSpec() },
            method = GET
        ).customers.first { user -> user.username == newUser.username }
        newUser shouldMatchResponse user
    }

    @Test
    fun adminCannotCreateUserWithInvalidDataTest() {
        //Шаг 1 админ залогинился в банке
        val admin = LoginUserRequest("admin", "admin")
        Selenide.open("/login")
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.password)
        `$`("button").click()
        `$`(Selectors.byText("Admin Panel")).shouldBe(Condition.visible)
        //Шаг 2 админ создает юзера в банке
        val newUser = generate<CreateUserRequest>(mapOf("username" to "sh"))
        `$`(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.username)
        `$`(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.password)
        `$`(Selectors.byText("Add User")).click()
        // шаг 3 проверка, что aлерт
        val alert = switchTo().alert()

        assertThat(alert.text).contains("Username must be between 3 and 15 characters")
        alert.accept()

        //Шаг 4 проверка, что юзер НЕ отображается на UI
        val allUsersFromDashboard = `$`(Selectors.byText("All Users")).parent().findAll("li")
        allUsersFromDashboard.findBy(Condition.exactText(newUser.username + "\nUSER")).shouldNotBe(Condition.exist)
        //Шаг 5 проверка, что юзера НЕ создан на API
        val user = GET_ALL_USER.validatedRequest<GetAllUserResponse>(
            auth = { adminAuthSpec() },
            method = GET
        ).customers.filter { user -> user.username == newUser.username }.size
        assertThat(user).isZero()
    }
}