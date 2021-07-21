package de.mcc.simra.services.osm.coloring.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "osm-coloring-service")
class OsmColoringConfigData {
    lateinit var geoJsonDir: String
}