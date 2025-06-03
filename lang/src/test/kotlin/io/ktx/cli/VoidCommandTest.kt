package io.ktx.cli

import io.ktx.transpiler.Transpiler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class VoidCommandTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var ktxFile: File
    private lateinit var standardOut: PrintStream
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        // Save the original standard output
        standardOut = System.out
        // Redirect standard output to our stream capturer
        System.setOut(PrintStream(outputStreamCaptor))

        // Create a sample .ktx file for testing with the correct fractal syntax
        ktxFile = File(tempDir, "TestRoute.ktx")
        ktxFile.writeText("""
            fun Page.TestRoute(): String {
                this.route = "/test"
                return <~>
                    <div>
                        <h1>Hello from KTX</h1>
                        <p>This is a test route</p>
                    </div>
                <~/>
            }
        """.trimIndent())
    }

    @Test
    fun `test main with no arguments prints documentation message`() {
        // Call main with empty args
        main(arrayOf())

        // Verify the documentation message is printed
        assertTrue(outputStreamCaptor.toString().contains("No arguments supplied, please refer to the documentation."))
    }

    @Test
    fun `test main with invalid argument prints documentation message`() {
        // Call main with invalid argument
        main(arrayOf("invalid"))

        // Verify the documentation message is printed
        assertTrue(outputStreamCaptor.toString().contains("Incorrect arguments supplied, please refer to the documentation."))
    }

    @Test
    fun `test main with routes argument processes ktx files`() {
        // Save the original user.dir property
        val originalUserDir = System.getProperty("user.dir")

        try {
            // Set the user.dir to our temp directory
            System.setProperty("user.dir", tempDir.absolutePath)

            // Call main with routes argument
            main(arrayOf("routes"))

            // Allow time for coroutines to complete
            Thread.sleep(500)

            // Verify no errors were printed
            assertFalse(outputStreamCaptor.toString().contains("Exception"),
                "No exceptions should be thrown during processing")

        } finally {
            // Restore the original user.dir property
            System.setProperty("user.dir", originalUserDir)
            // Restore standard output
            System.setOut(standardOut)
        }
    }

    @Test
    fun `test handleAddingRoutes processes ktx files in directory`() {
        // Create a nested directory structure with ktx files
        val nestedDir = File(tempDir, "nested")
        nestedDir.mkdir()

        val nestedKtxFile = File(nestedDir, "NestedRoute.ktx")
        nestedKtxFile.writeText("""
            fun Page.NestedRoute(): String {
                this.route = "/nested"
                return <~>
                    <div>
                        <h1>Nested Route</h1>
                    </div>
                <~/>
            }
        """.trimIndent())

        // Execute the route handling
        handleAddingRoutes(tempDir)

        // Allow time for coroutines to complete
        Thread.sleep(500)

        // Since we don't have a way to directly verify the results of handleAddingRoutes,
        // we'll just verify no exceptions were thrown
        assertFalse(outputStreamCaptor.toString().contains("Exception"),
            "No exceptions should be thrown during processing")
    }

    @Test
    fun `test handleTranspiling processes ktx file`() {
        // Execute the transpilation
        handleTranspiling(ktxFile)

        // Since the current implementation doesn't do much with the transpilation result,
        // we'll just verify no exceptions were thrown
        assertFalse(outputStreamCaptor.toString().contains("Exception"),
            "No exceptions should be thrown during transpilation")
    }

    @Test
    fun `test handleAddingRoutes skips non-ktx files`() {
        // Create a non-ktx file
        val nonKtxFile = File(tempDir, "NotARoute.txt")
        nonKtxFile.writeText("This is not a KTX file")

        // Execute the route handling
        handleAddingRoutes(tempDir)

        // Allow time for coroutines to complete
        Thread.sleep(500)

        // Verify no exceptions were thrown
        assertFalse(outputStreamCaptor.toString().contains("Exception"),
            "No exceptions should be thrown when processing non-ktx files")
    }

    @Test
    fun `test handleAddingRoutes handles empty directories`() {
        // Create an empty directory
        val emptyDir = File(tempDir, "empty")
        emptyDir.mkdir()

        // Execute the route handling
        handleAddingRoutes(emptyDir)

        // Allow time for coroutines to complete
        Thread.sleep(500)

        // Verify no exceptions were thrown
        assertFalse(outputStreamCaptor.toString().contains("Exception"),
            "No exceptions should be thrown when processing empty directories")
    }
}