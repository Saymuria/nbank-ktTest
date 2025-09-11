package framework.generators


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class GeneratingRule(val pattern: String)

// Email.kt
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Email(val format: String)

// PhoneNumber.kt
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class RusPhoneNumber()

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DateValue(val format: String)

// Для работы с фикисрованными значениями/типизированными полями
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FixedValue(val value: String)
