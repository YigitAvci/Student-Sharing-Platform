package com.avciy.studentsharingplatform.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.avciy.studentsharingplatform.LoginActivity
import com.avciy.studentsharingplatform.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account_details.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_upload.*
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class AccountDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var selectedImageUri: Uri? = null
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val user = FirebaseAuth.getInstance()
    private var isImageChanged = false

    private var departmentName = "Computer Engineering"

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        departmentName = parent!!.selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        departmentName = "Computer Engineering"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAccountDatasFromStorage()
    }

    private fun getPosForSpinner(department : String) {
        when(department) {
            "Computer Engineering" -> spinner_departments.setSelection(0)
            "Industry Engineering" -> spinner_departments.setSelection(1)
            "Electricity Engineering" -> spinner_departments.setSelection(2)
            "Mechatronics Engineering" -> spinner_departments.setSelection(3)
            "Civil Engineering" -> spinner_departments.setSelection(4)
        }
    }

    private fun getAccountDatasFromStorage() {
        firebaseFirestore.collection("Users").whereEqualTo("ID", user.uid.toString()).get().addOnSuccessListener {
            for (document in it) {
                if (document.get("photoURL") != null && document.get("photoURL") != "unknown") {
                    var uri = document.get("photoURL") as String
                    //Log.d("Main", uri)
                    Picasso.get().load(uri).into(profile_image)
                }

                if (document.get("displayName") != null) {
                    var displayName = document.get("displayName") as String
                    edittext_dis_name.hint = displayName
                }

                if (document.get("department") != null) {
                    var department = document.get("department") as String

                    getPosForSpinner(department)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edittext_dis_name.isEnabled = false
        spinner_departments.isEnabled = false
        edittext_new_password.isEnabled = false
        edittext_new_password_verify.isEnabled = false

        ArrayAdapter.createFromResource(requireContext(), R.array.departmentNames, R.layout.custom_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_departments.adapter = it
        }
        spinner_departments.onItemSelectedListener = this

        profile_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission
                            .READ_EXTERNAL_STORAGE
                    ), 1
                )
            } else {
                startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2)
            }
        }

        checkbox_dis_name.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!checkbox_dis_name.isChecked) {
                edittext_dis_name.isEnabled = false
                edittext_dis_name.text = null
            } else {
                edittext_dis_name.isEnabled = true
            }
        }

        checkbox_spinner.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!checkbox_spinner.isChecked) {
                spinner_departments.isEnabled = false
            } else {
                spinner_departments.isEnabled = true
            }
        }

        checkbox_password.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!checkbox_password.isChecked) {
                edittext_new_password.isEnabled = false
                edittext_new_password.text = null
                edittext_new_password_verify.isEnabled = false
                edittext_new_password_verify.text = null
            } else {
                edittext_new_password.isEnabled = true
                edittext_new_password_verify.isEnabled = true
            }
        }

        button_edit.setOnClickListener {

            var filename = user.currentUser!!.uid

            if (isImageChanged == true && selectedImageUri != null) {

                uploadPPToStorage(selectedImageUri!!, filename)
            }

            if (checkbox_dis_name.isChecked && edittext_dis_name != null) {
                firebaseFirestore.collection("Users").document(filename).update(
                    "displayName", edittext_dis_name.text
                        .toString()
                )
            }

            if (checkbox_spinner.isChecked) {
                firebaseFirestore.collection("Users").document(filename).update("department", departmentName)
            }

            if(checkbox_password.isChecked) {
                val newPassword = edittext_new_password.text.toString()
                val newPasswordVerified = edittext_new_password_verify.text.toString()
                if (newPassword == newPasswordVerified) {
                    user.currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
                        if (it.isSuccessful) {

                            Toast.makeText(requireContext(), "You have to login again since you have changed your password!", Toast.LENGTH_LONG).show()
                            user.signOut()
                            Toast.makeText(activity, "The session has ended!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(activity, LoginActivity::class.java))
                            activity?.finish()

                        }
                    }?.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
            Navigation.findNavController(activity!!, R.id.fragmentHolder)
                .navigate(R.id.action_accountDetailsFragment_to_settingsFragment)
        }
    }

    private fun uploadPPToStorage(uri: Uri, filename: String) {
        var ref = firebaseStorage.reference.child("Accounts").child(filename)
        ref.putFile(uri).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener { uri ->
                firebaseFirestore.collection("Users").document(filename).update("photoURL", uri.toString())
                //Log.d("Main", "new image(${uri.toString()}) is just uploaded ")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (data != null) {
                selectedImageUri = data.data

                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedImageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        profile_image.setImageBitmap(bitmap)
                        isImageChanged = true
                    } else {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            selectedImageUri
                        )
                        profile_image.setImageBitmap(bitmap)
                        isImageChanged = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}