package models.accounts.deposit

import framework.generators.BigDecimalRange
import models.BaseModel
import java.math.BigDecimal

data class DepositMoneyRequest(
    val id: Long,
    @BigDecimalRange(1.00, 5000.00)
    val balance: BigDecimal
): BaseModel
