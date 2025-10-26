package common.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UserSession(val value: Int = 1, val auth: Int = 1, val withAccount: Boolean = false)
