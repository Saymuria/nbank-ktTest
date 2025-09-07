package models

data class LoginUserResponse (
    val role: String,
    val username: String
) : BaseModel