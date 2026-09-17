package com.example.newsagreggator.data.location

import com.example.newsagreggator.business.model.Coordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SerbianCityResolverTest {
    private val resolver = SerbianCityResolver()

    @Test
    fun resolvesBelgradeFromNearbyCoordinates() {
        val city = resolver.findNearestCity(
            Coordinates(
                latitude = 44.8125,
                longitude = 20.4612,
            )
        )

        assertEquals("belgrade", city?.id)
    }

    @Test
    fun resolvesNoviSadFromNearbyCoordinates() {
        val city = resolver.findNearestCity(
            Coordinates(
                latitude = 45.2551,
                longitude = 19.8452,
            )
        )

        assertEquals("novi-sad", city?.id)
    }

    @Test
    fun returnsNullOutsideSupportedArea() {
        val city = resolver.findNearestCity(
            Coordinates(
                latitude = 48.8566,
                longitude = 2.3522,
            )
        )

        assertNull(city)
    }
}
