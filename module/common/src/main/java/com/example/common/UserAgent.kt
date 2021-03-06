package com.example.common

import android.content.Context
import android.os.Build
import com.example.utils.app.AppUtil
import com.example.utils.device.DeviceUtil

/**
 * @author fqxyi
 * @date 2020/8/24
 * UserAgent
 */
object UserAgent {

    @JvmStatic
    fun getUserAgent(context: Context): String {
        return StringBuilder().append(Build.MODEL).append(",")
            .append(DeviceUtil.getScreenWidth(context)).append("x")
            .append(DeviceUtil.getScreenHeight(context))
            .append(",Android ").append(Build.VERSION.RELEASE).append(",")
            .append(AppUtil.getAppName(context)).append(" ")
            .append(AppUtil.getAppVersionIncludeV(context)).append(" ")
            .append(AppUtil.getAppVersionCode(context)).toString()
    }

}