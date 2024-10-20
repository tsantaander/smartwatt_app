package com.example.smartwattapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnalisisPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.analisis_panel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener{ item ->
            when (item.itemId){
                R.id.navigation_home -> {
                    val intent = Intent(this , HomeActivity::class.java);
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                    startActivity(intent)
                    true
                }
                R.id.navigation_analysis -> {
                    val intent = Intent(this , AnalisisPanelActivity::class.java);
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                    startActivity(intent)
                    true

                }
                R.id.navigation_settings -> {
                    val intent = Intent(this , ConfigPanelActivity::class.java);
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }
    }
}