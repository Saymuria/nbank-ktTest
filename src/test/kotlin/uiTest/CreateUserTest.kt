package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide
import common.annotations.AdminSession
import dsl.check
import dsl.invoke
import framework.utils.generate
import models.admin.createUser.CreateUserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import steps.AdminSteps.Companion.getAllUsers
import ui.pages.AdminPanel
import ui.pages.BankAlerts
import ui.pages.BankAlerts.USER_CREATED_SUCCESSFULLY

class CreateUserTest : BaseUiTest() {
    val adminPanel  by lazy { AdminPanel() }

    @Test
    @AdminSession
    fun adminCanCreateUser() {
        adminPanel {
            open()
            getAdminPanelText().shouldBe(Condition.visible)
        }
        val newUser = generate<CreateUserRequest>()

        adminPanel {
            createUser(newUser.username, newUser.password)
            checkAlertMessageAndAccept(USER_CREATED_SUCCESSFULLY.message)
            Selenide.refresh()
            userBageByUsername(newUser.username)
        }
        val user = getAllUsers().customers.first { user -> user.username == newUser.username }
        check(softly) {
            newUser.username shouldBe user.username
        }
    }

    @Test
    @AdminSession
    fun adminCannotCreateUserWithInvalidDataTest() {
        adminPanel {
            open()
            getAdminPanelText().shouldBe(Condition.visible)
        }
        val newUser = generate<CreateUserRequest>(mapOf("username" to "sh"))
        adminPanel {
            createUser(newUser.username, newUser.password)
            checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.message)
        }
        val user = getAllUsers().customers.filter { user -> user.username == newUser.username }.size
        assertThat(user).isZero()
    }
}