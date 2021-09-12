package com.avciy.studentsharingplatform.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.avciy.studentsharingplatform.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_upload.*
import java.lang.Exception
import java.sql.Timestamp
import java.util.*
import java.util.jar.Manifest

class UploadFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var selectedImage : Uri? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseFirestore = FirebaseFirestore.getInstance()
    private var className = "Calculus 1 (Calc1)"

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        className = parent!!.selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        className = "Calculus 1 (Calc1)"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(requireContext(), R.array.classNames, R.layout.custom_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_upload_class_name.adapter = it
        }

        spinner_upload_class_name.onItemSelectedListener = this

        imageView_upload.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }else {
                startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2)
            }
        }

        button_upload.setOnClickListener {
            if(selectedImage != null  && edittext_upload_class_date.text.toString() != "") {

                var classDate = edittext_upload_class_date.text.toString()

                var uuid = UUID.randomUUID()
                var imageName = "${uuid}.jpg"
                val storage = FirebaseStorage.getInstance()
                val reference = storage.reference
                val notesReference = reference.child("notes").child(imageName)
                notesReference.putFile(selectedImage!!).addOnSuccessListener {

                    notesReference.downloadUrl.addOnSuccessListener {

                        val firestoreRef = firebaseFirestore.collection("Posts").document()

                        val postHashMap = hashMapOf<String, Any>()
                        postHashMap.put("likesAmount", "0")
                        postHashMap.put("postId", firestoreRef.id)
                        postHashMap.put("downloadUrl", it.toString())
                        postHashMap.put("userEmail", firebaseAuth.currentUser!!.email.toString())
                        postHashMap.put("classNameOrCode", className!!)
                        postHashMap.put("classDate", classDate)
                        postHashMap.put("UpdateDate", com.google.firebase.Timestamp.now())

                        firestoreRef.set(postHashMap).addOnCompleteListener {

                            if(it.isComplete && it.isSuccessful) {
                                Toast.makeText(requireContext(), "Your note is uploaded successfully", Toast.LENGTH_SHORT).show()
                                Navigation.findNavController(activity!!, R.id.fragmentHolder).navigate(R.id.action_uploadFragment_to_HomeFragment)
                            }

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    }
                }

            }else {
                Toast.makeText(requireContext(), "Something is lack!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data

            try {
                if(Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedImage!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    imageView_upload.setImageBitmap(bitmap)
                }else {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
                    imageView_upload.setImageBitmap(bitmap)
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}