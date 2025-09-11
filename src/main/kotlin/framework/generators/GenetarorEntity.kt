package framework.generators

import models.BaseModel
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class EntityGenerator(private val valueGenerator: RandomModelGenerator = RandomModelGenerator()) {

    inline fun <reified T : BaseModel> generate(): T {
        return generate(T::class)
    }

    fun <T : BaseModel> generate(clazz: KClass<T>): T {
        val instance = clazz.createInstance()

        clazz.memberProperties.forEach { property ->
            property.javaField?.let { field ->
                field.isAccessible = true
                val generatedValue = valueGenerator.generateValueForField(property, property.returnType.classifier as KClass<*>)
                field.set(instance, generatedValue)
            }
        }

        return instance
    }

    inline fun <reified T : BaseModel> generateList(count: Int): List<T> {
        return (1..count).map { generate<T>() }
    }
}