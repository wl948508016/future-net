package com.future.components.net.factory

import com.future.components.net.model.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 *
 * @Description:
 * @Author:         future
 * @CreateDate:     2023/5/16 15:38
 */
class NullOnEmptyConverterFactory: Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, Any> {
        val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)
        return Converter {
            if (it.contentLength() == 0L) {
               return@Converter BaseResponse(code = 200, error = 0, data = {})
            }
            delegate.convert(it)
        }
    }
}