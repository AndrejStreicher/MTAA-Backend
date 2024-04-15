package fiit.mtaa.yourslovakia.models

data class Castle(
    override val id: Long,
    override val name: String,
    override val location: GeoPoint,
    override val wikidataCode: String?,
    val castleType: String?
) : PointOfInterest

