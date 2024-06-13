package com.example.appfit_v2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Activity_GrupoMuscular : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_grupo_muscular)
    }

    fun Pecho(view: View){
        val intent = Intent(this, Activity_Ejercicios::class.java)
        startActivity(intent)
    }

}