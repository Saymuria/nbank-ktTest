package framework.specs

import configs.Config
import framework.skeleton.Endpoint
import framework.skeleton.requesters.CrudRequester
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import models.authentication.LoginUserRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MINUTES

class RequestSpecs {
    init {
        // Очистка старых ключей каждые 10 минут
        cacheCleanup.scheduleAtFixedRate({
            tokenCache.clear()
        }, 10, 10, MINUTES)
    }
    companion object {
        private val tokenCache = ConcurrentHashMap<String, String>()

        private val cacheCleanup = Executors.newSingleThreadScheduledExecutor()

        private fun defaultRequestBuilder(): RequestSpecBuilder {
            return RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(listOf(RequestLoggingFilter(), ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"))

        }

        fun unAuthSpec(): RequestSpecification {
            return defaultRequestBuilder().build()
        }

        fun adminAuthSpec(): RequestSpecification {
            return defaultRequestBuilder()
                .addHeader(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
                .build()
        }

        fun authAsUser(userName: String, password: String): RequestSpecification {
            val userAuthHeader = tokenCache[userName] ?: run {
                val newToken = CrudRequester(unAuthSpec(), ResponseSpec.requestReturnOk(), Endpoint.LOGIN).post(
                    LoginUserRequest(userName, password)
                )
                    .extract()
                    .header(AUTHORIZATION)
                tokenCache[userName] = newToken
                newToken
            }
            return defaultRequestBuilder()
                .addHeader(AUTHORIZATION, userAuthHeader)
                .build()
        }
    }
}
