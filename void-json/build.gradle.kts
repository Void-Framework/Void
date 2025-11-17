plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.21"
    `maven-publish`
    id("org.jetbrains.dokka")
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Unit test dependencies (JUnit 5 platform)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test.junit5)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    api(project(":void-base"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.9.0")
}

kotlin {
    jvmToolchain(21)
}

description = "void-json"

tasks.test {
    // Use JUnit Platform (JUnit 5)
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            artifact(tasks["jar"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = "com.github.Jadiefication"
            artifactId = "Void-JSON"
            version = rootProject.version.toString()
        }
    }
}
