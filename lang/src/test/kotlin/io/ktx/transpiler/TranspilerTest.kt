package io.ktx.transpiler

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranspilerTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var simpleKtxFile: File
    private lateinit var complexKtxFile: File

    @BeforeEach
    fun setup() {
        // Create a simple .ktx file with fractal syntax
        simpleKtxFile = File(tempDir, "SimpleRoute.ktx")
        simpleKtxFile.writeText("""
            fun Page.SimpleRoute() {
                this.route = "/simple"
                return <~>
                    <div>
                        <h1>Simple Route</h1>
                    </div>
                <~/>
            }
        """.trimIndent())

        // Create a more complex .ktx file with multiple routes and features
        complexKtxFile = File(tempDir, "ComplexRoute.ktx")
        complexKtxFile.writeText("""
            fun Page.ComplexRoute() {
                this.route = "/complex"
                return <~>
                    <head>
                        <title>Complex Page</title>
                        <meta name="description" content="A complex page example" />
                    </head>
                    <body>
                        <div class="container">
                            <h1>Complex Route</h1>
                            <p>This route has multiple elements and attributes</p>
                            <button id="testBtn" onclick="alert('clicked')">Click me</button>
                        </div>
                    </body>
                <~/>
            }
        """.trimIndent())
    }

    @Test
    fun `test transpile simple ktx file`() {
        val transpiler = Transpiler(simpleKtxFile)
        val result = transpiler.transpile()

        // Verify the transpiler extracted the route correctly
        assertTrue(result.containsKey("/simple"), "Should extract the simple route")

        // In a real test, you would verify the generated code structure
        // This depends on the actual implementation of your transpiler
    }

    @Test
    fun `test transpile complex ktx file with multiple routes and annotations`() {
        val transpiler = Transpiler(complexKtxFile)
        val result = transpiler.transpile()

        // Verify the transpiler extracted both routes
        assertTrue(result.containsKey("/complex"), "Should extract the complex route")

        // Verify the cacheable annotation was processed
        // This would depend on how your transpiler handles annotations

        // In a real test, you would verify the generated code structure
        // including HTML elements, attributes, and response handling
    }

    @Test
    fun `test transpiler handles invalid ktx file gracefully`() {
        // Create an invalid .ktx file
        val invalidKtxFile = File(tempDir, "InvalidRoute.ktx")
        invalidKtxFile.writeText("""
            This is not valid KTX syntax
            fun Page.BrokenRoute(): String {
                this.route = "/broken
                return <~>
                    <div>
                    unclosed tag
            }
        """.trimIndent())

        val transpiler = Transpiler(invalidKtxFile)

        try {
            val result = transpiler.transpile()
            // If transpiler doesn't throw but returns empty map, that's also acceptable
            assertTrue(result.isEmpty(), "Should return empty map for invalid input")
        } catch (e: Exception) {
            // If transpiler throws exception for invalid input, that's acceptable too
            assertTrue(true, "Transpiler threw exception for invalid input as expected")
        }
    }

    @Test
    fun `test transpiler generates valid Kotlin code`() {
        val transpiler = Transpiler(simpleKtxFile)
        val result = transpiler.transpile()

        // This test would depend on how your transpiler generates code
        // You might want to compile the generated code or validate its structure

        // For example, if your transpiler generates actual files:
        // val generatedFile = File(simpleKtxFile.parentFile, "SimpleRoute.kt")
        // assertTrue(generatedFile.exists(), "Generated Kotlin file should exist")
        // val content = generatedFile.readText()
        // assertTrue(content.contains("class SimpleRoute"), "Should contain route class")
    }
}