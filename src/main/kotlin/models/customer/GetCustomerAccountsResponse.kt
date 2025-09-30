package models.customer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import models.AbstractListResponseDeserializer
import models.Account
import models.BaseModel

@JsonDeserialize(using = CustomerAccountsResponseDeserializer::class)
data class GetCustomerAccountsResponse(
    val accounts: List<Account>
) : BaseModel


class CustomerAccountsResponseDeserializer :
    AbstractListResponseDeserializer<Account, GetCustomerAccountsResponse>(
        Account::class.java,
        ::GetCustomerAccountsResponse
    )