package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.model.ElasticSegment
import de.mcc.simra.services.osm.coloring.model.GeoFeature
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.*
import java.io.File

private val LOG: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
class TransformerServiceTest {

    private lateinit var geoFeatureChannel: Channel<GeoFeature>
    private lateinit var elasticSegmentChannel: Channel<ElasticSegment>

    @BeforeEach
    fun beforeEach() {
        geoFeatureChannel = Channel(10)
        elasticSegmentChannel = Channel(10)

        runBlocking {
            val readerService = ReaderService(File("src/test/resources/transformer"), geoFeatureChannel)
            readerService.processExistingFiles()
        }
        LOG.info("Finished BeforeEach")
    }

    @AfterEach
    fun afterEach() {
        geoFeatureChannel.close()
        elasticSegmentChannel.close()
    }

    @Test
    fun transformTest() = runBlocking {
        launch {
            val transformerService = TransformerService(geoFeatureChannel, elasticSegmentChannel)
            LOG.info("Starting to transform incoming GeoFeature function")
            transformerService.transformIncomingGeoFeaturesBlocking()
            LOG.info("GeoFeature transformation stopped, coroutine ends")
        }


        var result = withTimeoutOrNull(1000L) {
            LOG.info("Waiting for street")
            elasticSegmentChannel.receive()
        }
        assertEquals("Street has wrong id", "636b43e019a733f8ef7ae23bbe5a4275", result?.id)
        LOG.info("Street as expected")

        result = withTimeoutOrNull(1000L) {
            LOG.info("Waiting for intersection")
            elasticSegmentChannel.receive()
        }

        assertEquals("Intersection has wrong id", "33fb6346ef42072247ffd2ee93d6debb", result?.id)
        assertTrue("Elastic segment channel should have been empty", elasticSegmentChannel.isEmpty)
        LOG.info("Intersection as expected")

        // close GeoFeatureChannel since only then above coroutine ends
        geoFeatureChannel.close()

        Unit // we have to return unit
    }

    /**
     * This test an extension function
     */
    @Test
    fun transformToSegmentTest() = runBlocking {
        // receive the street segment
        val street = geoFeatureChannel.receive().transformToSegment()
        assertEquals("id not as expected", "636b43e019a733f8ef7ae23bbe5a4275", street.id)
        assertEquals("textId not as expected", "[3945, 7047, 7050, 7674, 13687].0", street.textId)
        assertEquals("region not as expected", "TwoSegments", street.region)
        assertEquals("type not as expected", "street", street.type)
        assertEquals("rides not as expected", 1, street.rides)
        assertEquals("score not as expected", 1.0, street.score)
        assertTrue("length not as expected", street.length!! - 43.66092995514574 < 0.000001)
        assertTrue(
            "first latitude in shape not as expected",
            street.shape.coordinates[0].coordinates[0].y - 48.00775683624214 < 0.000001
        )
        assertEquals("number of coordinates in shape not as expected", 30, street.shape.coordinates[0].coordinates.size)
        // receive the intersection
        val intersection = geoFeatureChannel.receive().transformToSegment()
        assertEquals("id not as expected", "33fb6346ef42072247ffd2ee93d6debb", intersection.id)
        assertEquals("textId not as expected", "[864955419, 864955574].0", intersection.textId)
        assertEquals("region not as expected", "TwoSegments", intersection.region)
        assertEquals("type not as expected", "intersection", intersection.type)
        assertEquals("rides not as expected", 3, intersection.rides)
        assertEquals("score not as expected", 1.4666666666666668, intersection.score)
        assertNull("length should be null", intersection.length)
        assertTrue(
            "first latitude in shape not as expected",
            intersection.shape.coordinates[0].coordinates[0].y - 48.00792922049365 < 0.000001
        )
        assertEquals(
            "number of coordinates in shape not as expected",
            15,
            intersection.shape.coordinates[0].coordinates.size
        )
    }
}