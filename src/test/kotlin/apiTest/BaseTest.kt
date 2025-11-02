package apiTest

import common.extensions.TimingExtension
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TimingExtension::class)
abstract class BaseTest {
    protected var softly = SoftAssertions()

    @BeforeEach
    fun setUpTest() {
        softly = SoftAssertions()
    }

    @AfterEach
    fun afterTest(){
        softly.assertAll()
    }

}