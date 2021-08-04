package de.mcc.simra.services.osm.coloring.runner

import de.mcc.simra.services.osm.coloring.config.OsmColoringConfig
import de.mcc.simra.services.osm.coloring.services.ReaderService
import de.mcc.simra.services.osm.coloring.services.TransformerService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

private val LOG: Logger = LogManager.getLogger()

@Profile("!test") // do not run during tests
@Component
class Runner(
    val osmColoringConfig: OsmColoringConfig,
    val readerService: ReaderService,
    val transformerService: TransformerService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        LOG.info("Reading files from ${osmColoringConfig.geoJsonDir.absolutePath}")
        runBlocking {
            val dispatcher = Dispatchers.Default + CoroutineName("Runner")

            launch(dispatcher) { readerService.processExistingFiles() }
            launch(dispatcher) { transformerService.transformIncomingGeoFeaturesBlocking() }
            LOG.info("Started all coroutines")
        }
    }
}