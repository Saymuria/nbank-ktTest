package ui.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide.`$`
import common.RetryUtils.retry
import hellpers.step
import hellpers.stepWithResult


class EditProfilePage : BasePage<EditProfilePage>() {
    private val editProfileTitle = `$`(Selectors.byText("✏️ Edit Profile"))
    private val enterNameInput = `$`(Selectors.byAttribute("placeholder", "Enter new name"))
    private val saveChangesButton = `$`(Selectors.byText("\uD83D\uDCBE Save Changes"))
    override fun url(): String {
        return "/edit-profile"
    }

    fun setName(name: String): EditProfilePage = stepWithResult("Установка имени счета") {
        editProfileTitle.shouldBe(Condition.visible)
        retry(
            action = {
                enterNameInput.click()
                enterNameInput.clear()
                enterNameInput.sendKeys(name)
            },
            condition = {
                enterNameInput.shouldHave(Condition.value(name))
            },
            maxAttempts = 3,
            delay = 1000
        )
        saveChangesButton.click()
        this
    }

    fun changeName(name: String, newName: String): EditProfilePage = stepWithResult("Изменение имени счета") {
        editProfileTitle.shouldBe(Condition.visible)
        enterNameInput.click()
        enterNameInput.shouldHave(Condition.value(name))
        enterNameInput.clear()
        enterNameInput.sendKeys(newName)
        saveChangesButton.click()
        this
    }

    fun tryToSubmitEmptyInputName(): EditProfilePage = stepWithResult("Сабмит пустой формы редактирования профиля") {
        editProfileTitle.shouldBe(Condition.visible)
        saveChangesButton.click()
        this
    }
}