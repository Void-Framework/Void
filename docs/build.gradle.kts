plugins {
    kotlin("jvm")
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
    mainClass.set("io.void.docs.ServerKt")
}

dependencies {
    implementation(project(":void-base"))
    implementation(project(":void-html"))
}

tasks.register<JavaExec>("buildDocs") {
    group = "documentation"
    description = "Generates static documentation site into build/site"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.void.docs.SiteKt")
}

tasks.register<JavaExec>("exportServerPages") {
    group = "documentation"
    description = "Exports the server-rendered docs routes to static HTML under build/pages"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.void.docs.ExportKt")
}
