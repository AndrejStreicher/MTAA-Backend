package fiit.mtaa.yourslovakia.models

import org.ktorm.schema.*
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

object GeoPointSqlType : SqlType<GeoPoint>(Types.OTHER, "geography") {
    override fun doGetResult(rs: ResultSet, index: Int): GeoPoint? {
        val point = rs.getString(index)  // Assume format: 'POINT(lon lat)'
        return point.let {
            val parts = it.removePrefix("POINT(").removeSuffix(")").split(" ")
            GeoPoint(parts[1].toFloat(), parts[0].toFloat())
        }
    }

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: GeoPoint) {
        ps.setObject(index, "POINT(${parameter.longitude} ${parameter.latitude})", Types.OTHER)
    }
}

object Users : Table<Nothing>("users") {
    val id = long("id").primaryKey()
    val email = varchar("email")
    val password = varchar("password")
}

sealed class GenericPointOfInterestSchemaClass(tableName: String) : Table<Nothing>(tableName) {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val location = registerColumn("location", GeoPointSqlType)
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