package com.seyfullahpolat.websocket.service

import com.seyfullahpolat.websocket.model.MockResponse
import retrofit2.Call
import retrofit2.http.GET

interface MyService {

    @GET("emredirican/mock-api/db")
    fun getData(): Call<MockResponse>
}
