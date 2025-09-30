package framework.skeleton

import models.BaseModel
import models.accounts.GetAccountTransactionsResponse
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.transfer.TransferMoneyRequest
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.deposit.DepositMoneyResponse
import models.accounts.transfer.TransferMoneyResponse
import models.admin.GetAllUserResponse
import models.admin.createUser.CreateUserRequest
import models.admin.createUser.CreateUserResponse
import models.authentication.LoginUserRequest
import models.authentication.LoginUserResponse
import models.customer.GetCustomerAccountsResponse
import models.customer.GetCustomerProfileResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileResponse
import kotlin.reflect.KClass

enum class Endpoint(
    val url: String,
    val requestModel: KClass<out BaseModel>,
    val responseModel: KClass<out BaseModel>
) {
    CREATE_USER("/admin/users", CreateUserRequest::class, CreateUserResponse::class),
    GET_ALL_USER("/admin/users", BaseModel::class, GetAllUserResponse::class),
    LOGIN("/auth/login", LoginUserRequest::class, LoginUserResponse::class),
    CREATE_ACCOUNT("/accounts", BaseModel::class, CreateAccountResponse::class),
    TRANSFER_MONEY("/accounts/transfer", TransferMoneyRequest::class, TransferMoneyResponse::class),
    DEPOSIT_MONEY("/accounts/deposit", DepositMoneyRequest::class, DepositMoneyResponse::class),
    GET_ALL_TRANSACTIONS("/accounts/%s/transactions", BaseModel::class, GetAccountTransactionsResponse::class),
    GET_CUSTOMER_ACCOUNTS("/customer/accounts", BaseModel::class, GetCustomerAccountsResponse::class),
    GET_CUSTOMER_PROFILE("/customer/profile", BaseModel::class, GetCustomerProfileResponse::class),
    UPDATE_CUSTOMER_PROFILE("/customer/profile", UpdateCustomerProfileRequest::class, UpdateCustomerProfileResponse::class),
}