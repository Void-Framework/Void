plugins {
    kotlin("jvm")
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
    // Needed by some tests for TLS cert generation
    testImplementation("org.bouncycastle:bcpkix-jdk18on:1.82")
    implementation(project(":void-base"))
}

kotlin {
    jvmToolchain(21)
}

description = "void-html"

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
            artifactId = "Void-HTML"
            version = rootProject.version.toString()
        }
    }
}
