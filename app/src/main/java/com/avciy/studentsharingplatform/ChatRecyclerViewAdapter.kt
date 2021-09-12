package com.avciy.studentsharingplatform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avciy.studentsharingplatform.fragments.ChatFragment

class ChatRecyclerViewAdapter(private val listener: ChatFragment) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatHolder>() {

    interface Listener {
        fun onItemClick(chatModel: ChatModel)
    }

    class ChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(chatModel: ChatModel, listener: Listener) {
            itemView.setOnClickListener {
                listener.onItemClick(chatModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {

        val inflater = LayoutInflater.from(parent.context)
        //var view: View? = null
        /*
        if(chatModel.side == "right") {
            view = inflater.inflate(R.layout.chat_recycler_view_item_going, parent, false)
        }else {
            view = inflater.inflate(R.layout.chat_recycler_view_item_coming, parent, false)
        }

         */

        val view = inflater.inflate(R.layout.chat_recycler_view_item_coming, parent, false)

        return ChatHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }
}