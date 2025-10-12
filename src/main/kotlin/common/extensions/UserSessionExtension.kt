package common.extensions

import common.annotations.UserSession
import dsl.TestUser
import dsl.createUser
import dsl.createUserWithAccount
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import storage.SessionStorage
import ui.pages.BasePage.Companion.authorizeAsUser
import java.util.*

class UserSessionExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        val annotation = context?.requiredTestMethod?.getAnnotation(UserSession::class.java)
        if (annotation != null) {
            val userCount = annotation.value
            SessionStorage.clear()
            val users = LinkedList<TestUser>()

            for (i in 0 until userCount) {
                val user = if (annotation.withAccount) {
                    createUserWithAccount()
                } else {
                    createUser()
                }

                users.add(user)
            }

            SessionStorage.addUsers(users)
            val authAsUser = annotation.auth
            SessionStorage.getUser(authAsUser).authorizeAsUser()
        }
    }
}