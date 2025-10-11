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
    val originalPassword: String
) {
    val username: String get() = userResponse.username
    val userSteps: UserSteps by lazy { UserSteps(username, originalPassword) }
}

fun TestUser.deposit(depositRequest: DepositMoneyRequest) =
    userSteps.makeDeposit(depositRequest)

fun TestUser.transfer(transferMoneyRequest: TransferMoneyRequest) =
    userSteps.makeTransfer(transferMoneyRequest)

fun TestUser.updateProfileName(updateCustomerProfileRequest: UpdateCustomerProfileRequest) =
    userSteps.updateName(updateCustomerProfileRequest)

fun TestUser.getAllAccounts() = userSteps.getAllAccounts()
fun TestUser.getCustomerProfile() = userSteps.getCustomerProfile()

fun createUser(): TestUser {
    val userRequest = generate<CreateUserRequest>()
    val originalPassword = userRequest.password
    val userResponse = AdminSteps().createUser(userRequest)
    return TestUser(userResponse, originalPassword)
}

fun createAccount(user: TestUser): CreateAccountResponse {
    return user.userSteps.createAccount()
}

fun createUserWithAccount(): Pair<TestUser, CreateAccountResponse> {
    val user = createUser()
    val account = createAccount(user)
    return user to account
}