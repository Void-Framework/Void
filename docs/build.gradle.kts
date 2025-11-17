import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    // Run the docs server by default
    mainClass.set("io.voidx.docs.ServerKt")
}

dependencies {
    implementation(project(":void-base"))
    implementation(project(":void-html"))
}

tasks.register<JavaExec>("buildDocs") {
    group = "documentation"
    description = "Generates static documentation site into build/site"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.voidx.docs.SiteKt")
}

tasks.register<JavaExec>("exportServerPages") {
    group = "documentation"
    description = "Exports the server-rendered docs routes to static HTML under build/pages"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.voidx.docs.ExportKt")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
