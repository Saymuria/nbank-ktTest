package steps

import dsl.validatedRequest
import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.CREATE_ACCOUNT
import framework.skeleton.Endpoint.GET_CUSTOMER_ACCOUNTS
import framework.skeleton.Endpoint.GET_CUSTOMER_PROFILE
import framework.skeleton.Endpoint.UPDATE_CUSTOMER_PROFILE
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnOk
import hellpers.step
import io.restassured.http.Method.GET
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.deposit.DepositMoneyResponse
import models.accounts.transfer.TransferMoneyRequest
import models.accounts.transfer.TransferMoneyResponse
import models.customer.GetCustomerAccountsResponse
import models.customer.GetCustomerProfileResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileResponse

class UserSteps(val username: String, val password: String) {

    fun createAccount(): CreateAccountResponse = step("User: Создание аккаунта") {
        ValidatedCrudRequester<CreateAccountResponse>(
            authAsUser(username, password),
            entityWasCreated(), CREATE_ACCOUNT
        ).post(null)
    }

    fun makeDeposit(request: DepositMoneyRequest): DepositMoneyResponse = step("User: Пополнение счета ${request.id}") {
        ValidatedCrudRequester<DepositMoneyResponse>(
            authAsUser(username, password),
            requestReturnOk(),
            Endpoint.DEPOSIT_MONEY
        ).post(request)
    }

    fun makeTransfer(request: TransferMoneyRequest): TransferMoneyResponse =
        step("User: Перевод денег со счета ${request.senderAccountId} на счет ${request.receiverAccountId}") {
            ValidatedCrudRequester<TransferMoneyResponse>(
                authAsUser(username, password),
                requestReturnOk(),
                Endpoint.TRANSFER_MONEY
            ).post(request)
        }

    fun updateName(request: UpdateCustomerProfileRequest): UpdateCustomerProfileResponse = step("User: Изменение имени аккаунта ${request.name}")  {
        ValidatedCrudRequester<UpdateCustomerProfileResponse>(
            authAsUser(username, password),
            requestReturnOk(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, request)
    }

    fun getAllAccounts(): GetCustomerAccountsResponse {
        return GET_CUSTOMER_ACCOUNTS.validatedRequest<GetCustomerAccountsResponse>(
            auth = { authAsUser(username, password) },
            method = GET
        )
    }

    fun getCustomerProfile(): GetCustomerProfileResponse {
        return GET_CUSTOMER_PROFILE.validatedRequest<GetCustomerProfileResponse>(
            auth = { authAsUser(username, password) },
            method = GET
        )
    }
}