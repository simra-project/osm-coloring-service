package de.mcc.simra.services.osm.coloring.runner

import de.mcc.simra.services.osm.coloring.config.OsmColoringConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

private val LOG: Logger = LogManager.getLogger()

@Profile("!test") // do not run during tests
@Component
class Runner(val osmColoringConfig: OsmColoringConfig) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        LOG.info(osmColoringConfig.geoJsonDir.absolutePath)
    }
}