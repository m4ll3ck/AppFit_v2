package com.example.appfit_v2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    companion object {
        const val URL_LOGIN = "http://192.168.1.6/AppFit/loginUsuario.php"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val txtCorreo: EditText = findViewById(R.id.txt_correo)
        val txtClave: EditText = findViewById(R.id.txt_clave)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(correo, clave) { success, message ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Inicio de sesión exitoso: $message", Toast.LENGTH_SHORT).show()
                            // Redirigir a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Para evitar que el usuario pueda regresar al login con el botón atrás
                        } else {
                            Toast.makeText(this, "Error en el inicio de sesión: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String, callback: (Boolean, String) -> Unit) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("correo", email)
        json.put("clave", password)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(URL_LOGIN)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Network Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData)
                val success = json.getBoolean("success")
                val message = json.getString("message")
                callback(success, message)
            }
        })
    }

     fun register(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
