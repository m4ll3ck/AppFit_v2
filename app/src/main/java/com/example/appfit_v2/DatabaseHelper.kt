package com.example.appfit_v2

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object DatabaseHelper {

    private const val BASE_URL = "http://192.168.101.10/proyectoFIT/conexion.php"
    private val client = OkHttpClient()

    fun makePostRequest(endpoint: String, params: Map<String, String>, callback: (Boolean, String) -> Unit) {
        val formBodyBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBodyBuilder.add(key, value)
        }

        val formBody = formBodyBuilder.build()
        val request = Request.Builder()
            .url(BASE_URL + endpoint)
            .post(formBody)
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