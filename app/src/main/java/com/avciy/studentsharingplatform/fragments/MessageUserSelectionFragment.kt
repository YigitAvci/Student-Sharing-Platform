package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.avciy.studentsharingplatform.R
import com.avciy.studentsharingplatform.UserModel
import com.avciy.studentsharingplatform.UsersRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_message_user_selection.*

class MessageUserSelectionFragment : Fragment(), UsersRecyclerViewAdapter.Listener {

    var firebaseAuth: FirebaseAuth? = null
    var firebaseFirestore: FirebaseFirestore? = null
    var studentNumberFromFB: ArrayList<String> = ArrayList()
    var photoUrlFromFB: ArrayList<String> = ArrayList()
    var departmentFromFB: ArrayList<String> = ArrayList()
    var displayNameFromFB: ArrayList<String> = ArrayList()

    var adapter: UsersRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        getDataFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_user_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var layoutManager = LinearLayoutManager(requireContext())
        recyclerView_users.layoutManager = layoutManager

        adapter = UsersRecyclerViewAdapter(
            this@MessageUserSelectionFragment, photoUrlFromFB, studentNumberFromFB,
            displayNameFromFB, departmentFromFB
        )
        recyclerView_users.adapter = adapter

        button_searchUser.setOnClickListener {
            getSpecificDataFromFireStore()

        }

    }

    override fun onItemClick(userModel: UserModel) {

        val action = MessageUserSelectionFragmentDirections.actionMessageFragmentUserSelectionToChatFragment(
            userModel.studentNumber,
            userModel.displayName,
            userModel.department,
            userModel.photoUrl
        )
        Navigation.findNavController(view!!).navigate(action)

    }

    private fun getSpecificDataFromFireStore() {
        if (edittext_searchBar.text != null && edittext_searchBar.text.toString() != "") {
            firebaseFirestore!!.collection("Users").whereNotEqualTo(
                "studentNumber", firebaseAuth!!.currentUser!!.email!!.substring(0, 9)
            ).orderBy("studentNumber", Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        if (value != null) {
                            if (!value.isEmpty) {
                                departmentFromFB.clear()
                                displayNameFromFB.clear()
                                photoUrlFromFB.clear()
                                studentNumberFromFB.clear()
                                val documents = value.documents
                                for (document in documents) {
                                    val deparment = document.get("department") as String
                                    val displayName = document.get("displayName") as String
                                    val photoUrl = document.get("photoURL") as String
                                    val studentNumber = document.get("studentNumber") as String
                                    if (displayName.contains(edittext_searchBar.text.toString()) == true) {
                                        departmentFromFB.add(deparment)
                                        displayNameFromFB.add(displayName)
                                        photoUrlFromFB.add(photoUrl)
                                        studentNumberFromFB.add(studentNumber)
                                        adapter!!.notifyDataSetChanged()
                                    }
                                }
                                if (displayNameFromFB.isEmpty()) {
                                    Toast.makeText(requireContext(), "Couldn't find any user", Toast.LENGTH_SHORT)
                                        .show()
                                    getDataFromFireStore()
                                }
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Username input is necessary", Toast.LENGTH_SHORT)
                .show()
            getDataFromFireStore()
        }

    }

    private fun getDataFromFireStore() {

        firebaseFirestore!!.collection("Users").whereNotEqualTo(
            "studentNumber", firebaseAuth!!.currentUser!!.email!!.substring(0, 9)
        ).orderBy("studentNumber", Query
        .Direction.ASCENDING)
        .addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        departmentFromFB.clear()
                        displayNameFromFB.clear()
                        photoUrlFromFB.clear()
                        studentNumberFromFB.clear()
                        val documents = value.documents
                        for (document in documents) {
                            val deparment = document.get("department") as String
                            val displayName = document.get("displayName") as String
                            val photoUrl = document.get("photoURL") as String
                            val studentNumber = document.get("studentNumber") as String
                            departmentFromFB.add(deparment)
                            displayNameFromFB.add(displayName)
                            photoUrlFromFB.add(photoUrl)
                            studentNumberFromFB.add(studentNumber)
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}