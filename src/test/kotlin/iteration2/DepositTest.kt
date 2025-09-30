package iteration2

import BaseTest
import framework.extentions.shouldMatchResponse
import framework.skeleton.Endpoint.DEPOSIT_MONEY
import framework.skeleton.Endpoint.GET_ALL_TRANSACTIONS
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import framework.utils.generate
import models.accounts.GetAccountTransactionsResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.deposit.DepositMoneyResponse
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


//Предположительные ограничения: депозит может быть в интервале от 1.00 до 10000.00
@DisplayName("User can deposit money")
class DepositTest : BaseTest() {
    private val adminSteps = AdminSteps()
    private val userSteps = UserSteps()

    companion object {
        @JvmStatic
        private fun invalidDepositSum(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(BigDecimal("10000.01")), //больше максимума
                Arguments.of(BigDecimal("0.99")), //меньше минимума
                Arguments.of(BigDecimal("-1.00")), //отрицаильное значение
                Arguments.of(BigDecimal("0.001")), //
                Arguments.of(BigDecimal("0.00")) //нулевые значения
            )
        }
    }

    @Test
    @DisplayName("User can make deposit to bank account: 1.00 < deposit < 10000.00")
    fun makeDepositInValidRange() {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val accountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoney = generate<DepositMoneyRequest>(mapOf("id" to accountId))
        val depositResponse = ValidatedCrudRequester<DepositMoneyResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            DEPOSIT_MONEY
        ).post(depositMoney)
        val userTransaction = ValidatedCrudRequester<GetAccountTransactionsResponse>(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(), GET_ALL_TRANSACTIONS
        ).get(accountId)
        depositResponse.shouldMatchResponse(userTransaction)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.00", "10000.00"])
    @DisplayName("User can make deposit to bank account: 1.00 < deposit < 10000.00")
    fun makeDepositEdgeCheck(depositSum: String) {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val accountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoneyRequest = DepositMoneyRequest(accountId, BigDecimal(depositSum))
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(),
            DEPOSIT_MONEY
        ).post(depositMoneyRequest)
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnOk(), GET_ALL_TRANSACTIONS
        ).get(accountId)
    }


    @ParameterizedTest
    @MethodSource("invalidDepositSum")
    @DisplayName("User cannot make deposit to bank account")
    fun depositWithInvalidDepositSum(
        depositSum: BigDecimal
    ) {
        val createUserRequest = generate<CreateUserRequest>()
        adminSteps.createUser(createUserRequest)
        val accountId = userSteps.createAccount(createUserRequest.username, createUserRequest.password).id
        val depositMoneyRequest = DepositMoneyRequest(accountId, depositSum)
        CrudRequester(
            authAsUser(createUserRequest.username, createUserRequest.password),
            requestReturnsBadRequest(),
            DEPOSIT_MONEY
        ).post(depositMoneyRequest)
    }
}