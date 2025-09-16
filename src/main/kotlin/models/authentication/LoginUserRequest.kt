package models.authentication

import framework.generators.Password
import framework.generators.Username
import models.BaseModel

data class LoginUserRequest(
    @Username
    val username: String,
    @Password
    val password: String,
) : BaseModel