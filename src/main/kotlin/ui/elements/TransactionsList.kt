package ui.elements

import com.codeborne.selenide.Selectors
import com.codeborne.selenide.SelenideElement

class TransactionElement(element: SelenideElement) : BaseElement(element) {
    fun hasType(type: String): Boolean = element.find(Selectors.byText(type)).exists()
    fun hasSum(sum: String): Boolean = element.find(Selectors.byText(sum)).exists()
    fun clickRepeat() = element.find(Selectors.byText("🔁 Repeat")).click()
}