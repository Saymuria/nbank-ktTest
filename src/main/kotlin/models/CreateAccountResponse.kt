package models

data class CreateAccountResponse(
    val id: Long,
    val accountNumber: String,
    val balance: Double,
    val transaction: List<String>
) : BaseModel
