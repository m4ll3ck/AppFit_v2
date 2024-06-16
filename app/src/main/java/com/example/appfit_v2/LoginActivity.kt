package com.example.appfit_v2

import android.content.Context
import android.content.Intent
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

class LoginActivity : AppCompatActivity() {

    companion object {
        const val URL_LOGIN = "http://192.168.101.10/proyectoFIT/loginUsuario.php"  // Reemplaza con tu IP
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val txtCorreo: EditText = findViewById(R.id.txt_correo)
        val txtClave: EditText = findViewById(R.id.txt_clave)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(correo, clave) { success, message, userId ->
                    runOnUiThread {
                        if (success) {
                            with(sharedPreferences.edit()) {
                                putInt("user_id", userId)
                                apply()
                            }
                            Toast.makeText(this, "Inicio de sesión exitoso: $message", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, EditUserActivity::class.java)
                            intent.putExtra("user_id", userId)
                            startActivity(intent)
                            finish() // Opcional: finalizar la actividad de inicio de sesión
                        } else {
                            Toast.makeText(this, "Error en el inicio de sesión: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String, callback: (Boolean, String, Int) -> Unit) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("correo", email)
        json.put("contrasenia", password)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(URL_LOGIN)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Network Error: ${e.message}", -1)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData)
                val success = json.getBoolean("success")
                val message = json.getString("message")
                val userId = json.optInt("user_id", -1)
                callback(success, message, userId)
            }
        })
    }
}
