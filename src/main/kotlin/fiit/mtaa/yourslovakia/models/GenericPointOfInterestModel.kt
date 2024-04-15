package fiit.mtaa.yourslovakia.models

data class GenericPointOfInterestModel(
    override val id: Long,
    override val name: String,
    override val latitude: Float,
    override val longitude: Float,
    override val wikidataCode: String?,
    val type: String,
) : PointOfInterest
