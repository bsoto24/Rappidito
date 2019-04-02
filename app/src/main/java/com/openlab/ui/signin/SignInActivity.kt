package com.openlab.ui.signin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.openlab.R
import com.openlab.model.User
import com.openlab.model.UserClient
import com.openlab.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val TAG = "SignInActivity"
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setUpFirebaseAuth()
        btnLetsGo.setOnClickListener {
            signIn()
        }
        imgBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        hideKeyboard()
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)
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

    private fun setUpFirebaseAuth() {
        Log.e(TAG, "setupFirebaseAuth: started.")
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                Log.e(TAG, "onAuthStateChanged:signed_in: ${user.uid}")
                Log.e(TAG, "Authenticated with: ${user.email}")
                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
                db.firestoreSettings = settings
                val userReference: DocumentReference =
                    db.collection(getString(R.string.collection_users)).document(user.uid)
                userReference.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "onComplete: successfully set the user client.")
                        Log.e(TAG, "onComplete ${task.result?.get("uid")}")
                        Log.e(TAG, "onComplete ${task.result?.get("firstName")}")
                        Log.e(TAG, "onComplete ${task.result?.get("lastName")}")
                        Log.e(TAG, "onComplete ${task.result?.get("email")}")
                        Log.e(TAG, "onComplete ${task.result?.get("phone")}")

                        (applicationContext as UserClient).user = task.result?.toObject(User::class.java)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.e(TAG, "onAuthStateChanged:signed_out")
            }
        }
    }

    private fun signIn() {
        if (edtEmail.text.toString().isNotEmpty() && edtPassword.text.toString().isNotEmpty()) {
            Log.e(TAG, "onClick: attempting to authenticate.")
            showProgress()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener {
                    hideProgress()
                }.addOnFailureListener {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    hideProgress()
                }
        } else {
            Toast.makeText(this, "You didn't fill in all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}
