package models.authentication

import framework.generators.Password
import framework.generators.Username
import models.BaseModel

data class LoginUserRequest(
    @Username
    val username: String,
    @Password
    val password: String,
) : BaseModel{
    companion object{
        fun getAdmin(): LoginUserRequest {
            return LoginUserRequest(configs.Config.getProperty("admin.username"), configs.Config.getProperty("admin.password"))
        }
    }
}