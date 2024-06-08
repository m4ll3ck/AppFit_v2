package com.example.appfit_v2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val URL_REGISTER = "http://192.168.101.10/proyectoFIT/registrarUsuario.php"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

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
                registerUser(nombreUsuario, correo, clave) { success, message ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Registro exitoso: $message", Toast.LENGTH_SHORT).show()
                            // Aquí puedes agregar lógica adicional, como redirigir al usuario a otra actividad
                        } else {
                            Toast.makeText(this, "Error en el registro: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("nombre", name)
        json.put("correo", email)
        json.put("clave", password)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(URL_REGISTER)
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
}