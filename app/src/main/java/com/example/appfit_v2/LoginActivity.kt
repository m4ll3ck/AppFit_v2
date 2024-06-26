package com.example.appfit_v2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import android.text.InputType

class LoginActivity : AppCompatActivity() {

    companion object {
        const val URL_LOGIN = "http://192.168.101.10/proyectoFIT/loginUsuario.php"  // Reemplaza con tu IP
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth;
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        auth = Firebase.auth

        val txtCorreo: EditText = findViewById(R.id.txt_correo)
        val txtClave: EditText = findViewById(R.id.txt_clave)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val signUpButton: TextView = findViewById(R.id.sign_up_text)
        val lockIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.lock)
        val eyeOpenDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.eye)
        val eyeClosedDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.eye_closed)

        txtClave.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (txtClave.right - txtClave.compoundDrawables[2].bounds.width())) {
                    if (isPasswordVisible) {
                        // Ocultar la contraseña
                        txtClave.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        txtClave.setCompoundDrawablesWithIntrinsicBounds(lockIcon, null, eyeOpenDrawable, null)
                    } else {
                        // Mostrar la contraseña
                        txtClave.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        txtClave.setCompoundDrawablesWithIntrinsicBounds(lockIcon, null, eyeClosedDrawable, null)
                    }
                    isPasswordVisible = !isPasswordVisible
                    txtClave.setSelection(txtClave.text.length) // Mueve el cursor al final
                    return@setOnTouchListener true
                }
            }
            false
        }

        btnLogin.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val clave = txtClave.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(correo, clave)
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        sendUserDataToServer(email, password)
                    } else {
                        user?.sendEmailVerification()
                        Toast.makeText(this, "Por favor, verifique su correo electrónico primero", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendUserDataToServer(email: String, password: String) {
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
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData)
                val success = json.getBoolean("success")
                val message = json.getString("message")
                val userId = json.optInt("user_id", -1)
                val nombreUsuario = json.optString("nombre_usuario", "")
                val correo = json.optString("correo", "")
                val contrasenia = json.optString("contrasenia", "")

                runOnUiThread {
                    if (success) {
                        // Guardar datos en SharedPreferences
                        val sharedPreferences: SharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putInt("user_id", userId)
                            putString("nombre_usuario", nombreUsuario)
                            putString("correo", correo)
                            putString("contrasenia", contrasenia)
                            apply()
                        }
                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso: $message", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, EditUserActivity::class.java)
                        intent.putExtra("user_id", userId)
                        /*intent.putExtra("nombre_usuario", nombreUsuario)
                        intent.putExtra("correo", correo)
                        intent.putExtra("contrasenia", contrasenia)*/
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error en el inicio de sesión: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
