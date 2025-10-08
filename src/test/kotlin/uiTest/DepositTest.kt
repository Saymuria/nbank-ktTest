package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import dsl.createUserWithAccount
import dsl.deposit
import dsl.validatedRequest
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.utils.generate
import io.restassured.http.Method.GET
import models.accounts.deposit.DepositMoneyRequest
import models.authentication.LoginUserRequest
import models.customer.GetCustomerAccountsResponse
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DepositTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpSelenoid() {
            Configuration.remote = "http://localhost:4444/wd/hub"
            Configuration.baseUrl = "http://192.168.0.5:3000"
            Configuration.browser = "chrome"
            Configuration.browserSize = "1920x1080"
            Configuration.browserCapabilities.setCapability(
                "selenoid:options", mapOf("enableVNC" to true, "enableLog" to true)
            )
        }
    }

    @Test
    fun userCanMakeDepositWithValidSumTest() {
        val (user, account) = createUserWithAccount()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("💰 Deposit Money")).click()
        `$`(Selectors.byText("💰 Deposit Money")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(account.accountNumber)
        val depositSum = "500.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositSum)
        `$`(Selectors.byText("💵 Deposit")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("✅ Successfully deposited \$$depositSum to account ${account.accountNumber}!")
        val balance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isEqualTo(depositSum.toBigDecimal())
    }
    @Test
    fun userCanMakeDepositWithInValidSumTest() {
        val (user, account) = createUserWithAccount()
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("💰 Deposit Money")).click()
        `$`(Selectors.byText("💰 Deposit Money")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(account.accountNumber)
        val depositSum = "10000.01"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositSum)
        `$`(Selectors.byText("💵 Deposit")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("User cannot make deposit for sum more than 10000.00")
        val balance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == account.accountNumber }.balance
        assertThat(balance).isZero
    }

    @Test
    fun userCanSeeHisBalanceTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/deposit")
        `$`(Selectors.byText("💰 Deposit Money")).click()
        `$`(Selectors.byText("💰 Deposit Money")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(account.accountNumber)
        `$`("select").selectOption("${account.accountNumber} (Balance: \$$depositSum)")
    }

    @Test
    fun userCannotSubmitFormWithoutAccountSelectionTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/deposit")
        `$`(Selectors.byText("💰 Deposit Money")).click()
        `$`(Selectors.byText("💰 Deposit Money")).shouldBe(Condition.visible)
        `$`(Selectors.byText("💵 Deposit")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Please select an account.")
    }

    @Test
    fun userCannotSubmitFormWithoutDepositSumTest() {
        val (user, account) = createUserWithAccount()
        val depositSum = BigDecimal("500.00")
        user.deposit(generate<DepositMoneyRequest>(mapOf("id" to account.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/deposit")
        `$`(Selectors.byText("💰 Deposit Money")).click()
        `$`(Selectors.byText("💰 Deposit Money")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(account.accountNumber)
        `$`(Selectors.byText("💵 Deposit")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Please enter a valid amount.")
    }
}