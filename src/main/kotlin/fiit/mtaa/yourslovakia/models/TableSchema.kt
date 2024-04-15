package fiit.mtaa.yourslovakia.models

import org.ktorm.schema.*
import org.postgresql.geometric.PGpoint
import org.postgresql.util.PGobject
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

object GeoPointSqlType : SqlType<GeoPoint>(Types.OTHER, "geography") {
    override fun doGetResult(rs: ResultSet, index: Int): GeoPoint? {
        val pgObject = rs.getObject(index) as? PGobject
        return pgObject?.value?.let {
            parseWKBToPoint(it)
        }
    }

    private fun parseWKBToPoint(wkb: String): GeoPoint? {
        val bytes = hexStringToByteArray(wkb)
        val longitude = extractFloat(bytes, 5)
        val latitude = extractFloat(bytes, 13)
        return GeoPoint(latitude, longitude)
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun extractFloat(bytes: ByteArray, offset: Int): Float {
        var longBits: Long = 0
        var i = 0
        while (i < 8) {
            longBits = longBits shl 8 or (bytes[offset + i].toLong() and 0xff)
            i++
        }
        return java.lang.Double.longBitsToDouble(longBits).toFloat()
    }

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: GeoPoint) {
        val point = PGpoint(parameter.longitude.toDouble(), parameter.latitude.toDouble())
        val pgObject = PGobject()
        pgObject.type = "geometry"
        pgObject.value = "SRID=4326;POINT(${parameter.longitude} ${parameter.latitude})"
        ps.setObject(index, pgObject)
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