package de.mcc.simra.services.osm.coloring

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertEquals

private val LOG: Logger = LogManager.getLogger()


class UtilTest {

    @Test
    fun md5Hash() {
        val text = "[3945, 7047, 7050, 7674, 13687].0"
        val md5 = text.toMD5()
        assertEquals("Calculated hash is not as expected", "636b43e019a733f8ef7ae23bbe5a4275", md5)
    }

}