package models

import java.math.BigDecimal

data class Account(
    val id: Long,
    val accountNumber: String,
    val balance: BigDecimal,
    val transactions: List<Transaction>
)
