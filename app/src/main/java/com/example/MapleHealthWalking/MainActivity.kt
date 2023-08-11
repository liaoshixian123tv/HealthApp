package com.example.MapleHealthWalking

//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.MapleHealthWalking.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        transparentStatusBar()
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
        navView.itemActiveIndicatorShapeAppearance = null
        navView.itemRippleColor = null

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

    private fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 確認取消半透明設置。
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE) // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // 跟系統表示要渲染 system bar 背景。
            window.statusBarColor = Color.TRANSPARENT
        }
    }

}