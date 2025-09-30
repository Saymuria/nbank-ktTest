package models

import entities.OperationType
import java.math.BigDecimal

data class Transaction(
    val id: Long,
    val amount: BigDecimal,
    val type: OperationType,
    val timestamp: String,
    val relatedAccountId : Long,
)
