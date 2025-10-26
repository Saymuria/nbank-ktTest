package apiTest.iteration2

import apiTest.BaseTest
import dsl.*
import entities.OperationType.TRANSFER_IN
import entities.OperationType.TRANSFER_OUT
import framework.skeleton.Endpoint.GET_ALL_TRANSACTIONS
import framework.specs.RequestSpecs.Companion.authAsUser
import io.restassured.http.Method.GET
import models.accounts.GetAccountTransactionsResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import java.math.BigDecimal

@DisplayName("Transaction History Test")
class TransactionHistoryTest : BaseTest() {

    @RepeatedTest(value = 10)
    @DisplayName("Check user transactions history")
    fun getTransactionHistory() {
        val sender = createUserWithAccount()
        val senderAccount = sender.getAccount()

        val depositAmount = BigDecimal("500.00")
        val depositMoneyRequest = DepositMoneyRequest(senderAccount.id, depositAmount)
        val depositMoneyResponse = sender.deposit(depositMoneyRequest)
        val receiver = createUserWithAccount()
        val receiverAccount = receiver.getAccount()

        val transferOutAmount = BigDecimal("200.00")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = sender.transfer(transferMoneyOutRequest)
        softly.assertThat(transferMoneyOutResponse.message).isEqualTo("Transfer successful")

        val transferInAmount = BigDecimal("100.00")
        val transferMoneyInRequest = TransferMoneyRequest(receiverAccount.id, senderAccount.id, transferInAmount)

        val transferMoneyInResponse = receiver.transfer(transferMoneyInRequest)

        softly.assertThat(transferMoneyInResponse.message).isEqualTo("Transfer successful")
        val getAccountTransactionsResponse = GET_ALL_TRANSACTIONS.validatedRequest<GetAccountTransactionsResponse>(
            auth = { authAsUser(sender.username, sender.originalPassword) },
            id = senderAccount.id,
            method = GET
        )

        check(softly) {
            getAccountTransactionsResponse.transactions.first { it.type == TRANSFER_OUT }.also {
                it.amount shouldBe transferOutAmount
            }
            getAccountTransactionsResponse.transactions.first { it.type == TRANSFER_IN }.also {
                it.amount shouldBe transferInAmount
            }
            getAccountTransactionsResponse.transactions.first { it.type ==  depositMoneyResponse.transactions.first().type }.also {
               // it.amount shouldBe depositMoneyRequest.balance
            }
        }
    }
}