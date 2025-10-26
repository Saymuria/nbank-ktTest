package common.extensions

import common.annotations.AdminSession
import models.authentication.LoginUserRequest.Companion.getAdmin
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import ui.pages.BasePage

class AdminSessionExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        //Шаг 1 проверка, есть ли у теста аннотация AdminSession
        val annotation = context?.requiredTestMethod?.getAnnotation(AdminSession::class.java)
        if (annotation != null) {
            BasePage.authorizeAsUser(getAdmin())
        }
    }
}