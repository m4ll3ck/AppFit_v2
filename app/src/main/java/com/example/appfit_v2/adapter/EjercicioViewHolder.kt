package com.example.appfit_v2.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appfit_v2.Ejercicios
import com.example.appfit_v2.R

class EjercicioViewHolder (view: View): RecyclerView.ViewHolder(view) {

    val nombre_ejercicios = view.findViewById<TextView>(R.id.tvNombre)
    val imagen = view.findViewById<ImageView>(R.id.ivEjercicio)

    fun render(ejerciciosModel: Ejercicios){
        nombre_ejercicios.text = ejerciciosModel.nombre_ejercicio

        // Cargar la imagen utilizando Glide
        Glide.with(imagen.context)
            .load("data:image/jpeg;base64,${ejerciciosModel.imagen_ejercicio}")
            .into(imagen)
    }
}