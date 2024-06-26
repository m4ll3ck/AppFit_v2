package com.example.appfit_v2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditUserActivity : AppCompatActivity() {

    companion object {
        const val URL_UPDATE = "http://192.168.101.10/proyectoFIT/editarUsuario.php"  // Reemplaza con tu IP
    }

    private var userId: Int = -1
    private var isPasswordVisible: Boolean = false

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

        // Obtener datos de SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("nombre_usuario", "")
        val correo = sharedPreferences.getString("correo", "")
        val contrasenia = sharedPreferences.getString("contrasenia", "")

        // Rellenar los campos de texto
        txtNombreUsuario.setText(nombreUsuario)
        txtCorreo.setText(correo)
        txtClave.setText(contrasenia)

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

    private fun updateFirebaseUser(email: String, password: String, callback: (Boolean, String) -> Unit) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        user?.let {
            // Actualizar el correo electrónico
            user.updateEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualizar la contraseña
                    user.updatePassword(password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            callback(true, "Firebase update successful")
                        } else {
                            callback(false, "Error updating password: ${task.exception?.message}")
                        }
                    }
                } else {
                    callback(false, "Error updating email: ${task.exception?.message}")
                }
            }
        } ?: callback(false, "No user logged in")
    }

    private fun updateUser(name: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        updateFirebaseUser(email, password) { success, message ->
            if (success) {
                val client = OkHttpClient()

                val json = JSONObject()
                json.put("usuario", name)
                json.put("correo", email)
                json.put("contrasenia", password)
                json.put("id_usuario", userId)  // Asegúrate de obtener y pasar el ID del usuario correcto

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
            } else {
                callback(false, message)
            }
        }
    }
}
