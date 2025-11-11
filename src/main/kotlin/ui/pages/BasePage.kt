package ui.pages

import com.codeborne.selenide.*
import com.codeborne.selenide.Selenide.*
import dsl.TestUser
import framework.specs.RequestSpecs.Companion.getUserAuthHeader
import hellpers.step
import hellpers.stepWithResult
import models.authentication.LoginUserRequest
import org.assertj.core.api.Assertions.assertThat
import ui.elements.BaseElement

abstract class BasePage<T : BasePage<T>> {
    abstract fun url(): String
    protected val usernameInput = `$`(Selectors.byAttribute("placeholder", "Username"))
    protected val passwordInput = `$`(Selectors.byAttribute("placeholder", "Password"))
    private val userNameButton = `$`(Selectors.byClassName("user-name"))
    private val logoutButton = `$`(Selectors.byText("🚪 Logout"))
    private val homeButton = `$`(Selectors.byText("\uD83C\uDFE0 Home"))
    protected val accountSelect = `$`("select")
    protected val amountInput = `$`(Selectors.byAttribute("placeholder", "Enter amount"))

    companion object {
        fun authorizeAsUser(username: String, password: String) = stepWithResult("Авторизация пользователя") {
            Selenide.open("/")
            val authHeader = getUserAuthHeader(username, password)
            executeJavaScript<Any>("localStorage.setItem('authToken', arguments[0])", authHeader)
        }

        fun authorizeAsUser(loginUserRequest: LoginUserRequest) {
            authorizeAsUser(loginUserRequest.username, loginUserRequest.password)
        }

        fun TestUser.authorizeAsUser() {
            authorizeAsUser(username, originalPassword)
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun open(): T = stepWithResult("Открываем страницу ${url()}") { Selenide.open(url(), this::class.java as Class<T>) }

    fun <T : BasePage<T>> getPage(pageClass: Class<T>): T {
        return Selenide.page(pageClass)
    }

    fun checkAlertMessageAndAccept(bankAlert: String): T = step("Проверка алерта") {
        val alert = switchTo().alert()
        assertThat(alert.text).contains(bankAlert)
        alert.accept()
        this as T
    }

    fun redirectToEditProfilePage(): T = stepWithResult("Переход на страницу редактирования профиля") {
        userNameButton.click()
        this as T
    }

    fun clickToHomeButton(): T = stepWithResult("Нажимаем на кнопку 'Домой'") {
        homeButton.click()
        this as T
    }

    fun logout(): T = stepWithResult("Разлогин") {
        logoutButton.click()
        this as T
    }

    fun refreshAndCheckUserName(name: String): T = stepWithResult("Проверяем username") {
        refresh()
        userNameButton.shouldBe(Condition.visible).shouldHave(Condition.text(name))
        this as T
    }

    //ElementCollection -> List<BaseElement>
    protected inline fun <reified T : BaseElement> generatePageElements(
        elementsCollection: ElementsCollection,
        constructor: (SelenideElement) -> T
    ) = elementsCollection.map(constructor)
}