package common.extensions

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.concurrent.ConcurrentHashMap

class TimingExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private val startTimes = ConcurrentHashMap<String, Long>()
    override fun beforeTestExecution(context: ExtensionContext?) {
        val testName = context?.requiredTestClass?.`package`?.name + "." + context?.displayName
        startTimes[testName] = System.currentTimeMillis()
        println("Thread ${Thread.currentThread().name}: test started testName : $testName at ${System.currentTimeMillis()}")
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        val testName = context?.requiredTestClass?.`package`?.name + "." + context?.displayName
        val testDuration = System.currentTimeMillis() - startTimes[testName]!!
        println("Thread ${Thread.currentThread().name}: test finished testName : $testName, test duration $testDuration")
    }
}