package models.accounts.deposit

import models.BaseModel
import models.Transaction
import java.math.BigDecimal

data class DepositMoneyResponse(
    val id: Long,
    val accountNumber: String,
    val balance: BigDecimal,
    val transactions: List<Transaction>
) : BaseModel
