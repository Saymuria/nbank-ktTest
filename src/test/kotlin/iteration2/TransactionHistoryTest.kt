package iteration2

import BaseTest
import dsl.check
import dsl.createUserWithAccount
import dsl.deposit
import dsl.transfer
import dsl.validatedRequest
import entities.OperationType.TRANSFER_IN
import entities.OperationType.TRANSFER_OUT
import framework.skeleton.Endpoint.GET_ALL_TRANSACTIONS
import framework.specs.RequestSpecs.Companion.authAsUser
import io.restassured.http.Method.GET
import models.accounts.GetAccountTransactionsResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Transaction History Test")
class TransactionHistoryTest : BaseTest() {

    @Test
    @DisplayName("Check user transactions history")
    fun getTransactionHistory() {
        val (sender, senderAccount) = createUserWithAccount()

        val depositAmount = BigDecimal("500.00")
        val depositMoneyRequest = DepositMoneyRequest(senderAccount.id, depositAmount)
        val depositMoneyResponse = sender.deposit(depositMoneyRequest)

        val (receiver, receiverAccount) = createUserWithAccount()
        val transferOutAmount = BigDecimal("200.00")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = sender.transfer(transferMoneyOutRequest)
        softly.assertThat(transferMoneyOutResponse.message).isEqualTo("Transfer successful")

        val transferInAmount = BigDecimal("100.00")
        val transferMoneyInRequest = TransferMoneyRequest(receiverAccount.id, senderAccount.id, transferInAmount)

        val transferMoneyInResponse = sender.transfer(transferMoneyOutRequest)

        softly.assertThat(transferMoneyInResponse.message).isEqualTo("Transfer successful")
        val getAccountTransactionsResponse =  GET_ALL_TRANSACTIONS.validatedRequest<GetAccountTransactionsResponse>(
            auth = { authAsUser(sender.username, sender.originalPassword) },
            id = senderAccount.id,
            method = GET
        )
        check(softly){
            getAccountTransactionsResponse.transactions[0].amount shouldBe depositMoneyRequest.balance
            getAccountTransactionsResponse.transactions[1].amount shouldBe transferMoneyOutRequest.amount
            getAccountTransactionsResponse.transactions[2].amount shouldBe transferMoneyInRequest.amount
            getAccountTransactionsResponse.transactions[0].type shouldBe depositMoneyResponse.transactions.first().type
            getAccountTransactionsResponse.transactions[1].type shouldBe TRANSFER_OUT
            getAccountTransactionsResponse.transactions[2].type shouldBe TRANSFER_IN
        }
    }
}