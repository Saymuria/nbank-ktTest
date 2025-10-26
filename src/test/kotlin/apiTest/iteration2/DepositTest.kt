package apiTest.iteration2

import apiTest.BaseTest
import dsl.createUserWithAccount
import dsl.deposit
import dsl.request
import dsl.validatedRequest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.DEPOSIT_MONEY
import framework.skeleton.Endpoint.GET_ALL_TRANSACTIONS
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import io.restassured.http.Method.GET
import io.restassured.http.Method.POST
import models.accounts.GetAccountTransactionsResponse
import models.accounts.deposit.DepositMoneyRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.stream.Stream


//Предположительные ограничения: депозит может быть в интервале от 1.00 до 10000.00
@DisplayName("User can deposit money")
class DepositTest : BaseTest() {

    companion object {
        @JvmStatic
        private fun invalidDepositSum(): Stream<Arguments> {
            return Stream.of(
                of(BigDecimal("5000.01")), //больше максимума
                of(BigDecimal("0.99")), //меньше минимума
                of(BigDecimal("-1.00")), //отрицаильное значение
                of(BigDecimal("0.001")), //
                of(BigDecimal("0.00")) //нулевые значения
            )
        }
    }

    @Test
    @DisplayName("User can make deposit to bank account: 1.00 < deposit < 5000.00")
    fun makeDepositInValidRange() {
        val user = createUserWithAccount()
        val account = user.getAccount()
        val depositResponse = user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id)))
        val userTransaction = GET_ALL_TRANSACTIONS.validatedRequest<GetAccountTransactionsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET,
            id = account.id
        )
        depositResponse shouldMatchResponse userTransaction
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.00", "5000.00"])
    @DisplayName("User can make deposit to bank account: 1.00 < deposit < 5000.00")
    fun makeDepositEdgeCheck(depositSum: String) {
        val user = createUserWithAccount()
        val account = user.getAccount()
        val depositResponse = user.deposit(DepositMoneyRequest(account.id, BigDecimal(depositSum)))
        val userTransaction = GET_ALL_TRANSACTIONS.validatedRequest<GetAccountTransactionsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET,
            id = account.id
        )
        depositResponse shouldMatchResponse userTransaction
    }


    @ParameterizedTest
    @MethodSource("invalidDepositSum")
    @DisplayName("User cannot make deposit to bank account")
    fun depositWithInvalidDepositSum(
        invalidDepositSum: BigDecimal
    ) {
        val user = createUserWithAccount()
        val account = user.getAccount()
        DEPOSIT_MONEY.request(
            auth = { authAsUser(user.username, user.originalPassword) },
            response = { requestReturnsBadRequest() },
            requestBody = DepositMoneyRequest(account.id, invalidDepositSum),
            method = POST

        )
        val userTransaction = GET_ALL_TRANSACTIONS.validatedRequest<GetAccountTransactionsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET,
            id = account.id
        )
    }
}