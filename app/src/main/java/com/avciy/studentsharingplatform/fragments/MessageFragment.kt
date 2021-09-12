package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.avciy.studentsharingplatform.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.latestmessages_recycler_view_item.view.*

class MessageFragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseFirestore = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView_latestMessages.adapter = adapter
        getLatestMessages()

        adapter.setOnItemClickListener { item, view ->
            item as LatestMessageItem
            val action = MessageFragmentDirections.actionMessageFragmentToChatFragment(
                item.otherUserStudentNumber,
                item.otherUserName,
                item.otherUserDepartment,
                item.photoUrl
            )
            Navigation.findNavController(view).navigate(action)
        }

        button_newMessage.setOnClickListener {
            val action = MessageFragmentDirections.actionMessageFragmentToMessageFragmentUserSelection()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun getLatestMessages() {
        var otherUserPhoto: String?
        var otherUserName: String?
        var otherUserDepartment: String?
        var otherUserStudentNumber: String?
        val ref = firebaseFirestore.collection("LatestMessages")
        val query = ref.orderBy("time", Query.Direction.DESCENDING)
        query.addSnapshotListener { value, error ->
            Log.d("getLatestMessages", "Query is executed")
            if (error != null) {
                Log.d("getLatestMessages", "error")
            } else if (value != null) {
                adapter.clear()
                for (doc in value!!) {
                    if (doc.getString("fromStudentNumber") == firebaseAuth.currentUser!!.email!!.substring(0, 9)) {
                        firebaseFirestore.collection("Users").whereEqualTo("studentNumber", doc
                            .getString("toStudentNumber")).get().addOnSuccessListener {
                            for(document in it) {
                                if (it != null) {
                                    Log.d("doc", doc.getString("fromStudentNumber").toString() + "\t" + doc.get("time"))
                                    otherUserPhoto =  document.getString("photoURL").toString()
                                    otherUserStudentNumber = document.getString("studentNumber").toString()
                                    otherUserDepartment = document.getString("department").toString()
                                    otherUserName = document.getString("displayName").toString()
                                    adapter.add(LatestMessageItem(true, doc.getString("message")!!,
                                        otherUserPhoto,
                                        otherUserName, otherUserStudentNumber, otherUserDepartment))
                                }
                            }
                        }
                    }
                    else if(doc.getString("toStudentNumber") == firebaseAuth.currentUser!!.email!!.substring(0, 9)) {
                        firebaseFirestore.collection("Users").whereEqualTo("studentNumber", doc
                            .getString("fromStudentNumber").toString()).get().addOnSuccessListener {
                            for (document in it) {
                                if (it != null) {
                                    Log.d("doc", doc.getString("fromStudentNumber").toString())
                                    otherUserPhoto = document.getString("photoURL").toString()
                                    otherUserStudentNumber = document.getString("studentNumber").toString()
                                    otherUserDepartment = document.getString("department").toString()
                                    otherUserName = document.getString("displayName").toString()
                                    adapter.add(LatestMessageItem(false, doc.getString("message")!!, otherUserPhoto,
                                        otherUserName, otherUserStudentNumber, otherUserDepartment))
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

class LatestMessageItem(val side: Boolean?, val text: String, val photoUrl: String?, val otherUserName: String?, val
otherUserStudentNumber: String?, val otherUserDepartment: String?) :
    Item<GroupieViewHolder>() {

    val finalText = "You:  " + text
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_displayName.text = otherUserName
        if(side == true) { //this case represents that current user is sender
            viewHolder.itemView.textView_message.text = finalText
        }else {//and this case is represents that current user is reciever
            viewHolder.itemView.textView_message.text = text
        }
        Picasso.get().load(photoUrl).into(viewHolder.itemView.imageView_profilePhoto)
    }

    override fun getLayout(): Int {
        return R.layout.latestmessages_recycler_view_item
    }

}
