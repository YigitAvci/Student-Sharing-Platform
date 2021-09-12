package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avciy.studentsharingplatform.HomeRecyclerViewAdapter
import com.avciy.studentsharingplatform.NoteModel
import com.avciy.studentsharingplatform.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycler_view_item.*
import kotlinx.coroutines.*
import kotlin.math.log

class HomeFragment : Fragment(), HomeRecyclerViewAdapter.Listener {

    var firebaseAuth: FirebaseAuth? = null
    var firebaseFirestore: FirebaseFirestore? = null
    var postIdArrayFromFB: ArrayList<String> = ArrayList()
    var userEmailFromFB: ArrayList<String> = ArrayList()
    var downloadUrlFromFB: ArrayList<String> = ArrayList()
    var classNameOrCodeFromFB: ArrayList<String> = ArrayList()
    var classDateFromFB: ArrayList<String> = ArrayList()
    var updateDateFromFB: ArrayList<String> = ArrayList()
    var likesArrayFromFB: ArrayList<String> = ArrayList()
    var ownLikesArrayFromFB: ArrayList<Boolean> = ArrayList()
    var layoutManager: LinearLayoutManager? = null
    var scrollPos: Int? = null

    var adapter: HomeRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        getDataFromFirestore()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        adapter = HomeRecyclerViewAdapter(
            this@HomeFragment, ownLikesArrayFromFB, likesArrayFromFB, postIdArrayFromFB, userEmailFromFB,
            downloadUrlFromFB,
            classNameOrCodeFromFB, classDateFromFB, updateDateFromFB, firebaseFirestore!!
        )
        recyclerView.adapter = adapter

    }

    override fun onItemClick(noteModel: NoteModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
            noteModel.userEmail,
            noteModel.classNameOrCode,
            noteModel.classDate,
            noteModel.downloadUrl,
            noteModel.updateDate
        )
        Navigation.findNavController(view!!).navigate(action)
    }

    //if an error exists, delete private key word
    private fun getDataFromFirestore() {
        firebaseFirestore!!.collection("Posts").orderBy("UpdateDate", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            postIdArrayFromFB.clear()
                            userEmailFromFB.clear()
                            downloadUrlFromFB.clear()
                            classNameOrCodeFromFB.clear()
                            classDateFromFB.clear()
                            updateDateFromFB.clear()
                            likesArrayFromFB.clear()
                            ownLikesArrayFromFB.clear()

                            val documents = value.documents
                            for (document in documents) {
                                var ownLikes = false
                                val likes = document.getString("likesAmount").toString()
                                val postId = document.get("postId") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val userEmail = document.get("userEmail") as String
                                val classNameOrCode = document.get("classNameOrCode") as String
                                val classDate = document.get("classDate") as String
                                val timestamp = document.get("UpdateDate") as Timestamp
                                val updateDate = timestamp.toDate().toString()

                                //coroutine'leri kullan
                                val docPath = postId + firebaseAuth!!.currentUser!!.email!!.substring(0, 9)
                                Log.d("docPath", docPath)
                                val docRef = firebaseFirestore!!.collection("Likes").document(docPath)
                                docRef.get().addOnSuccessListener {
                                    if (it != null && it.data != null) {
                                        ownLikes = true
                                        Log.d("data", it.data.toString())
                                        if (userEmail == firebaseAuth!!.currentUser!!.email) {
                                            Log.d("ownlikes***", ownLikes.toString())
                                            ownLikesArrayFromFB.add(ownLikes)
                                            likesArrayFromFB.add(likes)
                                            postIdArrayFromFB.add(postId)
                                            downloadUrlFromFB.add(downloadUrl)
                                            userEmailFromFB.add(userEmail)
                                            classNameOrCodeFromFB.add(classNameOrCode)
                                            classDateFromFB.add(classDate)
                                            updateDateFromFB.add(updateDate)
                                        }
                                    }else {
                                        if (userEmail == firebaseAuth!!.currentUser!!.email) {
                                            Log.d("ownlikes***", ownLikes.toString())
                                            ownLikesArrayFromFB.add(ownLikes)
                                            likesArrayFromFB.add(likes)
                                            postIdArrayFromFB.add(postId)
                                            downloadUrlFromFB.add(downloadUrl)
                                            userEmailFromFB.add(userEmail)
                                            classNameOrCodeFromFB.add(classNameOrCode)
                                            classDateFromFB.add(classDate)
                                            updateDateFromFB.add(updateDate)
                                        }
                                    }
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
    }

}