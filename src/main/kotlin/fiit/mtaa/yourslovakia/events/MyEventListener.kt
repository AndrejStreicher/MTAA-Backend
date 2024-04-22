package fiit.mtaa.yourslovakia.events

import fiit.mtaa.yourslovakia.controllers.NotificationController
import fiit.mtaa.yourslovakia.models.POIUpdatedEvent
import fiit.mtaa.yourslovakia.models.PointOfInterest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MyEventListener {
    @Autowired
    private lateinit var notificationController: NotificationController

    private fun sendNotificationToClients(updatedPoints: List<PointOfInterest>) {
        notificationController.sendToClients(updatedPoints)
    }

    @EventListener
    fun onPOIUpdate(event: POIUpdatedEvent) {
        sendNotificationToClients(event.updatedPointsOfInterest)
    }

}
