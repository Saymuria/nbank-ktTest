package ui.pages

import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import hellpers.step
import hellpers.stepWithResult

class LoginPage : BasePage<LoginPage>() {
    private val button = `$`("button")
    private val loginTitle = `$`(Selectors.byText("Login"))
    fun getLoginTitle() = loginTitle
    override fun url(): String {
        return "/login"
    }

    fun login(username: String, password: String): LoginPage = stepWithResult("Авторизация пользователя") {
        usernameInput.sendKeys(username)
        passwordInput.sendKeys(password)
        button.click()
        this
    }
}