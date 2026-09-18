package com.example.newsagreggator.appearance

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidAmbientLightMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : AmbientLightMonitor {
    private val sensorManager =
        context.getSystemService(SensorManager::class.java)

    //this is the function that converts listener values into flow stream
    override val readings: Flow<AmbientLightReading> = callbackFlow {
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            trySend(AmbientLightReading.Unavailable)
            close()
            return@callbackFlow
        }

        //creating listener that will observe sensor data
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                //for received values sending them throught flow
                event.values.firstOrNull()?.let { illuminanceLux ->
                    trySend(
                        AmbientLightReading.Available(
                            illuminanceLux = illuminanceLux.coerceAtLeast(0f)
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        //registering hat listener into sensorManager
        val registered = sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )


        if (!registered) {
            trySend(AmbientLightReading.Unavailable)
            close()
            return@callbackFlow
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.distinctUntilChanged()
}
