package cn.npsmeter.sdk.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FlickrApi {

    @GET("sdkapi/campaign/config")
    fun config(@Query("id") id: String,
               @Query("platform") platform: String,
               @Query("version") version: String,
               @Query("sdk_version") sdk_version: String): Call<ConfigResponseModel>

    @POST("sdkapi/campaign/openView")
    fun openView(@Body route: RequestBody): Call<Map<String,Any>>

    @POST("sdkapi/campaign/answer")
    fun answer(@Body route: RequestBody):  Call<Map<String,Any>>

    @POST("api/campaign/errorLog")
    fun errorLog(@Body route: RequestBody): Call<ErrorLogResponseModel>
}