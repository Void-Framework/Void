package io.void.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class VoidFrameworkExtension {
    var jsDir: String = "src/jsMain/kotlin"
    var jsOutput: String = "build/void-framework/js"
}

class VoidFrameworkPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create("voidJs", VoidFrameworkExtension::class.java)

        project.afterEvaluate {
            project.plugins.withId("org.jetbrains.kotlin.js") {
                project.extensions.configure<KotlinJsProjectExtension>("kotlin") {
                    js(org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR) {
                        browser { binaries.executable() }
                    }
                }
            }

            project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
                project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                    ?.js(org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR) {
                        browser { binaries.executable() }
                    }
            }

            // Task to bundle JS to framework output
            val bundleJs = project.tasks.register<Copy>("bundleJs") {
                from("${project.projectDir}/${ext.jsDir}")
                into("${project.projectDir}/${ext.jsOutput}")
            }

            // Make build depend on JS bundle
            tasks.named("build") {
                dependsOn(bundleJs)
            }
        }
    }
}