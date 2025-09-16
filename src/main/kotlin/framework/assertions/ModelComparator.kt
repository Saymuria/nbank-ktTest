package framework.assertions

import org.assertj.core.api.SoftAssertions
import java.math.BigDecimal

class ModelComparator(private val configParser: ComparisonConfigParser) {

    fun <T : Any, R : Any> compare(softly: SoftAssertions, request: T, response: R) {
        val requestClass = request::class.java.simpleName
        val responseClass = response::class.java.simpleName

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
            try {
                val requestValue = ReflectionUtils.getFieldValue(request, mapping.requestField)
                val responseValue = ReflectionUtils.getFieldValue(response, mapping.responseField)

                when {
                    requestValue is BigDecimal && responseValue is BigDecimal -> {
                        softly.assertThat(responseValue)
                            .`as`("Field '${mapping.requestField}' -> '${mapping.responseField}'")
                            .isEqualByComparingTo(requestValue)
                    }
                    else -> {
                        softly.assertThat(responseValue)
                            .`as`("Field '${mapping.requestField}' -> '${mapping.responseField}'")
                            .isEqualTo(requestValue)
                    }
                }
            } catch (e: Exception) {
                softly.assertThat(false)
                    .`as`("Error comparing '${mapping.requestField}' -> '${mapping.responseField}': ${e.message}")
                    .isTrue()
            }
        }
    }
}