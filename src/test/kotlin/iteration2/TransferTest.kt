package iteration2

import BaseTest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.TRANSFER_MONEY
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.accounts.transfer.TransferMoneyResponse
import models.admin.createUser.CreateUserRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import steps.AdminSteps
import steps.UserSteps
import java.math.BigDecimal
import java.util.stream.Stream

@DisplayName("User can transfer money")
class TransferTest :BaseTest() {
    private val adminSteps = AdminSteps()
    private val userSteps = UserSteps()

    companion object {
        @JvmStatic
        private fun invalidTransferSum(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(BigDecimal("5000.01")),
                Arguments.of(BigDecimal("0.99")),
                Arguments.of(BigDecimal("-1.00")),
                Arguments.of(BigDecimal("0.001")),
                Arguments.of(BigDecimal("0.00"))
            )
        }
    }

    @Test
    @DisplayName("User can make transfer through his accounts all sum")
    fun transferAllDepositTroughAccounts() {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val senderAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoneyRequest = generate<DepositMoneyRequest>(mapOf("id" to senderAccountId))
        userSteps.makeDeposit(createUserRequest.username, createUserRequest.password, depositMoneyRequest)
        val receiverAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val transferMoneyRequest = TransferMoneyRequest(senderAccountId, receiverAccountId, depositMoneyRequest.balance)
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            TRANSFER_MONEY
        ).post(transferMoneyRequest)
    }

    @Test
    @DisplayName("User can make transfer to other user's account")
    fun transferMoney() {
        val createSender = generate<CreateUserRequest>()
        adminSteps.createUser(createSender)
        val senderAccountId = userSteps.createAccount(createSender.username, createSender.password).id
        val deposit = BigDecimal("300.00")
        val depositMoneyRequest = DepositMoneyRequest(senderAccountId, deposit)
        userSteps.makeDeposit(createSender.username, createSender.password, depositMoneyRequest)

        val createReceiver = generate<CreateUserRequest>()
        adminSteps.createUser(createReceiver)

        val receiverAccountId = userSteps.createAccount(createReceiver.username, createReceiver.password).id
        val transfer = BigDecimal("50.00")
        val transferMoneyRequest = TransferMoneyRequest(senderAccountId, receiverAccountId, transfer)
        val transferMoneyResponse = ValidatedCrudRequester<TransferMoneyResponse>(
            authAsUser(createSender.username, createSender.password),
            requestReturnOk(),
            TRANSFER_MONEY
        ).post(transferMoneyRequest)
        softly.assertThat(transferMoneyResponse.message).isEqualTo("Transfer successful")
        transferMoneyRequest.shouldMatchResponse(softly, transferMoneyResponse)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.00", "5000.00"])
    @DisplayName("User can make transfer to bank account")
    fun makeTransferEdgeCheck(transferSum: String) {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val senderAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoneyRequest = DepositMoneyRequest(senderAccountId, BigDecimal("5500.00"))
        userSteps.makeDeposit(createUserRequest.username, createUserRequest.password, depositMoneyRequest)
        val receiverAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val transferMoneyRequest = TransferMoneyRequest(senderAccountId, receiverAccountId, BigDecimal(transferSum))
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            TRANSFER_MONEY
        ).post(transferMoneyRequest)
    }

    @ParameterizedTest
    @MethodSource("invalidTransferSum")
    @DisplayName("User cannot make deposit to bank account")
    fun depositWithInvalidDepositSum(
        transferSum: BigDecimal
    ) {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val senderAccountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoneyRequest = DepositMoneyRequest(senderAccountId, BigDecimal("10000.00"))
        userSteps.makeDeposit(createUserRequest.username, createUserRequest.password, depositMoneyRequest)
        val receiverAccountId = ValidatedCrudRequester<CreateAccountResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            ResponseSpec.entityWasCreated(), Endpoint.CREATE_ACCOUNT
        ).post(null).id
        val transferMoneyRequest = TransferMoneyRequest(senderAccountId, receiverAccountId, transferSum)
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnsBadRequest(),
            TRANSFER_MONEY
        ).post(transferMoneyRequest)
    }
}