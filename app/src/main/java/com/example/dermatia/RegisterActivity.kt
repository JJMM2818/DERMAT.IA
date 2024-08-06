package com.example.dermatia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dermatia.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var bindingRegister: ActivityRegisterBinding
    private lateinit var authRegister:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingRegister = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingRegister.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        authRegister = Firebase.auth

        bindingRegister.btnVolverRegister.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        bindingRegister.btnRegistrarUsuario.setOnClickListener {
            val correo: String = bindingRegister.etEmailRegister.text.toString()
            val contrasena: String = bindingRegister.etPasswordRegister.text.toString()
            val contrasenaConfirm: String = bindingRegister.etPasswordConfirmRegister.text.toString()
            if(correo.isEmpty()){
                bindingRegister.etEmailRegister.error = "Ingrese un correo"
                return@setOnClickListener
            }
            if(contrasena.isEmpty()){
                bindingRegister.etPasswordRegister.error = "Ingrese una contraseña"
                return@setOnClickListener
            }
            if(contrasenaConfirm.isEmpty()){
                bindingRegister.etPasswordConfirmRegister.error = "Ingrese confirmacion de contraseña"
                return@setOnClickListener
            }
            if(contrasena == contrasenaConfirm){
                registrarUsuario(correo, contrasena)
            }
        }
    }

    private fun registrarUsuario(correo: String, contrasena: String) {
        authRegister.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText( this, "El usuario se ha creado correctamente",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText( this, "Informacion invalida",Toast.LENGTH_SHORT).show()
            }
        }

    }
}