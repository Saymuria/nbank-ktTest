package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`

class EditProfilePage : BasePage<EditProfilePage>() {
    private val editProfileTitle = `$`(Selectors.byText("✏️ Edit Profile"))
    private val enterNameInput = `$`(Selectors.byAttribute("placeholder", "Enter new name"))
    private val saveChangesButton = `$`(Selectors.byText("\uD83D\uDCBE Save Changes"))
    override fun url(): String {
        return "/edit-profile"
    }

    fun setName(name: String): EditProfilePage {
        editProfileTitle.shouldBe(Condition.visible)
        enterNameInput.click()
        enterNameInput.sendKeys(name)
        saveChangesButton.click()
        return this
    }

    fun changeName(name: String, newName: String): EditProfilePage {
        editProfileTitle.shouldBe(Condition.visible)
        enterNameInput.click()
        enterNameInput.value == name
        enterNameInput.clear()
        enterNameInput.sendKeys(newName)
        saveChangesButton.click()
        return this
    }

    fun tryToSubmitEmptyInputName(): EditProfilePage {
        editProfileTitle.shouldBe(Condition.visible)
        saveChangesButton.click()
        return this
    }
}