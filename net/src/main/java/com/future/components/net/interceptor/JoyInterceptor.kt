package com.future.components.net.interceptor

import com.future.components.client.ext.ifLet
import com.future.components.net.model.BaseResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
  *
  * @Description:    过滤404、401，并将Error返回
  * @Author:         future
  * @CreateDate:     2022/5/26 17:06
 */
class JoyInterceptor : Interceptor {

    private val gson: Gson by lazy { Gson() }

    @Throws(Throwable::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            ifLet(response.body,response.body?.contentType()){ a, _ ->
                val mediaType = a.contentType()
                val string = a.string()
                if((response.code == 200||response.code == 404||response.code == 401) && a.contentLength()>0){
                    return try {
                        val formatJson = when(response.code){
                            200-> successFormat(string)
                            else -> errorFormat(string)
                        }
                        Response.Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("请求成功")
                            .body(formatJson.toResponseBody(mediaType)).build()
                    }catch (e: Throwable) {
                        chain.proceed(chain.request())
                    }
                }
            }
           return response

        } catch (e: Throwable) {
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message("网络出错")
                .body("{${e}}".toResponseBody(null)).build()
        }
    }

    private fun errorFormat(string: String): String{
        val errors = string.split(":")
        return gson.toJson(BaseResponse("网络出错",404, if (errors.size > 1) errors[1].toInt() else 0,""))
    }

    private fun successFormat(string: String): String {
        val map = HashMap<String, Any>(1)
        val params = string.replace("{", "").replace("}", "").split(",")
        for (param in params) {
            val dataArray = param.split(":")
            map[dataArray[0].replace("\"", "")] = dataArray[1]
        }
        return gson.toJson(BaseResponse(code = 200, error = 0, data = map))
    }

}