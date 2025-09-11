package generators

import com.github.curiousoddman.rgxgen.RgxGen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class RandomModelGenerator {

    private val random = Random()
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val dateFormatters = mapOf(
        "yyyy-MM-dd" to DateTimeFormatter.ISO_LOCAL_DATE,
        "dd.MM.yyyy" to DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        "MM/dd/yyyy" to DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        "yyyy/MM/dd" to DateTimeFormatter.ofPattern("yyyy/MM/dd")
    )

    fun generateValueForField(field: KProperty<*>, fieldType: KClass<*>): Any {
        // Обработка аннотаций в порядке приоритета
        field.findAnnotation<FixedValue>()?.let { annotation ->
            return convertToType(annotation.value, fieldType)
        }

        field.findAnnotation<GeneratingRule>()?.let { annotation ->
            return generateFromRegex(annotation.pattern, fieldType)
        }

        field.findAnnotation<Email>()?.let {
            return generateEmail()
        }

        field.findAnnotation<RusPhoneNumber>()?.let {
            return generateRusPhoneNumber()
        }

        field.findAnnotation<DateValue>()?.let { annotation ->
            return generateDate(annotation.format)
        }

        // Генерация по типу поля, если нет аннотаций
        return when {
            fieldType.isSubclassOf(String::class) -> generateRandomString(8)
            fieldType == Int::class -> random.nextInt()
            fieldType == Long::class -> random.nextLong()
            fieldType == Double::class -> random.nextDouble()
            fieldType == Float::class -> random.nextFloat()
            fieldType == Boolean::class -> random.nextBoolean()
            fieldType.java.isEnum -> generateEnumValue(fieldType)
            else -> throw IllegalArgumentException("Unsupported field type: ${fieldType.simpleName}")
        }
    }

    private fun generateFromRegex(pattern: String, fieldType: KClass<*>): Any {
        return try {
            val generatedString = generateStringFromRegex(pattern)
            convertToType(generatedString, fieldType)
        } catch (e: Exception) {
            // Fallback на базовую генерацию при ошибках в regexp
            when (fieldType) {
                String::class -> generateRandomString(8)
                Int::class -> random.nextInt()
                Long::class -> random.nextLong()
                Double::class -> random.nextDouble()
                else -> generateRandomString(8)
            }
        }
    }

    private fun generateStringFromRegex(pattern: String): String {
        return try {
            val rgxGen = RgxGen(pattern)
            rgxGen.generate()
        } catch (e: Exception) {
            // Fallback для сложных regexp, которые не поддерживаются библиотекой
            generateFallbackFromRegex(pattern)
        }
    }

    private fun generateFallbackFromRegex(pattern: String): String {
        // Базовые fallback-шаблоны для распространенных случаев
        return when {
            pattern.contains("@") && pattern.contains("\\.") -> generateEmail()
            pattern.contains("\\+?[0-9]") -> generateRusPhoneNumber()
            pattern.startsWith("^[A-Za-z0-9]") -> {
                val length = extractLengthFromPattern(pattern) ?: 8
                generateAlphanumericString(length, length + 5)
            }
            pattern.startsWith("^[a-zA-Z]") -> {
                val length = extractLengthFromPattern(pattern) ?: 8
                generateAlphabeticString(length, length + 5)
            }
            pattern.startsWith("^\\d") -> random.nextInt(10000).toString()
            else -> generateRandomString(8)
        }
    }

    private fun extractLengthFromPattern(pattern: String): Int? {
        val regex = "\\{(\\d+)(?:,(\\d+))?}".toRegex()
        val match = regex.find(pattern)
        return match?.groupValues?.get(1)?.toInt()
    }

    private fun generateAlphanumericString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        return (1..length)
            .map { charPool[random.nextInt(charPool.size)] }
            .joinToString("")
    }

    private fun generateAlphabeticString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val alphaPool = ('a'..'z') + ('A'..'Z')
        return (1..length)
            .map { alphaPool[random.nextInt(alphaPool.size)] }
            .joinToString("")
    }

    private fun generateEmail(): String {
        val username = generateAlphanumericString(5, 10)
        val domain = generateAlphanumericString(5, 8)
        val tld = listOf("com", "net", "org", "io", "ru", "de", "fr").random()
        return "$username@$domain.$tld"
    }

    private fun generateRusPhoneNumber(): String {
        // Дефолтные коды операторов для России
        val actualOperatorCode = arrayOf(
            "900", "901", "902", "903", "904", "905", "906", "908", "909",
            "910", "911", "912", "913", "914", "915", "916", "917", "918", "919"
        ).random()
        val numberPart = (1000000..9999999).random().toString()
        return "+7$actualOperatorCode$numberPart"
    }

    //работает только в том случае, если нужна какая-то дата рандомная,
    // для определенной даты в определенном формате нужны доработки
    private fun generateDate(format: String): LocalDate {
        val formatter = dateFormatters[format] ?: DateTimeFormatter.ofPattern(format)
        // Генерируем дату за последние 10 лет
        val daysAgo = random.nextInt(3650)
        return LocalDate.now().minusDays(daysAgo.toLong())
    }

    private fun generateRandomString(length: Int): String {
        return (1..length)
            .map { charPool[random.nextInt(charPool.size)] }
            .joinToString("")
    }

    private fun generateEnumValue(enumType: KClass<*>): Any {
        val values = enumType.java.enumConstants
        return values[random.nextInt(values.size)]
    }

    private fun convertToType(value: String, targetType: KClass<*>): Any {
        return when (targetType) {
            String::class -> value
            Int::class -> value.toInt()
            Long::class -> value.toLong()
            Double::class -> value.toDouble()
            Float::class -> value.toFloat()
            Boolean::class -> value.toBoolean()
            else -> {
                if (targetType.java.isEnum) {
                    targetType.java.enumConstants.firstOrNull {
                        (it as Enum<*>).name == value
                    } ?: throw IllegalArgumentException("Value '$value' not found in enum ${targetType.simpleName}")
                } else {
                    throw IllegalArgumentException("Unsupported type for FixedValue: ${targetType.simpleName}")
                }
            }
        }
    }
}