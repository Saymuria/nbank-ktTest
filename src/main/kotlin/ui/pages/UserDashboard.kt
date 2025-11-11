package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import hellpers.stepWithResult

class UserDashboard : BasePage<UserDashboard>() {
    private val welcomeText = `$`(Selectors.byClassName("welcome-text"))
    private val createNewAccount = `$`(Selectors.byText("➕ Create New Account"))
    private val deposit = `$`(Selectors.byText("💰 Deposit Money"))
    private val transfer = `$`(Selectors.byText("🔄 Make a Transfer"))

    override fun url(): String {
        return "/dashboard"
    }

    fun createNewAccount(): UserDashboard = stepWithResult("Создание счет") {
        createNewAccount.click()
        this
    }

    fun redirectToDepositPage(): UserDashboard = stepWithResult("Переход на страницу пополнение") {
        deposit.click()
        this
    }

    fun checkWelcomeText(name: String): UserDashboard = stepWithResult("Проверка приветственного сообщения") {
        welcomeText.shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, $name!"))
        this
    }

    fun redirectToTransferPage(): UserDashboard = stepWithResult("Переход на страницу перевода") {
        transfer.click()
        this
    }
}