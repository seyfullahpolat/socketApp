package com.seyfullahpolat.websocket.service

import com.seyfullahpolat.websocket.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    val httpService: MyService
    private var unSecureRetrofitInstance: Retrofit? = null

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val timeoutInterval = 60
        val httpClientWithHeader = OkHttpClient.Builder()
        httpClientWithHeader.addInterceptor(logging)
        httpClientWithHeader.connectTimeout(timeoutInterval.toLong(), TimeUnit.SECONDS)
        httpClientWithHeader.readTimeout(timeoutInterval.toLong(), TimeUnit.SECONDS)

        val client = httpClientWithHeader.build()

        if (unSecureRetrofitInstance == null)
            unSecureRetrofitInstance = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BuildConfig.SERVER_URL)
                .build()

        httpService = unSecureRetrofitInstance!!.create(MyService::class.java)
    }
}

