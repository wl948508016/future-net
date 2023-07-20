package com.future.components.net

import android.util.Log
import android.widget.Toast
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.future.components.client.appContext
import com.future.components.client.utils.LogUtils
import com.future.components.net.factory.NullOnEmptyConverterFactory
import com.future.components.net.interceptor.JoyInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 *
 * @Description:
 * @Author:         future
 * @CreateDate:     2022/5/24 11:38
 */
class NetworkHelper {

    companion object {
        val INSTANCE: NetworkHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkHelper()
        }
    }

    fun <T> getApi(serviceClass: Class<T>, baseUrl: String): T? {
        var retrofitBuilder:Retrofit.Builder? = null
        try {
            retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
        }catch (t:Throwable){
            LogUtils.e("Throwable: $t")
            Toast.makeText(appContext,"Expected URL scheme 'http' or 'https' but no colon was found",Toast.LENGTH_LONG).show()
        }
        return if(retrofitBuilder==null) null else setRetrofitBuilder(retrofitBuilder).build().create(serviceClass)
    }

    private fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder{
        builder.apply {
            //设置缓存配置 缓存最大10M
            cache(Cache(File(appContext.cacheDir, "future_cache"), 10 * 1024 * 1024))
            //添加Cookies自动持久化
            cookieJar(cookieJar)
//            //示例：添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息
//            addInterceptor(HeaderInterceptor())
//            //添加缓存拦截器 可传入缓存天数，不传默认7天
//            addInterceptor(CacheInterceptor())
            //Joy特殊请求结构
            addInterceptor(JoyInterceptor())
            // 日志拦截器
            addInterceptor(HttpLoggingInterceptor { message -> if(isDebug) Log.d("————Retrofit————: ", "————网络访问————OkHttp: $message") }.setLevel(HttpLoggingInterceptor.Level.BODY))
            //超时时间 连接、读、写
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
            createSSLSocketFactory()?.let {
                writeTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(it, AppTrustManager())
                    .hostnameVerifier(TrustAllHostnameVerifier())

            }
        }
        return builder
    }

    private fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder{
        return builder.apply {
            addConverterFactory(NullOnEmptyConverterFactory())
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }
    }

    fun getHttpClientBuilder():OkHttpClient.Builder{
        if(okHttpBuilder==null){
            okHttpBuilder = setHttpClientBuilder(OkHttpClient.Builder())
        }
        return okHttpBuilder as OkHttpClient.Builder
    }

    private var okHttpBuilder:OkHttpClient.Builder? = null

    /**
     * 配置http
     */
    private val okHttpClient: OkHttpClient
        get() {
            return getHttpClientBuilder().build()
        }

    private val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))
    }

    private var isDebug:Boolean =false

    /**
     * 是否debug
     */
    fun setDebug(b:Boolean){
        isDebug = b
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        var sSocketFactory: SSLSocketFactory?=null
        try {
            // Install the all-trusting trust manager  //指定的信任管理器  { 忽略证书 }
            val trustManagers = arrayOf<TrustManager>(AppTrustManager())
            // 创建SSLContext对象    SSL/TLS
            val sslContext = SSLContext.getInstance("TLS")
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            sslContext.init(null, trustManagers, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            //用所有信任的管理器创建SSL套接字工厂        // 从上述SSLContext对象中得到SSLSocketFactory对象
            sSocketFactory = sslContext.socketFactory
        } catch (e: Exception) {

        }
        return sSocketFactory
    }

    /**
     * 忽略信任证书
     */
    class AppTrustManager : X509TrustManager {
        // 检查客户端证书
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

        }

        // 检查服务器端证书
        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

        }

        // 返回受信任的X509证书数组
        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    /**
     * 忽略Hostname 的验证
     * （仅仅用于测试阶段，不建议用于发布后的产品中。）
     */
    class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(s: String, sslSession: SSLSession): Boolean {
            return true
        }
    }

}