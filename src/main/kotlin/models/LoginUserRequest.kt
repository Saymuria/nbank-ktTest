package models

data class LoginUserRequest(
    val username: String,
    val password: String
) : BaseModel

