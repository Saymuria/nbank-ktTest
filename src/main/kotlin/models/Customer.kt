package models

import entities.UserRole

data class Customer (
    val id: Long,
    val username: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val accounts: List<Account>
)