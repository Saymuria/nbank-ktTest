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

        failOnVersionConflict()
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")

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

    // Тестирование
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}