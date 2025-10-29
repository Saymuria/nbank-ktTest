package uiTest

import common.annotations.UserSession
import dsl.getCustomerProfile
import dsl.invoke
import dsl.updateProfileName
import framework.utils.generate
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import storage.SessionStorage
import ui.pages.BankAlerts.*
import ui.pages.EditProfilePage
import ui.pages.UserDashboard

class EditProfileTest : BaseUiTest() {
    val userDashboard by lazy { UserDashboard() }
    val editProfilePage by lazy { EditProfilePage() }

    @Test
    @UserSession
    fun userCanSetNameTest() {
        val user = SessionStorage.getUser()
        val name = generate<UpdateCustomerProfileRequest>()
        userDashboard {
            open()
            redirectToEditProfilePage()
            getPage(EditProfilePage::class.java).setName(name.name)
            checkAlertMessageAndAccept(
                NAME_UPDATED_SUCCESSFULLY.message
            )
            clickToHomeButton()
            getPage(UserDashboard::class.java).checkWelcomeText(name.name)
        }
        val getCustomerProfileResponse = user.getCustomerProfile().name

        assertThat(getCustomerProfileResponse).isEqualTo(name.name)
    }

    @Test
    @UserSession
    fun userCannotSetInvalidNameTest() {
        val user = SessionStorage.getUser()
        val invalidName = "12345"
        val defaultName = "noname"
        userDashboard {
            open()
            redirectToEditProfilePage()
            getPage(EditProfilePage::class.java).setName(invalidName)
            checkAlertMessageAndAccept(
                INVALID_NAME.message
            )
            clickToHomeButton()
            getPage(UserDashboard::class.java).checkWelcomeText(defaultName)
            refreshAndCheckUserName(defaultName)
        }
        user.getCustomerProfile().name.isNullOrEmpty()
    }

    @Test
    @UserSession
    fun userCanChangeNameTest() {
        val user = SessionStorage.getUser()
        val updateCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        val name = user.updateProfileName(updateCustomerProfileRequest).customer.name!!
        val newName = generate<UpdateCustomerProfileRequest>().name
        editProfilePage {
            open()
            changeName(name, newName)
            checkAlertMessageAndAccept(NAME_UPDATED_SUCCESSFULLY.message)
            clickToHomeButton()
            getPage(UserDashboard::class.java).checkWelcomeText(newName)
            refreshAndCheckUserName(newName)
        }
        val getCustomerProfileResponse = user.getCustomerProfile().name
        assertThat(getCustomerProfileResponse).isEqualTo(newName)
    }

    @Test
    @UserSession
    fun userCannotSubmitEmptyEditNameFormTest() {
        editProfilePage {
            open()
            tryToSubmitEmptyInputName()
            checkAlertMessageAndAccept(FILLING_VALID_NAME.message)
        }
    }


    @Test
    @UserSession
    fun userCannotSetSameNameTest() {
        val user = SessionStorage.getUser()
        val updateCustomerProfileRequest = generate<UpdateCustomerProfileRequest>()
        val name = user.updateProfileName(updateCustomerProfileRequest).customer.name!!
        editProfilePage {
            open()
            changeName(name, name)
            checkAlertMessageAndAccept(SAME_NAME_IS_NOT_UPDATED.message)
        }
    }
}