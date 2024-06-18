package com.example.appfit_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val URL_REGISTER = "http://192.168.101.10/proyectoFIT/registrarUsuario.php"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        auth = Firebase.auth

        val txtNombreUsuario: EditText = findViewById(R.id.txt_nombreUsuario)
        val txtCorreo: EditText = findViewById(R.id.txt_correo)
        val txtClave: EditText = findViewById(R.id.txt_clave)
        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombreUsuario = txtNombreUsuario.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (nombreUsuario.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(nombreUsuario, correo, clave)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            sendUserDataToServer(name, email, password)
                            Toast.makeText(this, "Se ha enviado un correo electrónico de verificación. Verifique su bandeja de entrada.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error al enviar correo de verificación: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendUserDataToServer(name: String, email: String, password: String) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("usuario", name)
        json.put("correo", email)
        json.put("contrasenia", password)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(URL_REGISTER)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData)
                val success = json.getBoolean("success")
                val message = json.getString("message")

                runOnUiThread {
                    if (success) {
                        Toast.makeText(this@RegisterActivity, "Datos guardados exitosamente: $message", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error al guardar datos: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}