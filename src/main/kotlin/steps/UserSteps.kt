package steps

import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.CREATE_ACCOUNT
import framework.skeleton.Endpoint.UPDATE_CUSTOMER_PROFILE
import framework.skeleton.requesters.CrudRequester
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnOk
import framework.specs.ResponseSpec.Companion.requestReturnsBadRequest
import io.restassured.response.ValidatableResponse
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.deposit.DepositMoneyResponse
import models.accounts.transfer.TransferMoneyRequest
import models.accounts.transfer.TransferMoneyResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest

class UserSteps {

    fun createAccount(username: String, password: String): CreateAccountResponse {
        return ValidatedCrudRequester<CreateAccountResponse>(
            authAsUser(username, password),
            entityWasCreated(), CREATE_ACCOUNT
        ).post(null)
    }

    fun makeDeposit(username: String, password: String, request: DepositMoneyRequest): DepositMoneyResponse {
        return ValidatedCrudRequester<DepositMoneyResponse>(
            authAsUser(username, password),
            requestReturnOk(),
            Endpoint.DEPOSIT_MONEY
        ).post(request)
    }

    fun makeTransfer(username: String, password: String, request: TransferMoneyRequest): TransferMoneyResponse {
        return ValidatedCrudRequester<TransferMoneyResponse>(
            authAsUser(username, password),
            ResponseSpec.requestReturnOk(),
            Endpoint.TRANSFER_MONEY
        ).post(request)
    }

    fun updateName(username: String, password: String, request: UpdateCustomerProfileRequest) : ValidatableResponse {
       return CrudRequester(authAsUser(username, password),
            requestReturnOk(),
            UPDATE_CUSTOMER_PROFILE
        ).update(null, request)
    }
}