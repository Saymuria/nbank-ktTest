package uiTest

import com.codeborne.selenide.Condition
import common.annotations.Browsers
import dsl.createUser
import dsl.invoke
import models.authentication.LoginUserRequest.Companion.getAdmin
import org.junit.jupiter.api.Test
import ui.pages.AdminPanel
import ui.pages.LoginPage
import ui.pages.UserDashboard

class LoginUserTest : BaseUiTest() {
    val loginPage = LoginPage()


    @Test
    @Browsers(["firefox"])
    fun adminCanLoginWithCorrectDataTest() {
        val admin = getAdmin()
        loginPage {
            open()
            login(admin.username, admin.password)
            getPage(AdminPanel::class.java).getAdminPanelText().shouldBe(Condition.visible)

        }
    }

    @Test
    fun userCanLoginWithCorrectDataTest() {
        val user = createUser()
        loginPage {
            open()
            login(user.username, user.originalPassword)
            getPage(UserDashboard::class.java).checkWelcomeText("noname")
        }
    }
}