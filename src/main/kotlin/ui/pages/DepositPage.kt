package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.SelenideElement
import hellpers.step
import hellpers.stepWithResult

class DepositPage : BasePage<DepositPage>() {
    private val depositTitle = `$`(Selectors.byText("💰 Deposit Money"))
    private val depositButton = `$`(Selectors.byText("💵 Deposit"))

    override fun url(): String {
        return "/deposit"
    }

    fun makeDeposit(depositSum: String, accountNumber: String): DepositPage = stepWithResult("Пополнение счета") {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(accountNumber)
        amountInput.sendKeys(depositSum)
        depositButton.click()
        this
    }

    fun userSeeHisBalanceInSelect(accountNumber: String): SelenideElement = stepWithResult("Пользователь видит баланс в селекте") {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(accountNumber)
        val selectedOption = accountSelect.selectedOption
        selectedOption
    }

    fun tryToMakeDepositWithoutFillingForm(): DepositPage = stepWithResult("Сабмит пустой формы пополнения") {
        depositTitle.shouldBe(Condition.visible)
        depositButton.click()
        this
    }

    fun tryToMakeDepositWithoutFillingSum(accountNumber: String): DepositPage = stepWithResult("Сабмит формы пополнения без заполнения суммы") {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(accountNumber)
        depositButton.click()
        this
    }
}
