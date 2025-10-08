package uiTest

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import dsl.createUser
import dsl.validatedRequest
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.skeleton.Endpoint.LOGIN
import framework.skeleton.requesters.CrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.RequestSpecs.Companion.unAuthSpec
import framework.specs.ResponseSpec.Companion.requestReturnOk
import io.restassured.http.Method.GET
import models.authentication.LoginUserRequest
import models.customer.GetCustomerAccountsResponse
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class CreateAccountTest {
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
    fun userCanCreatAccountTest(){
        //Шаг 1 админ логинится в банке
        //Шаг 2 админ создает юзера
        val user = createUser()

        //Шаг 3 юзер логинится в банке
        val userLoginRequest = LoginUserRequest(user.username, user.originalPassword)
        val authHeader = CrudRequester(unAuthSpec(), requestReturnOk(), LOGIN).post(userLoginRequest).extract()
            .header(AUTHORIZATION)
        //Шаг 4 юзер создает аккаунт
        Selenide.open("/")
        executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        Selenide.open("/dashboard")
        `$`(Selectors.byText("➕ Create New Account")).click()
        // Шаг 5 проверка, что аккаунт создался на UI
        val alert = switchTo().alert()
        assertThat(alert.text).contains("✅ New Account Created! Account Number:")
        val pattern = Pattern.compile("Account Number: (\\w+)")
        val matcher = pattern.matcher(alert.text)
        matcher.find()
        val accNumber = matcher.group(1)
        alert.accept()

        //Шаг 6 аккаунт был создан на API
        val existingUserAccount = GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(user.username, user.originalPassword) },
            method = GET
        )
        val account = existingUserAccount.accounts.first { account -> account.accountNumber == accNumber }
        assertThat(account.balance).isZero
        assertThat(existingUserAccount.accounts).hasSize(1)

    }
}