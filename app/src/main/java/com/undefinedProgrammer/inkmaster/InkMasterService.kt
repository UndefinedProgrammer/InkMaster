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
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import androidx.core.content.edit


@SuppressLint("AccessibilityPolicy")
class InkMasterService : AccessibilityService() {
    private val TAG: String = "InkMasterService"
    private lateinit var windowManager: WindowManager
    private val meinkController: MeinkController = MeinkController()
    private lateinit var sharedPreferences: SharedPreferences
    private var currentApp: String = "none"
        set(name) {
            field = name.toSharedPreferencesKey()
        }
    private var currentMode = RefreshMode.SPEED
        set(mode) {
            field = mode; meinkController.setMeinkMode(currentApp,mode)
        }

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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // do something with imeShown
        val packageName = event.packageName?.toString()
        Log.d(TAG, "onAccessibilityEvent $packageName $event $windows")

        if (packageName == "com.undefinedProgrammer.inkmaster") {
            Log.d(TAG, "onAccessibilityEvent: Ignoring InkMasterApp")
            return
        }

        if (editableInFocus()) {
            Log.d(TAG, "onAccessibilityEvent: keyboard probably visible")
            meinkController.setMeinkMode(currentApp,RefreshMode.SPEED)
            return
        }
        if (packageName != null) {
            Log.d(TAG, "onAccessibilityEvent: Loading mode for $packageName")
            currentApp = packageName
            loadMode()
            smallRefresh()
        }


    }
    fun loadMode() {
        val fromPreferences = sharedPreferences.getInt(currentApp, RefreshMode.SPEED.mode)
        currentMode = RefreshMode.fromInt(fromPreferences)
        Log.d(TAG, "loadMode: $currentApp -> $currentMode")
    }

    fun saveMode() {
        sharedPreferences.edit {
            putInt(
                currentApp, currentMode.mode
            )
        }
        Log.d(TAG, "saveMode: $currentApp, $currentMode")
    }

    fun editableInFocus(): Boolean {
        val root = rootInActiveWindow ?: return false

        fun hasFocusedEditable(node: AccessibilityNodeInfo?): Boolean {
            node ?: return false

            if (node.isEditable && node.isFocused) return true

            for (i in 0 until node.childCount) {
                if (hasFocusedEditable(node.getChild(i))) return true
            }
            return false
        }

        return hasFocusedEditable(root)
    }

    private fun String.toSharedPreferencesKey(isPerAppEnabled: Boolean = true): String {
        if (isPerAppEnabled) {
            if (startsWith("package:")) return this
            return "package:$this"
        }
        return "package:None"
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
                    currentMode = RefreshMode.CONTRAST
                    saveMode()
                    updateButtons(currentMode.mode)
                    smallRefresh()
                }
                button2.setOnClickListener {
                    currentMode = RefreshMode.SPEED
                    saveMode()
                    updateButtons(currentMode.mode)
                    smallRefresh()
                }
                button3.setOnClickListener {
                    currentMode = RefreshMode.CLEAR
                    updateButtons(currentMode.mode)
                    saveMode()
                    smallRefresh()
                }
                button4.setOnClickListener {
                    currentMode = RefreshMode.LIGHT
                    updateButtons(currentMode.mode)
                    saveMode()
                    smallRefresh()
                }
                updateButtons(currentMode.mode)
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

        val params = WindowManager.LayoutParams(
            4, 4,  // tiny region
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.x = 0
        params.y = 0

        overlay.setBackgroundColor(Color.TRANSPARENT)

        windowManager.addView(overlay, params)

        Handler(Looper.getMainLooper()).postDelayed({
            overlay.setBackgroundColor(Color.argb(1, 0, 0, 0)) // tiny change
            overlay.invalidate()

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    windowManager.removeView(overlay)
                } catch (_: Exception) {}
            }, 120) // ~1 frame
        }, 120)
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

