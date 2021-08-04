package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.chunked
import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import de.mcc.simra.services.osm.coloring.model.GeoFeature
import de.mcc.simra.services.osm.coloring.repositories.ElasticRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

private val LOG: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
@Service
class ElasticIndexService(val elasticRepository: ElasticRepository, val elasticSegmentChannel: Channel<ElasticSegment>) {

    suspend fun indexIncomingElasticSegmentsBlocking() = coroutineScope {
        while (!elasticSegmentChannel.isClosedForReceive) {
            // we want to batch 50 elements, but never wait more than a second
            val chunk = elasticSegmentChannel.chunked(1000, 1000)
            // we run this in a coroutine since it is IO, one coroutine per successful chunk
            // TODO maximum of four parallel jobs
            launch(Dispatchers.Default + CoroutineName("ElasticIndexer")) {
                index(chunk)
            }
        }
        LOG.info("GeoFeatureChannel was closed, blocking function ends.")
    }

    fun index(chunk: List<ElasticSegment>) {
        if (chunk.isNotEmpty()) {
            elasticRepository.saveAll(chunk)
            LOG.info("Indexed ${chunk.size} ElasticSegments")
        } else {
            LOG.debug("Chunk was empty, not running index operation")
        }
    }


}