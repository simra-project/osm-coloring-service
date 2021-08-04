package de.mcc.simra.services.osm.coloring.services

import de.mcc.simra.services.osm.coloring.model.GeoFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.*
import java.io.File

private val LOG: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
class ReaderServiceTest {

    private lateinit var channel: Channel<GeoFeature>

    @BeforeEach
    fun beforeEach() {
        channel = Channel(1)
    }

    @AfterEach
    fun afterEach() {
        channel.close()
    }

    @Test
    fun testNoFiles() = runBlocking {
        val readerService = ReaderService(File("src/test/resources/reader/no_files"), channel)
        readerService.processExistingFiles()
        assertTrue("Channel was not empty", channel.isEmpty)
    }

    @Test
    fun testTwoFiles() = runBlocking {
        val readerService = ReaderService(File("src/test/resources/reader/two_files"), channel)
        launch { readerService.processExistingFiles() }
        for (i in 0 until 5) {
            // we expect five elements
            receiveElementNotNull()
        }
        assertTrue("There were more than five elements in channel", channel.isEmpty)
    }

    @Test
    fun testMalformatted() = runBlocking {
        val readerService = ReaderService(File("src/test/resources/reader/malformatted"), channel)
        launch { readerService.processExistingFiles() }
        // we expect no element since both files are malformatted
        assertTrue("Channel was not empty", channel.isEmpty)

    }

    private suspend fun receiveElementNotNull() {
        val result = withTimeoutOrNull(1000L) {
            channel.receive()
        }
        LOG.debug("Received $result")
        assertNotNull("There should have been an element in the channel", result)
    }

}