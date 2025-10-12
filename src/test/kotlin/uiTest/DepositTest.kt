package uiTest

import dsl.createUserWithAccount
import dsl.deposit
import dsl.getAllAccounts
import framework.utils.generate
import models.accounts.deposit.DepositMoneyRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ui.pages.BankAlerts
import ui.pages.DepositPage
import ui.pages.UserDashboard
import java.math.BigDecimal

class DepositTest : BaseUiTest() {

    @Test
    fun userCanMakeDepositWithValidSumTest() {
        val (user, account) = createUserWithAccount()
        user.authorizeAsUser()
        val depositSum = "500.0"
        UserDashboard().open().redirectToDepositPage().getPage(DepositPage::class.java)
            .makeDeposit(depositSum, account.accountNumber)
            .checkAlertMessageAndAccept(BankAlerts.SUCCESSFUL_DEPOSIT.format(depositSum, account.accountNumber))
        val balance = user.getAllAccounts().accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isEqualTo(depositSum.toBigDecimal())
    }

    @Test
    fun userCanMakeDepositWithInValidSumTest() {
        val (user, account) = createUserWithAccount()
        user.authorizeAsUser()
        val depositSum = "10000.01"
        UserDashboard().open().redirectToDepositPage().getPage(DepositPage::class.java)
            .makeDeposit(depositSum, account.accountNumber)
            .checkAlertMessageAndAccept(BankAlerts.INVALID_DEPOSIT_SUM.message)
        val balance = user.getAllAccounts().accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isZero
    }

    @Test
    fun userCanSeeHisBalanceTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        user.authorizeAsUser()
        UserDashboard().open().redirectToDepositPage().getPage(DepositPage::class.java)
            .userSeeHisBalanceInSelect(depositSum.toString(), account.balance.toString())
    }

    @Test
    fun userCannotSubmitFormWithoutAccountSelectionTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        user.authorizeAsUser()
        UserDashboard().open().redirectToDepositPage().getPage(DepositPage::class.java)
            .tryToMakeDepositWithoutFillingForm().checkAlertMessageAndAccept(
                BankAlerts.ACCOUNT_SELECTION_NEEDED.message
            )
    }

    @Test
    fun userCannotSubmitFormWithoutDepositSumTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        user.authorizeAsUser()
        UserDashboard().open().redirectToDepositPage().getPage(DepositPage::class.java)
            .tryToMakeDepositWithoutFillingSum(account.accountNumber).checkAlertMessageAndAccept(
                BankAlerts.FILLING_VALID_SUM_NEEDED.message
            )
    }
}