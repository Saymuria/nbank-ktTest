package uiTest

import com.codeborne.selenide.Condition
import dsl.createUser
import models.authentication.LoginUserRequest.Companion.getAdmin
import org.junit.jupiter.api.Test
import ui.pages.AdminPanel
import ui.pages.LoginPage
import ui.pages.UserDashboard

class LoginUserTest : BaseUiTest() {


    @Test
    fun adminCanLoginWithCorrectDataTest() {
        val admin = getAdmin()
        LoginPage().open().login(admin.username, admin.password).getPage(AdminPanel::class.java).getAdminPanelText()
            .shouldBe(Condition.visible)
    }

    @Test
    fun userCanLoginWithCorrectDataTest() {
        val user = createUser()
        LoginPage().open().login(user.username, user.originalPassword).getPage(UserDashboard::class.java).checkWelcomeText("noname")
    }
}