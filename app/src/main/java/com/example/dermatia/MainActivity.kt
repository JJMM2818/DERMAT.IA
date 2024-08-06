package com.example.dermatia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dermatia.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.btnAcceder.setOnClickListener {
            val correo = binding.etEmail.text.toString()
            val contrasena = binding.etPassword.text.toString()
            if (correo.isEmpty()){
                binding.etEmail.error = "Ingresa un correo"
                return@setOnClickListener
            }
            if(contrasena.isEmpty()){
                binding.etPassword.error = "Ingresa la contraseña"
                return@setOnClickListener
            }
            iniciarSesion(correo,contrasena)
        }
        binding.btnRegistrar.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }




    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText( this, "Inicio de sesion correcto",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText( this, "Datos incorrectos",Toast.LENGTH_SHORT).show()
            }
        }


    }


}