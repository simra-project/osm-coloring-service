package de.mcc.simra.services.osm.coloring.config

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

private val LOG: Logger = LogManager.getLogger()


// set profile so that we can skip components that should not be run during tests (e.g., Runner.kt)
@ActiveProfiles("test")
@SpringBootTest // load the complete application context
class OsmColoringConfigTest {

    @Autowired
    private lateinit var validConfig: OsmColoringConfig

    @Test
    fun runs() {
        assert(validConfig.geoJsonDir.exists())
        LOG.info("Configuration loaded successfully, wiring works as well")
    }

    @Test
    fun exception() {
        // create our own config instead of relying on dependency injection
        val osmColoringConfigData = OsmColoringConfigData().apply { this.geoJsonDir = "./does_not_exist" }
        val exception = assertThrows(IllegalArgumentException::class.java) {
            OsmColoringConfig(osmColoringConfigData)
        }
        LOG.info("Received expected exception, message is ${exception.message}")
    }

}