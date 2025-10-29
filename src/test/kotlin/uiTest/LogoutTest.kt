package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide.executeJavaScript
import common.annotations.UserSession
import dsl.invoke
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ui.pages.LoginPage
import ui.pages.UserDashboard

class LogoutTest : BaseUiTest() {
    val userDashboard = UserDashboard()

    @Test
    @UserSession
    fun userCanLogoutTest() {
        userDashboard {
            open()
            logout()
            getPage(LoginPage::class.java).getLoginTitle().shouldBe(Condition.visible)
        }
        val tokenValue = executeJavaScript<String?>(
            "localStorage.getItem('authToken')"
        )
        assertThat(tokenValue).isNull()
    }
}