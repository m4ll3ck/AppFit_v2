package com.example.appfit_v2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class EditUserActivity : AppCompatActivity() {

    companion object {
        const val URL_UPDATE = "http://192.168.101.10/proyectoFIT/editarUsuario.php"  // Reemplaza con tu IP
    }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        userId = intent.getIntExtra("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Error al obtener ID de usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val txtNombreUsuario: EditText = findViewById(R.id.txt_edit_nombreUsuario)
        val txtCorreo: EditText = findViewById(R.id.txt_edit_correo)
        val txtClave: EditText = findViewById(R.id.txt_edit_clave)
        val btnUpdate: Button = findViewById(R.id.btnUpdate)

        // Obtener datos del usuario (este es un ejemplo, ajusta según tu lógica)
        // Aquí debes obtener los datos del usuario actual y rellenar los campos de texto
        // Por ejemplo, desde SharedPreferences o pasando los datos por intent.

        btnUpdate.setOnClickListener {
            val nombreUsuario = txtNombreUsuario.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (nombreUsuario.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                updateUser(nombreUsuario, correo, clave) { success, message ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Actualización exitosa: $message", Toast.LENGTH_SHORT).show()
                            // Aquí puedes agregar lógica adicional, como redirigir al usuario a otra actividad
                        } else {
                            Toast.makeText(this, "Error en la actualización: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateUser(name: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("nombre", name)
        json.put("correo", email)
        json.put("clave", password)
        json.put("id", userId)  // Asegúrate de obtener y pasar el ID del usuario correcto

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(URL_UPDATE)
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
