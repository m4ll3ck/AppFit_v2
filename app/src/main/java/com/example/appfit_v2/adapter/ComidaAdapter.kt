package com.example.appfit_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appfit_v2.Comida
import com.example.appfit_v2.R


class ComidaAdapter(private val comidadataList: List<Comida>): RecyclerView.Adapter<ComidaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ComidaViewHolder(layoutInflater.inflate(R.layout.disenio_recycler_comida, parent, false))
    }

    override fun getItemCount(): Int = comidadataList.size



    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val item = comidadataList[position]
        holder.render(item)
    }

}