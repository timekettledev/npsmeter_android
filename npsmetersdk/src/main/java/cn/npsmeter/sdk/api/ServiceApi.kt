package cn.npsmeter.sdk.api

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.google.gson.Gson
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.HashMap


object ServiceApi {

    private const val sdkVersion = "1.19.3"
    private val flickrApi: FlickrApi

    private fun getVersion(context: Context): String {
        val mg: PackageManager = context.packageManager
        return try {
            // getPackageInfo(packageName 包名, flags 标志位（表示要获取什么数据）)
            // 0表示获取基本数据
            val info: PackageInfo = mg.getPackageInfo(context.packageName, 0)
            info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    private fun getMacAddress(): String? {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", true)) {
                    continue
                }
                val macBytes: ByteArray = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://app.npsmeter.cn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun config(
        id: String,
        context: Context,
        e: ((ConfigResponseModel.ConfigModel?, String?) -> Unit),
    ) {

        val gson = Gson()

        val paramsMap: HashMap<String, Any> = HashMap()

//        paramsMap["id"] = id
//        paramsMap["platform"] = "Android"
//        paramsMap["version"] = version
//        paramsMap["sdk_version"] = sdkVersion


        val imei = getMacAddress()
        if (imei != null) {
            paramsMap["uuid"] = imei
        }
//        paramsMap["first_view_time"] = Date().timeIntervalSince1970;
        val clientInfo: HashMap<String, Any> = HashMap()
        clientInfo["os_name"] = "Android"


//        clientInfo["screen"] = String(format: "%ld*%ld",UIScreen.main.bounds.width,UIScreen.main.bounds.height)
//        clientInfo["language"] = Locale.preferredLanguages.first
        paramsMap["client_info"] = clientInfo
        paramsMap["is_customer_open"] = 1

        val strEntity = gson.toJson(paramsMap)

//        val body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity)
        paramsMap["client_info"] = strEntity

        val flickrRequest: Call<ConfigResponseModel> = flickrApi.config(
            id = id,
            platform = "Android",
            version = getVersion(context),
            sdk_version = sdkVersion
        )//.fetchPhotos()



        flickrRequest.enqueue(object : Callback<ConfigResponseModel> {

            override fun onFailure(call: Call<ConfigResponseModel>, t: Throwable) {
                e(null, t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ConfigResponseModel>,
                response: Response<ConfigResponseModel>
            ) {
                val configResponseModel: ConfigResponseModel? = response.body()
                e(configResponseModel?.data, configResponseModel?.message)
            }
        })

    }

    fun openView(
        id: String,
        userid: String?,
        username: String?,
        remark: String?,
        context: Context,
        e: ((QuestionResponseModel.QuestionModel?) -> Unit),
    ) {

        val gson = Gson()

        val paramsMap: HashMap<String, Any> = HashMap()

        paramsMap["id"] = id
        if (userid != null) {
            paramsMap["userid"] = userid
        }
        if (username != null) {
            paramsMap["username"] = username
        }

        if (remark != null) {
            val params: HashMap<String, Any> = HashMap()
            params["remark"] = remark
            paramsMap["params"] = params
        }

        paramsMap["platform"] = "Android"
        val imei = getMacAddress()
        if (imei != null) {
            paramsMap["uuid"] = imei
        }
        val version = getVersion(context)
        paramsMap["version"] = version
        paramsMap["sdk_version"] = sdkVersion
        paramsMap["first_view_time"] = (System.currentTimeMillis() / 1000).toString()
        val clientInfo: HashMap<String, Any> = HashMap()
        clientInfo["os_name"] = "Android"

        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels

        clientInfo["screen"] =
            "$screenWidth*$screenHeight"
        clientInfo["language"] = Locale.getDefault().language
        paramsMap["client_info"] = clientInfo
        paramsMap["is_customer_open"] = 1

        val strEntity = gson.toJson(paramsMap)

        val body =
            RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity)

        val flickrRequest: Call<Map<String, Any>> = flickrApi.openView(body)

        flickrRequest.enqueue(object : Callback<Map<String, Any>> {

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                e(null)
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                val map: Map<String, Any>? = response.body()
                if (map != null) {
                    e(QuestionResponseModel.mapToQuestion(map).data)
                } else {
                    e(null)
                }
            }
        })
    }

    fun answer(
        id: String,
        question_id: String,
        view_id: Int?,
        view_unique_id: String?,
        rating: Int?,
        answer: Any?,
        context: Context,
        e: ((QuestionResponseModel.QuestionModel?, String?) -> Unit),
    ) {

        val gson = Gson()

        val paramsMap: HashMap<String, Any> = HashMap()

        paramsMap["id"] = id
        paramsMap["question_id"] = question_id
        if (view_id != null) {
            paramsMap["view_id"] = view_id
        }
        if (view_unique_id != null) {
            paramsMap["view_unique_id"] = view_unique_id
        }
        if (rating != null) {
            paramsMap["rating"] = rating
        }
        if (answer != null) {
            paramsMap["answer"] = answer
        }
        paramsMap["platform"] = "Android"
        val version = getVersion(context)
        paramsMap["version"] = version

        val strEntity = gson.toJson(paramsMap)

        val body =
            RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity)

        val flickrRequest: Call<Map<String, Any>> = flickrApi.answer(body)

        flickrRequest.enqueue(object : Callback<Map<String, Any>> {

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                e(null,t.localizedMessage)
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                val map: Map<String, Any>? = response.body()
                if (map != null) {
                    val model :QuestionResponseModel =  QuestionResponseModel.mapToQuestion(map)
                    e(model.data,model.message)
                } else {
                    e(null,null)
                }
            }
        })
    }


    fun errorLog(log: String, context: Context) {
        try {
            val gson = Gson()
            val errorLogMap: HashMap<String, Any> = HashMap()
            errorLogMap["error"] = log

            val imei = getMacAddress()
            if (imei != null) {
                errorLogMap["uuid"] = imei
            }
            val version = getVersion(context)
            errorLogMap["version"] = version
            errorLogMap["sdk_version"] = sdkVersion
            errorLogMap["first_view_time"] = (System.currentTimeMillis() / 1000).toString()
            errorLogMap["os_name"] = "Android"
            errorLogMap["packageName"] = context.packageName

            errorLogMap["MANUFACTURER"] = Build.MANUFACTURER
            errorLogMap["build_version"] = Build.VERSION.RELEASE
            val dm = context.resources.displayMetrics
            val screenWidth = dm.widthPixels
            val screenHeight = dm.heightPixels
            errorLogMap["screen"] =
                "$screenWidth*$screenHeight"

            val paramsMap: HashMap<String, Any> = HashMap()
            paramsMap["error_log"] = errorLogMap
            val strEntity = gson.toJson(paramsMap)
            val body = RequestBody.create(
                okhttp3.MediaType.parse("application/json;charset=UTF-8"),
                strEntity
            )
            val flickrRequest: Call<ErrorLogResponseModel> = flickrApi.errorLog(body)
            flickrRequest.enqueue(object : Callback<ErrorLogResponseModel> {
                override fun onFailure(call: Call<ErrorLogResponseModel>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ErrorLogResponseModel>,
                    response: Response<ErrorLogResponseModel>
                ) {
                }
            })
        } catch (exception: Exception) {

        }
    }
}