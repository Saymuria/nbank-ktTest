package framework.generators

import models.BaseModel
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

class EntityGenerator(private val valueGenerator: ValueGenerator = ValueGenerator()) {

    inline fun <reified T : BaseModel> generate(
        customValues: Map<String, Any> = emptyMap()
    ): T {
        return generate(T::class, customValues)
    }

    fun <T : BaseModel> generate(
        clazz: KClass<T>,
        customValues: Map<String, Any> = emptyMap()
    ): T {
        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("Class ${clazz.simpleName} has no primary constructor")

        val parameters = mutableMapOf<KParameter, Any?>()

        constructor.parameters.forEach { parameter ->
            val propertyName = parameter.name

            // Проверяем, есть ли кастомное значение для этого параметра
            if (customValues.containsKey(propertyName)) {
                parameters[parameter] = customValues[propertyName]
            } else {
                val property = clazz.memberProperties.find { it.name == propertyName }
                val generatedValue = property?.let { generateValue(it) }
                parameters[parameter] = generatedValue
            }
        }

        return constructor.callBy(parameters)
    }

    private fun generateValue(property: kotlin.reflect.KProperty<*>): Any {
        val field = property.javaField
        val annotations = field?.declaredAnnotations?.toList() ?: emptyList()
        return valueGenerator.generateValueForField(property.returnType.jvmErasure, annotations)
    }
}