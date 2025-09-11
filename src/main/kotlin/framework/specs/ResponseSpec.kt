package framework.specs

import io.restassured.builder.ResponseSpecBuilder
import io.restassured.specification.ResponseSpecification
import org.apache.http.HttpStatus.SC_BAD_REQUEST
import org.apache.http.HttpStatus.SC_CREATED
import org.apache.http.HttpStatus.SC_OK
import org.hamcrest.Matchers

class ResponseSpec {
    companion object {
        private fun defaultResponseBuilder(): ResponseSpecBuilder {
            return ResponseSpecBuilder()
        }

        fun entityWasCreated(): ResponseSpecification {
            return defaultResponseBuilder()
                .expectStatusCode(SC_CREATED)
                .build()
        }

        fun requestReturnOk(): ResponseSpecification {
            return defaultResponseBuilder()
                .expectStatusCode(SC_OK)
                .build()
        }

        fun requestReturnsBadRequest(errorKey: String, errorValue: String): ResponseSpecification {
            return defaultResponseBuilder()
                .expectStatusCode(SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.equalTo(errorValue))
                .build()
        }
    }
}
