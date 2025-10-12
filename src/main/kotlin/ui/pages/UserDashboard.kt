package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`

class UserDashboard : BasePage<UserDashboard>() {
    private val welcomeText = `$`(Selectors.byClassName("welcome-text"))
    private val createNewAccount = `$`(Selectors.byText("➕ Create New Account"))
    private val deposit = `$`(Selectors.byText("💰 Deposit Money"))
    private val transfer = `$`(Selectors.byText("🔄 Make a Transfer"))

    override fun url(): String {
        return "/dashboard"
    }

    fun createNewAccount(): UserDashboard {
        createNewAccount.click()
        return this
    }

    fun redirectToDepositPage(): UserDashboard {
        deposit.click()
        return this
    }

    fun checkWelcomeText(name: String): UserDashboard {
        welcomeText.shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, $name!"))
        return this
    }

    fun redirectToTransferPage(): UserDashboard {
        transfer.click()
        return this
    }
}