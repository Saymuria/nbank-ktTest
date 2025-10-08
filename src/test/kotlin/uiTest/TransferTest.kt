package uiTest

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.executeJavaScript
import com.codeborne.selenide.Selenide.switchTo
import dsl.createAccount
import dsl.createUserWithAccount
import dsl.deposit
import dsl.updateProfileName
import dsl.validatedRequest
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
import models.authentication.LoginUserRequest
import models.customer.GetCustomerAccountsResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.random.Random

class TransferTest {
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

        val valueGenerator = ValueGenerator()
    }

    @Test
    fun userCanMakeTransferWithValidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name.customer.name)
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient account number"
            )
        ).sendKeys(receiverAccount.accountNumber)
        val transferSum = "200.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferSum)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Successfully transferred \$$transferSum to account ${receiverAccount.accountNumber}!")
        val receiverBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(receiverUser.username, receiverUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(senderUser.username, senderUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    fun userCanMakeTransferWithValidSumToAccountWithoutNameTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient account number"
            )
        ).sendKeys(receiverAccount.accountNumber)
        val transferSum = "200.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferSum)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Successfully transferred \$$transferSum to account ${receiverAccount.accountNumber}!")
        val receiverBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(receiverUser.username, receiverUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferSum)
        val senderBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(senderUser.username, senderUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferSum.toBigDecimal())
    }

    @Test
    fun userCannotMakeTransferWithInvalidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("10000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name.customer.name)
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient account number"
            )
        ).sendKeys(receiverAccount.accountNumber)
        val transferSum = "6000.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferSum)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Transfer is not available fo sum \$$transferSum")
        val receiverBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(receiverUser.username, receiverUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(senderUser.username, senderUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    fun userCannotMakeTransferForSumMoreThanDepositTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("5000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name.customer.name)
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient account number"
            )
        ).sendKeys(receiverAccount.accountNumber)
        val transferSum = "6000.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferSum)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Error: Invalid transfer: insufficient funds or invalid accounts")
        val receiverBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(receiverUser.username, receiverUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isZero()
        val senderBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(senderUser.username, senderUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum)
    }

    @Test
    fun userCannotMakeTransferToInCorrectUserNameTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("10000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient name"
            )
        ).sendKeys(valueGenerator.generateAlphabeticString(5, 10))
        `$`(
            Selectors.byAttribute(
                "placeholder",
                "Enter recipient account number"
            )
        ).sendKeys(receiverAccount.accountNumber)
        val transferSum = "6000.0"
        `$`(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferSum)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("The recipient name does not match the registered name")
    }

    @Test
    fun userCannotSubmitEmptyTransferFormTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("10000.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("🔄 Make a Transfer")).click()
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("Please fill all fields and confirm.")
    }
}