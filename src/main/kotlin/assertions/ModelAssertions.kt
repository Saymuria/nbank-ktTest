package assertions

import org.assertj.core.api.SoftAssertions

object ModelAssertions {

    private val configParser = ComparisonConfigParser()
    private val comparator = ModelComparator(configParser)

    fun <T : Any, R : Any> match(request: T, response: R) {
        SoftAssertions.assertSoftly { softly ->
            comparator.compare(softly, request, response)
        }
    }

    fun <T : Any, R : Any> match(softly: SoftAssertions, request: T, response: R) {
        comparator.compare(softly, request, response)
    }
}