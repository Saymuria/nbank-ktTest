package models.accounts

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import models.AbstractListResponseDeserializer
import models.BaseModel
import models.Transaction

@JsonDeserialize(using = AccountTransactionsResponseDeserializer::class)
data class GetAccountTransactionsResponse (
    val transactions: List<Transaction>
) : BaseModel

class AccountTransactionsResponseDeserializer :
    AbstractListResponseDeserializer<Transaction, GetAccountTransactionsResponse>(
        Transaction::class.java,
        ::GetAccountTransactionsResponse
    )