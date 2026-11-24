package com.drdisagree.iconify.xposed.utils

import android.os.Build
import com.drdisagree.iconify.data.common.Const.SYSTEMUI_PACKAGE
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Runtime facts about the SystemUI process being hooked.
 *
 * Samsung changes package names and class layouts between One UI releases, so
 * compatibility code must use the loaded class loader rather than assuming an
 * AOSP layout from the Android API level alone.
 */
object SystemUiProfile {
    private var initialized = false
    private var classLoader: ClassLoader? = null

    var isSystemUi: Boolean = false
        private set
    var isSamsung: Boolean = false
        private set
    var androidApi: Int = 0
        private set

    fun init(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        if (initialized && classLoader === loadPackageParam.classLoader) return
        initialized = true
        classLoader = loadPackageParam.classLoader
        isSystemUi = loadPackageParam.packageName == SYSTEMUI_PACKAGE
        androidApi = Build.VERSION.SDK_INT
        isSamsung = isSystemUi && (
            Build.MANUFACTURER.equals("samsung", ignoreCase = true) ||
                classExists("com.android.systemui.qs.SecQuickStatusBarHeader") ||
                classExists("com.android.systemui.navigationbar.SamsungNavigationBarProxy")
            )
    }

    fun classExists(name: String): Boolean = try {
        val loader = classLoader ?: return false
        XposedHelpers.findClassIfExists(name, loader) != null
    } catch (_: Throwable) {
        false
    }
}
