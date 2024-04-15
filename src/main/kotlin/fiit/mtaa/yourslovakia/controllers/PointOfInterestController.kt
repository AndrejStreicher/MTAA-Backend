package fiit.mtaa.yourslovakia.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PointOfInterestController {
    @GetMapping("/pointsofinterest")
    fun getPointsOfInterestByLocation(latitude: Float, longitude: Float) {

    }
}