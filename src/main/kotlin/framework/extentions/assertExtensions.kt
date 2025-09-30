package framework.extentions

import framework.assertions.ModelAssertions
import org.assertj.core.api.SoftAssertions
import java.math.BigDecimal

// Базовое расширение
infix fun <T : Any> T.shouldMatchResponse(response: Any) {
    ModelAssertions.match(this, response)
}

class Assertions(private val softly: SoftAssertions) {

    infix fun <T : Any> T.shouldMatch(expected: Any) {
        ModelAssertions.match(softly, this, expected)
    }

    infix fun Any.shouldBe(expected: Any) {
        softly.assertThat(this).isEqualTo(expected)
    }

    infix fun Any.shouldNotBe(expected: Any) {
        softly.assertThat(this).isNotEqualTo(expected)
    }

    infix fun BigDecimal.shouldBe(expected: BigDecimal) {
        softly.assertThat(this).isEqualByComparingTo(expected)
    }
    infix fun BigDecimal.shouldNotBe(expected: BigDecimal) {
        softly.assertThat(this).isEqualByComparingTo(expected)
    }
}