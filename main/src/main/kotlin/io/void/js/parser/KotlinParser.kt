package io.void.js.parser

import io.void.js.BaseJSImplementation
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

class KotlinParser private constructor() {

    companion object {
        val singleton = KotlinParser()
    }

    private val disposable = Disposer.newDisposable()
    private val configuration = CompilerConfiguration()
    private val environment = KotlinCoreEnvironment.createForProduction(
        projectDisposable = disposable,
        configuration = configuration,
        configFiles = EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
    private val psiFactory = KtPsiFactory(environment.project)

    init {
        setIdeaIoUseFallback()
    }

    fun getAST(function: BaseJSImplementation.() -> Unit) {
        val body = (function::reflect as KtNamedFunction).bodyExpression?.text
        println(body)
    }
}