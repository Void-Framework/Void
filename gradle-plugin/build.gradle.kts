plugins {
    kotlin("jvm") version "2.2.10"
    `kotlin-dsl`
}

group = "io.void"
version = "0.5"

repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}

gradlePlugin {
    plugins {
        val voidPlugin by plugins.creating {
            id = "io.void.plugin"
            implementationClass = "io.void.plugin.VoidFrameworkPlugin"
            version = 1.0
        }
    }
}
