package dsl

import framework.utils.generate
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.transfer.TransferMoneyRequest
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import steps.AdminSteps
import steps.UserSteps

data class TestUser(
    val userResponse: CreateUserResponse,
    val originalPassword: String,
    private val account: CreateAccountResponse? = null
) {
    val username: String get() = userResponse.username
    val userSteps: UserSteps by lazy { UserSteps(username, originalPassword) }
    val hasAccount: Boolean get() = account != null

    fun getAccount(): CreateAccountResponse {
        return account ?: throw IllegalStateException("User $username doesn't have an account")
    }
}

fun TestUser.deposit(depositRequest: DepositMoneyRequest) =
    userSteps.makeDeposit(depositRequest)

fun TestUser.transfer(transferMoneyRequest: TransferMoneyRequest) =
    userSteps.makeTransfer(transferMoneyRequest)

fun TestUser.updateProfileName(updateCustomerProfileRequest: UpdateCustomerProfileRequest) =
    userSteps.updateName(updateCustomerProfileRequest)

fun TestUser.getAllAccounts() = userSteps.getAllAccounts()
fun TestUser.getCustomerProfile() = userSteps.getCustomerProfile()
fun TestUser.createAccount() = userSteps.createAccount()

fun createUser(): TestUser {
    val userRequest = generate<CreateUserRequest>()
    val originalPassword = userRequest.password
    val userResponse = AdminSteps().createUser(userRequest)
    return TestUser(userResponse, originalPassword)
}


fun createUserWithAccount(): TestUser {
    val user = createUser()
    val account = user.userSteps.createAccount()
    return TestUser(user.userResponse, user.originalPassword, account)
}