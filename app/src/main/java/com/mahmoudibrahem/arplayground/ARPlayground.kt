package com.mahmoudibrahem.arplayground

import android.app.Application
import android.content.Context
import com.google.ar.core.ArCoreApk
import com.mahmoudibrahem.arplayground.DeviceResults.isSupportsAR
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update

class ARPlayground : Application() {

    override fun onCreate() {
        super.onCreate()
        isDeviceSupportsAR(this)
    }

    private fun isDeviceSupportsAR(context: Context) {
        ArCoreApk.getInstance().checkAvailabilityAsync(context) { availability ->
            if (availability.isSupported) {
                isSupportsAR.update { true }
            } else {
                isSupportsAR.update { false }
            }
        }
    }

}