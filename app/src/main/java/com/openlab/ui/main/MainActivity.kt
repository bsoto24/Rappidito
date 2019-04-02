package com.openlab.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.openlab.R
import com.openlab.model.UserClient
import com.openlab.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgUser.setOnClickListener {

            logOut()

        }

        Toast.makeText(this, "Bienvenido ${(applicationContext as UserClient).user?.firstName}", Toast.LENGTH_SHORT).show()

    }

    private fun logOut(){

        FirebaseAuth.getInstance().signOut()
        (applicationContext as UserClient).user = null

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }

}
