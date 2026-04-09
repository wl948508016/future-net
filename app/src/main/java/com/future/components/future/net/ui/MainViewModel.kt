package com.future.components.future.net.ui

import androidx.databinding.ObservableBoolean
import com.future.components.client.databinding.StringObservableField
import com.future.components.client.utils.LogUtils
import com.future.components.client.viewmodel.BaseViewModel
import com.future.components.future.net.RetrofitService
import com.future.components.net.NetworkHelper
import com.future.components.net.ext.requestNoCheck
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 *
 * @Description:
 * @Author:         future
 * @CreateDate:     2024/3/27 10:45
 */
class MainViewModel:BaseViewModel() {

    var zhiNumber = StringObservableField()

    var bracket = StringObservableField()

    var status = ObservableBoolean()

    var waring = StringObservableField()

    var alarm = StringObservableField()

    fun getConfigData(){
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({getConfigData("COAL_CUTTER_STOP_IDENTIFIER_SWITCH")},{
                LogUtils.e("获取采煤机牵引控制开关状态: $it")
                if(it.isSuccess()) status.set(it.data=="1")
            },{
                LogUtils.e("获取采煤机牵引控制开关状态: $it")
            })
        }
    }

    fun getStentsGetAll(){
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({stentsGetAll()},{
                LogUtils.e("获取异常支架列表: $it")
                if(it.isSuccess()&&it.data.isNotEmpty()) {
                    val str = StringBuffer()
                    for (d in it.data){
                        str.append(if(str.isEmpty()) d.toString() else "，$d")
                    }
                    bracket.set("异常支架列表：$str")
                }
            },{
                LogUtils.e("获取异常支架列表: $it")
            })
        }
    }

    fun saveAiRecord(status : Int){
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            val map = HashMap<String, Any?>()
            map["key"] = "COAL_CUTTER_STOP_IDENTIFIER_SWITCH"
            map["value"] = status
            val mediaType = "application/json;charset=utf-8".toMediaTypeOrNull()
            val requestBody: RequestBody = Gson().toJson(map).toRequestBody(mediaType)
            requestNoCheck({saveAiRecord(requestBody)},{
                LogUtils.e("采煤机牵引控制开关: $it")
            },{
                LogUtils.e("采煤机牵引控制开关: $it")
            })
        }
    }

    fun stentsAdd(){
        if(zhiNumber.get().isEmpty()) {
            postMessage("请输入异常支架号")
            return
        }
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({stentsAdd(zhiNumber.get().toInt())},{
                LogUtils.e("采煤机牵引控制开关: $it")
                getStentsGetAll()
            },{
                LogUtils.e("采煤机牵引控制开关: $it")
            })
        }
    }

    fun stentsDelete(){
        if(zhiNumber.get().isEmpty()) {
            postMessage("请输入异常支架号")
            return
        }
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({stentsDelete(zhiNumber.get().toInt())},{
                LogUtils.e("采煤机牵引控制开关: $it")
                getStentsGetAll()
            },{
                LogUtils.e("采煤机牵引控制开关: $it")
            })
        }
    }

    fun getCollisionGetInfo(){
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({collisionGetInfo()},{
                LogUtils.e("当前预警阈值: $it")
                if(it.isSuccess()) {
                    waring.set(it.data.warnScale)
                    alarm.set(it.data.collisionScale)
                }
            },{
                LogUtils.e("当前预警阈值: $it")
            })
        }
    }

    fun collisionUpdate(){
        when{
            waring.get().isEmpty()->postMessage("请输入预警阈值")
            alarm.get().isEmpty()->postMessage("请输入报警阈值")
            else->{
                val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
                apiService?.apply {
                    val map = HashMap<String, Any?>()
                    map["warnScale"] = waring.get()
                    map["collisionScale"] = alarm.get()
                    val mediaType = "application/json;charset=utf-8".toMediaTypeOrNull()
                    val requestBody: RequestBody = Gson().toJson(map).toRequestBody(mediaType)
                    requestNoCheck({collisionUpdate(requestBody)},{
                        LogUtils.e("预警阈值设定: $it")
                    },{
                        LogUtils.e("预警阈值设定: $it")
                    })
                }
            }
        }

    }

    fun opcIssueControl(type:String){
        val apiService = NetworkHelper.INSTANCE.getApi(RetrofitService::class.java, RetrofitService.SERVER_URL)
        apiService?.apply {
            requestNoCheck({opcIssueControl(type)},{
                LogUtils.e("状态控制: $it")
                postMessage(it.msg)
            },{
                postMessage("操作失败")
            })
        }

    }
}