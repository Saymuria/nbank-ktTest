package uiTest

import dsl.createUser
import dsl.getCustomerProfile
import dsl.updateProfileName
import framework.utils.generate
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ui.pages.BankAlerts
import ui.pages.EditProfilePage
import ui.pages.UserDashboard

class EditProfileTest : BaseUiTest() {

    @Test
    fun userCanSetNameTest() {
        val user = createUser()
        user.authorizeAsUser()
        val name = generate<UpdateCustomerProfileRequest>()
        UserDashboard().open().redirectToEditProfilePage().getPage(EditProfilePage::class.java).setName(name.name)
            .checkAlertMessageAndAccept(
                BankAlerts.NAME_UPDATED_SUCCESSFULLY.message
            ).clickToHomeButton().getPage(UserDashboard::class.java).checkWelcomeText(name.name)
            .refreshAndCheckUserName(name.name)
        val getCustomerProfileResponse = user.getCustomerProfile().name

        assertThat(getCustomerProfileResponse).isEqualTo(name.name)
    }

    @Test
    fun userCannotSetInvalidNameTest() {
        val user = createUser()
        user.authorizeAsUser()
        val name = generate<UpdateCustomerProfileRequest>(mapOf("name" to "12345"))
        val defaultName = "noname"
        UserDashboard().open().redirectToEditProfilePage().getPage(EditProfilePage::class.java).setName(name.name)
            .checkAlertMessageAndAccept(
                BankAlerts.INVALID_NAME.message
            ).clickToHomeButton().getPage(UserDashboard::class.java).checkWelcomeText(defaultName)
            .refreshAndCheckUserName(defaultName)
        user.getCustomerProfile().name.isNullOrEmpty()
    }

    @Test
    fun userCanChangeNameTest() {
        val user = createUser()
        user.authorizeAsUser()
        val updateCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        val name = user.updateProfileName(updateCustomerProfileRequest).customer.name!!
        val newName = generate<UpdateCustomerProfileRequest>().name
        EditProfilePage().open().changeName(name, newName)
            .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATED_SUCCESSFULLY.message).clickToHomeButton()
            .getPage(UserDashboard::class.java).checkWelcomeText(newName).refreshAndCheckUserName(newName)
        val getCustomerProfileResponse = user.getCustomerProfile().name
        assertThat(getCustomerProfileResponse).isEqualTo(newName)
    }

    @Test
    fun userCannotSubmitEmptyEditNameFormTest() {
        val user = createUser()
        user.authorizeAsUser()
        EditProfilePage().open().tryToSubmitEmptyInputName()
            .checkAlertMessageAndAccept(BankAlerts.FILLING_VALID_NAME.message)
    }


    @Test
    fun userCannotSetSameNameTest() {
        val user = createUser()
        val updateCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        val name = user.updateProfileName(updateCustomerProfileRequest).customer.name!!
        user.authorizeAsUser()
        EditProfilePage().open().changeName(name, name)
            .checkAlertMessageAndAccept(BankAlerts.SAME_NAME_IS_NOT_UPDATED.message)

    }
}