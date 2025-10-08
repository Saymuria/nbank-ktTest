package uiTest

import apiTest.iteration2.TransactionHistoryTest
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.executeJavaScript
import com.codeborne.selenide.Selenide.switchTo
import dsl.createUserWithAccount
import dsl.deposit
import dsl.transfer
import dsl.updateProfileName
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
import models.accounts.transfer.TransferMoneyRequest
import models.authentication.LoginUserRequest
import models.customer.GetCustomerAccountsResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransferAgainTest {
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
    fun userCanMakeTransferWithValidSumTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/transfer")
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🔁 Transfer Again")).click()
        `$`(Selectors.byText("Matching Transactions")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions")).sendKeys(name.customer.name)
        `$`(Selectors.byText("🔍 Search Transactions")).click()
        val allTransactions = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        allTransactions.find { it.`$`(Selectors.byText("TRANSFER_IN")).exists() }?.`$`(Selectors.byText("🔁 Repeat"))
            ?.click()
        `$`(Selectors.byClassName("modal-title")).shouldHave(Condition.exactText("🔁 Repeat Transfer"))
        `$`("select").click()
        `$`("select").selectOptionContainingText(senderAccount.accountNumber)
        `$`(Selectors.by("for", "confirmCheck")).shouldBe(Condition.exactText("Confirm details are correct"))
        `$`(Selectors.byId("confirmCheck")).click()
        `$`(Selectors.byText("🚀 Send Transfer")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).contains("✅ Transfer of \$200 successful from Account ${senderAccount.id} to ${receiverAccount.id}!")
        val receiverBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(receiverUser.username, receiverUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == receiverAccount.accountNumber }.balance
        assertThat(receiverBalance).isEqualTo(transferOutAmount + transferOutAmount)
        val senderBalance = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(senderUser.username, senderUser.originalPassword) },
            method = GET
        ).accounts.first { it.accountNumber == senderAccount.accountNumber }.balance
        assertThat(senderBalance).isEqualTo(depositSum - transferOutAmount - transferOutAmount)
    }

    @Test
    fun userCanMakeSeeTransferOutTransactionsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val name = receiverUser.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        val transferMoneyInRequest = TransferMoneyRequest(receiverAccount.id, senderAccount.id, transferOutAmount)
        val transferMoneyInResponse = receiverUser.transfer(transferMoneyInRequest)

        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/transfer")
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🔁 Transfer Again")).click()
        `$`(Selectors.byText("Matching Transactions")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions")).sendKeys(name.customer.name)
        `$`(Selectors.byText("🔍 Search Transactions")).click()
        val allTransactions = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        assertThat(allTransactions).hasSize(1)
        //- Проверяем, что данные по транзакции совпадают с теми, которые были в исходящей транзакции из предшагов(сумма и тип перевода)
    }

    @Test
    fun userCanFindTransferOutTransactionsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser1, receiverAccount1) = createUserWithAccount()
        val (receiverUser2, receiverAccount2) = createUserWithAccount()

        val name = receiverUser1.updateProfileName(generate<UpdateCustomerProfileRequest>())
        val depositSum = BigDecimal("500.0")
        senderUser.deposit(generate<DepositMoneyRequest>(mapOf("id" to senderAccount.id, "balance" to depositSum)))
        val transferOutAmount = BigDecimal("200.0")
        val transferMoneyOutRequest = TransferMoneyRequest(senderAccount.id, receiverAccount1.id, transferOutAmount)
        val transferMoneyOutResponse = senderUser.transfer(transferMoneyOutRequest)
        val transferMoneyOutRequest2 = TransferMoneyRequest(senderAccount.id, receiverAccount2.id, transferOutAmount)
        val transferMoneyOutResponse2 = senderUser.transfer(transferMoneyOutRequest)

        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/transfer")
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🔁 Transfer Again")).click()
        `$`(Selectors.byText("Matching Transactions")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions")).sendKeys(name.customer.name)
        `$`(Selectors.byText("🔍 Search Transactions")).click()
        val allTransactions = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        assertThat(allTransactions).hasSize(1)
        //- Проверяем, что данные по транзакции совпадают с теми, которые были в исходящей транзакции из предшагов(сумма и тип перевода)
        `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions")).sendKeys(receiverUser2.username)
        `$`(Selectors.byText("🔍 Search Transactions")).click()
        val allTransactions2 = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
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
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/transfer")
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🔁 Transfer Again")).click()
        `$`(Selectors.byText("Matching Transactions")).shouldBe(Condition.visible)
        `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions")).sendKeys(name.customer.name)
        `$`(Selectors.byText("🔍 Search Transactions")).click()
        val alert = switchTo().alert()
        assertThat(alert.text).isEqualTo("No matching users found.")
        alert.accept()
        val allTransactions2 = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        assertThat(allTransactions2).hasSize(0)
    }

    @Test
    fun userCannotFindTransferOutWithoutAnyOperationsTest() {
        val (senderUser, senderAccount) = createUserWithAccount()
        val (receiverUser, receiverAccount) = createUserWithAccount()
        val userLoginRequest = LoginUserRequest(senderUser.username, senderUser.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/transfer")
        `$`(Selectors.byText("🔄 Make a Transfer")).shouldBe(Condition.visible)
        `$`(Selectors.byText("🔁 Transfer Again")).click()
        `$`(Selectors.byText("Matching Transactions")).shouldBe(Condition.visible)
        val allTransactions2 = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        assertThat(allTransactions2).hasSize(0)
    }

}