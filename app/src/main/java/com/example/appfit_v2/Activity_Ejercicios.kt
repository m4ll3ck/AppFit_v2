package com.example.appfit_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfit_v2.adapter.EjercicioAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Activity_Ejercicios  : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EjercicioAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_ejercicios)

        recyclerView = findViewById(R.id.recyclerView_ejercicios)
        recyclerView.layoutManager = LinearLayoutManager(this)


        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        val url = "http://192.168.1.6/AppFit/listarEjercicios.php"
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
                val ejerciciosList = mutableListOf<Ejercicios>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val idEjercicio = jsonObject.getInt("id_ejercicio")
                    val nombreEjercicio = jsonObject.getString("nombre_ejercicio")
                    val preparacionEjercicio = jsonObject.getString("preparacion_ejercicio")
                    val ejecucionEjercicio = jsonObject.getString("ejecucion_ejercicio")
                    val detalleEjercicio = jsonObject.getString("detalle_ejercicio")
                    val idGrupoMuscular = jsonObject.getInt("id_grupo_muscular")
                    val idEscenario = jsonObject.getInt("id_escenario")
                    val imagenEjercicio = jsonObject.getString("imagen_ejercicio")

                    val ejercicio = Ejercicios(idEjercicio, nombreEjercicio, preparacionEjercicio, ejecucionEjercicio, detalleEjercicio, idGrupoMuscular, idEscenario, imagenEjercicio)
                    ejerciciosList.add(ejercicio)
                }
                adapter = EjercicioAdapter(ejerciciosList)
                recyclerView.adapter = adapter
            } else {

            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error parsing data", e)
        }
    }

}