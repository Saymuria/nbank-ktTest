package models.admin.createUser

import entities.UserRole
import framework.generators.FixedValues
import framework.generators.GeneratingRule
import framework.generators.Password
import framework.generators.Username
import groovy.transform.Field
import models.BaseModel

data class CreateUserRequest(
    @Username
    val username: String,
    @Password
    val password: String,
    @FixedValues("USER")
    val role: UserRole,
) : BaseModel