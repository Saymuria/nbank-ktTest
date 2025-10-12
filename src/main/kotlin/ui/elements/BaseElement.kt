package ui.elements

import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By


abstract class BaseElement(val element: SelenideElement) {
    protected fun find(selector: By) = element.find(selector)
    protected fun find(cssSelector: String) = element.find(cssSelector)

    protected fun findAll(selector: By) = element.findAll(selector)
    protected fun findAll(cssSelector: String) = element.find(cssSelector)
}