package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`

class DepositPage : BasePage<DepositPage>() {
    private val depositTitle = `$`(Selectors.byText("💰 Deposit Money"))
    private val depositButton = `$`(Selectors.byText("💵 Deposit"))

    override fun url(): String {
        return "/deposit"
    }

    fun makeDeposit(depositSum: String, accountNumber: String): DepositPage {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(accountNumber)
        amountInput.sendKeys(depositSum)
        depositButton.click()
        return this
    }

    fun userSeeHisBalanceInSelect(depositSum: String, accountNumber: String): DepositPage {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText("$accountNumber (Balance: \$$depositSum)")
        return this
    }

    fun tryToMakeDepositWithoutFillingForm(): DepositPage {
        depositTitle.shouldBe(Condition.visible)
        depositButton.click()
        return this
    }

    fun tryToMakeDepositWithoutFillingSum(accountNumber: String): DepositPage {
        depositTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(accountNumber)
        depositButton.click()
        return this
    }
}