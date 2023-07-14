package com.derdilla.waterLevel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.derdilla.waterLevel.ui.theme.WaterLevelTheme
import java.text.DecimalFormat
import java.util.Arrays


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var useFallback = false
    private var x = 0.0f
    private var y = 0.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;


        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GRAVITY)
            gravitySensor = gravSensors.reduce { a, b -> if(a.resolution < b.resolution) { a } else { b }}
        }


        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            val accSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
            accelerometer = accSensors.reduce { a, b -> if(a.resolution < b.resolution) { a } else { b }}
        }

        if (gravitySensor == null) useFallback = true

        print('a')

        draw()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (useFallback || event!!.sensor.name.contains("gravity")) {
            x = event!!.values[0]
            y = event.values[1]
        }
        draw()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("Not yet implemented")
        print(accuracy)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    private fun draw() {
        val df = DecimalFormat("#.##")
        setContent {
            WaterLevelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("X: " + df.format(x) + "\nY: " + df.format(y))
                        /* Text(gravitySensor.toString())
                        //Text(accelerometer.toString())
                        Text(acel) */
                        Canvas(
                            modifier = Modifier
                                .size(size = 300.dp)
                        ) {
                            drawCircle(
                                color = Color.LightGray,
                                radius = 100.dp.toPx()
                            )
                            drawCircle(
                                color = Color.Green,
                                radius = 20.dp.toPx()
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = 10.dp.toPx(),
                                center = Offset(
                                    x = this.size.width/2 + x*20,
                                    y = this.size.height/2 - y*20 // substract for bubble effect
                                )
                            )
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}