package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.switchTo
import org.assertj.core.api.Assertions.assertThat

abstract class BasePage<T : BasePage<T>> {
    abstract fun url(): String
    protected val usernameInput = `$`(Selectors.byAttribute("placeholder", "Username"))
    protected val passwordInput = `$`(Selectors.byAttribute("placeholder", "Password"))
    private val userNameButton = `$`(Selectors.byClassName("user-name"))
    private val logoutButton = `$`(Selectors.byText("🚪 Logout"))
    private val homeButton = `$`(Selectors.byText("\uD83C\uDFE0 Home"))
    protected val accountSelect = `$`("select")
    protected val amountInput = `$`(Selectors.byAttribute("placeholder", "Enter amount"))



    @Suppress("UNCHECKED_CAST")
    fun open(): T = Selenide.open(url(), this::class.java as Class<T>)

    fun <T : BasePage<T>> getPage(pageClass: Class<T>): T {
        return Selenide.page(pageClass)
    }

    fun checkAlertMessageAndAccept(bankAlert: String): T {
        val alert = switchTo().alert()
        assertThat(alert.text).contains(bankAlert)
        alert.accept()
        return this as T
    }

    fun redirectToEditProfilePage(): T {
        userNameButton.click()
        return this as T
    }

    fun clickToHomeButton(): T {
        homeButton.click()
        return this as T
    }

    fun logout(): T {
        logoutButton.click()
        return this as T
    }


    fun refreshAndCheckUserName(name: String): T {
        Selenide.refresh()
        userNameButton.shouldBe(Condition.visible).shouldHave(Condition.text(name))
        return this as T
    }

}
