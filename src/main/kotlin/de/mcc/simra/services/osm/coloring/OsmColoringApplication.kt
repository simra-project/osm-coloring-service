package de.mcc.simra.services.osm.coloring

import de.mcc.simra.services.osm.coloring.config.OsmColoringConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.exitProcess

private val LOG: Logger = LogManager.getLogger()

@SpringBootApplication
class OsmColoringApplication(val osmColoringConfig: OsmColoringConfig) : CommandLineRunner {

	override fun run(vararg args: String?) {
		LOG.info(osmColoringConfig.geoJsonDir.absolutePath)
		exitProcess(0)
	}
}

fun main(args: Array<String>) {
	runApplication<OsmColoringApplication>(*args)
}
