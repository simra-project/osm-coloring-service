package de.mcc.simra.services.osm.coloring.model

import org.springframework.boot.configurationprocessor.json.JSONObject

data class GeoFeature(val textId: String, val feature: JSONObject, val fileName: String)