package com.undefinedProgrammer.inkmaster


import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button


class InkMasterService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: View
    private lateinit var refreshModeManager: RefreshModeManager
    private lateinit var sharedPreferences: SharedPreferences
    private var currentApp: String? = null

    private lateinit var accessibilityButtonCallback: AccessibilityButtonCallback
    private var mIsAccessibilityButtonAvailable = false
    private var menuVisible: Boolean = false
    private var lastClose = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        sharedPreferences = getSharedPreferences(
            "${packageName}_preferences", // your own filename
            MODE_PRIVATE
        )
        refreshModeManager = RefreshModeManager(
            sharedPreferences
        )

        serviceInfo = serviceInfo.apply {
            // Make sure you request accessibility button
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }


        accessibilityButtonCallback = object : AccessibilityButtonCallback() {
            override fun onClicked(controller: AccessibilityButtonController?) {
                openFloatingMenu()
            }

            override fun onAvailabilityChanged(
                controller: AccessibilityButtonController, available: Boolean
            ) {
                if (controller == accessibilityButtonController) {
                    mIsAccessibilityButtonAvailable = available
                }
            }
        }


        accessibilityButtonController.registerAccessibilityButtonCallback(
            accessibilityButtonCallback
        )

    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent) {
        if (event.eventType == android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            Log.d("accesevent", "fired $packageName")

            if (packageName != "com.undefinedProgrammer.inkmaster" && currentApp != packageName) {
                currentApp = packageName
                refreshModeManager.onAppChange(packageName)
                refresh()
            }
        }
    }

    override fun onInterrupt() {}

    private var menuBinding: FloatingMenuViewAccessor? = null

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    fun openFloatingMenu() {
        val now = System.currentTimeMillis()
        if (now - lastClose < 300) return
        try {
            if (menuBinding?.root?.isAttachedToWindow == true) {
                try {
                    windowManager.removeView(menuBinding!!.root)
                    lastClose = System.currentTimeMillis()
                } catch (_: Exception) {
                }

                menuBinding = null
                menuVisible = false
                return
            }
            // create the menu if it has not been initialized yet

            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.floating_menu_layout, null, false)

            val layoutParams = WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                format = PixelFormat.TRANSLUCENT
                flags =
                    flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
                y = getNavBarHeight()
            }

            menuBinding = FloatingMenuViewAccessor(view).apply {
                root.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_OUTSIDE) {
                        menuBinding?.root?.let { windowManager.removeView(it) }
                        menuBinding = null
                        menuVisible = false
                        refresh()
                        lastClose = System.currentTimeMillis()
                    }
                    false
                }

                button1.setOnClickListener {
                    refreshModeManager.changeMode(RefreshMode.CONTRAST)
                    updateButtons(refreshModeManager.currentMode.mode)
                    smallRefresh()
                }
                button2.setOnClickListener {
                    refreshModeManager.changeMode(RefreshMode.SPEED)
                    updateButtons(refreshModeManager.currentMode.mode)
                    smallRefresh()
                }
                button3.setOnClickListener {
                    refreshModeManager.changeMode(RefreshMode.CLEAR)
                    updateButtons(refreshModeManager.currentMode.mode)
                    smallRefresh()
                }
                button4.setOnClickListener {
                    refreshModeManager.changeMode(RefreshMode.LIGHT)
                    updateButtons(refreshModeManager.currentMode.mode)
                    smallRefresh()
                }


                updateButtons(refreshModeManager.currentMode.mode)


            }
            windowManager.addView(menuBinding!!.root, layoutParams)

            menuVisible = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        menuBinding?.root?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val bounds = windowManager.currentWindowMetrics.bounds
        return Point(bounds.width(), bounds.height())
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val bounds = windowManager.maximumWindowMetrics.bounds
        return Point(bounds.width(), bounds.height())
    }

    private fun getNavBarHeight(): Int {
        val appUsableSize: Point = getAppUsableScreenSize(this)
        val realScreenSize: Point = getRealScreenSize(this)

        if (appUsableSize.y < realScreenSize.y) return (realScreenSize.y - appUsableSize.y)

        return 0
    }

    fun refresh() {

        val overlay = View(this)

        windowManager.addView(
            overlay, WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
        )

        overlay.bringToFront()
        Handler(Looper.getMainLooper()).postDelayed({
            overlay.setBackgroundColor(Color.BLACK)

            Handler(Looper.getMainLooper()).postDelayed({
                overlay.setBackgroundColor(Color.WHITE)
                try {
                    windowManager.removeView(overlay)
                } catch (_: Exception) {
                }
            }, 100L)
        }, 100L)

    }

    fun smallRefresh() {

        val overlay = View(this)

        windowManager.addView(
            overlay, WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
        )

        overlay.bringToFront()
        Handler(Looper.getMainLooper()).postDelayed({
            overlay.setBackgroundColor(Color.BLACK)

        }, 50L)
        try {
            windowManager.removeView(overlay)
        } catch (_: Exception) {
        }
    }
}

fun Button.deselect() {
    setBackgroundResource(R.drawable.drawable_border_normal)
    setTextColor(Color.BLACK)
}

fun Button.select() {
    setBackgroundResource(R.drawable.drawable_border_pressed)
    setTextColor(Color.WHITE)
}

