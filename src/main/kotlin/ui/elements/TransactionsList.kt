package ui.elements

import com.codeborne.selenide.SelenideElement

class TransactionElement(element: SelenideElement) : BaseElement(element) {
    private val transactionType: String =
        element.find("span").text().split(" - ")[0].trim()


    private val transactionSum: String =
        element.find("span").text().split(" - ")[1].split("\n")[0].trim()


    private val repeatButton: SelenideElement =
        element.find("button.custom-btn")


    fun getTransactionType() = transactionType
    fun getTransactionSum() = transactionSum
    fun clickRepeat() = repeatButton.click()
}