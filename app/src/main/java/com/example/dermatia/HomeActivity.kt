package com.example.dermatia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dermatia.databinding.ActivityHomeBinding
import com.example.dermatia.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class HomeActivity : AppCompatActivity() {
    private lateinit var bindingHome: ActivityHomeBinding
    private lateinit var authHome: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHome = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingHome.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        authHome = Firebase.auth
        bindingHome.btnCerrarSesion.setOnClickListener {
            authHome.signOut()
            finish()
        }

        bindingHome.btnIniciar.setOnClickListener {
            val intent = Intent(this,Camara::class.java)
            startActivity(intent)
        }



    }





    }
