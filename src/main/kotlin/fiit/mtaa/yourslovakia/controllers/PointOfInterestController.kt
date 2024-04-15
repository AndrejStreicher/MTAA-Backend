package fiit.mtaa.yourslovakia.controllers

import fiit.mtaa.yourslovakia.models.GeoPoint
import fiit.mtaa.yourslovakia.models.PointOfInterest
import fiit.mtaa.yourslovakia.services.PointOfInterestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PointOfInterestController(private val pointOfInterestService: PointOfInterestService) {

    @GetMapping("/points_of_interest")
    fun getPointsOfInterestByLocation(
        @RequestParam latitude: Float,
        @RequestParam longitude: Float,
        @RequestParam maxDistance: Double,
        @RequestParam monumentTypes: List<String>
    ): ResponseEntity<List<PointOfInterest>> {
        val pointsOfInterests =
            pointOfInterestService.getMonuments(GeoPoint(latitude, longitude), maxDistance, monumentTypes)
        return if (pointsOfInterests.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(pointsOfInterests)
        }
    }
}