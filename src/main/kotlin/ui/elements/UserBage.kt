package ui.elements

import com.codeborne.selenide.SelenideElement

class UserBage(element: SelenideElement) : BaseElement(element) {
    private val username: String = element.text.split("\n")[0]
    private val role: String = element.text.split("\n")[1]
    fun getUsername() = username
    fun getRole() = role
}