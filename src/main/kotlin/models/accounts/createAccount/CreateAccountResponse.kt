package models.accounts.createAccount

import models.BaseModel
import models.Transaction
import java.math.BigDecimal

data class CreateAccountResponse(
    val id: Long,
    val accountNumber: String,
    val balance: BigDecimal,
    val transactions: List<Transaction>
) : BaseModel