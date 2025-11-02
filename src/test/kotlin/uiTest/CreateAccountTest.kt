package uiTest

import common.annotations.Browsers
import common.annotations.UserSession
import dsl.invoke
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import storage.SessionStorage
import ui.pages.BankAlerts.NEW_ACCOUNT_CREATED
import ui.pages.UserDashboard

class CreateAccountTest : BaseUiTest() {
    val userDashboard by lazy { UserDashboard() }

    @Test
    @UserSession
    @Browsers(["chrome"])
    fun userCanCreateAccountTest() {
        userDashboard {
            open()
            createNewAccount()
        }
        val existingUserAccount = SessionStorage.getSteps().getAllAccounts()
        assertThat(existingUserAccount.accounts).hasSize(1)
        val account = existingUserAccount.accounts.first()
        userDashboard {
            checkAlertMessageAndAccept(NEW_ACCOUNT_CREATED.message + account.accountNumber)
        }
        assertThat(account.balance).isZero
    }
}
