package framework.generators

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import kotlin.reflect.KClass

class ValueGenerator {
    private val random = Random.Default

    // Пул символов для разных типов генерации
    private val usernameCharPool: List<Char> by lazy {
        ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('.', '_', '-')
    }

    private val passwordCharPool: List<Char> by lazy {
        ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    private val passwordSpecialCharPool by lazy {
        listOf('!', '@', '#', '$', '%', '^', '&', '+', '=')
    }

    private val alphaLowerPool: List<Char> by lazy { ('a'..'z').toList() }
    private val alphaUpperPool: List<Char> by lazy { ('A'..'Z').toList() }
    private val digitPool: List<Char> by lazy { ('0'..'9').toList() }

    private val specialCharPool: List<Char> by lazy {
        listOf(
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '=',
            '[', ']', '{', '}', ';', ':', '"', '\\', '|', ',', '.', '<', '>', '/', '?'
        ).filter { it != ' ' }
    }

    // Кэш для часто используемых значений
    private val emailTlds = listOf("com", "net", "org", "io", "ru", "de", "fr")

    fun generateValueForField(fieldType: KClass<*>, annotations: List<Annotation>): Any {
        // Обработка аннотаций в порядке приоритета
        annotations.forEach { annotation ->
            when (annotation) {
                is FixedValues -> return convertToType(annotation.value, fieldType)
                is Username -> return generateUsername()
                is Password -> return generatePassword()
                is Email -> return generateEmail()
                is GeneratingRule -> return generateFromRegex(annotation.pattern, fieldType)
                is BigDecimalRange -> return generateBigDecimal(annotation.min, annotation.max)
            }
        }

        // Генерация по типу поля, если нет аннотаций
        return generateByType(fieldType)
    }

    private fun generateByType(fieldType: KClass<*>): Any {
        return when {
            fieldType == String::class -> generateRandomString(8, 12)
            fieldType == Int::class -> random.nextInt()
            fieldType == Long::class -> random.nextLong()
            fieldType == Double::class -> random.nextDouble()
            fieldType == Float::class -> random.nextFloat()
            fieldType == Boolean::class -> random.nextBoolean()
            fieldType == BigDecimal::class -> BigDecimal.valueOf(random.nextDouble())
            fieldType.java.isEnum -> generateEnumValue(fieldType)
            else -> throw IllegalArgumentException("Unsupported field type: ${fieldType.simpleName}")
        }
    }

    private fun generateUsername(minLength: Int = 3, maxLength: Int = 15): String {
        require(minLength in 3..15 && maxLength in 3..15 && minLength <= maxLength) {
            "Username length must be between 3 and 15 characters"
        }

        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val username = StringBuilder()

        // Гарантируем, что username начинается с буквы
        username.append((alphaLowerPool + alphaUpperPool).random())

        // Заполняем оставшуюся часть допустимыми символами
        repeat(length - 1) {
            username.append(usernameCharPool.random())
        }

        return username.toString()
    }

    private fun generatePassword(minLength: Int = 8, maxLength: Int = 20): String {
        require(minLength >= 8) { "Password must be at least 8 characters long" }
        require(maxLength >= minLength) { "Max length must be >= min length" }

        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val password = StringBuilder()

        // Добавляем минимум по одному символу каждого требуемого типа
        password.append(alphaLowerPool.random())
        password.append(alphaUpperPool.random())
        password.append(digitPool.random())
        password.append(passwordSpecialCharPool.random())

        // Заполняем оставшуюся длину
        val remainingLength = length - password.length
        if (remainingLength > 0) {
            val fullPool = passwordCharPool + passwordSpecialCharPool
            repeat(remainingLength) {
                password.append(fullPool.random())
            }
        }

        // Перемешиваем символы
        return password.toString().toList().shuffled(random).joinToString("")
    }

    private fun generateEmail(domain: String? = null, tld: String? = null): String {
        val username = generateAlphanumericString(5, 10)
        val emailDomain = domain ?: generateAlphanumericString(5, 8)
        val emailTld = tld ?: emailTlds.random()
        return "$username@$emailDomain.$emailTld"
    }

    private fun generateFromRegex(pattern: String, fieldType: KClass<*>): Any {
        return try {
            val generatedString = generateFromRegexPattern(pattern)
            convertToType(generatedString, fieldType)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid regex pattern: $pattern", e)
        }
    }

    private fun generateFromRegexPattern(pattern: String): String {
        // Более продвинутая обработка regex
        return when {
            pattern.matches(Regex("^\\d+\$")) -> generateNumericString(pattern.toInt(), pattern.toInt())
            pattern.matches(Regex("^\\d+\\-\\d+\$")) -> {
                val (min, max) = pattern.split("-").map { it.toInt() }
                generateNumericString(min, max)
            }
            else -> {
                // Базовая обработка простых паттернов
                val length = extractLengthFromPattern(pattern) ?: (8 to 12)
                val actualLength = random.nextInt(length.second - length.first + 1) + length.first

                when {
                    pattern.contains("[a-zA-Z0-9]") -> generateAlphanumericString(actualLength, actualLength)
                    pattern.contains("[a-zA-Z]") -> generateAlphabeticString(actualLength, actualLength)
                    pattern.contains("\\d") -> generateNumericString(actualLength, actualLength)
                    else -> generateRandomString(actualLength, actualLength)
                }
            }
        }
    }

    private fun extractLengthFromPattern(pattern: String): Pair<Int, Int>? {
        val lengthMatch = Regex("\\{(\\d+)(?:,(\\d+))?\\}").find(pattern)
        return if (lengthMatch != null) {
            val min = lengthMatch.groups[1]?.value?.toIntOrNull() ?: 8
            val max = lengthMatch.groups[2]?.value?.toIntOrNull() ?: min
            min to max
        } else {
            null
        }
    }

    private fun generateAlphanumericString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val pool = alphaLowerPool + alphaUpperPool + digitPool
        return buildString {
            repeat(length) {
                append(pool.random(random))
            }
        }
    }

    fun generateAlphabeticString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val pool = alphaLowerPool + alphaUpperPool
        return buildString {
            repeat(length) {
                append(pool.random(random))
            }
        }
    }

    private fun generateNumericString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        return buildString {
            repeat(length) {
                append(digitPool.random(random))
            }
        }
    }

    private fun generateRandomString(minLength: Int = 8, maxLength: Int = 12): String {
        val length = random.nextInt(maxLength - minLength + 1) + minLength
        val allCharsPool = alphaLowerPool + alphaUpperPool + digitPool + specialCharPool
        return buildString {
            repeat(length) {
                append(allCharsPool.random(random))
            }
        }
    }

    private fun generateEnumValue(enumType: KClass<*>): Any {
        val values = enumType.java.enumConstants
        return values[random.nextInt(values.size)]
    }

    fun generateBigDecimal(min: Double, max: Double, scale: Int = 2): BigDecimal {
        require(min >= 0) { "Min value must be non-negative" }
        require(max >= min) { "Max value must be greater or equal to min" }

        val amount = min + Random.nextDouble() * (max - min)
        return BigDecimal(amount).setScale(scale, RoundingMode.HALF_UP)
    }


    private fun convertToType(value: String, targetType: KClass<*>): Any {
        return when (targetType) {
            String::class -> value
            Int::class -> value.toInt()
            Long::class -> value.toLong()
            Double::class -> value.toDouble()
            Float::class -> value.toFloat()
            Boolean::class -> value.toBooleanStrict()
            BigDecimal::class -> value.toBigDecimal()
            else -> {
                if (targetType.java.isEnum) {
                    targetType.java.enumConstants.firstOrNull {
                        (it as Enum<*>).name.equals(value, ignoreCase = true)
                    } ?: throw IllegalArgumentException("Value '$value' not found in enum ${targetType.simpleName}")
                } else {
                    throw IllegalArgumentException("Unsupported type for conversion: ${targetType.simpleName}")
                }
            }
        }
    }
}