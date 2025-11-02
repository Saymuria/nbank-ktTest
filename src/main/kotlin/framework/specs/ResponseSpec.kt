package framework.specs

import io.restassured.builder.ResponseSpecBuilder
import io.restassured.specification.ResponseSpecification
import org.apache.http.HttpStatus.*
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

        fun requestReturnsBadRequestWithError(errorKey: String, errorValues: List<String>): ResponseSpecification {
            return defaultResponseBuilder()
                .expectStatusCode(SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.hasItems(*errorValues.toTypedArray()))
                .build()
        }

        fun requestReturnsBadRequest(): ResponseSpecification {
            return defaultResponseBuilder()
                .expectStatusCode(SC_BAD_REQUEST)
                .build()
        }
    }
}
