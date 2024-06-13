package com.example.appfit_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appfit_v2.Ejercicios
import com.example.appfit_v2.R

class EjercicioAdapter(private val ejerciciosdatalist: List<Ejercicios>): RecyclerView.Adapter<EjercicioViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EjercicioViewHolder(layoutInflater.inflate(R.layout.disenio_recycler_view, parent, false))
    }

    override fun getItemCount(): Int = ejerciciosdatalist.size

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        val item = ejerciciosdatalist[position]
        holder.render(item)
    }
}