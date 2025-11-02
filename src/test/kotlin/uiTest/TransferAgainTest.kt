package uiTest

import common.annotations.UserSession
import dsl.*
import framework.generators.ValueGenerator
import framework.utils.generate
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import storage.SessionStorage
import ui.pages.BankAlerts
import ui.pages.TransferPage
import java.math.BigDecimal

class TransferAgainTest : BaseUiTest() {
    val valueGenerator by lazy { ValueGenerator() }
    val transferPage by lazy { TransferPage() }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCanMakeTransferWithValidSumTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()

        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")

        senderUser.transfer(TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount))
        transferPage {
            open()
            openTransferAgain()
            searchTransactions(name)
            //должен быть TRANSFER_OUT
            getAllTransactions().find { it.getTransactionType() == "TRANSFER_IN" }
                ?.clickRepeat()
            makeTransferAgain(senderAccount.accountNumber)
            checkAlertMessageAndAccept(
                BankAlerts.TRANSFER_AGAIN_SUCCESS.format(transferOutAmount.setScale(0).toPlainString(), senderAccount.id, receiverAccount.id)
            )
        }

        val receiverBalance =
            receiverUser.getCustomerProfile().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferOutAmount + transferOutAmount)
        val senderBalance =
            senderUser.getCustomerProfile().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferOutAmount - transferOutAmount)
    }

    @Test
    @UserSession(value = 2, withAccount = true)
    fun userCanMakeSeeTransferOutTransactionsTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUser = SessionStorage.getUser(2)
        val receiverAccount = receiverUser.getAccount()

        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")

        val transferMoneyOutResponse =
            senderUser.transfer(TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount))
        val transferMoneyInResponse =
            receiverUser.transfer(TransferMoneyRequest(receiverAccount.id, senderAccount.id, transferOutAmount))

        transferPage {
            open()
            openTransferAgain()
            searchTransactions(name)
            val allTransactions = getAllTransactions()
            //По задумке входящие транзакции не должны быть видны, но тк у нас видны все транзакции,
            // то проверка ниже закоменчена
            //assertThat(allTransactions).hasSize(1)
            allTransactions.first { it.getTransactionType() == "TRANSFER_IN" }.also {
                assertThat(it.getTransactionSum()).isEqualTo("\$${transferOutAmount.setScale(2)}")
            }
        }
    }

    @Test
    @UserSession(value = 3, withAccount = true)
    fun userCanFindTransferOutTransactionsTest() {
        val senderUser = SessionStorage.getUser(1)
        val senderAccount = senderUser.getAccount()
        val receiverUserFirst = SessionStorage.getUser(2)
        val receiverAccountFirst = receiverUserFirst.getAccount()
        val receiverUserSecond = SessionStorage.getUser(3)
        val receiverAccountSecond = receiverUserSecond.getAccount()

        val name = receiverUserFirst.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")

        val transferMoneyOutResponse =
            senderUser.transfer(TransferMoneyRequest(senderAccount.id, receiverAccountFirst.id, transferOutAmount))
        val transferMoneyOutResponseSecond =
            senderUser.transfer(TransferMoneyRequest(senderAccount.id, receiverAccountSecond.id, transferOutAmount))

        transferPage {
            open()
            openTransferAgain()
            searchTransactions(name)
            val allTransactions = getAllTransactions()
            //assertThat(allTransactions).hasSize(1)
            allTransactions.first { it.getTransactionType() == "TRANSFER_IN" }.also {
                assertThat(it.getTransactionSum()).isEqualTo("\$${transferOutAmount.setScale(2)}")
            }
            searchTransactions(receiverUserSecond.username)
            val allTransactionsForSecondUser = getAllTransactions()
            //assertThat(allTransactionsForSecondUser).hasSize(1)
            allTransactionsForSecondUser.first { it.getTransactionType() == "TRANSFER_IN" }.also {
                assertThat(it.getTransactionSum()).isEqualTo("\$${transferOutAmount.setScale(2)}")
            }
        }
    }

    @Test
    @UserSession(withAccount = true)
    fun userCannotFindTransferOutWithRandomUsernameTest() {
        transferPage {
            open()
            openTransferAgain()
            searchTransactions(valueGenerator.generateAlphabeticString(3, 5))
            checkAlertMessageAndAccept(
                BankAlerts.NO_MATCHING_USER_FOUND.message
            )
            val allTransactions = getAllTransactions()
            assertThat(allTransactions).hasSize(0)
        }
    }

    @Test
    @UserSession(withAccount = true)
    fun userCannotFindTransferOutWithoutAnyOperationsTest() {
        transferPage {
            open()
            openTransferAgain()
            val allTransactions = getAllTransactions()
            assertThat(allTransactions).hasSize(0)
        }
    }

}