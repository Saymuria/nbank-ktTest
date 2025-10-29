package ui.pages

import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import common.RetryUtils.retry
import ui.elements.UserBage

class AdminPanel : BasePage<AdminPanel>() {
    private val adminPanelText = `$`(Selectors.byText("Admin Panel"))
    fun getAdminPanelText() = adminPanelText

    private val addUserButton = `$`(Selectors.byText("Add User"))

    override fun url(): String {
        return "/admin"
    }

    fun createUser(username: String, password: String): AdminPanel {
        usernameInput.sendKeys(username)
        passwordInput.sendKeys(password)
        addUserButton.click()
        return this
    }

    fun getAllUsers(): List<UserBage> {
        val elementsCollection = `$`(Selectors.byText("All Users")).parent().findAll("li")
        println(elementsCollection)
        return generatePageElements(elementsCollection, ::UserBage)
    }

    fun userBageByUsername(username: String): UserBage {
        return retry(
            action = {
                getAllUsers().find { user -> user.getUsername() == username }
                    ?: throw NoSuchElementException("User '$username' not found")
            },
            condition = { result -> true }, // Если дошли сюда - пользователь найден
            maxAttempts = 3,
            delay = 1000
        )
    }
}
