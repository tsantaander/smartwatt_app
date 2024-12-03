package com.example.smartwattapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.BoringLayout
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartwattapp.Database; // Base de datos global
import com.example.smartwattapp.databinding.LoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class LogInActivity : AppCompatActivity() {
    /// Se ha creado un valor privado dentro de la clase para simular una base de datos
    /// Esta poseera la siguiente estructura
    /// DataBase => HashMap {
    //      "cancinod080@gmail.com" to HasMap({
    //          "nombre" to "Diego Cancino",
    //          "correo" to "cancinod080@gmail.com",
    //          "codigo" to "1231231231",
    //          "contraseña" to  "contraseña123."
    //      },
    //      ...
    //      )
    // }
    private lateinit var binding : LoginBinding;
    private lateinit var auth : FirebaseAuth;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater);
        auth = Firebase.auth;
        setContentView(binding.root);
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        try {
            //Siempre que se abre el LogInActivity cargamos la base de datos
            // En busca de un nuevo usuario registrado.
            val loginBtn = findViewById<Button>(R.id.loginbtn);
            val returnBtn = findViewById<Button>(R.id.returnbtn);
            //val currentUser = auth.currentUser;
            //Log.v("Fire Auth" , currentUser.toString());

            loginBtn.setOnClickListener{
                val textResult = findViewById<TextView>(R.id.loginResult);
                val emailTry : String = findViewById<TextInputEditText>(R.id.emailinpt).text.toString();
                val passwordTry : String = findViewById<TextInputEditText>(R.id.passwordinpt).text.toString();
                //Se debe hacer login aqui con Firabas
                auth.signInWithEmailAndPassword(emailTry , passwordTry).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val home : Intent = Intent(this , HomeActivity::class.java);
                        applyFadeInOutEffect(textResult , "Datos validados correctamente\nEspere un momento..." ,R.drawable.bordered_textview_success , visibleDuration = 2000 , CallBack = {startActivity(home)});
                    }else {
                        applyFadeInOutEffect(textResult , task.exception.toString() , R.drawable.bordered_textview_error , visibleDuration = 2000)
                    }
                }
            }

            returnBtn.setOnClickListener{
                val intent = Intent(this , MainActivity::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent);
            }
        }catch (e : Exception){
            Toast.makeText(this , "Hubo un error :(" , Toast.LENGTH_SHORT).show();
        }
    }

    private fun applyFadeInOutEffect(
        textView: TextView,
        message: String?,
        backgroundColor: Int,
        fadeInDuration: Long = 1000,
        visibleDuration: Long = 3000,
        fadeOutDuration: Long = 1000,
        CallBack : (() -> Unit)? = null // Función ejecutable opcional
    ) {
        // Configurar el TextView
        textView.setBackgroundResource(backgroundColor)
        textView.text = message
        textView.visibility = View.VISIBLE

        // Configurar la animación de FadeIn
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = fadeInDuration
        }

        // Configurar la animación de FadeOut
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = fadeOutDuration
            startOffset = visibleDuration
        }

        // Aplicar FadeIn
        textView.startAnimation(fadeIn)

        // Programar FadeOut
        Handler(Looper.getMainLooper()).postDelayed({
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    textView.visibility = View.GONE // Ocultar el TextView al final
                    CallBack?.invoke();
                }
            })
            textView.startAnimation(fadeOut)
        }, visibleDuration)
    }
}