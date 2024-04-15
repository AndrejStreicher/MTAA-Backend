package fiit.mtaa.yourslovakia.models

data class Peak(
    override val id: Long,
    override val name: String,
    override val location: GeoPoint,
    override val wikidataCode: String?,
    val elevation: Float?
) : PointOfInterest

