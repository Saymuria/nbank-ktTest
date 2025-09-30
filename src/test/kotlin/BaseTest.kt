import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

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