package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute
import main.test.routes.setter.SetterRoute
import java.io.File

val router = Router().addRoutes(listOf(HomeRoute(), SetterRoute()))

fun main() {
    val server = Server(router = router)

    // Get the resource as a stream and create a temporary file
    val certStream = object {}.javaClass.getResourceAsStream("/localhost.p12")
    val tempFile = File.createTempFile("cert", ".p12")
    tempFile.deleteOnExit()

    certStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    } ?: throw IllegalStateException("Certificate file not found in resources")

    server.startHTTPSServer(8080, "testing", tempFile, false)

}
