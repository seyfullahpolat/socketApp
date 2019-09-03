package com.seyfullahpolat.websocket.viewmodel

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyfullahpolat.websocket.BuildConfig
import com.seyfullahpolat.websocket.model.MessageType
import com.seyfullahpolat.websocket.model.MockItem
import com.seyfullahpolat.websocket.model.MockResponse
import com.seyfullahpolat.websocket.service.RetrofitClient
import com.seyfullahpolat.websocket.view.jsonParse
import com.seyfullahpolat.websocket.view.splitByHyphen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


/**
 * Created by seyfullahpolat on 2019-08-30.
 */

class MockViewModel : ViewModel() {
    var webSocket: WebSocket? = null
    var mockList: MutableLiveData<ArrayList<MockItem>> = MutableLiveData()
    var messageCatch: MutableLiveData<MessageType> = MutableLiveData()

    init {
        getData()
        connectSocket()
    }

    private fun connectSocket() {
        val request = Request.Builder().url(BuildConfig.WEB_SOCKET_URL).build()
        val listener = WebSocketOnSubscribe()
        val okHttpClient = OkHttpClient()
        webSocket = okHttpClient.newWebSocket(request, listener)
        okHttpClient.dispatcher().executorService().shutdown()

    }

    fun sendData(json: String) {
        webSocket?.send(json)
    }

    private fun getData() {
        RetrofitClient().httpService.getData().enqueue(object : Callback<MockResponse> {
            override fun onResponse(call: Call<MockResponse>, response: Response<MockResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    mockList.value = response.body()!!.data
                }
            }

            override fun onFailure(call: Call<MockResponse>, t: Throwable) {
                Log.d("error", "e")
            }
        })
    }

    inner class WebSocketOnSubscribe : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            super.onOpen(webSocket, response)
            val json = "{\"type\":\"chat\",\"message\":\"im online, whats up\"}"
            webSocket.send(json)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            super.onFailure(webSocket, t, response)

        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            webSocket.close(1000, null)

        }

//        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//            super.onClosed(webSocket, code, reason)
//        }
//
//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            super.onMessage(webSocket, bytes)
//
//        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val messageType = text.jsonParse()
            if (messageType.messageType.equals("action", false)) {
                val editingMockItem = messageType.messageBody?.splitByHyphen()
                if (editingMockItem != null) {
                    mockList.value?.find { it.id == editingMockItem.id }?.name = editingMockItem.name

                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                messageCatch.value = messageType

            }
        }
    }
}