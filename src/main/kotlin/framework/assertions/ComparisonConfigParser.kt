package framework.assertions

class ComparisonConfigParser {

    companion object {
        private const val CONFIG_FILE = "model.comparison.properties"
    }

    private val configs = mutableMapOf<String, ModelComparisonConfig>()

    init {
        loadConfigurations()
    }

    private fun loadConfigurations() {
        val inputStream = javaClass.classLoader.getResourceAsStream(CONFIG_FILE)
            ?: return // Если файла нет - просто выходим, configs останется пустым

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                if (line.isNotBlank() && !line.startsWith("#")) {
                    parseConfigLine(line.trim())
                }
            }
        }
    }

    private fun parseConfigLine(line: String) {
        val parts = line.split(":", limit = 2)
        if (parts.size != 2) return

        val classParts = parts[0].split("=", limit = 2)
        if (classParts.size != 2) return

        val requestClass = classParts[0].trim()
        val responseClass = classParts[1].trim()
        val fieldsPart = parts[1].trim()

        val fieldMappings = fieldsPart.split(",").mapNotNull { fieldPair ->
            val fieldParts = fieldPair.split("=", limit = 2)
            if (fieldParts.size != 2) null
            else FieldMapping(fieldParts[0].trim(), fieldParts[1].trim())
        }

        if (fieldMappings.isNotEmpty()) {
            val config = ModelComparisonConfig(
                requestClass = requestClass,
                responseClass = responseClass,
                fieldMappings = fieldMappings
            )

            configs["$requestClass:$responseClass"] = config
        }
    }

    fun getConfig(requestClass: String, responseClass: String): ModelComparisonConfig {
        return configs["$requestClass:$responseClass"]
            ?: throw IllegalArgumentException(
                "No configuration found for $requestClass -> $responseClass. " +
                        "Add mapping to $CONFIG_FILE"
            )
    }

}