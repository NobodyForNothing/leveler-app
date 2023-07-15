package com.derdilla.spiritLevel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import kotlin.math.sign


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var useFallback = false
    private var x = 0.0f
    private var y = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        determineSensors()
        draw()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        x = event!!.values[0]
        y = event.values[1]
        draw()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        onPause()
        determineSensors()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        if (useFallback) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun determineSensors() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GRAVITY)
            gravitySensor = gravSensors.reduce { a, b ->
                if (a.resolution < b.resolution) {
                    a
                } else {
                    b
                }
            }
        }


        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            val accSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
            accelerometer = accSensors.reduce { a, b ->
                if (a.resolution < b.resolution) {
                    a
                } else {
                    b
                }
            }
        }

        if (gravitySensor == null) useFallback = true
    }

    private fun draw() {
        val df = DecimalFormat("#.##")
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "X: " + df.format(x) + "\nY: " + df.format(y),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Canvas(
                    modifier = Modifier
                        .size(size = 300.dp)
                ) {
                    drawCircle(
                        color = Color.LightGray,
                        radius = 150.dp.toPx()
                    )
                    drawCircle(
                        color = Color.Green,
                        radius = 25.dp.toPx()
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 10.dp.toPx(),
                        center = Offset(
                            x = (this.size.width.toDp().toPx() / 2 + sign(x) * f(x) * 250).toDp().toPx(),
                            y = (this.size.height.toDp().toPx() / 2 - sign(y) * f(y) * 250).toDp().toPx() // substract for bubble effect
                        )
                    )
                }
            }
        }
    }

    /**
     * function to calculate the position of the knob returns a value between 0 and 1
     */
    private fun f(x: Float): Float {
        return 1 - 1*(1/(0.5* kotlin.math.abs(x) + 1)).toFloat()
    }
}