package fiit.mtaa.yourslovakia.models

interface PointOfInterest {
    val id: Long
    val name: String
    val location: GeoPoint
    val wikidataCode: String?
}