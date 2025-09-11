package models

data class CreateUserResponse(
    val id: Long?,
    val username: String?,
    val password: String?,
    val role: String?,
    val name: String?,
    val accounts: List<String>?
) : BaseModel