package fiit.mtaa.yourslovakia.models

import org.ktorm.schema.Table
import org.ktorm.schema.float
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Users : Table<Nothing>("users") {
    val id = long("id").primaryKey()
    val email = varchar("email")
    val password = varchar("password")
}

sealed class GenericPointOfInterestSchemaClass(tableName: String) : Table<Nothing>(tableName) {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val latitude = float("latitude")
    val longitude = float("longitude")
    val wikidataCode = varchar("wikidata_code")
}

object GenericPointsOfInterest : GenericPointOfInterestSchemaClass("generic_points_of_interest") {
    val type = varchar("type")
}

object Castles : GenericPointOfInterestSchemaClass("castles") {
    val castleType = varchar("castle_type")
}

object PlacesOfWorshipSchemaClass : GenericPointOfInterestSchemaClass("places_of_worship") {
    val religion = varchar("religion")
    val denomination = varchar("denomination")
}

object Peaks : GenericPointOfInterestSchemaClass("peaks") {
    val elevation = float("elevation")
}