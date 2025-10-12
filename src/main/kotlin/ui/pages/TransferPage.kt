package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import ui.elements.TransactionElement

class TransferPage : BasePage<TransferPage>() {
    private val transferTitle = `$`(Selectors.byText("🔄 Make a Transfer"))
    private val recipientInput = `$`(Selectors.byAttribute("placeholder", "Enter recipient name"))
    private val recipientAccountNumberInput =
        `$`(Selectors.byAttribute("placeholder", "Enter recipient account number"))
    private val confirmCheckText = `$`(Selectors.by("for", "confirmCheck"))
    private val confirmCheckbox = `$`(Selectors.byId("confirmCheck"))
    private val transferButton = `$`(Selectors.byText("🚀 Send Transfer"))
    private val transferAgainButton = `$`(Selectors.byText("🔁 Transfer Again"))
    private val matchingTransactionsTitle = `$`(Selectors.byText("Matching Transactions"))
    private val findTransactionInput = `$`(Selectors.byAttribute("placeholder", "Enter name to find transactions"))
    private val searchTransactionButton = `$`(Selectors.byText("🔍 Search Transactions"))
    private val modalTitle = `$`(Selectors.byClassName("modal-title"))

    override fun url(): String = "/transfer"

    fun makeTransfer(
        senderAccount: String,
        receiverName: String? = "",
        receiverAccount: String,
        transferSum: String
    ): TransferPage {
        transferTitle.shouldBe(Condition.visible)
        accountSelect.click()
        accountSelect.selectOptionContainingText(senderAccount)
        recipientInput.sendKeys(receiverName)
        recipientAccountNumberInput.sendKeys(receiverAccount)
        amountInput.sendKeys(transferSum)
        confirmCheckText.shouldBe(Condition.exactText("Confirm details are correct"))
        confirmCheckbox.click()
        transferButton.click()
        return this
    }

    fun tryToSubmitEmptyTransferForm(): TransferPage {
        transferTitle.shouldBe(Condition.visible)
        transferButton.click()
        return this
    }

    fun getAllTransactions(): List<TransactionElement> {
        val elementsCollection = `$`(Selectors.byText("Matching Transactions")).parent().findAll("li")
        return generatePageElements(elementsCollection, ::TransactionElement)
    }


    fun openTransferAgain(): TransferPage {
        transferTitle.shouldBe(Condition.visible)
        transferAgainButton.click()
        matchingTransactionsTitle.shouldBe(Condition.visible)
        return this
    }

    fun searchTransactions(name: String): TransferPage {
        findTransactionInput.sendKeys(name)
        searchTransactionButton.click()
        return this
    }

    fun makeTransferAgain(senderAccount: String): TransferPage {
        modalTitle.shouldHave(Condition.exactText("🔁 Repeat Transfer"))
        accountSelect.click()
        accountSelect.selectOptionContainingText(senderAccount)
        confirmCheckText.shouldBe(Condition.exactText("Confirm details are correct"))
        confirmCheckbox.click()
        transferButton.click()
        return this
    }
}