package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.avciy.studentsharingplatform.HomeRecyclerViewAdapter
import com.avciy.studentsharingplatform.NoteModel
import com.avciy.studentsharingplatform.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), HomeRecyclerViewAdapter.Listener {

    var firebaseAuth : FirebaseAuth? = null
    var firebaseFirestore : FirebaseFirestore? = null
    var ownLikesArrayFromFB: ArrayList<Boolean> = ArrayList()
    var likesArrayFromFB : ArrayList<String> = ArrayList()
    var postIdArrayFromFB : ArrayList<String> = ArrayList()
    var userEmailFromFB : ArrayList<String> = ArrayList()
    var downloadUrlFromFB : ArrayList<String> = ArrayList()
    var classNameOrCodeFromFB : ArrayList<String> = ArrayList()
    var classDateFromFB : ArrayList<String> = ArrayList()
    var updateDateFromFB : ArrayList<String> = ArrayList()

    var adapter : HomeRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        getDataFromFirestore(null)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipAll.isChecked = true

        var layoutManager = LinearLayoutManager(requireContext())
        recyclerViewDetails.layoutManager = layoutManager

        adapter = HomeRecyclerViewAdapter(this@SearchFragment, ownLikesArrayFromFB, likesArrayFromFB,
            postIdArrayFromFB, userEmailFromFB,
            downloadUrlFromFB,
            classNameOrCodeFromFB, classDateFromFB, updateDateFromFB, firebaseFirestore!!)
        recyclerViewDetails.adapter = adapter

        chipAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore(null)
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }
        }
        chipAI.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Artificial Intelligence")
                chipAll.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipCalc1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Calculus 1 (Calc1)")
                chipAI.isChecked = false
                chipAll.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipCalc2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Calculus 2 (Calc2)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipAll.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipML.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Machine Learning (ML)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipAll.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipPhys1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Physics 1 (Phys1)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipAll.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipPhys2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Physics 2 (Phys2)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipAll.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipGeom.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Geometry (Geom)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipAll.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipDiff.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Differential Equation (Differ Equ)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipOS.isChecked = false
                chipGeom.isChecked = false
                chipAll.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

        chipOS.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == true) {
                getDataFromFirestore("Operating Systems (OS)")
                chipAI.isChecked = false
                chipCalc1.isChecked = false
                chipCalc2.isChecked = false
                chipPhys1.isChecked = false
                chipPhys2.isChecked = false
                chipML.isChecked = false
                chipAll.isChecked = false
                chipGeom.isChecked = false
                chipDiff.isChecked = false
            }else {
                chipAll.isChecked = true
            }
        }

    }

    override fun onItemClick(noteModel: NoteModel) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(noteModel.userEmail,
            noteModel.classNameOrCode, noteModel.classDate, noteModel.downloadUrl, noteModel.updateDate)
        Navigation.findNavController(view!!).navigate(action)
    }

    /**
     * redundant function
    fun getFilteredDataFromFirestore(className : String) {
        firebaseFirestore!!.collection("Posts").orderBy("UpdateDate", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
            }else {
                if(value != null) {
                    if(!value.isEmpty) {
                        userEmailFromFB.clear()
                        downloadUrlFromFB.clear()
                        classNameOrCodeFromFB.clear()
                        classDateFromFB.clear()
                        updateDateFromFB.clear()

                        val documents = value.documents
                        for(document in documents) {
                            val downloadUrl = document.get("downloadUrl") as String
                            val userEmail = document.get("userEmail") as String
                            val classNameOrCode = document.get("classNameOrCode") as String
                            val classDate = document.get("classDate") as String
                            val timestamp = document.get("UpdateDate") as Timestamp
                            val updateDate = timestamp.toDate().toString()

                            if(classNameOrCode == className) {
                                downloadUrlFromFB.add(downloadUrl)
                                userEmailFromFB.add(userEmail)
                                classNameOrCodeFromFB.add(classNameOrCode)
                                classDateFromFB.add(classDate)
                                updateDateFromFB.add(updateDate)

                                adapter!!.notifyDataSetChanged()
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }
                }else {

                }
            }

        }
    }
    */

    /** all-in-one function */
    fun getDataFromFirestore(className : String?) {
        firebaseFirestore!!.collection("Posts").orderBy("UpdateDate", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
            }else {
                if(value != null) {
                    if(!value.isEmpty) {
                        ownLikesArrayFromFB.clear()
                        likesArrayFromFB.clear()
                        postIdArrayFromFB.clear()
                        userEmailFromFB.clear()
                        downloadUrlFromFB.clear()
                        classNameOrCodeFromFB.clear()
                        classDateFromFB.clear()
                        updateDateFromFB.clear()

                        val documents = value.documents
                        for(document in documents) {
                            var ownLikes = false
                            val likes = document.getString("likesAmount").toString()
                            val postId = document.get("postId") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userEmail = document.get("userEmail") as String
                            val classNameOrCode = document.get("classNameOrCode") as String
                            val classDate = document.get("classDate") as String
                            val timestamp = document.get("UpdateDate") as Timestamp
                            val updateDate = timestamp.toDate().toString()

                            val docPath = postId + firebaseAuth!!.currentUser!!.email!!.substring(0, 9)
                            Log.d("docPath", docPath)
                            val docRef = firebaseFirestore!!.collection("Likes").document(docPath)
                            docRef.get().addOnSuccessListener {
                                if (it != null && it.data != null) {
                                    ownLikes = true
                                    Log.d("data", it.data.toString())
                                    Log.d("ownlikes", ownLikes.toString())
                                    if(className != null) {
                                        if(classNameOrCode == className) {
                                            ownLikesArrayFromFB.add(ownLikes!!)
                                            likesArrayFromFB.add(likes)
                                            postIdArrayFromFB.add(postId)
                                            downloadUrlFromFB.add(downloadUrl)
                                            userEmailFromFB.add(userEmail)
                                            classNameOrCodeFromFB.add(classNameOrCode)
                                            classDateFromFB.add(classDate)
                                            updateDateFromFB.add(updateDate)
                                        }
                                    }else {
                                        ownLikesArrayFromFB.add(ownLikes!!)
                                        likesArrayFromFB.add(likes)
                                        postIdArrayFromFB.add(postId)
                                        downloadUrlFromFB.add(downloadUrl)
                                        userEmailFromFB.add(userEmail)
                                        classNameOrCodeFromFB.add(classNameOrCode)
                                        classDateFromFB.add(classDate)
                                        updateDateFromFB.add(updateDate)
                                    }
                                }else {
                                    if(className != null) {
                                        if(classNameOrCode == className) {
                                            ownLikesArrayFromFB.add(ownLikes!!)
                                            likesArrayFromFB.add(likes)
                                            postIdArrayFromFB.add(postId)
                                            downloadUrlFromFB.add(downloadUrl)
                                            userEmailFromFB.add(userEmail)
                                            classNameOrCodeFromFB.add(classNameOrCode)
                                            classDateFromFB.add(classDate)
                                            updateDateFromFB.add(updateDate)
                                        }
                                    }else {
                                        ownLikesArrayFromFB.add(ownLikes!!)
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