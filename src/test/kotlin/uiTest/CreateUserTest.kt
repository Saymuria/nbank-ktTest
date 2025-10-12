package uiTest

import com.codeborne.selenide.Condition
import framework.extentions.shouldMatchResponse
import framework.utils.generate
import models.admin.createUser.CreateUserRequest
import models.authentication.LoginUserRequest.Companion.getAdmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import steps.AdminSteps
import ui.pages.AdminPanel
import ui.pages.BankAlerts

class CreateUserTest : BaseUiTest() {
    val adminSteps = AdminSteps()

    @Test
    fun adminCanCreateUser() {
        //Шаг 1 админ залогинился в банке
        val admin = getAdmin()
        authorizeAsUser(admin)
        AdminPanel().open().getAdminPanelText().shouldBe(Condition.visible)
        //Шаг 2 админ создает юзера в банке
        val newUser = generate<CreateUserRequest>()
        AdminPanel().createUser(newUser.username, newUser.password)
            .checkAlertMessageAndAccept(BankAlerts.USER_CREATED_SUCCESSFULLY.message).getAllUsers()
            .findBy(Condition.exactText(newUser.username + "\nUSER")).shouldBe(Condition.visible)
        //Шаг 5 проверка, что юзера создан на API
        val user = adminSteps.getAllUsers().customers.first { user -> user.username == newUser.username }
        newUser shouldMatchResponse user
    }

    @Test
    fun adminCannotCreateUserWithInvalidDataTest() {
        //Шаг 1 админ залогинился в банке
        val admin = getAdmin()
        authorizeAsUser(admin)
        AdminPanel().open().getAdminPanelText().shouldBe(Condition.visible)
        val newUser = generate<CreateUserRequest>(mapOf("username" to "sh"))

        AdminPanel().createUser(newUser.username, newUser.password)
            .checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.message).getAllUsers()
            .findBy(Condition.exactText(newUser.username + "\nUSER")).shouldNotBe(Condition.exist)
        //Шаг 5 проверка, что юзера НЕ создан на API
        val user = adminSteps.getAllUsers().customers.filter { user -> user.username == newUser.username }.size
        assertThat(user).isZero()
    }
}