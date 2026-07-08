package com.ajla.digitalnialbum.ui.packet

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTimestamp = 0L
    private val shakeThreshold = 12f
    private val minIntervalMs = 800L

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

        if (gForce > shakeThreshold) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimestamp > minIntervalMs) {
                lastShakeTimestamp = now
                onShakeDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    val isAvailable: Boolean get() = accelerometer != null
}