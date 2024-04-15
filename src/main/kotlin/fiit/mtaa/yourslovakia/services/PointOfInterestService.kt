package fiit.mtaa.yourslovakia.services

import fiit.mtaa.yourslovakia.models.PointOfInterest
import fiit.mtaa.yourslovakia.repositories.PointOfInterestRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PointOfInterestService(
    private val repository: PointOfInterestRepository,
    private val overpass: OverpassAPIService
) {

    @Transactional
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
