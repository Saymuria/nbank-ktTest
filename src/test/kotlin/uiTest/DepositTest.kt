package uiTest

import com.codeborne.selenide.Condition
import common.annotations.UserSession
import dsl.createAccount
import dsl.deposit
import dsl.getAllAccounts
import dsl.invoke
import framework.utils.generate
import models.accounts.deposit.DepositMoneyRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import storage.SessionStorage
import ui.pages.BankAlerts
import ui.pages.BankAlerts.*
import ui.pages.DepositPage
import ui.pages.UserDashboard
import java.math.BigDecimal

class DepositTest : BaseUiTest() {
    val userDashboard = UserDashboard()

    @Test
    @UserSession(withAccount = true)
    fun userCanMakeDepositWithValidSumTest() {
        val depositSum = "500.0"
        val user = SessionStorage.getUser()
        val account = user.getAccount()
        userDashboard {
            open()
            redirectToDepositPage()
            getPage(DepositPage::class.java).makeDeposit(depositSum, account.accountNumber)
            checkAlertMessageAndAccept(BankAlerts.SUCCESSFUL_DEPOSIT.format(depositSum, account.accountNumber))
        }
        val balance = user.getAllAccounts().accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isEqualTo(depositSum.toBigDecimal())
    }

    @Test
    @UserSession(withAccount = true)
    fun userCanMakeDepositWithInValidSumTest() {
        val depositSum = "10000.01"
        val user = SessionStorage.getUser()
        val account = user.getAccount()
        userDashboard {
            open()
            redirectToDepositPage()
            getPage(DepositPage::class.java).makeDeposit(depositSum, account.accountNumber)
            checkAlertMessageAndAccept(INVALID_DEPOSIT_SUM.message)
        }
        val balance = user.getAllAccounts().accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isZero
    }

    @Test
    @UserSession(withAccount = true)
    fun userCanSeeHisBalanceTest() {
        val depositSum = BigDecimal("500.00")
        val user = SessionStorage.getUser()
        val account = user.getAccount()
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        userDashboard {
            open()
            redirectToDepositPage()
            getPage(DepositPage::class.java).userSeeHisBalanceInSelect(
                account.accountNumber
            ).shouldHave(Condition.exactText("${account.accountNumber} (Balance: \$$depositSum)"))
        }
    }

    @Test
    @UserSession(withAccount = true)
    fun userCannotSubmitFormWithoutAccountSelectionTest() {
        val depositSum = BigDecimal("500.00")
        val user = SessionStorage.getUser()
        val account = user.getAccount()
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        userDashboard {
            open()
            redirectToDepositPage()
            getPage(DepositPage::class.java).tryToMakeDepositWithoutFillingForm()
            checkAlertMessageAndAccept(
                ACCOUNT_SELECTION_NEEDED.message
            )
        }
    }

    @Test
    @UserSession(withAccount = true)
    fun userCannotSubmitFormWithoutDepositSumTest() {
        val depositSum = BigDecimal("500.00")
        val user = SessionStorage.getUser()
        val account = user.getAccount()
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        userDashboard {
            open()
            redirectToDepositPage()
            getPage(DepositPage::class.java).tryToMakeDepositWithoutFillingSum(account.accountNumber)
            checkAlertMessageAndAccept(
                FILLING_VALID_SUM_NEEDED.message
            )
        }
    }
}