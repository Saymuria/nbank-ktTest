package models.accounts.transfer

import framework.generators.BigDecimalRange
import models.BaseModel
import java.math.BigDecimal

data class TransferMoneyRequest(
    val senderAccountId: Long,
    val receiverAccountId : Long,
    @BigDecimalRange(1.00, 10000.00)
    val amount: BigDecimal
): BaseModel