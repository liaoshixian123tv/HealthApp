package com.example.healthapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Global {
    companion object {
        var motionState = true
        var processState = true
        var stepCount = 0
        var originalValue: Double = 0.0
        var lastValue: Double = 0.0
        var currentValue: Double = 0.0
    }

    object GlobalVariable {
        private val countValue = MutableLiveData<Int>()
        val stepCount: LiveData<Int> = countValue

        fun updateStepCount(newVal: Int) {
            countValue.value = newVal
        }
    }
}