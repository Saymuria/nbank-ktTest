package common.extensions

import com.codeborne.selenide.Configuration
import common.annotations.Browsers
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext

class BrowserMatchExtension : ExecutionCondition {
    override fun evaluateExecutionCondition(context: ExtensionContext?): ConditionEvaluationResult? {
        val annotation = context?.element?.map { element -> element.getAnnotation(Browsers::class.java) }
        val nothing = null
        if (annotation == nothing) {
            return ConditionEvaluationResult.enabled("Нет ограничений к браузеру")
        }
        val currentBrowser = Configuration.browser
        val matches = annotation.stream().anyMatch { browser -> browser.equals(currentBrowser) }
        return if (matches) {
            ConditionEvaluationResult.enabled("Текущий браузер удовлетворяет условию: $currentBrowser")
        } else ConditionEvaluationResult.disabled("Тест пропущен, тк текущий браузер $currentBrowser не находится в списке допустимых браузеров для теста: $annotation")
    }
}