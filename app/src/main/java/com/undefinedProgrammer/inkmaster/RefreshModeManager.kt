package com.undefinedProgrammer.inkmaster

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.core.content.edit


enum class RefreshMode(val mode: Int) {
    CONTRAST(1), SPEED(2), CLEAR(3), LIGHT(4);

    companion object {

        fun fromInt(value: Int) = entries.firstOrNull { it.mode == value } ?: SPEED
    }
}

class RefreshModeManager(
    private val sharedPreferences: SharedPreferences,
) {

    companion object {
        private const val TAG = "EinkHelper"
        private const val MEINK_SERVICE_NAME = "meink"
        private const val DISABLE_PR_APP = "disable_perapprefresh"

    }

    private var meinkService: IMeinkService? = null
    private var isMeinkServiceInitialized = false

    var currentMode = RefreshMode.SPEED
        private set(v) {
            field = v; applyMode()
        }

    init {
        currentMode = RefreshMode.fromInt(defaultRefreshMode())
        sharedPreferences.edit {putBoolean(DISABLE_PR_APP, false)}
    }

    private var currentClassifier = ""

    private fun defaultRefreshMode() = (sharedPreferences.getInt("refresh_setting", 2))

    fun onAppChange(packageName: String) {
        Log.d(TAG, "App changed to: $packageName")

        // Remember the new app
        if (packageName != "com.example.inkmaster") {
            currentClassifier = packageName
        }

        // Determine if per-app refresh is enabled
        val perAppEnabled = !sharedPreferences.getBoolean(DISABLE_PR_APP, false)

        // Build the SharedPreferences key for this app (or "package:None" if per-app is disabled)
        val prefsKey = currentClassifier.toSharedPreferencesKey(perAppEnabled)

        // Read the stored refresh mode for this app; fallback to current mode if none stored
        val storedModeInt = sharedPreferences.getInt(prefsKey, currentMode.mode)
        val appRefreshMode = RefreshMode.fromInt(storedModeInt)

        Log.d(TAG, "$currentClassifier $perAppEnabled $prefsKey $storedModeInt $appRefreshMode")

        // If the mode for this app is different from the current mode, apply it

            currentMode = appRefreshMode

    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun initializeMeinkService() {
        if (isMeinkServiceInitialized) return

        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod =
                serviceManagerClass.getDeclaredMethod("getService", String::class.java)

            val binder = getServiceMethod.invoke(null, MEINK_SERVICE_NAME) as? IBinder
            meinkService = IMeinkService.Stub.asInterface(binder)
            isMeinkServiceInitialized = true

            if (meinkService != null) {
                Log.d(TAG, "MeInk service initialized successfully")
                //setMeinkMode(currentMeinkMode)
                meinkService!!.setDisplayMode("com.example.inkmaster", currentMode.mode)
            } else {
                Log.w(TAG, "MeInk service not available, will retry")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MeInk service", e)
        }
    }

    fun setMeinkMode(mode: Int) {
        if (!isMeinkServiceInitialized || meinkService == null) {
            initializeMeinkService()
            return
        }

        try {
            meinkService!!.setDisplayMode("com.example.inkmaster", mode)
        } catch (e: RemoteException) {
            Log.w(TAG, "MeInk setMode($mode) failed")
        }
        Log.d(TAG, "MeInk setMode($mode) gave no exception")
    }

    fun changeMode(refreshMode: RefreshMode) {

        currentMode = refreshMode
        val key = currentClassifier.toSharedPreferencesKey(
            !sharedPreferences.getBoolean(
                DISABLE_PR_APP, false
            )
        )
        sharedPreferences.edit {
            putInt(
                key, refreshMode.mode
            )
        }
        Log.d(TAG, "changemode, $key ${refreshMode.mode}")

    }

    fun applyMode() {
        setMeinkMode(currentMode.mode)
    }
}

private fun String.toSharedPreferencesKey(isPerAppEnabled: Boolean): String {
    if (isPerAppEnabled) {
        if (startsWith("package:")) return this
        return "package:$this"
    }
    return "package:None"
}