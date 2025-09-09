plugins {
    kotlin("js")
}

group = "io.jadiefication"
version = "0.5"

kotlin {
    jvmToolchain(24)

    js(IR) {
        browser {
            binaries.executable()
        }
    }
}

tasks.register<Copy>("exportFrontend") {
    dependsOn("browserProductionWebpack")
    from("$buildDir/distributions/frontend.js")
    into("$rootDir/backend/src/main/resources/static/js")
}
