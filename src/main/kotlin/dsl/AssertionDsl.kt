package dsl

import framework.extentions.Assertions
import org.assertj.core.api.SoftAssertions

fun check(softly: SoftAssertions, block: Assertions.() -> Unit) {
    Assertions(softly).block()
}