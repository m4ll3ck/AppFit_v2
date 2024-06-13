package com.example.appfit_v2.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appfit_v2.Comida
import com.example.appfit_v2.R

class ComidaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val nombre_comida = view.findViewById<TextView>(R.id.tvNombre_comida)
    val imagen_comida = view.findViewById<ImageView>(R.id.ivComida)

    fun render(ComidaModel: Comida){
        nombre_comida.text = ComidaModel.nombre_comida

        // Cargar la imagen utilizando Glide
        Glide.with(imagen_comida.context)
            .load("data:image/jpeg;base64,${ComidaModel.imagen_comida}")
            .into(imagen_comida)
    }

}