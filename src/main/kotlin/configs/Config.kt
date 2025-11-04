package configs

import java.io.IOException
import java.util.Properties

object Config {
    private val properties = Properties()

    init {
        try {
            val input = javaClass.classLoader.getResourceAsStream("config.properties")
                ?: throw RuntimeException("config.properties not found in resources")
            input.use { properties.load(it) }
        } catch (e: IOException) {
            throw RuntimeException("Fail to load config.properties", e)
        }
    }

    fun getProperty(key: String): String {
        val systemValue = System.getProperty(key)
        if (systemValue != null) return systemValue

        val envValue = System.getenv(key.toUpperCase().replace(".", "_"))
        if (envValue != null) return envValue
        return properties.getProperty(key)
    }
}
