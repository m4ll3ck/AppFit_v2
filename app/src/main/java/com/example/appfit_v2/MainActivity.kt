package com.example.appfit_v2

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.appfit_v2.databinding.ActivityMainBinding
import com.example.appfit_v2.databinding.ActividadHomeBinding

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActividadHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActividadHomeBinding.inflate(layoutInflater)
        val bottomNavigationView1 = binding.bottomNavigationView1
        enableEdgeToEdge()
        setContentView(binding.root)
        replaceFragment(mi_programa())

        bottomNavigationView1.setOnItemSelectedListener {

            when(it.itemId){
                R.id.miprograma -> replaceFragment(mi_programa())
                else -> {

                }
            }
            true

        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView1)

        // Forzar actualización de íconos y texto
        bottomNavigationView.itemIconTintList = getColorStateList(R.color.nav_item_color)
        bottomNavigationView.itemTextColor = getColorStateList(R.color.nav_item_color)
    }

    private fun enableEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutpadre, fragment)
        fragmentTransaction.commit()
    }
}
