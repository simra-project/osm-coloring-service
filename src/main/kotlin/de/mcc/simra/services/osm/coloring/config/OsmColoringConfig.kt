package de.mcc.simra.services.osm.coloring.config

import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class OsmColoringConfig(osmColoringConfigData: OsmColoringConfigData) {

    val geoJsonDir = File(osmColoringConfigData.geoJsonDir).apply {
        require(this.isDirectory) { "Supplied GeoJson directory ${this.absolutePath} is not a directory."}
    }

}