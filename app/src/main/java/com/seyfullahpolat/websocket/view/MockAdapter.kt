package com.seyfullahpolat.websocket.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seyfullahpolat.websocket.R
import com.seyfullahpolat.websocket.databinding.MockListItemLayoutBinding
import com.seyfullahpolat.websocket.model.MockItem
import kotlinx.android.synthetic.main.mock_list_item_layout.view.*


/**
 * Created by seyfullahpolat on 2019-08-30.
 */

class MockAdapter(private val mockList: MutableList<MockItem>) :
        RecyclerView.Adapter<MockAdapter.MockViewHolder>() {

    private var binding: MockListItemLayoutBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockViewHolder {
        binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.mock_list_item_layout, parent, false)
        return MockViewHolder(binding!!.root)

    }

    override fun getItemCount(): Int {
        return mockList.size

    }

    override fun onBindViewHolder(holder: MockViewHolder, position: Int) {
        val mockItem = mockList[position]
        holder.itemView.tv_mock_item_id.text = mockItem.id.toString()
        holder.itemView.tv_mock_item_name.text = mockItem.name

    }

    inner class MockViewHolder(v: View) : RecyclerView.ViewHolder(v)
}