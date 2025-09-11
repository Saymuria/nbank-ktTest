package utils

import generators.EntityGenerator
import models.BaseModel
import kotlin.reflect.KClass

fun <T : BaseModel> KClass<T>.generate(): T {
    return EntityGenerator().generate(this)
}