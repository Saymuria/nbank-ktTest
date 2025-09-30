package framework.assertions

object ReflectionUtils {

    fun getFieldValue(obj: Any, fieldPath: String): Any? {
        val fieldNames = fieldPath.split(".")
        var currentValue: Any? = obj

        for (fieldName in fieldNames) {
            currentValue = getSingleFieldValue(currentValue, fieldName) ?: return null
        }

        return currentValue
    }

    private fun getSingleFieldValue(obj: Any?, fieldName: String): Any? {
        if (obj == null) return null

        return try {
            // Проверяем, является ли поле коллекцией/массивом с индексом
            if (fieldName.contains("[")) {
                handleIndexedField(obj, fieldName)
            } else {
                // Обычное поле
                val field = getDeclaredField(obj, fieldName)
                field.isAccessible = true
                field.get(obj)
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to access field '$fieldName' in ${obj.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun handleIndexedField(obj: Any, fieldName: String): Any? {
        val pattern = Regex("""(.+)\[(\d+)\]""")
        val matchResult = pattern.find(fieldName)

        if (matchResult != null) {
            val (actualFieldName, indexStr) = matchResult.destructured
            val index = indexStr.toInt()

            val field = getDeclaredField(obj, actualFieldName)
            field.isAccessible = true
            val fieldValue = field.get(obj)

            return when (fieldValue) {
                is List<*> -> fieldValue.getOrNull(index)
                is Array<*> -> fieldValue.getOrNull(index)
                else -> throw IllegalArgumentException("Field '$actualFieldName' is not a collection or array")
            }
        }

        throw IllegalArgumentException("Invalid indexed field format: $fieldName")
    }

    private fun getDeclaredField(obj: Any, fieldName: String): java.lang.reflect.Field {
        var clazz: Class<*> = obj.javaClass
        while (clazz != Any::class.java) {
            try {
                return clazz.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        throw NoSuchFieldException("Field '$fieldName' not found in ${obj.javaClass.simpleName}")
    }
}