package uiTest

import common.annotations.UserSession
import dsl.*
import framework.generators.ValueGenerator
import framework.utils.generate
import models.accounts.deposit.DepositMoneyRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import storage.SessionStorage
import ui.pages.BankAlerts
import ui.pages.TransferPage
import ui.pages.UserDashboard
import java.math.BigDecimal

class TransferTest : BaseUiTest() {
    val valueGenerator = ValueGenerator()
    val userDashboard = UserDashboard()
    val transferPage = TransferPage()

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCanMakeTransferWithValidSumTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferSum = "200.0"
        userDashboard {
            open()
            redirectToTransferPage()
            getPage(TransferPage::class.java).makeTransfer(
                senderAccount.accountNumber,
                receiverName.customer.name,
                receiverAccount.accountNumber,
                transferSum
            )
            checkAlertMessageAndAccept(
                BankAlerts.SUCCESSFUL_TRANSFER.format(
                    transferSum,
                    receiverAccount.accountNumber
                )
            )
        }

        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCanMakeTransferWithValidSumToAccountWithoutNameTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()

        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferSum = "200.0"
        userDashboard {
            open()
            redirectToTransferPage()
            getPage(TransferPage::class.java).makeTransfer(
                senderAccount = senderAccount.accountNumber,
                receiverAccount = receiverAccount.accountNumber,
                transferSum = transferSum
            )
        }

        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCannotMakeTransferWithInvalidSumTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()

        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("10000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferSum = "6000.0"
        userDashboard {
            open()
            redirectToTransferPage()
            getPage(TransferPage::class.java).makeTransfer(
                senderAccount.accountNumber,
                receiverName.customer.name,
                receiverAccount.accountNumber,
                transferSum
            )
            checkAlertMessageAndAccept(BankAlerts.UNSUCCESSFUL_TRANSFER.format(transferSum))
        }

        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCannotMakeTransferForSumMoreThanDepositTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()
        val receiverName = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("5000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferSum = "6000.0"
        userDashboard {
            open()
            redirectToTransferPage()
            getPage(TransferPage::class.java).makeTransfer(
                senderAccount.accountNumber,
                receiverName.customer.name,
                receiverAccount.accountNumber,
                transferSum
            )
            checkAlertMessageAndAccept(BankAlerts.TRANSFER_MORE_THAN_DEPOSIT.message)
        }

        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCannotMakeTransferToIncorrectUserNameTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()

        receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("5000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferSum = "6000.0"
        userDashboard {
            open()
            redirectToTransferPage()
            getPage(TransferPage::class.java).makeTransfer(
                senderAccount.accountNumber,
                valueGenerator.generateAlphabeticString(5, 10),
                receiverAccount.accountNumber,
                transferSum
            )
            checkAlertMessageAndAccept(BankAlerts.INCORRECT_RECIPIENT_NAME.message)
        }

        val receiverBalance =
            receiverUser.getAllAccounts().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance =
            senderUser.getAllAccounts().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    @UserSession
    fun userCannotSubmitEmptyTransferFormTest() {
        transferPage {
            open()
            tryToSubmitEmptyTransferForm()
            checkAlertMessageAndAccept(BankAlerts.SUBMIT_EMPTY_TRANSFER_FORM.message)
        }
    }
}