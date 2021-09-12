package com.avciy.studentsharingplatform

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var user: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


    }

    fun signUp(view: View) {
        user = FirebaseAuth.getInstance()
        //our values which are constant
        val studentNumber: String
        val domain = "@ogr.gelisim.edu.tr"
        val email: String
        val password: String

        //control the user inputs whether they are proper or not
        if(edittext_userstudentemail_signUp.text.trim().toString().length == 9) {

            if(edittext_password_signUp.text.trim().toString().length >= 8) {

                if(edittext_verify.text.trim().toString() == edittext_password_signUp.text.trim().toString()) {

                    studentNumber = edittext_userstudentemail_signUp.text.trim().toString()
                    email = studentNumber + domain
                    password = edittext_verify.text.trim().toString()

                    user.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
                        if(it.isSuccessful) {
                            firebaseFirestore = FirebaseFirestore.getInstance()
                            val filename = user.currentUser!!.uid
                            firebaseFirestore.collection("Users").document(filename).set(hashMapOf(
                                "ID" to filename,
                                "displayName" to "unknown",
                                "department" to "Computer Engineering",//default value is computer engineering
                                "photoURL" to "unknown",
                                "studentNumber" to studentNumber
                            ))

                            val user = Firebase.auth.currentUser
                            user!!.sendEmailVerification().addOnCompleteListener {  it->
                                if(it.isSuccessful) {
                                    Toast.makeText(applicationContext, "User creation completed succesfully! We sent " +
                                            "you a verification email, check it out!", Toast.LENGTH_LONG).show()
                                    val intent = Intent(applicationContext, LoginActivity::class.java)
                                    intent.putExtra("isFirst", true)

                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }.addOnFailureListener { it->
                        Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }


                }else {

                    Toast.makeText(applicationContext, "you couldn't verify your password", Toast.LENGTH_LONG).show()

                }

            }else {

                Toast.makeText(applicationContext, "your password must have 8 characters", Toast.LENGTH_LONG).show()

            }

        }else {

            Toast.makeText(applicationContext, "you have to enter your student number which has 9 digit", Toast.LENGTH_LONG).show()

        }
    }
}