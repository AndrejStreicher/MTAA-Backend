package fiit.mtaa.yourslovakia.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import fiit.mtaa.yourslovakia.models.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OverpassAPIService(private val webClient: WebClient) {
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val baseUrl = "http://overpass-api.de/api/interpreter"

    private fun buildQuery(type: String, attributes: Map<String, String>): String {
        val attributeString = attributes.entries.joinToString("") { "[\"${it.key}\"=\"${it.value}\"]" }
        return """
            [out:json][timeout:25];
            area["ISO3166-1"="SK"]->.searchArea;
            nwr$type$attributeString(area.searchArea);
            out center;
        """.trimIndent()
    }

    private val castlesQuery: String = buildQuery("[\"historic\"=\"castle\"]", mapOf("name" to ""))
    private val cavesQuery: String = buildQuery("[\"natural\"=\"cave_entrance\"]", mapOf("name" to ""))
    private val museumsQuery: String = buildQuery("[\"tourism\"=\"museum\"]", mapOf("name" to ""))
    private val nationalParkQuery: String = buildQuery("[\"boundary\"=\"national_park\"]", mapOf("name" to ""))
    private val lakesQuery: String = buildQuery("[\"water\"=\"lake\"]", mapOf("name" to ""))
    private val reservoirsQuery: String = buildQuery("[\"water\"=\"reservoir\"]", mapOf("name" to ""))
    private val worshipsQuery: String =
        buildQuery("[\"amenity\"=\"place_of_worship\"]", mapOf("name" to "", "religion" to "", "denomination" to ""))
    private val theatresQuery: String = buildQuery("[\"amenity\"=\"theatre\"]", mapOf("name" to ""))
    private val monasteriesQuery: String = buildQuery("[\"building\"=\"monastery\"]", mapOf("name" to ""))
    private val zoosQuery: String = buildQuery("[\"tourism\"=\"zoo\"]", mapOf("name" to ""))
    private val peaksQuery: String = buildQuery("[\"natural\"=\"peak\"]", mapOf("name" to ""))
    private val saddlesQuery: String = buildQuery("[\"natural\"=\"saddle\"]", mapOf("name" to ""))

    fun downloadCastles(): Mono<List<Castle>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(castlesQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { parseCastles(it) } // Assuming parseCastles parses the API response to a list of Castle objects
    }

    fun downloadWorships(): Mono<List<PlaceOfWorship>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(worshipsQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { parseWorships(it) }
    }

    fun downloadCaves(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(cavesQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "cave"
                )
            }
    }

    fun downloadMuseums(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(museumsQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "museum"
                )
            }
    }

    fun downloadNationalParks(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(nationalParkQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "national_park"
                )
            }
    }

    fun downloadLakes(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(lakesQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "lake"
                )
            }
    }

    fun downloadReservoirs(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(reservoirsQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "reservoir"
                )
            }
    }

    fun downloadTheatres(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(theatresQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "theatre"
                )
            }
    }

    fun downloadMonasteries(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(monasteriesQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "monastery"
                )
            }
    }

    fun downloadZoos(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(zoosQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "zoo"
                )
            }
    }

    fun downloadPeaks(): Mono<List<Peak>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(peaksQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parsePeaks(it)
            }
    }

    fun downloadSaddles(): Mono<List<GenericPointOfInterestModel>> {
        return webClient.post()
            .uri(baseUrl)
            .bodyValue(saddlesQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                parseGeneric(
                    it,
                    "saddle"
                )
            }
    }


    fun downloadAllPOIsExceptSpecialCategories(): Mono<List<GenericPointOfInterestModel>> {
        return Mono.zip(
            listOf(
                downloadCaves(),
                downloadMuseums(),
                downloadNationalParks(),
                downloadLakes(),
                downloadReservoirs(),
                downloadTheatres(),
                downloadMonasteries(),
                downloadZoos(),
                downloadSaddles()
            )
        ) { results ->
            results.map { it as List<GenericPointOfInterestModel> }.flatten()
        }
    }


    private fun parseCastles(response: String): List<Castle> {
        val root = objectMapper.readTree(response)
        val elements = root["elements"] ?: return emptyList() // Early return if "elements" is missing

        return elements.mapNotNull { element ->
            val tags = element["tags"] ?: return@mapNotNull null
            val center = element["center"]
            if (tags["name"] == null || element["lat"] == null || element["lon"] == null || tags["castle_type"] == null) {
                return@mapNotNull null  // Skip this element if essential fields are missing
            }
            Castle(
                id = element["id"].asLong(),
                name = tags["name"].asText(""),
                location = GeoPoint(
                    latitude = element["lat"]?.asDouble(0.0)?.toFloat() ?: center["lat"]?.asDouble()?.toFloat() ?: 0.0f,
                    longitude = element["lon"]?.asDouble(0.0)?.toFloat() ?: center["lon"]?.asDouble()?.toFloat()
                    ?: 0.0f,
                ),
                wikidataCode = tags["wikidata"]?.asText(null),
                castleType = tags["castle_type"].asText(""),
            )
        }
    }

    private fun parseWorships(response: String): List<PlaceOfWorship> {
        val root = objectMapper.readTree(response)
        val elements = root["elements"] ?: return emptyList()

        return elements.mapNotNull { element ->
            val tags = element["tags"] ?: return@mapNotNull null
            val center = element["center"]
            if (tags["name"] == null) {
                return@mapNotNull null
            }
            if (element["lat"] == null && element["lon"] == null && center["lat"] == null && center["lon"] == null) {
                return@mapNotNull null
            }
            PlaceOfWorship(
                id = element["id"].asLong(),
                name = tags["name"].asText(""),
                location = GeoPoint(
                    latitude = element["lat"]?.asDouble(0.0)?.toFloat() ?: center["lat"]?.asDouble()?.toFloat() ?: 0.0f,
                    longitude = element["lon"]?.asDouble(0.0)?.toFloat() ?: center["lon"]?.asDouble()?.toFloat()
                    ?: 0.0f,
                ),
                wikidataCode = tags["wikidata"]?.asText(null),
                religion = tags["religion"]?.asText(null),
                denomination = tags["denomination"]?.asText(null)
            )
        }
    }

    private fun parsePeaks(response: String): List<Peak> {
        val root = objectMapper.readTree(response)
        val elements = root["elements"] ?: return emptyList()

        return elements.mapNotNull { element ->
            val tags = element["tags"] ?: return@mapNotNull null
            val center = element["center"]
            if (tags["name"] == null) {
                return@mapNotNull null
            }
            if (element["lat"] == null && element["lon"] == null && center["lat"] == null && center["lon"] == null) {
                return@mapNotNull null
            }
            Peak(
                id = element["id"].asLong(),
                name = tags["name"].asText(""),
                location = GeoPoint(
                    latitude = element["lat"]?.asDouble(0.0)?.toFloat() ?: center["lat"]?.asDouble()?.toFloat() ?: 0.0f,
                    longitude = element["lon"]?.asDouble(0.0)?.toFloat() ?: center["lon"]?.asDouble()?.toFloat()
                    ?: 0.0f,
                ),
                wikidataCode = tags["wikidata"]?.asText(null),
                elevation = tags["ele:bpv"]?.asDouble(0.0)?.toFloat() ?: tags["ele"]?.asDouble(0.0)?.toFloat()
            )
        }
    }

    private fun parseGeneric(response: String, type: String): List<GenericPointOfInterestModel> {
        val root = objectMapper.readTree(response)
        val elements = root["elements"] ?: return emptyList()

        return elements.mapNotNull { element ->
            val tags = element["tags"] ?: return@mapNotNull null
            val center = element["center"]
            if (tags["name"] == null) {
                return@mapNotNull null
            }
            if (element["lat"] == null && element["lon"] == null && center["lat"] == null && center["lon"] == null) {
                return@mapNotNull null
            }
            GenericPointOfInterestModel(
                id = element["id"].asLong(),
                name = tags["name"].asText(""),
                location = GeoPoint(
                    latitude = element["lat"]?.asDouble(0.0)?.toFloat() ?: center["lat"]?.asDouble()?.toFloat() ?: 0.0f,
                    longitude = element["lon"]?.asDouble(0.0)?.toFloat() ?: center["lon"]?.asDouble()?.toFloat()
                    ?: 0.0f,
                ),
                wikidataCode = tags["wikidata"]?.asText(null),
                type = type,
            )
        }
    }

}
