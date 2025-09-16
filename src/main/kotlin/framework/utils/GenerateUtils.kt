package framework.utils

import framework.generators.EntityGenerator
import models.BaseModel

inline fun <reified T : BaseModel> generate(customValues: Map<String, Any> = emptyMap()): T {
    return EntityGenerator().generate<T>(customValues)
}
