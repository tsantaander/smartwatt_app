package com.example.smartwattapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch

class ConfigPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.configuration_panel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val switchDark = findViewById<MaterialSwitch>(R.id.switchDark);
        val returnIcon = findViewById<ImageView>(R.id.return_icon);
        switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        returnIcon.setOnClickListener{
            val intent = Intent(this , AnalisisPanelActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
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