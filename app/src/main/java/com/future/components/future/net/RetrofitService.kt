package com.future.components.future.net

import com.future.components.future.net.model.CollisionModel
import com.future.components.net.model.BaseResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Administrator on 2017/10/25.
 */
interface RetrofitService {
    /**
     * @Headers({"Content-type:application/json;charset=UTF-8"})
     * 1. JSON  格式 请求体
     * RequestBody body = RequestBody.create(JSON, "json格式的字符串");
     *
     * 2.  文件上传
     * RequestBody requestBody = new MultipartBody.Builder()
     * .setType(MultipartBody.FORM)
     * .addFormDataPart("file", file.getName(), RequestBody.create(PNG, file))
     * .build();
     * 3. 表单
     * FormBody body = new FormBody.Builder()
     * .add("limit", String.valueOf(LIMIT))
     * .add("page", String.valueOf(pageValue))
     * .build();
     */

    companion object {
//        private var SERVER_URL = "http://10.10.9.107:8080" //乌海
//        private var SERVER_URL = "http://10.226.108.15:8080" //转龙湾
//        private var SERVER_URL = "http://10.225.165.148:8081"
//        private var SERVER_URL = "http://192.168.110.233:8080"
         var SERVER_URL = "http://119.167.159.196:12000"
//        private var SERVER_URL = "http://192.168.110.106:8082"
    }

    @Headers("Content_Type:application/json", "charset:UTF-8")
    @POST("/system/config/save")
    suspend fun saveAiRecord(@Body requestBody: RequestBody): BaseResponse<Any>

    /**
     * 请求体验参数
     */
    @Headers("Content_Type:application/json", "charset:UTF-8")
    @GET("/system/config/get")
    suspend fun getConfigData(@Query("key") key: String?): BaseResponse<String>

    @Headers("Content_Type:application/json", "charset:UTF-8")
    @GET("/error/stents/getAll")
    suspend fun stentsGetAll(): BaseResponse<MutableList<Int>>

    @Headers("Content_Type:application/json", "charset:UTF-8")
    @POST("/error/stents/add")
    suspend fun stentsAdd(@Query("stentNo") stentNo: Int): BaseResponse<Any>

    @Headers("Content_Type:application/json", "charset:UTF-8")
    @POST("/error/stents/delete")
    suspend fun stentsDelete(@Query("stentNo") stentNo: Int): BaseResponse<Any>

    /**
     * 获取阈值参数
     */
    @Headers("Content_Type:application/json", "charset:UTF-8")
    @GET("/collision/config/getInfo")
    suspend fun collisionGetInfo(): BaseResponse<CollisionModel>

    /**
     * 设置阈值
     */
    @Headers("Content_Type:application/json", "charset:UTF-8")
    @POST("/collision/config/update")
    suspend fun collisionUpdate(@Body requestBody: RequestBody): BaseResponse<Any>

    /**
     * 控制信号
     */
    @Headers("Content_Type:application/json", "charset:UTF-8")
    @POST("/opc/issue/control")
    suspend fun opcIssueControl(@Query("levelEnum") levelEnum: String): BaseResponse<Any>

}