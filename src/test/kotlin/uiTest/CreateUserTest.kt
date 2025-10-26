package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide
import common.annotations.AdminSession
import dsl.invoke
import framework.extentions.shouldMatchResponse
import framework.utils.generate
import models.admin.createUser.CreateUserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import steps.AdminSteps.Companion.getAllUsers
import ui.pages.AdminPanel
import ui.pages.BankAlerts
import ui.pages.BankAlerts.USER_CREATED_SUCCESSFULLY

class CreateUserTest : BaseUiTest() {
    val adminPanel = AdminPanel()

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
            assertTrue(
                getAllUsers().any { userBage -> userBage.getUsername() == newUser.username },
                "Пользователь '${newUser.username}' не найден"
            )

        }
        val user = getAllUsers().customers.first { user -> user.username == newUser.username }
        newUser shouldMatchResponse user
    }

    @Test
    @AdminSession
    fun adminCannotCreateUserWithInvalidDataTest() {
        AdminPanel().open().getAdminPanelText().shouldBe(Condition.visible)
        val newUser = generate<CreateUserRequest>(mapOf("username" to "sh"))
        adminPanel {
            createUser(newUser.username, newUser.password)
            checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.message)
            Selenide.refresh()
            assertFalse(
                getAllUsers().any { userBage -> userBage.getUsername() == newUser.username },
                "Пользователь '${newUser.username}' не найден"
            )
        }
        val user = getAllUsers().customers.filter { user -> user.username == newUser.username }.size
        assertThat(user).isZero()
    }
}