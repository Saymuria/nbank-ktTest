package uiTest

import com.codeborne.selenide.Condition
import common.annotations.UserSession
import dsl.invoke
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
        // проверить, что localStorage пустой
    }
}