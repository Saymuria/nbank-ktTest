package models.accounts.transfer

import models.BaseModel
import java.math.BigDecimal

data class TransferMoneyResponse(
    val message: String,
    val senderAccountId: Long,
    val receiverAccountId: Long,
    val amount: BigDecimal,
) : BaseModel
