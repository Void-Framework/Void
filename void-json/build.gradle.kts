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
    testImplementation(kotlin("test"))
    api(project(":void-base"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.9.0")
}

kotlin {
    jvmToolchain(21)
}

description = "void-json"

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
