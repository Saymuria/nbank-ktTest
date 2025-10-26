package apiTest.iteration2

import apiTest.BaseTest
import dsl.*
import framework.skeleton.Endpoint.GET_CUSTOMER_PROFILE
import framework.skeleton.Endpoint.TRANSFER_MONEY
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import io.restassured.http.Method.GET
import io.restassured.http.Method.POST
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.customer.GetCustomerProfileResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.stream.Stream

@DisplayName("User can transfer money")
class TransferTest : BaseTest() {

    companion object {
        @JvmStatic
        private fun invalidTransferSum(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(BigDecimal("10000.01")),
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
        val sender = createUserWithAccount()
        val senderAccount = sender.getAccount()
        val receiver = createUserWithAccount()
        val receiverAccount = receiver.getAccount()
        val depositMoneyRequest = generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id))
        sender.deposit(depositMoneyRequest)
        val transferMoneyRequest =
            TransferMoneyRequest(senderAccount.id, receiverAccount.id, depositMoneyRequest.balance)
        val transferMoneyResponse = sender.transfer(transferMoneyRequest)
        val receiverBalance = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(receiver.username, receiver.originalPassword) },
            method = GET
        ).accounts.first().balance
        check(softly) {
            transferMoneyResponse.message shouldBe "Transfer successful"
            transferMoneyResponse.amount shouldBe receiverBalance
        }
    }

    @Test
    @DisplayName("User can make transfer to other user's account")
    fun transferMoney() {
        val sender = createUserWithAccount()
        val senderAccount = sender.getAccount()
        val validDepositSum = BigDecimal("300.00")
        sender.deposit(DepositMoneyRequest(senderAccount.id, validDepositSum))

        val receiver = createUserWithAccount()
        val receiverAccount = receiver.getAccount()

        val transfer = BigDecimal("50.00")
        val transferMoneyRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transfer)

        val transferMoneyResponse = sender.transfer(transferMoneyRequest)
        val receiverBalance = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(receiver.username, receiver.originalPassword) },
            method = GET
        ).accounts.first().balance
        check(softly) {
            transferMoneyResponse.message shouldBe "Transfer successful"
            transferMoneyResponse.amount shouldBe receiverBalance
            transferMoneyRequest shouldMatch transferMoneyResponse
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.00", "10000.00"])
    @DisplayName("User can make transfer with edge amount")
    fun makeTransferEdgeCheck(transferSum: String) {
        val sender = createUserWithAccount()
        val senderAccount = sender.getAccount()
        val validDepositSum = BigDecimal("5000.00")
        sender.deposit(DepositMoneyRequest(senderAccount.id, validDepositSum))
        sender.deposit(DepositMoneyRequest(senderAccount.id, validDepositSum))

        val receiver = createUserWithAccount()
        val receiverAccount = receiver.getAccount()

        val transferMoneyRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, BigDecimal(transferSum))
        val transferMoneyResponse = sender.transfer(transferMoneyRequest)
        check(softly) {
            transferMoneyResponse.message shouldBe "Transfer successful"
            transferMoneyRequest shouldMatch transferMoneyResponse
        }
    }

    @ParameterizedTest
    @MethodSource("invalidTransferSum")
    @DisplayName("User cannot make transfer with invalid sum")
    fun transferWithInvalidDepositSum(
        invalidTransferSum: BigDecimal
    ) {
        val sender = createUserWithAccount()
        val senderAccount = sender.getAccount()
        val validDepositSum = BigDecimal("5000.00")
        sender.deposit(DepositMoneyRequest(senderAccount.id, validDepositSum))
        val receiver = createUserWithAccount()
        val receiverAccount = receiver.getAccount()
        val transferMoneyRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, invalidTransferSum)
        TRANSFER_MONEY.request(
            auth = { authAsUser(sender.username, sender.originalPassword) },
            response = { requestReturnsBadRequest() },
            requestBody = transferMoneyRequest,
            method = POST
        )
        val senderBalance = GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(sender.username, sender.originalPassword) },
            method = GET
        ).accounts.first().balance
        check(softly) {
            senderBalance shouldNotBe validDepositSum
        }
    }
}