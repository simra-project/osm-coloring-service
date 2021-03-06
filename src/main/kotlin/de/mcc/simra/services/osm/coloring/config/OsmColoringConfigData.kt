package de.mcc.simra.services.osm.coloring.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
@ConfigurationProperties(prefix = "osm-coloring-service")
class OsmColoringConfigData {
    lateinit var geoJsonDirPath: String

    // fields with default values that can be overwritten by applications.yml
    var elasticHostAndPort: String = "localhost:9200"
    var elasticSegmentIndexName = "segments"

}