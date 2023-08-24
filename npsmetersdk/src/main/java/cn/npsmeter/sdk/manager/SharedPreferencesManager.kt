package cn.npsmeter.sdk.manager

import android.content.Context
import android.content.SharedPreferences
import cn.npsmeter.sdk.api.ConfigResponseModel

object SharedPreferencesManager {

    private const val firstRequestTimeKey = "cn.npsmeter.FirstRequestTimeKey"
    private const val lastShowDateKey = "cn.npsmeter.LastShowDateKey"

    private fun sharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("NpsMeter", Context.MODE_PRIVATE)
    }

    fun show(context: Context) {
        val sharedPreferences =
            sharedPreferences(context)
        val nowData = System.currentTimeMillis() / 1000
        val editor = sharedPreferences.edit()
        editor.putInt(lastShowDateKey, nowData.toInt())
        editor.apply()
    }

    fun canShowWithLastShow(
        context: Context,
        configModel: ConfigResponseModel.ConfigModel
    ): Boolean {
        val sharedPreferences = sharedPreferences(context)
        val lastShowDate = sharedPreferences.getInt(lastShowDateKey, 0)
        if (lastShowDate != 0) {
            val nowData = System.currentTimeMillis() / 1000
            if (nowData - lastShowDate < configModel.min_fatigue_duration * 24 * 60 * 60) {
                return false
            }
        }
        return true
    }

    fun saveRequestConfig(context: Context) {
        val sharedPreferences = sharedPreferences(context)
        val contains = sharedPreferences.contains(firstRequestTimeKey)
        if (!contains) {
            val nowData = System.currentTimeMillis() / 1000
            val editor = sharedPreferences.edit()
            editor.putInt(firstRequestTimeKey, nowData.toInt())
            editor.apply()
        }
    }

    fun canShowWithFirstRequest(
        context: Context,
        configModel: ConfigResponseModel.ConfigModel
    ): Boolean {
        val sharedPreferences = sharedPreferences(context)
        val firstRequestTime = sharedPreferences.getInt(firstRequestTimeKey, 0)
        val nowData = System.currentTimeMillis() / 1000
        if (nowData - firstRequestTime < configModel.from_first_day * 24 * 60 * 60) {
            return false
        }
        return true
    }

    private fun haveShowKey(id: String): String {
        return "cn.npsmeter.last.time.$id"
    }

    fun saveHaveShow(context: Context, configModel: ConfigResponseModel.ConfigModel) {
        val sharedPreferences = sharedPreferences(context)
        val key = haveShowKey(configModel.id)
        val nowData = System.currentTimeMillis() / 1000
        val editor = sharedPreferences.edit()
        editor.putInt(key, nowData.toInt())
        editor.apply()
    }

    fun canShowWithHaveShow(
        context: Context,
        configModel: ConfigResponseModel.ConfigModel
    ): Boolean {
        val sharedPreferences = sharedPreferences(context)
        val key = haveShowKey(configModel.id)
        val lastShowDate = sharedPreferences.getInt(key, 0)
        if (lastShowDate != 0) {
            val nowData = System.currentTimeMillis() / 1000
            if (nowData - lastShowDate < configModel.repeat_duration * 24 * 60 * 60) {
                return false
            }
        }
        return true
    }

}