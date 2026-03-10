package com.undefinedProgrammer.inkmaster

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestOverlayPermission()
        startService(Intent(this, InkMasterService::class.java))
        finish()
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_ACCESSIBILITY_SETTINGS
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("OverlayPermission", "Activity not found exception", e)
        }
    }
}


