package com.avciy.studentsharingplatform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        var intentExtra = intent.getBooleanExtra("isFirst", false)


        val firebaseUser : FirebaseUser? = auth.currentUser
        if(firebaseUser != null && intentExtra == false) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext, "wellcome ${firebaseUser.email?.subSequence(0, 9)}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun logInClicked(view: View) {
        if(edittext_userstudentemail.text.trim().toString().length == 9){

            if(edittext_password.text.trim().toString().length >= 8){
                val studentNumber = edittext_userstudentemail.text.trim().toString()
                val domain = "@ogr.gelisim.edu.tr"
                val email = studentNumber + domain
                val password = edittext_password.text.trim().toString()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { it ->
                    if(it.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        if(user!!.isEmailVerified) {

                            firebaseFirestore = FirebaseFirestore.getInstance()
                            firebaseFirestore.collection("Users").document(auth.uid.toString()).get()
                                .addOnSuccessListener {
                                    if (it != null && it.data == null) {
                                        var userProfileHashMap = HashMap<String, Any>()
                                        userProfileHashMap.put("ID", auth.uid.toString())
                                        userProfileHashMap.put("studentNumber", user.email.toString().subSequence(0, 9))
                                        firebaseFirestore = FirebaseFirestore.getInstance()
                                        firebaseFirestore.collection("Users").document(auth.uid.toString()).set(userProfileHashMap)
                                    }else {
                                        Log.d("*****************", "no such data")
                                    }
                                }.addOnFailureListener {
                                    Log.d("*****************", "get failed with ", it)
                                }
                            Toast.makeText(applicationContext, "welcome " + studentNumber, Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else {
                            Toast.makeText(applicationContext, "please, verify your email from email address university gave you", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener { it ->
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }else {
            Toast.makeText(applicationContext, "Student number must be 9 digits!", Toast.LENGTH_LONG).show()
        }
    }

    fun intentToSignUp(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}