package test

import io.voidx.router.router
import io.voidx.util.ModuleInit
import kotlin.test.Test
import kotlin.test.assertEquals

class LegacyModuleInitBridgeTests {
    @Test
    fun module_init_bridge_runs_only_once_on_first_router_creation() {
        var calls = 0
        class TMI : ModuleInit() {
            override fun init() { calls += 1 }
        }
        // instantiate legacy module to register itself
        TMI()

        // Create two routers; bridge should execute once on first router creation
        router { }
        router { }

        assertEquals(1, calls, "ModuleInit.init() should be invoked exactly once by bootstrap bridge")
    }
}
