package cn.npsmeter.sdk.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import cn.npsmeter.sdk.api.ConfigResponseModel
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ThanksIconManager {

    private var map: HashMap<String, Bitmap> = HashMap()

    fun downIcon(strUrl: String?) {
        if (strUrl == null) {
            return
        }
        try {
            val url = URL(strUrl)
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.readTimeout = 20000
            httpURLConnection.connectTimeout = 20000
            val inputStream = httpURLConnection.inputStream
            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bitmapFromNetwork: Bitmap = BitmapFactory.decodeStream(inputStream)
                map[strUrl] = bitmapFromNetwork;
            }
        } catch (e: IOException) {
        }
    }

    fun getImage(config: ConfigResponseModel.ConfigModel): Bitmap? {
        if (config.is_show_thanks_icon == 1) {
            if (config.thanks_icon == null) {
                return null
            }
            return map[config.thanks_icon]
        }
        return null
    }
}