package de.mcc.simra.services.osm.coloring

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

// set profile so that we can skip components that should not be run during tests (e.g., Runner.kt)
@ActiveProfiles("test")
@SpringBootTest
class OsmColoringApplicationTests {

	@Test
	fun contextLoads() {
	}

}
