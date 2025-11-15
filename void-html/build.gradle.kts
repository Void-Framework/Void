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
    testImplementation(kotlin("test"))
    implementation(project(":void-base"))
}

kotlin {
    jvmToolchain(21)
}

description = "void-html"

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
