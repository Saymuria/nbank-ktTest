package models

import generators.GeneratingRule

data class LoginUserRequest(
    @GeneratingRule("^[A-Za-z0-9]{3,15}$")
    val username: String,
    val password: String
) : BaseModel

