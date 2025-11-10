package io.void.gradle

import io.void.ws.TinyWebSocketServer
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.nio.file.*
import kotlin.io.path.absolute
import kotlin.io.path.extension

class VoidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("devWatch") {
            group = "void"
            description = "Run development hot reload watcher"

            doLast {
                TinyWebSocketServer.start()
                System.setProperty("io.void.dev", "true")
                val kotlinExt = project.extensions.findByType(KotlinProjectExtension::class.java)
                val sourceDirs = kotlinExt?.sourceSets
                    ?.flatMap { it.kotlin.srcDirs }
                    ?.filter { it.exists() } ?: emptyList()

                if (sourceDirs.isEmpty()) {
                    println("⚠️ No Kotlin source directories found")
                    return@doLast
                }

                val watcher = FileSystems.getDefault().newWatchService()
                sourceDirs.forEach { it.toPath().registerAll(watcher) }

                val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
                val compilationMutex = Mutex()
                var isCompiling = false
                var pendingCompilation: Job? = null
                val debounceDelayMs = 300L // Wait 300ms for events to settle

                scope.launch {
                    println("👀 Watching Kotlin sources for changes...")
                    while (isActive) {
                        val key = watcher.take() // blocking
                        var changed = false

                        for (event in key.pollEvents()) {
                            val kind = event.kind()
                            if (kind == StandardWatchEventKinds.OVERFLOW) continue

                            val changedFile = (key.watchable() as Path).resolve(event.context() as Path)
                            if (changedFile.extension == "kt") {
                                println("💡 Kotlin file changed: $changedFile")
                                changed = true
                            }

                            // if a new directory is created, register it recursively
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(changedFile)) {
                                println("📁 New dir created -> registering recursively: $changedFile")
                                changedFile.registerAll(watcher)
                            }
                        }

                        key.reset()

                        if (changed) {
                            // Cancel any pending compilation
                            pendingCompilation?.cancel()
                            
                            // Schedule a new compilation after debounce delay
                            pendingCompilation = scope.launch {
                                delay(debounceDelayMs)
                                
                                // Check if we can compile
                                if (compilationMutex.tryLock()) {
                                    try {
                                        if (!isCompiling) {
                                            isCompiling = true
                                            try {
                                                println("⚡ Running incremental compileKotlin…")
                                                val connector = GradleConnector.newConnector()
                                                    .useBuildDistribution()
                                                    .forProjectDirectory(project.rootDir)
                                                
                                                connector.connect().use { connection ->
                                                    connection.newBuild()
                                                        .forTasks("compileKotlin")
                                                        .setStandardOutput(System.out)
                                                        .setStandardError(System.err)
                                                        .run()
                                                }
                                                println("✅ compileKotlin finished")
                                                TinyWebSocketServer.broadcast("reload")
                                            } catch (e: Exception) {
                                                println("❌ Compilation failed: ${e.message}")
                                                e.printStackTrace()
                                            } finally {
                                                isCompiling = false
                                            }
                                        } else {
                                            println("⏭️ Compilation already in progress, skipping...")
                                        }
                                    } finally {
                                        compilationMutex.unlock()
                                    }
                                } else {
                                    println("⏭️ Compilation already queued, skipping...")
                                }
                            }
                        }
                    }
                }

                // keep the task alive
                runBlocking { scope.coroutineContext[Job]?.join() }
            }
        }
    }

    private fun Path.registerAll(watcher: WatchService) {
        Files.walk(this)
            .filter { Files.isDirectory(it) }
            .forEach {
                println("👀 Watching: ${it.absolute()}")
                it.register(
                    watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
                )
            }
    }
}
