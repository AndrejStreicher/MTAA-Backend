package fiit.mtaa.yourslovakia.services

import fiit.mtaa.yourslovakia.DatabaseManager
import fiit.mtaa.yourslovakia.models.*
import fiit.mtaa.yourslovakia.repositories.PointOfInterestRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Service
class PointOfInterestService(
    private val repository: PointOfInterestRepository,
    private val overpass: OverpassAPIService
) {

    fun getMonuments(
        userLocation: GeoPoint,
        maxDistance: Double,
        monumentTypes: List<String>
    ): List<PointOfInterest> {
        var castleQuery = """
WITH DistanceData AS (
    SELECT 
        *, 
        ST_Distance(
            ST_Transform(ST_SetSRID(ST_MakePoint(${userLocation.longitude}, ${userLocation.latitude}), 4326), 3857), 
            ST_Transform(location::geometry, 3857)  
        ) AS distance,
        ST_X(ST_Transform(location::geometry, 4326)) AS longitude,
        ST_Y(ST_Transform(location::geometry, 4326)) AS latitude
    FROM 
        castles
)
SELECT * 
FROM DistanceData
WHERE distance < ${maxDistance}
ORDER BY distance ASC;

    """
        var peakQuery = """
WITH DistanceData AS (
    SELECT 
        *, 
        ST_Distance(
            ST_Transform(ST_SetSRID(ST_MakePoint(${userLocation.longitude}, ${userLocation.latitude}), 4326), 3857), 
            ST_Transform(location::geometry, 3857)  -- Cast to geometry if needed
        ) AS distance,
        ST_X(ST_Transform(location::geometry, 4326)) AS longitude,
        ST_Y(ST_Transform(location::geometry, 4326)) AS latitude
    FROM 
        peaks
)
SELECT * 
FROM DistanceData
WHERE distance < ${maxDistance}
ORDER BY distance ASC;

    """
        var placesOfWorshipQuery = """
WITH DistanceData AS (
    SELECT 
        *, 
        ST_Distance(
            ST_Transform(ST_SetSRID(ST_MakePoint(${userLocation.longitude}, ${userLocation.latitude}), 4326), 3857), 
            ST_Transform(location::geometry, 3857)  -- Cast to geometry if needed
        ) AS distance,
        ST_X(ST_Transform(location::geometry, 4326)) AS longitude,
        ST_Y(ST_Transform(location::geometry, 4326)) AS latitude
    FROM 
        places_of_worship
)
SELECT * 
FROM DistanceData
WHERE distance < ${maxDistance}
ORDER BY distance ASC;


    """
        var returnedMonuments = mutableListOf<PointOfInterest>()
        if (monumentTypes.contains("castle")) {
            val castlesList = DatabaseManager.database.useConnection<List<Castle>> { conn ->
                conn.prepareStatement(castleQuery).use { statement ->
                    val resultSet = statement.executeQuery()
                    val results = mutableListOf<Castle>()
                    while (resultSet.next()) {
                        val poi = Castle(
                            id = resultSet.getLong("id"),
                            name = resultSet.getString("name"),
                            location = GeoPoint(resultSet.getFloat("latitude"), resultSet.getFloat("longitude")),
                            wikidataCode = resultSet.getString("wikidata_code"),
                            castleType = resultSet.getString("castle_type")
                        )
                        results.add(poi)
                    }
                    results
                }
            }
            returnedMonuments.addAll(castlesList)
        }
        if (monumentTypes.contains("peak")) {
            val peaksList = DatabaseManager.database.useConnection<List<Peak>> { conn ->
                conn.prepareStatement(peakQuery).use { statement ->
                    val resultSet = statement.executeQuery()
                    val results = mutableListOf<Peak>()
                    while (resultSet.next()) {
                        val poi = Peak(
                            id = resultSet.getLong("id"),
                            name = resultSet.getString("name"),
                            location = GeoPoint(resultSet.getFloat("latitude"), resultSet.getFloat("longitude")),
                            wikidataCode = resultSet.getString("wikidata_code"),
                            elevation = resultSet.getFloat("elevation")
                        )
                        results.add(poi)
                    }
                    results
                }
            }
            returnedMonuments.addAll(peaksList)
        }
        if (monumentTypes.contains("placesOfWorship")) {
            val placeOfWorshipList = DatabaseManager.database.useConnection<List<PlaceOfWorship>> { conn ->
                conn.prepareStatement(placesOfWorshipQuery).use { statement ->
                    val resultSet = statement.executeQuery()
                    val results = mutableListOf<PlaceOfWorship>()
                    while (resultSet.next()) {
                        val poi = PlaceOfWorship(
                            id = resultSet.getLong("id"),
                            name = resultSet.getString("name"),
                            location = GeoPoint(resultSet.getFloat("latitude"), resultSet.getFloat("longitude")),
                            wikidataCode = resultSet.getString("wikidata_code"),
                            religion = resultSet.getString("religion"),
                            denomination = resultSet.getString("denomination")
                        )
                        results.add(poi)
                    }
                    results
                }
            }
            returnedMonuments.addAll(placeOfWorshipList)
        }
        for (typeI in monumentTypes) {
            if (typeI.equals("castle") || typeI.equals("peak") || typeI.equals("place_of_worship")) {
                continue
            }
            var genericQuery = """
WITH DistanceData AS (
    SELECT 
        *,
        ST_Distance(
            ST_Transform(ST_SetSRID(ST_MakePoint(${userLocation.longitude}, ${userLocation.latitude}), 4326), 3857), 
            ST_Transform(location::geometry, 3857)
        ) AS distance,
        ST_X(ST_Transform(location::geometry, 4326)) AS longitude,
        ST_Y(ST_Transform(location::geometry, 4326)) AS latitude
    FROM 
        generic_points_of_interest
)
SELECT * 
FROM DistanceData
WHERE distance < ${maxDistance} AND type = '${typeI}'
ORDER BY distance ASC;


    """
            val genericPOIsList = DatabaseManager.database.useConnection<List<GenericPointOfInterestModel>> { conn ->
                conn.prepareStatement(genericQuery).use { statement ->
                    val resultSet = statement.executeQuery()
                    val results = mutableListOf<GenericPointOfInterestModel>()
                    while (resultSet.next()) {
                        val poi = GenericPointOfInterestModel(
                            id = resultSet.getLong("id"),
                            name = resultSet.getString("name"),
                            location = GeoPoint(resultSet.getFloat("latitude"), resultSet.getFloat("longitude")),
                            wikidataCode = resultSet.getString("wikidata_code"),
                            type = resultSet.getString("type")
                        )
                        results.add(poi)
                    }
                    results
                }
            }
            returnedMonuments.addAll(genericPOIsList)
        }
        return returnedMonuments
    }

    fun decodeWKB(wkbHex: String): GeoPoint {
        val bytes = wkbHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.position(5)

        val longitude = byteBuffer.double
        val latitude = byteBuffer.double

        val geoPoint = GeoPoint(convertToFloatSafely(latitude), convertToFloatSafely(longitude))
        return geoPoint
    }

    fun convertToFloatSafely(value: Double): Float {
        return when {
            value > Float.MAX_VALUE -> Float.MAX_VALUE // Clamps to the max float value
            value < -Float.MAX_VALUE -> -Float.MAX_VALUE // Clamps to the min float value
            else -> value.toFloat()
        }
    }

    @Transactional
    @Scheduled(fixedRate = 86400000)
    fun syncDataWithExternalSource() {
        syncCastles()
        syncPlacesOfWorship()
        syncPeaks()
        syncGenericPointsOfInterest()
    }

    private fun syncCastles() {
        val latestCastles = overpass.downloadCastles().block() ?: emptyList()
        val databaseCastles = repository.getAllCastles().associateBy { it.id }

        syncData(
            latestCastles,
            databaseCastles,
            repository::insertCastle,
            repository::updateCastle,
            repository::deleteCastle
        )
    }

    private fun syncPlacesOfWorship() {
        val latestPlacesOfWorship = overpass.downloadWorships().block() ?: emptyList()
        val databasePlacesOfWorship = repository.getAllPlacesOfWorship().associateBy { it.id }

        syncData(
            latestPlacesOfWorship,
            databasePlacesOfWorship,
            repository::insertPlaceOfWorship,
            repository::updatePlaceOfWorship,
            repository::deletePlaceOfWorship
        )
    }

    private fun syncPeaks() {
        val latestPeaks = overpass.downloadPeaks().block() ?: emptyList()
        val databasePeaks = repository.getAllPeaks().associateBy { it.id }

        syncData(latestPeaks, databasePeaks, repository::insertPeak, repository::updatePeak, repository::deletePeak)
    }

    private fun syncGenericPointsOfInterest() {
        val latestGenericPointsOfInterest = overpass.downloadAllPOIsExceptSpecialCategories().block() ?: emptyList()
        val databaseGenericPointsOfInterest = repository.getAllGenericPointsOfInterest().associateBy { it.id }

        syncData(
            latestGenericPointsOfInterest,
            databaseGenericPointsOfInterest,
            repository::insertGenericPointOfInterest,
            repository::updateGenericPointOfInterest,
            repository::deleteGenericPointOfInterest
        )
    }

    private fun <T : PointOfInterest> syncData(
        latestData: List<T>,
        existingData: Map<Long, T>,
        insert: (T) -> Unit,
        update: (T) -> Unit,
        delete: (T) -> Unit
    ) {
        latestData.forEach { newItem ->
            val existingItem = existingData[newItem.id]
            if (existingItem == null) {
                insert(newItem)
            } else if (existingItem != newItem) {
                update(newItem)
            }
        }

        existingData.values.forEach { existingItem ->
            if (latestData.none { it.id == existingItem.id }) {
                delete(existingItem)
            }
        }
    }
}