package de.mcc.simra.services.osm.coloring.config

import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import de.mcc.simra.services.osm.coloring.model.GeoFeature
import kotlinx.coroutines.channels.Channel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class OsmColoringConfig(osmColoringConfigData: OsmColoringConfigData) {

    val geoJsonDir = File(osmColoringConfigData.geoJsonDirPath).apply {
        require(this.isDirectory) { "Supplied GeoJson directory ${this.absolutePath} is not a directory."}
    }

    @Bean
    fun geoJsonDir(): File {
        return geoJsonDir
    }

    @Bean
    fun geoFeatureChannel(): Channel<GeoFeature> {
        return Channel(10000)
    }

    @Bean
    fun elasticSegmentChannel(): Channel<ElasticSegment> {
        return Channel(10000)
    }

    @Bean
    fun elasticMarkerChannel(): Channel<ElasticSegment> {
        return Channel(1000)
    }

}