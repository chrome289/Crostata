package xyz.siddharthseth.crostata.util.device

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

class DeviceUtils {
    companion object {
        fun getScreenHeight(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y
        }

        fun getScreenWidth(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }
    }
}