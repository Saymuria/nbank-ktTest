package framework.extentions

import framework.assertions.ModelAssertions
import org.assertj.core.api.SoftAssertions

// Базовое расширение
fun <T : Any> T.shouldMatchResponse(response: Any) {
    ModelAssertions.match(this, response)
}

// Расширение с SoftAssertions
fun <T : Any> T.shouldMatchResponse(softly: SoftAssertions, response: Any) {
    ModelAssertions.match(softly, this, response)
}
