package com.avciy.studentsharingplatform.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.avciy.studentsharingplatform.LoginActivity
import com.avciy.studentsharingplatform.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        button_logout.setOnClickListener {
            if(auth != null) {
                auth.signOut()
                Toast.makeText(activity, "The session has ended!", Toast.LENGTH_LONG).show()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }

        button_edit_account.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAccountDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

}