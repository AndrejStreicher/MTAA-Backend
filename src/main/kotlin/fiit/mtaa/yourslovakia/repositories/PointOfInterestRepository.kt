package fiit.mtaa.yourslovakia.repositories

import fiit.mtaa.yourslovakia.DatabaseManager
import fiit.mtaa.yourslovakia.models.*
import jakarta.transaction.Transactional
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository

@Repository
class PointOfInterestRepository {
    fun getAllCastles(): List<Castle> {
        return DatabaseManager.database.from(Castles).select().map { row ->
            Castle(
                id = row[Castles.id]!!,
                name = row[Castles.name]!!,
                latitude = row[Castles.latitude]!!,
                longitude = row[Castles.longitude]!!,
                wikidataCode = row[Castles.wikidataCode],
                castleType = row[Castles.castleType],
            )
        }
    }

    fun getAllPeaks(): List<Peak> {
        return DatabaseManager.database.from(Peaks).select().map { row ->
            Peak(
                id = row[Peaks.id]!!,
                name = row[Peaks.name]!!,
                latitude = row[Peaks.latitude]!!,
                longitude = row[Peaks.longitude]!!,
                wikidataCode = row[Peaks.wikidataCode],
                elevation = row[Peaks.elevation]
            )
        }
    }

    fun getAllPlacesOfWorship(): List<PlaceOfWorship> {
        return DatabaseManager.database.from(PlacesOfWorshipSchemaClass).select().map { row ->
            PlaceOfWorship(
                id = row[PlacesOfWorshipSchemaClass.id]!!,
                name = row[PlacesOfWorshipSchemaClass.name]!!,
                latitude = row[PlacesOfWorshipSchemaClass.latitude]!!,
                longitude = row[PlacesOfWorshipSchemaClass.longitude]!!,
                wikidataCode = row[PlacesOfWorshipSchemaClass.wikidataCode],
                religion = row[PlacesOfWorshipSchemaClass.religion],
                denomination = row[PlacesOfWorshipSchemaClass.denomination]
            )
        }
    }

    fun getAllGenericPointsOfInterest(): List<GenericPointOfInterestModel> {
        return DatabaseManager.database.from(GenericPointsOfInterest).select().map { row ->
            GenericPointOfInterestModel(
                id = row[GenericPointsOfInterest.id]!!,
                name = row[GenericPointsOfInterest.name]!!,
                latitude = row[GenericPointsOfInterest.latitude]!!,
                longitude = row[GenericPointsOfInterest.longitude]!!,
                wikidataCode = row[GenericPointsOfInterest.wikidataCode],
                type = row[GenericPointsOfInterest.type]!!
            )
        }
    }

    @Transactional
    fun insertCastle(castle: Castle) {
        DatabaseManager.database.insert(Castles) {
            set(it.id, castle.id)
            set(it.name, castle.name)
            set(it.latitude, castle.latitude)
            set(it.longitude, castle.longitude)
            set(it.wikidataCode, castle.wikidataCode)
            set(it.castleType, castle.castleType)
        }
    }

    @Transactional
    fun insertPeak(peak: Peak) {
        DatabaseManager.database.insert(Peaks) {
            set(it.id, peak.id)
            set(it.name, peak.name)
            set(it.latitude, peak.latitude)
            set(it.longitude, peak.longitude)
            set(it.wikidataCode, peak.wikidataCode)
            set(it.elevation, peak.elevation)
        }
    }

    @Transactional
    fun insertPlaceOfWorship(place: PlaceOfWorship) {
        DatabaseManager.database.insert(PlacesOfWorshipSchemaClass) {
            set(it.id, place.id)
            set(it.name, place.name)
            set(it.latitude, place.latitude)
            set(it.longitude, place.longitude)
            set(it.wikidataCode, place.wikidataCode)
            set(it.religion, place.religion)
            set(it.denomination, place.denomination)
        }
    }

    @Transactional
    fun insertGenericPointOfInterest(poi: GenericPointOfInterestModel) {
        DatabaseManager.database.insert(GenericPointsOfInterest) {
            set(it.id, poi.id)
            set(it.name, poi.name)
            set(it.latitude, poi.latitude)
            set(it.longitude, poi.longitude)
            set(it.wikidataCode, poi.wikidataCode)
            set(it.type, poi.type)
        }
    }

    @Transactional
    // Update methods
    fun updateCastle(castle: Castle) {
        DatabaseManager.database.update(Castles) {
            set(it.id, castle.id)
            set(it.name, castle.name)
            set(it.latitude, castle.latitude)
            set(it.longitude, castle.longitude)
            set(it.wikidataCode, castle.wikidataCode)
            set(it.castleType, castle.castleType)
            where { it.id eq castle.id }
        }
    }

    @Transactional
    fun updatePlaceOfWorship(placeOfWorship: PlaceOfWorship) {
        DatabaseManager.database.update(PlacesOfWorshipSchemaClass) {
            set(it.id, placeOfWorship.id)
            set(it.name, placeOfWorship.name)
            set(it.latitude, placeOfWorship.latitude)
            set(it.longitude, placeOfWorship.longitude)
            set(it.wikidataCode, placeOfWorship.wikidataCode)
            set(it.religion, placeOfWorship.religion)
            set(it.denomination, placeOfWorship.denomination)
            where { it.id eq placeOfWorship.id }
        }
    }

    @Transactional
    fun updatePeak(peak: Peak) {
        DatabaseManager.database.update(Peaks) {
            set(it.id, peak.id)
            set(it.name, peak.name)
            set(it.latitude, peak.latitude)
            set(it.longitude, peak.longitude)
            set(it.wikidataCode, peak.wikidataCode)
            set(it.elevation, peak.elevation)
            where { it.id eq peak.id }
        }
    }

    @Transactional
    fun updateGenericPointOfInterest(generic: GenericPointOfInterestModel) {
        DatabaseManager.database.update(Peaks) {
            set(it.id, generic.id)
            set(it.name, generic.name)
            set(it.latitude, generic.latitude)
            set(it.longitude, generic.longitude)
            set(it.wikidataCode, generic.wikidataCode)
            where { it.id eq generic.id }
        }
    }

    @Transactional
    // Delete methods
    fun deleteCastle(castle: Castle) {
        DatabaseManager.database.delete(Castles) {
            it.id eq castle.id
        }
    }

    @Transactional
    fun deletePeak(peak: Peak) {
        DatabaseManager.database.delete(Peaks) {
            it.id eq peak.id
        }
    }

    @Transactional
    fun deletePlaceOfWorship(placeOfWorship: PlaceOfWorship) {
        DatabaseManager.database.delete(PlacesOfWorshipSchemaClass) {
            it.id eq placeOfWorship.id
        }
    }

    @Transactional
    fun deleteGenericPointOfInterest(genericPointOfInterest: GenericPointOfInterestModel) {
        DatabaseManager.database.delete(GenericPointsOfInterest) {
            it.id eq genericPointOfInterest.id
        }
    }


}