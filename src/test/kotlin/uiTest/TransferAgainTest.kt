package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import dsl.*
import framework.generators.ValueGenerator
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import io.restassured.http.Method.GET
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.authentication.LoginUserRequest
import models.customer.GetCustomerAccountsResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ui.pages.BankAlerts
import ui.pages.TransferPage
import java.math.BigDecimal

class TransferAgainTest : BaseUiTest() {
    companion object {
        val valueGenerator = ValueGenerator()
    }
    @Test
    fun userCanMakeTransferWithValidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        senderUser.authorizeAsUser()
        TransferPage().open().openTransferAgain().searchTransactions(name).getAllTransactions().find { it.`$`(Selectors.byText("TRANSFER_IN")).exists() }?.`$`(Selectors.byText("🔁 Repeat"))
            ?.click()
        TransferPage().makeTransferAgain(senderAccount.accountNumber)
            .checkAlertMessageAndAccept(
                BankAlerts.TRANSFER_AGAIN_SUCCESS.format(transferOutAmount, senderAccount.id, receiverAccount.id)
            )
        val receiverBalance =
            receiverUser.getCustomerProfile().accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance

        assertThat(receiverBalance).isEqualTo(transferOutAmount + transferOutAmount)
        val senderBalance =
            senderUser.getCustomerProfile().accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferOutAmount - transferOutAmount)
    }

    @Test
    fun userCanMakeSeeTransferOutTransactionsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        val transferMoneyInRequest = TransferMoneyRequest(receiverAccount.id, senderAccount.id, transferOutAmount)
        val transferMoneyInResponse = receiverUser.transfer(transferMoneyInRequest)

        senderUser.authorizeAsUser()
        val allTransactions = TransferPage().open().openTransferAgain().searchTransactions(name).getAllTransactions()
        assertThat(allTransactions).hasSize(1)

        //- Проверяем, что данные по транзакции совпадают с теми, которые были в исходящей транзакции из предшагов(сумма и тип перевода)
    }

    @Test
    fun userCanFindTransferOutTransactionsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser1, receiverAccount1) = createUserWithAccount()
        val (receiverUser2, receiverAccount2) = createUserWithAccount()

        val name = receiverUser1.updateProfileName(generate<UpdateCustomerProfileRequest>()).customer.name!!
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount1.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        val transferMoneyOutRequest2 = TransferMoneyRequest(senderAccount.id, receiverAccount2.id, transferOutAmount)
        val transferMoneyOutResponse2 = senderUser.transfer(transferMoneyOutRequest)

        senderUser.authorizeAsUser()
        val allTransactions = TransferPage().open().openTransferAgain().searchTransactions(name).getAllTransactions()
        assertThat(allTransactions).hasSize(1)
        //- Проверяем, что данные по транзакции совпадают с теми, которые были в исходящей транзакции из предшагов(сумма и тип перевода)

        val allTransactions2 = TransferPage().searchTransactions(receiverUser2.username)

        assertThat(allTransactions).hasSize(1)
        //    - Данные по транзакции совпадают с теми, которые были в исходящей транзакции из предшагов(сумма и тип перевода)
    }

    @Test
    fun userCannotFindTransferOutWithRandomUsernameTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        senderUser.authorizeAsUser()
        TransferPage().open().openTransferAgain().searchTransactions(valueGenerator.generateAlphabeticString(3,5)).checkAlertMessageAndAccept(
            BankAlerts.NO_MATCHING_USER_FOUND.message
        )
        val allTransactions = TransferPage().getAllTransactions()
        assertThat(allTransactions).hasSize(0)
    }

    @Test
    fun userCannotFindTransferOutWithoutAnyOperationsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        senderUser.authorizeAsUser()
        val allTransactions = TransferPage().open().getAllTransactions()
        assertThat(allTransactions).hasSize(0)
    }

}