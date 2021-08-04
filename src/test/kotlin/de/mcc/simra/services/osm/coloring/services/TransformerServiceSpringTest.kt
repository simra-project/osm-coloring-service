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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.AssertionErrors.*

private val LOG: Logger = LogManager.getLogger()

/**
 * This class contains a similar test to [TransformerServiceTest] but starts the complete SpringContext.
 * Such as setup should better be used for integration tests than a unit tests since one has to recreate the context
 * if one has to reset fields. For example, if one needs new channels, the complete context has to be restarted.
 *
 * Thus, this class should be consider of being an example for how to run a SpringBootTest that overwrites properties
 * and uses multiple beans.
 */
@ExperimentalCoroutinesApi
@ActiveProfiles("test") // do not run the runner
@SpringBootTest(properties = ["osm-coloring-service.geo-json-dir-path=src/test/resources/transformer"])
class TransformerServiceSpringTest {

    @Autowired
    lateinit var readerService: ReaderService

    @Autowired
    lateinit var transformerService: TransformerService

    @Autowired
    lateinit var geoFeatureChannel: Channel<GeoFeature>

    @Autowired
    lateinit var elasticSegmentChannel: Channel<ElasticSegment>

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            readerService.processExistingFiles()
        }
        LOG.info("Finished beforeEach")
    }

    @AfterEach
    fun afterEach() {
        geoFeatureChannel.close()
        elasticSegmentChannel.close()
        LOG.info("Finished afterEach")
    }

    @Test
    fun transformTest() = runBlocking {
        launch {
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

}