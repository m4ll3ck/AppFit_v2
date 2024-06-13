package com.example.appfit_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfit_v2.adapter.ComidaAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Activity_Comida : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComidaAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_comida)

        recyclerView = findViewById(R.id.recyclerView_comida)
        recyclerView.layoutManager = LinearLayoutManager(this)


        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        val url = "http://192.168.1.6/AppFit/listarComida.php"
        GlobalScope.launch(Dispatchers.IO) {
            val data = StringBuilder()
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.connect()
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    data.append(line)
                }
                reader.close()
                Log.d("MainActivity", "Data fetched: $data")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching data", e)
            } finally {
                connection.disconnect()
            }

            launch(Dispatchers.Main) {
                parseData(data.toString())
            }
        }
    }

    private fun parseData(data: String) {
        try {
            if (data.isNotEmpty()) {
                val jsonArray = JSONArray(data)
                val comidaList = mutableListOf<Comida>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val idComida = jsonObject.getInt("id_comida")
                    val nombreComida = jsonObject.getString("nombre_comida")
                    val descripcionComida = jsonObject.getString("descripcion_comida")
                    val idTipoComida = jsonObject.getInt("id_tipo_comida")
                    val imagenComida = jsonObject.getString("imagen_comida")

                    val comida = Comida(idComida, nombreComida, descripcionComida, idTipoComida, imagenComida)
                    comidaList.add(comida)
                }
                adapter = ComidaAdapter(comidaList)
                recyclerView.adapter = adapter
            } else {

            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error parsing data", e)
        }
    }

}