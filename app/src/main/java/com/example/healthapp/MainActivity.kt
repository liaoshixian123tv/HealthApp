package com.example.healthapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.healthapp.databinding.ActivityMainBinding
import com.example.healthapp.event.EventAction
import com.google.android.material.bottomnavigation.BottomNavigationView
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var context: Context

    private lateinit var mainScreenView: LinearLayout
    private lateinit var rootView: View

    private lateinit var navController: NavController

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        context = this

        binding = ActivityMainBinding.inflate(layoutInflater)
        rootView = binding.root

//        EventBus.getDefault().register(this)

        val rootBody: View = binding.centralWidgetBodyLayout

        mainScreenView = rootBody.findViewById(R.id.main_screen_view_layout)

        // bottom navigation view
        val navView: BottomNavigationView = mainScreenView.findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_body_main_screen
        ) as NavHostFragment
        navController = navHostFragment.navController
        navView.setupWithNavController(navController)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)



        setContentView(rootView)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val range = 10
        val value: FloatArray? = event?.values
        if (value?.size!! >= 3) {
            Global.currentValue = magnitude(x = value[0], y = value[1], z = value[2]); //计算当前的模
        }
        if (Global.motionState) {
            if (Global.currentValue >= Global.lastValue) {
                Global.lastValue = Global.currentValue
            } else {
                if (abs(Global.currentValue - Global.lastValue) > range) {
                    Global.originalValue = Global.currentValue
                    Global.motionState = false
                }
            }
        } else {
            if (Global.currentValue <= Global.lastValue) {
                Global.lastValue = Global.currentValue
            } else {
                if (abs(Global.currentValue - Global.lastValue) > range) {
                    Global.originalValue = Global.currentValue
                    if (Global.processState) {
                        Global.stepCount++
                        Global.GlobalVariable.updateStepCount(Global.stepCount)
                        println(Global.stepCount)
                    }
                    Global.motionState = true
                }
            }
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun magnitude(x: Float, y: Float, z: Float): Double {
        var magnitude: Double = 0.0
        val input = x * x + y * y + z * z
        magnitude = sqrt(input.toDouble())
        return magnitude
    }

}