package fiit.mtaa.yourslovakia.controllers

import fiit.mtaa.yourslovakia.models.GeoPoint
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PointOfInterestController {
    @GetMapping("/pointsofinterest")
    fun getPointsOfInterestByLocation(location: GeoPoint) {

    }
}