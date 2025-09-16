package models.authentication

import models.BaseModel

data class LoginUserResponse (
    val role: String,
    val username: String
) : BaseModel