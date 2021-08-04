package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.model.GeoFeature
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.configurationprocessor.json.JSONException
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Service
import java.io.File

private val LOG: Logger = LogManager.getLogger()

/**
 * This service reads in all files from the defined [geoJsonDir] and writes individual [GeoFeature] into the [geoFeatureChannel]
 *
 * TODO Creates a watcher service that monitors file changes.
 *  - Create/modify: write features into channel
 *  - Delete: log name of file that got deleted
 */
@Service
class ReaderService(val geoJsonDir: File, val geoFeatureChannel: Channel<GeoFeature>) {

    suspend fun processExistingFiles() = coroutineScope {
        val files = getCurrentFiles()
        // read in files in parallel
        val jobs = mutableListOf<Job>()
        for (file in files) {
            // use IO dispatcher since file operation
            jobs.add(launch(Dispatchers.IO + CoroutineName(file.nameWithoutExtension)) { extractGeoJsonFeatures(file) })
        }
        jobs.joinAll()
        LOG.info("Processed all ${files.size} GeoJson files.")
    }

    /**
     * Get all json files in GeoJsons directory
     */
    private fun getCurrentFiles(): List<File> {
        val files = geoJsonDir.listFiles()?.toList() ?: emptyList()
        val actualFiles = files.filter { it.isFile }.filter { it.extension == "json" }
        LOG.debug("Found ${actualFiles.size} files in GeoJsons directory")
        return actualFiles
    }

    /**
     * Extract GeoJson features from file and write into channel
     */
    private suspend fun extractGeoJsonFeatures(file: File) {
        LOG.trace("Processing ${file.name}")
        val jsonString = file.readText()
        val jsonObject = try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject["type"] != "FeatureCollection") {
                LOG.error("${file.name} does not contain a FeatureCollection, skipping file")
                return
            }
            jsonObject // return object
        } catch (e: JSONException) {
            LOG.error("${file.name} is not a valid json, skipping file")
            return
        }

        val features = jsonObject.getJSONArray("features")
        LOG.trace("Found ${features.length()} features in ${file.name}")
        for (i in 0 until features.length()) {
            val o = features.getJSONObject(i)
            val textId = o.getString("id")
            // write features in channel
            geoFeatureChannel.send(GeoFeature(textId, o, file.nameWithoutExtension))
            LOG.debug("Wrote GeoFeature with textId {}", textId)
        }
    }

}