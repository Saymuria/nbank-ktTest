package models

import entities.UserRole

data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: UserRole
) : BaseModel
