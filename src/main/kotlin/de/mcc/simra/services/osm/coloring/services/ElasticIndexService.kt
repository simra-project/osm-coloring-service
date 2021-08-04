package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.chunked
import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import de.mcc.simra.services.osm.coloring.model.GeoFeature
import de.mcc.simra.services.osm.coloring.repositories.ElasticRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

private val LOG: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
@Service
class ElasticIndexService(val elasticRepository: ElasticRepository, val elasticSegmentChannel: Channel<ElasticSegment>) {

    suspend fun indexIncomingElasticSegmentsBlocking() {
        while (!elasticSegmentChannel.isClosedForReceive) {
            // we want to batch 50 elements, but never wait more than a second
            val chunk = elasticSegmentChannel.chunked(50, 1000)
            index(chunk)
        }
        LOG.info("GeoFeatureChannel was closed, blocking function ends.")
    }

    fun index(chunk: List<ElasticSegment>) {
        elasticRepository.saveAll(chunk)
        LOG.info("Indexed ${chunk.size} ElasticSegments")
    }


}