package uiTest

import dsl.createUserWithAccount
import dsl.deposit
import dsl.getAllAccounts
import dsl.updateProfileName
import framework.generators.ValueGenerator
import framework.utils.generate
import models.accounts.deposit.DepositMoneyRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ui.pages.BankAlerts
import ui.pages.TransferPage
import ui.pages.UserDashboard
import java.math.BigDecimal

class TransferTest : BaseUiTest() {
    companion object {
        val valueGenerator = ValueGenerator()
    }

    @Test
    fun userCanMakeTransferWithValidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        senderUser.authorizeAsUser()
        val transferSum = "200.0"
        UserDashboard().open().redirectToTransferPage().getPage(TransferPage::class.java).makeTransfer(
            senderAccount.accountNumber,
            receiverName.customer.name,
            receiverAccount.accountNumber,
            transferSum
        ).checkAlertMessageAndAccept(BankAlerts.SUCCESSFUL_TRANSFER.format(transferSum, receiverAccount.accountNumber))
        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    fun userCanMakeTransferWithValidSumToAccountWithoutNameTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        senderUser.authorizeAsUser()
        val transferSum = "200.0"
        UserDashboard().open().redirectToTransferPage().getPage(TransferPage::class.java).makeTransfer(
            senderAccount = senderAccount.accountNumber,
            receiverAccount = receiverAccount.accountNumber,
            transferSum = transferSum
        )
        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    fun userCannotMakeTransferWithInvalidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("10000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        senderUser.authorizeAsUser()
        val transferSum = "6000.0"
        UserDashboard().open().redirectToTransferPage().getPage(TransferPage::class.java).makeTransfer(
            senderAccount.accountNumber,
            receiverName.customer.name,
            receiverAccount.accountNumber,
            transferSum
        ).checkAlertMessageAndAccept(BankAlerts.UNSUCCESSFUL_TRANSFER.format(transferSum))
        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    fun userCannotMakeTransferForSumMoreThanDepositTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("5000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        senderUser.authorizeAsUser()
        val transferSum = "6000.0"
        UserDashboard().open().redirectToTransferPage().getPage(TransferPage::class.java).makeTransfer(
            senderAccount.accountNumber,
            receiverName.customer.name,
            receiverAccount.accountNumber,
            transferSum
        ).checkAlertMessageAndAccept(BankAlerts.TRANSFER_MORE_THAN_DEPOSIT.message)
        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    fun userCannotMakeTransferToIncorrectUserNameTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("5000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        senderUser.authorizeAsUser()
        val transferSum = "6000.0"
        UserDashboard().open().redirectToTransferPage().getPage(TransferPage::class.java).makeTransfer(
            senderAccount.accountNumber,
            valueGenerator.generateAlphabeticString(5, 10),
            receiverAccount.accountNumber,
            transferSum
        ).checkAlertMessageAndAccept(BankAlerts.INCORRECT_RECIPIENT_NAME.message)
        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    fun userCannotSubmitEmptyTransferFormTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        senderUser.authorizeAsUser()
        TransferPage().open().tryToSubmitEmptyTransferForm().checkAlertMessageAndAccept(BankAlerts.SUBMIT_EMPTY_TRANSFER_FORM.message)
    }
}