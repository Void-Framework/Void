package test

import io.voidx.Server
import io.voidx.exception.NotEnoughCarriers
import io.voidx.router.Router
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerUseCarriersAndServerBitsTests {
    @Test
    fun start_http_server_with_use_carriers_true_throws_immediately() {
        val r = Router()
        val s = Server(r)
        s.useCarriers = true
        assertFailsWith<NotEnoughCarriers> {
            s.startHTTPServer(0)
        }
    }

    @Test
    fun start_https_server_with_use_carriers_true_throws_immediately() {
        val r = Router()
        val s = Server(r)
        s.useCarriers = true
        // Should throw before accessing the keystore file
        assertFailsWith<NotEnoughCarriers> {
            s.startHTTPSServer(0, password = "pass", file = File("does-not-exist.p12"), needsAuth = false)
        }
    }

    @Test
    fun is_https_server_running_is_false_by_default() {
        val r = Router()
        val s = Server(r)
        assertEquals(false, s.isHTTPSServerRunning())
    }
}
