package common

object RetryUtils {

    inline fun <reified T> retry(
        action: () -> T,
        condition: (T) -> Unit,
        maxAttempts: Int = 3,
        delay: Long = 1000
    ): T {
        var attempts = 0
        var lastException: Exception? = null

        while (attempts < maxAttempts) {
            attempts++
            try {
                val result = action()
                condition(result) // Если исключения нет - условие выполнено
                return result
            } catch (e: Exception) {
                lastException = e
            }
            Thread.sleep(delay)
        }
        throw RuntimeException("Retry failed after $maxAttempts attempts", lastException)
    }
}