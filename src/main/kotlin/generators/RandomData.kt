package generators

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomStringUtils.randomNumeric

class RandomData {
    companion object {
        fun getUserName(): String {
            return RandomStringUtils.randomAlphabetic(10)
        }

        fun getUserPassword(): String {
            return RandomStringUtils.randomAlphabetic(3).uppercase() + randomAlphabetic(5).lowercase() + randomNumeric(3) + "%@#"
        }
    }

}