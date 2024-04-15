package fiit.mtaa.yourslovakia.services

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OverpassAPIServiceTests {

    @Autowired
    lateinit var overpassApiService: OverpassAPIService

    @Test
    fun testDownloadCastles() {
        val castles = overpassApiService.downloadCastles().block()
        assertNotNull(castles, "Castles list should not be null")
        assertFalse(castles.isNullOrEmpty(), "Castles list should not be empty")
    }

    @Test
    fun testDownloadCaves() {
        val caves = overpassApiService.downloadCaves().block()
        assertNotNull(caves, "Caves list should not be null")
        assertFalse(caves.isNullOrEmpty(), "Caves list should not be empty")
    }

    @Test
    fun testDownloadMuseums() {
        val museums = overpassApiService.downloadMuseums().block()
        assertNotNull(museums, "Museums list should not be null")
        assertFalse(museums.isNullOrEmpty(), "Museums list should not be empty")
    }

    @Test
    fun testDownloadNationalParks() {
        val nationalParks = overpassApiService.downloadNationalParks().block()
        assertNotNull(nationalParks, "National parks list should not be null")
        assertFalse(nationalParks.isNullOrEmpty(), "National parks list should not be empty")
    }

    @Test
    fun testDownloadLakes() {
        val lakes = overpassApiService.downloadLakes().block()
        assertNotNull(lakes, "Lakes list should not be null")
        assertFalse(lakes.isNullOrEmpty(), "Lakes list should not be empty")
    }

    @Test
    fun testDownloadReservoirs() {
        val reservoirs = overpassApiService.downloadReservoirs().block()
        assertNotNull(reservoirs, "Reservoirs list should not be null")
        assertFalse(reservoirs.isNullOrEmpty(), "Reservoirs list should not be empty")
    }

    @Test
    fun testDownloadWorships() {
        val worships = overpassApiService.downloadWorships().block()
        assertNotNull(worships, "Worship places list should not be null")
        assertFalse(worships.isNullOrEmpty(), "Worship places list should not be empty")
    }

    @Test
    fun testDownloadTheatres() {
        val theatres = overpassApiService.downloadTheatres().block()
        assertNotNull(theatres, "Theatres list should not be null")
        assertFalse(theatres.isNullOrEmpty(), "Theatres list should not be empty")
    }

    @Test
    fun testDownloadMonasteries() {
        val monasteries = overpassApiService.downloadMonasteries().block()
        assertNotNull(monasteries, "Monasteries list should not be null")
        assertFalse(monasteries.isNullOrEmpty(), "Monasteries list should not be empty")
    }

    @Test
    fun testDownloadZoos() {
        val zoos = overpassApiService.downloadZoos().block()
        assertNotNull(zoos, "Zoos list should not be null")
        assertFalse(zoos.isNullOrEmpty(), "Zoos list should not be empty")
    }

    @Test
    fun testDownloadPeaks() {
        val peaks = overpassApiService.downloadPeaks().block()
        assertNotNull(peaks, "Peaks list should not be null")
        assertFalse(peaks.isNullOrEmpty(), "Peaks list should not be empty")
    }

    @Test
    fun testDownloadSaddles() {
        val saddles = overpassApiService.downloadSaddles().block()
        assertNotNull(saddles, "Saddles list should not be null")
        assertFalse(saddles.isNullOrEmpty(), "Saddles list should not be empty")
    }

    @Test
    fun testDownloadAllGenerics() {
        val allGeneric = overpassApiService.downloadAllPOIsExceptSpecialCategories().block()
        assertNotNull(allGeneric, "Saddles list should not be null")
        assertFalse(allGeneric.isNullOrEmpty(), "Saddles list should not be empty")
    }
}
