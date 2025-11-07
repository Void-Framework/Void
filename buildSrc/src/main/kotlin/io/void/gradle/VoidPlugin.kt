package io.void.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService
import kotlin.concurrent.thread
import kotlin.io.path.absolute
import kotlin.io.path.extension

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
                    dir.toPath().registerAll(watcher)
                }

                thread {
                    while (true) {
                        val key = watcher.take()
                        key.pollEvents().forEach { e ->
                            val changedFile = (key.watchable() as Path).resolve(e.context() as Path)
                            if (changedFile.extension == "kt") {
                                println("💡 Changed: $changedFile")

                                ProcessBuilder("gradle", "compileKotlin").inheritIO().start()
                            }
                        }
                        key.reset()
                    }
                }
                Thread.currentThread().join()
            }
        }
    }

    fun Path.registerAll(watcher: WatchService) {
        Files.walk(this).filter { Files.isDirectory(it) }.forEach {
            println("👀 Watching: ${it.absolute()}")
            it.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY)
        }
    }

}