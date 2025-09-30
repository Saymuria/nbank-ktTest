package steps

import framework.skeleton.Endpoint
import framework.skeleton.Endpoint.CREATE_ACCOUNT
import framework.skeleton.Endpoint.UPDATE_CUSTOMER_PROFILE
import framework.skeleton.requesters.ValidatedCrudRequester
import framework.specs.RequestSpecs.Companion.authAsUser
import framework.specs.ResponseSpec.Companion.entityWasCreated
import framework.specs.ResponseSpec.Companion.requestReturnOk
import models.accounts.createAccount.CreateAccountResponse
import models.accounts.deposit.DepositMoneyRequest
import models.accounts.deposit.DepositMoneyResponse
import models.accounts.transfer.TransferMoneyRequest
import models.accounts.transfer.TransferMoneyResponse
import models.customer.updateCustomerProfile.UpdateCustomerProfileRequest
import models.customer.updateCustomerProfile.UpdateCustomerProfileResponse

class UserSteps(val username: String,val  password: String) {

    fun createAccount(): CreateAccountResponse {
        return ValidatedCrudRequester<CreateAccountResponse>(
            authAsUser(username, password),
            entityWasCreated(), CREATE_ACCOUNT
        ).post(null)
    }

    fun makeDeposit(request: DepositMoneyRequest): DepositMoneyResponse {
        return ValidatedCrudRequester<DepositMoneyResponse>(
            authAsUser(username, password),
            requestReturnOk(),
            Endpoint.DEPOSIT_MONEY
        ).post(request)
    }

    fun makeTransfer(request: TransferMoneyRequest): TransferMoneyResponse {
        return ValidatedCrudRequester<TransferMoneyResponse>(
            authAsUser(username, password),
            requestReturnOk(),
            Endpoint.TRANSFER_MONEY
        ).post(request)
    }

    fun updateName(request: UpdateCustomerProfileRequest) : UpdateCustomerProfileResponse {
       return ValidatedCrudRequester<UpdateCustomerProfileResponse>(
           authAsUser(username, password),
           requestReturnOk(),
           UPDATE_CUSTOMER_PROFILE
       ).update(null, request)
    }
}