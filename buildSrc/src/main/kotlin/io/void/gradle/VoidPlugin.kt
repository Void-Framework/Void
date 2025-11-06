package io.void.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import kotlin.concurrent.thread

class VoidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("devWatch") {
            group = "void"
            description = "Run development hot reload watcher"

            doLast {
                val kotlinExt = project.extensions
                    .findByType(KotlinProjectExtension::class.java)
                val sourceDirs = kotlinExt?.sourceSets
                    ?.flatMap { it.kotlin.srcDirs }
                    ?.filter { it.exists() }
                    ?: emptyList()

                val watcher = FileSystems.getDefault().newWatchService()
                sourceDirs.forEach { dir ->
                    println("👀 Watching: ${dir.absolutePath}")
                    dir.toPath().register(
                        watcher,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE
                    )
                }

                thread {
                    while (true) {
                        val key = watcher.take()
                        key.pollEvents().forEach { e ->
                            val file = e.context() as Path
                            if (file.toString().endsWith(".kt")) {
                                println("💡 Changed: $file")

                                GradleConnector.newConnector()
                                    .forProjectDirectory(project.projectDir)
                                    .connect().use {
                                        it.newBuild().forTasks("compileKotlin").run()
                                    }
                            }
                        }
                        key.reset()
                    }
                }
                Thread.sleep(Long.MAX_VALUE)
            }
        }
    }
}