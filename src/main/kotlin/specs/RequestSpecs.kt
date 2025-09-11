package specs

import configs.Config
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import models.LoginUserRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import requests.skeleton.Endpoint
import requests.skeleton.requesters.CrudRequester

class RequestSpecs {

    companion object {
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
            // получаем токен
            val userAuthHeader = CrudRequester(unAuthSpec(), ResponseSpec.requestReturnOk(), Endpoint.LOGIN).post(
                LoginUserRequest(userName, password))
                .extract()
                .header(AUTHORIZATION)
            return defaultRequestBuilder()
                .addHeader(AUTHORIZATION, userAuthHeader)
                .build()
        }
    }

}