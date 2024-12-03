package com.example.smartwattapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.smartwattapp.Models.Medidor
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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