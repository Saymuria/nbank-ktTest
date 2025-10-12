package ui.pages

import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
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
        return generatePageElements(elementsCollection, ::UserBage)
    }
}
