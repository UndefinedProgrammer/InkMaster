package com.undefinedProgrammer.inkmaster

import android.annotation.SuppressLint
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

enum class RefreshMode(val mode: Int) {
    CONTRAST(1), SPEED(2), CLEAR(3), LIGHT(4);

    companion object {

        fun fromInt(value: Int) = entries.firstOrNull { it.mode == value } ?: SPEED
    }
}

class MeinkController {
    companion object {
        private const val TAG = "MeinkController"
        private const val MEINK_SERVICE_NAME = "meink"

    }

    private var meinkService: IMeinkService? = null
    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun initializeMeinkService() {
        if (isMeinkInitialised()) {
            return
        }
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod =
                serviceManagerClass.getDeclaredMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, MEINK_SERVICE_NAME) as? IBinder
            meinkService = IMeinkService.Stub.asInterface(binder)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MeInk service", e)
        }
    }

    fun isMeinkInitialised(): Boolean {
        return meinkService != null
    }

    fun setMeinkMode(packageName: String,refreshMode: RefreshMode) {
        Log.d(TAG, "setMeinkMode($packageName,$refreshMode)")
        if (!isMeinkInitialised()) {
            initializeMeinkService()
        }
        try {
            meinkService!!.setDisplayMode( packageName,refreshMode.mode)
            /*meinkService!!.setCustomDisplayMode(
                "com.undefinedProgrammer.inkmaster",        // packageName
                "GL16",                  // waveform mode
                "BPP_4",                // bpp
                "GAMMA_2_20",       // gamma profile
                1.1f,                  // brightness
                1.2f                   // contrast
            )*/
        } catch (e: RemoteException) {
            Log.e(TAG, "setMeinkMode($packageName,$refreshMode) failed $e")
        }
    }
}


