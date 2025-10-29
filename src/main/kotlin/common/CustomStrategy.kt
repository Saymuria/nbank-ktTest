package common
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy

class CustomStrategy : ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {

    override fun getParallelism(): Int = 2
    override fun getMinimumRunnable(): Int = 2
    override fun getMaxPoolSize(): Int = 2
    override fun getCorePoolSize(): Int = 2
    override fun getKeepAliveSeconds(): Int = 60

    override fun createConfiguration(configurationParameters: ConfigurationParameters): ParallelExecutionConfiguration {
        return this
    }
}