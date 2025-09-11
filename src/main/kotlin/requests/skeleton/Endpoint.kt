package requests.skeleton

import models.BaseModel
import models.CreateAccountResponse
import models.CreateUserRequest
import models.CreateUserResponse
import models.LoginUserRequest
import models.LoginUserResponse
import kotlin.reflect.KClass

enum class Endpoint(val url: String, val requestModel: KClass<out BaseModel>, val responseModel: KClass<out BaseModel>) {
    ADMIN_USER("/admin/users", CreateUserRequest::class, CreateUserResponse::class),
    LOGIN("/auth/login", LoginUserRequest ::class, LoginUserResponse::class),
    ACCOUNTS("/accounts", BaseModel ::class, CreateAccountResponse ::class),

}