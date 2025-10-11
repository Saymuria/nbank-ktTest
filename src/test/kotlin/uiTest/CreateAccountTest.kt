package uiTest

import dsl.createUser
import dsl.getAllAccounts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ui.pages.BankAlerts
import ui.pages.UserDashboard

class CreateAccountTest : BaseUiTest() {


    @Test
    fun userCanCreateAccountTest() {

        val user = createUser()
        user.authorizeAsUser()
        UserDashboard().open().createNewAccount()
        val existingUserAccount = user.getAllAccounts()
        assertThat(existingUserAccount.accounts).hasSize(1)
        val account = existingUserAccount.accounts.first()
        UserDashboard().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.message + account.accountNumber)
        assertThat(account.balance).isZero
    }
}