package com.openlab.ui.signup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.openlab.R
import com.openlab.model.User
import com.openlab.model.UserClient
import com.openlab.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val TAG: String = "SignUpActivity"
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        db = FirebaseFirestore.getInstance()

        imgBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        btnRegister.setOnClickListener {

            signUp()

        }

        hideKeyboard()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        if (progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.INVISIBLE
    }

    private fun hideKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun registerNewAccout(firstName: String, lastName: String, phone: String, email: String, password: String) {
        showProgress()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "onComplete: AuthState:  ${FirebaseAuth.getInstance().currentUser?.uid}")
                val user = User(FirebaseAuth.getInstance().currentUser?.uid.toString(), firstName, lastName, phone, email)
                val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
                db.firestoreSettings = settings
                val userReference: DocumentReference = db.collection(getString(R.string.collection_users)).document(user.uid)
                userReference.set(user).addOnCompleteListener { task ->
                    Log.e(TAG, task.toString())
                    if (task.isSuccessful) {
                        (applicationContext as UserClient).user = user
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                hideProgress()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                hideProgress()
            }
        }.addOnFailureListener {
            Log.e(TAG, it.toString())
        }
    }

    private fun signUp() {
        if (edtEmail.text.toString().isNotEmpty()
            && edtPassword.text.toString().isNotEmpty()
            && edtPasswordVerification.text.toString().isNotEmpty()
            && edtFirstName.text.toString().isNotEmpty()
            && edtLastName.text.toString().isNotEmpty()
            && edtPhone.text.toString().isNotEmpty()
        ) {
            if (edtPassword.text.toString() == edtPasswordVerification.text.toString()) {
                Log.e(TAG, "onClick: attempting to registration.")
                registerNewAccout(
                    edtFirstName.text.toString(),
                    edtLastName.text.toString(),
                    edtPhone.text.toString(),
                    edtEmail.text.toString(),
                    edtPassword.text.toString()
                )
            } else {
                Toast.makeText(this, "Passwords do not Match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "You must fill out all the fields", Toast.LENGTH_SHORT).show()
        }
    }

}
