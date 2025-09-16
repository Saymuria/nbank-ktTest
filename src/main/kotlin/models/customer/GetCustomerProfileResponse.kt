package models.customer

import entities.UserRole
import models.Account
import models.BaseModel

data class GetCustomerProfileResponse(
    val id: Long,
    val username: String,
    val password: String,
    val name: String?,
    val role: UserRole,
    val accounts: List<Account>

) : BaseModel