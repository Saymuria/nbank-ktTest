package models

data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: String
) : BaseModel
