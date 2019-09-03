package com.seyfullahpolat.websocket.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.seyfullahpolat.websocket.R
import com.seyfullahpolat.websocket.databinding.ActivityMainBinding
import com.seyfullahpolat.websocket.model.MessageType
import com.seyfullahpolat.websocket.model.MockItem
import com.seyfullahpolat.websocket.viewmodel.MockViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private var mockList: ArrayList<MockItem>? = null
    private var mockAdapter: MockAdapter? = null

    private var mockViewModel: MockViewModel? = null

    private val mockViewModelObservable = Observer<ArrayList<MockItem>> {
        it?.let {
            mockList = it
            mockAdapter = MockAdapter(mockList!!)

            binding?.mockList?.adapter = mockAdapter
        }
        binding?.loading?.visibility = View.GONE
    }
    private val viewModelMessageObservable = Observer<MessageType> {
        it?.let {
            when {
                it.messageType.equals("action", false) -> {
                    mockList = mockViewModel?.mockList?.value
                    mockAdapter?.notifyDataSetChanged()
                }
                it.messageType.equals("LOGIN", false) -> login_status?.text = it.messageBody
                it.messageType.equals("LOGOUT", false) -> login_status?.text = it.messageBody
                else -> {
                    //any message
                }
            }
        }
        binding?.loading?.visibility = View.GONE
        binding?.messageEditText?.text = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.lifecycleOwner = this
        binding!!.mockList.layoutManager = LinearLayoutManager(this)

        mockViewModel = ViewModelProviders.of(this).get(MockViewModel::class.java)

        binding?.viewmodel = mockViewModel

        mockViewModel!!.messageCatch.observe(this, viewModelMessageObservable)
        mockViewModel!!.mockList.observe(this, mockViewModelObservable)
        binding?.sendMessageBtn?.setOnClickListener {
            binding?.loading?.visibility = View.VISIBLE

            val messageType = (binding?.messageEditText?.text).toString().detectType()
            mockViewModel?.sendData("{\"type\":\"${messageType.messageType}\",\"message\":\"${messageType.messageBody}\"}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mockViewModel?.mockList?.removeObserver(mockViewModelObservable)
        mockViewModel?.messageCatch?.removeObserver(viewModelMessageObservable)
    }
}

fun String.detectType(): MessageType {
    val messageType = MessageType()
    when {
        this.equals("LOGIN", ignoreCase = true) -> {
            messageType.messageType = "LOGIN"
            messageType.messageBody = "LOGIN"
        }
        this.equals("LOGOUT", ignoreCase = true) -> {
            messageType.messageType = "LOGOUT"
            messageType.messageBody = "LOGOUT"
        }
        else -> {
            messageType.messageType = "action"
            messageType.messageBody = this
        }
    }
    return messageType
}


fun String.jsonParse(): MessageType {
    val messageType = MessageType()
    val json = JSONObject(this)
    messageType.messageType = json.getString("type")
    messageType.messageBody = json.getString("message")
    return messageType
}

fun String.splitByHyphen(): MockItem? {
    var mockItem: MockItem? = MockItem()

    try {
        this.split("-")[0].toInt()
        mockItem?.id = this.split("-")[0].toInt()
        mockItem?.name = this.split("-")[1]

    } catch (e: NumberFormatException) {
        mockItem = null
    }

    return mockItem
}

operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.value = value
}
