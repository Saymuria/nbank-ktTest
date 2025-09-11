package assertions

import org.assertj.core.api.SoftAssertions

class ModelComparator(private val configParser: ComparisonConfigParser) {

    fun <T : Any, R : Any> compare(softly: SoftAssertions, request: T, response: R) {
        val requestClass = request::class.java.simpleName
        val responseClass = response::class.java.simpleName

        // Только явная конфигурация!
        val config = configParser.getConfig(requestClass, responseClass)
        compareUsingConfig(softly, request, response, config)
    }

    private fun <T : Any, R : Any> compareUsingConfig(
        softly: SoftAssertions,
        request: T,
        response: R,
        config: ModelComparisonConfig
    ) {
        config.fieldMappings.forEach { mapping ->
            val requestValue = ReflectionUtils.getFieldValue(request, mapping.requestField)
            val responseValue = ReflectionUtils.getFieldValue(response, mapping.responseField)

            softly.assertThat(responseValue)
                .`as`("Field '${mapping.requestField}' -> '${mapping.responseField}'")
                .isEqualTo(requestValue)
        }
    }
}