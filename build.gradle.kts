plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {
    jvmToolchain(21)
}

configurations.all {
    resolutionStrategy {
        // Для Kotlin
        eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("1.9.23")
            }
        }

        // Для JUnit
        force(
            "org.junit.jupiter:junit-jupiter-api:5.9.3",
            "org.junit.jupiter:junit-jupiter-params:5.9.3",
            "org.junit.jupiter:junit-jupiter-engine:5.9.3"
        )
        force(
            "com.fasterxml.jackson.core:jackson-databind:2.15.3",
            "com.fasterxml.jackson.core:jackson-core:2.15.3",
            "com.fasterxml.jackson.core:jackson-annotations:2.15.3"
        )
        force(
            "net.bytebuddy:byte-buddy:1.17.5",
            "org.opentest4j:opentest4j:1.3.0",
            "org.apache.httpcomponents.client5:httpclient5:5.4.3",
            "org.slf4j:slf4j-api:1.7.36",
            "com.google.errorprone:error_prone_annotations:2.38.0",
            "com.google.guava:guava:33.4.8-jre",
            "org.apache.commons:commons-lang3:3.17.0"
        )
        failOnVersionConflict()
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    //Selenide
    implementation("com.codeborne:selenide:7.9.1")
    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

    // REST Assured
    implementation("io.rest-assured:rest-assured:5.4.0")
    implementation("io.rest-assured:kotlin-extensions:5.4.0")

    //asserts
    implementation("org.assertj:assertj-core:3.24.2")
// SLF4J API + Simple implementation
    testImplementation("org.slf4j:slf4j-api:1.7.36")
    testImplementation("org.slf4j:slf4j-simple:1.7.36")
    // Тестирование
    implementation("org.junit.jupiter:junit-jupiter:5.9.3")
    implementation("org.junit.platform:junit-platform-engine:1.9.3")
    implementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    // Определяем какие тесты запускать в зависимости от профиля
    if (project.hasProperty("profile")) {
        val profile = project.property("profile") as String
        when (profile) {
            "api" -> include("**/apiTest/**/*Test.class")
            "ui" -> include("**/uiTest/*Test.class")
            "all" -> include("**/apiTest/**/*Test.class", "**/uiTest/*Test.class")
        }
    }
}
