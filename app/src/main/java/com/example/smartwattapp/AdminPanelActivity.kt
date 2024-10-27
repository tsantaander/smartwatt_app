package com.example.smartwattapp

import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartwattapp.Database;

class AdminPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge();
        setContentView(R.layout.admin_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        for ((email, userDetails) in Database.data) {
            val tableRow = TableRow(this)
            val emailText = TextView(this).apply {
                text = email
                gravity = Gravity.CENTER;
            }
            val nameText = TextView(this).apply {
                text = userDetails["nombre"] ?: "N/A"
                gravity = Gravity.CENTER;
            }
            val codeText = TextView(this).apply {
                text = userDetails["codigo"] ?: "N/A"
                gravity = Gravity.CENTER;

            }
            val passwordText = TextView(this).apply {
                text = userDetails["password"] ?: "N/A"
                gravity = Gravity.CENTER;
            }
            //tableRow.setPadding(20 , 20 , 20 , 20);
            tableRow.addView(emailText)
            tableRow.addView(nameText)
            tableRow.addView(codeText)
            tableRow.addView(passwordText)

            tableLayout.addView(tableRow)
        }
    }
}