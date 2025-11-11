package hellpers

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import io.qameta.allure.Allure
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import java.io.ByteArrayInputStream
import java.util.Base64

fun <T> step(title: String, block: () -> T): T {
    val blockToExecute = Allure.ThrowableRunnable { block() }
    return Allure.step(title, blockToExecute)
}

fun checkThat(description: String, whatCheck: () -> Boolean) {
    step("Проверяем, что $description") {
        assertThat(description, whatCheck())
    }
}

fun <T> checkThat(description: String, o: T, matcher: Matcher<T>) {
    step("Проверяем, что $description. Ожидаем: $matcher.") {
        assertThat(description, o, matcher)
    }
}

fun <T> stepWithResult(title: String, block: () -> T): T {
    return Allure.step(title, Allure.ThrowableRunnable<T> {
        val result = block()
        Thread.sleep(1000)
        takeScreenshot(title)
        result
    })
}

private fun takeScreenshot(stepName: String) {
    try {
        // Проверяем, есть ли активный alert
        try {
            val alert = WebDriverRunner.getWebDriver().switchTo().alert()
            val alertText = alert.text
            // Если есть alert, делаем текстовое описание
            Allure.addAttachment(
                "Alert состояние: $stepName",
                "text/plain",
                "Alert текст: $alertText\nСкриншот отключен для сохранения alert."
            )
            return
        } catch (e: org.openqa.selenium.NoAlertPresentException) {
            // Alert нет - делаем обычный скриншот
            val screenshotBytes = Selenide.screenshot(org.openqa.selenium.OutputType.BYTES)
            Allure.addAttachment(
                "Скриншот: $stepName",
                "image/png",
                ByteArrayInputStream(screenshotBytes),
                "png"
            )
        }
    } catch (e: Exception) {
        println("Не удалось сделать скриншот: ${e.message}")
    }
}




