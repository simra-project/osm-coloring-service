package de.mcc.simra.services.osm.coloring.repositories

import de.mcc.simra.services.osm.coloring.config.OsmColoringConfigData
import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertTrue

private val LOG: Logger = LogManager.getLogger()

@ActiveProfiles("test") // do not run the runner
@SpringBootTest(properties = ["osm-coloring-service.elastic-segment-index-name=segments-test"])
class ElasticRepositoryTest {

    @Autowired
    lateinit var elasticRepository: ElasticRepository

    @Autowired
    lateinit var elasticsearchTemplate: ElasticsearchOperations

    @Autowired
    lateinit var osmColoringConfigData: OsmColoringConfigData

    @BeforeEach
    fun beforeEach() {
        val index = elasticsearchTemplate.indexOps(IndexCoordinates.of(osmColoringConfigData.elasticSegmentIndexName))

        if (!index.exists()) {
            val success = elasticsearchTemplate.indexOps(IndexCoordinates.of(osmColoringConfigData.elasticSegmentIndexName)).create()
            LOG.info("Created index ${index.indexCoordinates.indexName}: $success")
        } else {
            LOG.info("Index already existed")
        }

    }

    @AfterEach
    fun afterEach() {
        val index = elasticsearchTemplate.indexOps(IndexCoordinates.of(osmColoringConfigData.elasticSegmentIndexName))

        val success = index.delete()
        LOG.info("Deleted index ${index.indexCoordinates.indexName}: $success")
    }

    @Test
    fun storeOne() {
        val elasticSegment = ElasticSegment(
            "id", "textId", "region", "street", 1, 1.0, 4.8, GeoJsonPolygon.ofGeoPoints(
                listOf(
                    GeoPoint(10.0, 10.0), GeoPoint(10.0, 11.0),
                    GeoPoint(11.0, 11.0), GeoPoint(10.0, 10.0)
                )
            )
        )

        elasticRepository.save(elasticSegment)
        val queried = elasticRepository.findById("id")
        assertTrue("Query did not return a result", queried.isPresent)
        assertEquals("Stored and queried segment are not the same", elasticSegment, queried.get())
        LOG.info("Storing and querying successful")
    }

}