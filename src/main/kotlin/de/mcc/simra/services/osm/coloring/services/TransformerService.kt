package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.model.ElasticMarker
import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import de.mcc.simra.services.osm.coloring.model.GeoFeature
import de.mcc.simra.services.osm.coloring.toMD5
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.stereotype.Service

private val LOG: Logger = LogManager.getLogger()

/**
 * This service transforms [GeoFeature] to [ElasticSegment] and [ElasticMarker]
 *
 * TODO Add transformToMarker implementation and tests
 */
@Service
class TransformerService(val geoFeatureChannel: Channel<GeoFeature>, val elasticSegmentChannel: Channel<ElasticSegment>) {

    suspend fun transformIncomingGeoFeaturesBlocking() = coroutineScope {
        for (geoFeature in geoFeatureChannel) {
            launch(CoroutineName("Transformer")) {
                transform(geoFeature)
            }
        }
        LOG.info("GeoFeatureChannel was closed, blocking function ends.")
    }

    suspend fun transform(geoFeature: GeoFeature) {
        // TODO Only when transformToSegment and transformToMarkers is successful, the complete document should be commited
        val elasticSegment = geoFeature.transformToSegment()
        elasticSegmentChannel.send(elasticSegment)
        LOG.debug("Wrote ElasticSegment with textId {}", elasticSegment.textId)
    }

}

fun GeoFeature.transformToSegment() : ElasticSegment {
    val geoFeature = this
    LOG.trace("Transforming $geoFeature")

    val textId = geoFeature.feature.getString("id")
    val id = textId.toMD5()
    val region = geoFeature.fileName.replace("_all", "")

    val properties = geoFeature.feature.getJSONObject("properties")
    val rides = properties.getInt("rides")
    val score = properties.getDouble("score")
    val type = if (properties.has("length")) {
        "street"
    } else {
        "intersection"
    }
    val length = if (type == "street") {
        properties.getDouble("length")
    } else {
        null
    }

    // geoJsonPolygon
    val coordinates = geoFeature.feature
        .getJSONObject("geometry")
        .getJSONArray("coordinates")
        .getJSONArray(0)
    val points = mutableListOf<GeoPoint>()
    for (i in 0 until coordinates.length()) {
        val pointPair = coordinates.getJSONArray(i)
        points.add(GeoPoint(pointPair.getDouble(1), pointPair.getDouble(0)))
    }
    val shape = GeoJsonPolygon.ofGeoPoints(points)

    val segment = ElasticSegment(id, textId, region, type, rides, score, length, shape)
    LOG.trace("Created segment $segment")

    return segment
}

fun GeoFeature.transformToMarkers() : List<ElasticMarker> {
    val geoFeature = this
    TODO("Implement")
}