package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avciy.studentsharingplatform.ChatModel
import com.avciy.studentsharingplatform.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.chat_recycler_view_item_coming.view.*
import kotlinx.android.synthetic.main.chat_recycler_view_item_coming.view.textView
import kotlinx.android.synthetic.main.chat_recycler_view_item_going.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.function.LongFunction

class ChatFragment : Fragment() {
    var otherUserPhotoUrl: String? = null
    var otherUserNumber: String? = null

    //var adapter: ChatRecyclerViewAdapter? = null
    val firebaseFirestore = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<GroupieViewHolder>()
    val currentUserNumber = FirebaseAuth.getInstance().currentUser!!.email!!.substring(0, 9)
    var currentUserPhotoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            otherUserPhotoUrl = ChatFragmentArgs.fromBundle(it).photoUrl
            otherUserNumber = ChatFragmentArgs.fromBundle(it).studentNumber
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView_chat.adapter = adapter

        firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                currentUserPhotoUrl = it.getString("photoURL").toString()
                getMessages()
            }.addOnFailureListener {
                Log.d("currentUserProfilePhoto", "error")
            }

        button_send.setOnClickListener {
            Log.d("usageButton", "send button is clicked!")
            sendMessage()
        }
        /*
        var layoutManager = LinearLayoutManager(requireContext())
        recyclerView_chat.layoutManager = layoutManager
        adapter = ChatRecyclerViewAdapter(this@ChatFragment)
        recyclerView_chat.adapter = adapter
         */
        Toast.makeText(requireContext(), "${otherUserNumber}  ", Toast.LENGTH_SHORT).show()
    }

    private fun getMessages() {
        /**
         * bu kısımda firestoredan veriler çekilecek ve recyclerviewda listelenecek.................
         */
        var userPair: String? = null
        if (otherUserNumber!!.toInt() < currentUserNumber.toInt()) {
            userPair = otherUserNumber + currentUserNumber
        } else {
            userPair = currentUserNumber + otherUserNumber
        }
        val ref = firebaseFirestore.collection("Messages")
        val query = ref.orderBy("time", Query.Direction.ASCENDING)
        query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("getMessage", "error")
            } else if (value != null) {
                adapter.clear()
                for (doc in value!!) {
                    if (doc.getString("userPair").equals(userPair)) {
                        doc.getString("message")?.let {
                            if (doc.getString("fromStudentNumber") == otherUserNumber || doc
                                    .getString("fromStudentNumber") == currentUserNumber || doc.getString("toStudentNumber") == currentUserNumber || doc
                                    .getString
                                        ("toStudentNumber") == otherUserNumber
                            ) {
                                if (doc.getString("fromStudentNumber").toString() == otherUserNumber) {
                                    adapter.add((ChatComingItem(it, otherUserPhotoUrl)))
                                } else {
                                    adapter.add(ChatGoingItem(it, currentUserPhotoUrl))
                                }

                            }
                        }
                    }
                }
                if(adapter.itemCount > 0) {
                    println(adapter.itemCount)
                    recyclerView_chat.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    private fun sendMessage() {
        if (editTextView.text.toString() != "") {
            var userPair: String?
            if (otherUserNumber!!.toInt() < currentUserNumber.toInt()) {
                userPair = otherUserNumber + currentUserNumber
            } else {
                userPair = currentUserNumber + otherUserNumber
            }
            val ref = firebaseFirestore.collection("Messages").document()
            val chat = ChatModel(
                ref.id, editTextView.text.trim().toString(), System.currentTimeMillis(), otherUserNumber!!,
                currentUserNumber, userPair
            )
            ref.set(chat).addOnSuccessListener {
                Log.d("ChatLog", "message has been stored in the database")
                //listenForMessages(ref)
            }
            val refLatestMessages = firebaseFirestore.collection("LatestMessages").document(userPair)
            refLatestMessages.set(chat).addOnSuccessListener {
                Log.d("ChatLog", "message also has been stored in the latestMessages database")
            }
            editTextView.text.clear()
        }
    }

    /**
    private fun listenForMessages(ref: DocumentReference) {
    ref.addSnapshotListener { value, error ->
    if(error != null) {
    Log.d("listenForMessage", "error")
    }
    if(value != null && value.exists()) {
    Log.d("listenForMessage", "success")
    Log.d("listenforMessage", value.get("message").toString())
    if(value.get("fromStudentNumber") == otherUserNumber) {
    adapter.add(ChatGoingItem(value.get("message").toString()))
    }else if(value.get("fromStudentNumber") == otherUserNumber) {
    adapter.add(ChatGoingItem(value.get("message").toString()))
    }
    adapter!!.notifyDataSetChanged()
    }
    }
    }
     */

}

class ChatComingItem(val text: String, val photoUrl: String?) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        Picasso.get().load(photoUrl).into(viewHolder.itemView.imageView_profilePhoto_coming)
    }

    override fun getLayout(): Int {
        return R.layout.chat_recycler_view_item_coming
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}

class ChatGoingItem(val text: String, val photoUrl: String?) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        Picasso.get().load(photoUrl).into(viewHolder.itemView.imageView_profilePhoto_going)
    }

    override fun getLayout(): Int {
        return R.layout.chat_recycler_view_item_going
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}