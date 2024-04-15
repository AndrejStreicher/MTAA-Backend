package fiit.mtaa.yourslovakia.models

interface PointOfInterest {
    val id: Long
    val name: String
    val latitude: Float
    val longitude: Float
    val wikidataCode: String?
}