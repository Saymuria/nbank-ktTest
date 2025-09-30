package iteration2

import BaseTest
import entities.OperationType.TRANSFER_IN
import entities.OperationType.TRANSFER_OUT
import framework.skeleton.Endpoint.GET_ALL_TRANSACTIONS
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import models.accounts.GetAccountTransactionsResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.admin.createUser.CreateUserRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import steps.AdminSteps
import steps.UserSteps
import java.math.BigDecimal

@DisplayName("Transaction History Test")
class TransactionHistoryTest: BaseTest() {
    private val adminSteps = AdminSteps()
    private val userSteps = UserSteps()

    @Test
     @DisplayName("Check user transactions history")
    fun getTransactionHistory() {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val senderAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val deposit = BigDecimal("500.00")
        val depositMoneyRequest = DepositMoneyRequest(senderAccountId, deposit)
        val depositMoneyResponse = userSteps.makeDeposit(createUserRequest.username, createUserRequest.password, depositMoneyRequest)
        val receiverAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id

        val transferMoneyOutRequest = TransferMoneyRequest(senderAccountId, receiverAccountId, BigDecimal("200.00"))
        val transferMoneyOutResponse = userSteps.makeTransfer(createUserRequest.username, createUserRequest.password, transferMoneyOutRequest)
        softly.assertThat(transferMoneyOutResponse.message).isEqualTo("Transfer successful")
        val transferMoneyInRequest = TransferMoneyRequest(receiverAccountId, senderAccountId, BigDecimal("100.00"))
        val transferMoneyInResponse = userSteps.makeTransfer(createUserRequest.username, createUserRequest.password, transferMoneyInRequest)
        softly.assertThat(transferMoneyInResponse.message).isEqualTo("Transfer successful")

        val getAccountTransactionsResponse = ValidatedCrudRequester<GetAccountTransactionsResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            GET_ALL_TRANSACTIONS
        ).get(senderAccountId)
        //тут можно еще улучшить
        softly.assertThat(getAccountTransactionsResponse.transactions.size).isEqualTo(3)
        softly.assertThat(getAccountTransactionsResponse.transactions[0].amount).isEqualByComparingTo(depositMoneyRequest.balance)
        softly.assertThat(getAccountTransactionsResponse.transactions[1].amount).isEqualByComparingTo(transferMoneyOutRequest.amount)
        softly.assertThat(getAccountTransactionsResponse.transactions[2].amount).isEqualByComparingTo(transferMoneyInRequest.amount)
        softly.assertThat(getAccountTransactionsResponse.transactions[0].type).isEqualTo(depositMoneyResponse.transactions.first().type)
        softly.assertThat(getAccountTransactionsResponse.transactions[1].type).isEqualTo(TRANSFER_OUT)
        softly.assertThat(getAccountTransactionsResponse.transactions[2].type).isEqualTo(TRANSFER_IN)

    }
}