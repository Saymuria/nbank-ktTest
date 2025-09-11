package assertions

object ReflectionUtils {

    fun getFieldValue(obj: Any, fieldName: String): Any? {
        return try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(obj)
        } catch (e: NoSuchFieldException) {
            throw IllegalArgumentException("Field '$fieldName' not found in ${obj.javaClass.simpleName}")
        }
    }
}