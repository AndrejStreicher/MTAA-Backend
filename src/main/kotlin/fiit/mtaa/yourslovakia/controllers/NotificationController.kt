package fiit.mtaa.yourslovakia.controllers

import fiit.mtaa.yourslovakia.models.PointOfInterest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@RestController
class NotificationController {
    private val emitter: SseEmitter = SseEmitter(Long.MAX_VALUE)

    @GetMapping("/notifications")
    fun subscribeToNotifications(): SseEmitter {
        return emitter
    }

    fun sendToClients(data: List<PointOfInterest>) {
        try {
            emitter.send(SseEmitter.event().name("update").data(data))
        } catch (e: IOException) {
            emitter.completeWithError(e)
        }
    }
}
