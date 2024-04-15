package fiit.mtaa.yourslovakia.services

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PointOfInterestServiceTests {

    @Autowired
    lateinit var pointOfInterestService: PointOfInterestService

    @Test
    fun testSyncDataWithExternalSource() {
        pointOfInterestService.syncDataWithExternalSource()
    }
}