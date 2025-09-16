package models.admin.createUser

import entities.UserRole
import models.Account
import models.BaseModel

data class CreateUserResponse(
    val id: Long,
    val username: String,
    val password: String,
    val role: UserRole,
    val name: String?,
    val accounts: List<Account>
) : BaseModel