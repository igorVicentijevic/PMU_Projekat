package com.example.newsagreggator.data.location

import com.example.newsagreggator.business.model.City
import com.example.newsagreggator.business.model.Coordinates
import com.example.newsagreggator.business.service.CityCatalog
import com.example.newsagreggator.business.service.CityResolver
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SerbianCityResolver @Inject constructor() : CityResolver, CityCatalog {
    override val cities: List<City> = SERBIAN_CITIES

    override fun findNearestCity(coordinates: Coordinates): City? {
        val nearestCity = cities.minByOrNull { city ->
            distanceKilometers(coordinates, city.coordinates)
        } ?: return null

        return nearestCity.takeIf { city ->
            distanceKilometers(coordinates, city.coordinates) <=
                MAX_SUPPORTED_DISTANCE_KILOMETERS
        }
    }

    override fun findCityById(cityId: String): City? =
        cities.firstOrNull { city -> city.id == cityId }

    private fun distanceKilometers(
        first: Coordinates,
        second: Coordinates,
    ): Double {
        val latitudeDistance =
            Math.toRadians(second.latitude - first.latitude)
        val longitudeDistance =
            Math.toRadians(second.longitude - first.longitude)
        val firstLatitude = Math.toRadians(first.latitude)
        val secondLatitude = Math.toRadians(second.latitude)

        val haversine =
            sin(latitudeDistance / 2).pow(2) +
                cos(firstLatitude) *
                cos(secondLatitude) *
                sin(longitudeDistance / 2).pow(2)

        return 2 * EARTH_RADIUS_KILOMETERS * asin(sqrt(haversine))
    }

    private companion object {
        const val EARTH_RADIUS_KILOMETERS = 6_371.0
        const val MAX_SUPPORTED_DISTANCE_KILOMETERS = 100.0

        val SERBIAN_CITIES = listOf(
            city(
                id = "belgrade",
                name = "Beograd",
                latitude = 44.7866,
                longitude = 20.4489,
                aliases = setOf("Beograd", "Beogradu", "beogradski"),
            ),
            city(
                id = "novi-sad",
                name = "Novi Sad",
                latitude = 45.2671,
                longitude = 19.8335,
                aliases = setOf("Novi Sad", "Novom Sadu", "novosadski"),
            ),
            city(
                id = "nis",
                name = "Niš",
                latitude = 43.3209,
                longitude = 21.8958,
                aliases = setOf("Niš", "Nis", "Nišu", "niski", "niški"),
            ),
            city(
                id = "kragujevac",
                name = "Kragujevac",
                latitude = 44.0128,
                longitude = 20.9114,
                aliases = setOf(
                    "Kragujevac",
                    "Kragujevcu",
                    "kragujevački",
                ),
            ),
            city(
                id = "subotica",
                name = "Subotica",
                latitude = 46.1005,
                longitude = 19.6676,
                aliases = setOf("Subotica", "Subotici", "subotički"),
            ),
            city(
                id = "zrenjanin",
                name = "Zrenjanin",
                latitude = 45.3816,
                longitude = 20.3686,
                aliases = setOf("Zrenjanin", "Zrenjaninu", "zrenjaninski"),
            ),
            city(
                id = "pancevo",
                name = "Pančevo",
                latitude = 44.8708,
                longitude = 20.6403,
                aliases = setOf("Pančevo", "Pancevo", "Pančevu", "pančevački"),
            ),
            city(
                id = "cacak",
                name = "Čačak",
                latitude = 43.8914,
                longitude = 20.3497,
                aliases = setOf("Čačak", "Cacak", "Čačku", "čačanski"),
            ),
            city(
                id = "kraljevo",
                name = "Kraljevo",
                latitude = 43.7244,
                longitude = 20.6870,
                aliases = setOf("Kraljevo", "Kraljevu", "kraljevački"),
            ),
            city(
                id = "novi-pazar",
                name = "Novi Pazar",
                latitude = 43.1407,
                longitude = 20.5184,
                aliases = setOf("Novi Pazar", "Novom Pazaru", "novopazarski"),
            ),
            city(
                id = "leskovac",
                name = "Leskovac",
                latitude = 42.9981,
                longitude = 21.9461,
                aliases = setOf("Leskovac", "Leskovcu", "leskovački"),
            ),
            city(
                id = "uzice",
                name = "Užice",
                latitude = 43.8556,
                longitude = 19.8425,
                aliases = setOf("Užice", "Uzice", "Užicu", "užički"),
            ),
            city(
                id = "krusevac",
                name = "Kruševac",
                latitude = 43.5758,
                longitude = 21.3317,
                aliases = setOf("Kruševac", "Krusevac", "Kruševcu", "kruševački"),
            ),
            city(
                id = "vranje",
                name = "Vranje",
                latitude = 42.5514,
                longitude = 21.9003,
                aliases = setOf("Vranje", "Vranju", "vranjski"),
            ),
            city(
                id = "sabac",
                name = "Šabac",
                latitude = 44.7558,
                longitude = 19.6939,
                aliases = setOf("Šabac", "Sabac", "Šapcu", "šabački"),
            ),
            city(
                id = "smederevo",
                name = "Smederevo",
                latitude = 44.6638,
                longitude = 20.9259,
                aliases = setOf("Smederevo", "Smederevu", "smederevski"),
            ),
            city(
                id = "valjevo",
                name = "Valjevo",
                latitude = 44.2751,
                longitude = 19.8982,
                aliases = setOf("Valjevo", "Valjevu", "valjevski"),
            ),
            city(
                id = "sombor",
                name = "Sombor",
                latitude = 45.7733,
                longitude = 19.1122,
                aliases = setOf("Sombor", "Somboru", "somborski"),
            ),
            city(
                id = "pozarevac",
                name = "Požarevac",
                latitude = 44.6213,
                longitude = 21.1878,
                aliases = setOf(
                    "Požarevac",
                    "Pozarevac",
                    "Požarevcu",
                    "požarevački",
                ),
            ),
            city(
                id = "pirot",
                name = "Pirot",
                latitude = 43.1531,
                longitude = 22.5861,
                aliases = setOf("Pirot", "Pirotu", "pirotski"),
            ),
        )

        fun city(
            id: String,
            name: String,
            latitude: Double,
            longitude: Double,
            aliases: Set<String>,
        ) = City(
            id = id,
            name = name,
            coordinates = Coordinates(latitude, longitude),
            aliases = aliases,
        )
    }
}
