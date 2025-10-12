package uiTest

import com.codeborne.selenide.Condition
import dsl.createUser
import org.junit.jupiter.api.Test
import ui.pages.LoginPage
import ui.pages.UserDashboard

class LogoutTest : BaseUiTest() {

    @Test
    fun userCanLogoutTest() {
        val user = createUser()
        user.authorizeAsUser()
        UserDashboard().open().logout().getPage(LoginPage::class.java).getLoginTitle().shouldBe(Condition.visible)
        // проверить, что localStorage пустой
    }
}