package de.mcc.simra.services.osm.coloring.runner

import de.mcc.simra.services.osm.coloring.config.OsmColoringConfig
import de.mcc.simra.services.osm.coloring.services.ElasticIndexService
import de.mcc.simra.services.osm.coloring.services.ReaderService
import de.mcc.simra.services.osm.coloring.services.TransformerService
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

private val LOG: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
@Profile("!test") // do not run during tests
@Component
class Runner(
    val osmColoringConfig: OsmColoringConfig,
    val readerService: ReaderService,
    val transformerService: TransformerService,
    val elasticIndexService: ElasticIndexService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        LOG.info("Reading files from ${osmColoringConfig.geoJsonDir.absolutePath}")
        // to have multiple threads available
        runBlocking(Dispatchers.Default + CoroutineName("Runner")) {
            // start in reverse order to prevent backlog
            launch { elasticIndexService.indexIncomingElasticSegmentsBlocking() }
            launch { transformerService.transformIncomingGeoFeaturesBlocking() }
            launch { readerService.processExistingFiles() }
            LOG.info("Started all coroutines")
        }
    }
}