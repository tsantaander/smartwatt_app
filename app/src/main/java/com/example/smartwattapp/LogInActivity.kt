package com.example.smartwattapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.textfield.TextInputEditText

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
    private val DataBase : HashMap<String , HashMap<String , String>> = HashMap();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        try {
            //Siempre que se abre el LogInActivity cargamos la base de datos
            // En busca de un nuevo usuario registrado.
            loadDataBase();
            val loginBtn = findViewById<Button>(R.id.loginbtn);
            val returnBtn = findViewById<Button>(R.id.returnbtn);


            loginBtn.setOnClickListener{
                val login : Boolean = tryLogin();
                val textResult = findViewById<TextView>(R.id.loginResult);
                if (login){
                    val home : Intent = Intent(this , HomeActivity::class.java);
                    applyFadeInOutEffect(textResult , "Datos validados correctamente\nEspere un momento..." ,R.drawable.bordered_textview_success , visibleDuration = 2000 , CallBack = {startActivity(home)});
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
    private fun tryLogin() : Boolean {
        val textResult = findViewById<TextView>(R.id.loginResult);
        val emailTry : String = findViewById<TextInputEditText>(R.id.emailinpt).text.toString();
        val passwordTry : String = findViewById<TextInputEditText>(R.id.passwordinpt).text.toString();

        if (emailTry.isEmpty() || passwordTry.isEmpty()){
            applyFadeInOutEffect(textResult , "Debe rellenar todos los campos" ,R.drawable.bordered_textview_error , visibleDuration = 2000);
            return false
        }

        if (!emailTry.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")) || emailTry.isEmpty()){
            applyFadeInOutEffect(textResult , "Debe ingresar un email valido" ,R.drawable.bordered_textview_error , visibleDuration = 2000);
            return false
        }

        //Verificamos si el correo esta registrado en la base de datos.
        val existEmail : Boolean = this.DataBase.containsKey(emailTry);
        if (!existEmail){
            applyFadeInOutEffect(textResult , "El correo registrado no \nesta registrado en sistema." ,R.drawable.bordered_textview_error , visibleDuration = 2000);
            return false
        }
        val validPassword : Boolean = validatePassword(emailTry , passwordTry);
        if (!validPassword){
            applyFadeInOutEffect(textResult , "La contraseña ingresada es incorrecta." ,R.drawable.bordered_textview_error , visibleDuration = 2000);
            return false
        }
        return true
    }
    private fun validatePassword(correo : String , testPassword : String) : Boolean{
        // Función privada encargada de recuperar el modelo del usuario en la base de datos y
        // validar si el password con el que se logea coincide con el registrado en la base de datos.

        val modelUserByEmail : HashMap<String , String>? = this.DataBase.get(correo);
        if (!modelUserByEmail.isNullOrEmpty()){
            val realPasswordUser : String? = modelUserByEmail.get("password");
            return testPassword == realPasswordUser
        }
        return false
    }
    private fun loadNewUser() : HashMap<String , String>? {
        //Esta es una función encargada de cargar y retornar un HashMap de un Nuevo Usuario recibido desde el SigInActivity
        //En caso de que uno de sus campos sea null o este vacio devolvera null y no retornara el HashMap previamente mencionado
        val currentIntent = intent;
        val nombre : String? = intent.getStringExtra("NOMBRE");
        val correo : String? = intent.getStringExtra("CORREO");
        val codigo : String? = intent.getStringExtra("CODIGO");
        val password : String? = intent.getStringExtra("PASSWORD");
        if (!nombre.isNullOrEmpty() || !correo.isNullOrEmpty() || !codigo.isNullOrEmpty() || !password.isNullOrEmpty()){
            val newUser = HashMap<String , String>();
            newUser["nombre"] = nombre.toString();
            newUser["correo"] = correo.toString();
            newUser["codigo"] = codigo.toString();
            newUser["password"] = password.toString();
            return newUser
        }
        return null
    }
    private fun loadDataBase(){
        //Esta es la función encargada de registrar el nuevo usuario enviado desde SignInActivity
        //En la variable local DataBase, que simulara la base de datos en memoria.
        val newUser : HashMap<String , String>? = loadNewUser();
        if (!newUser.isNullOrEmpty() && newUser.containsKey("correo")){ // Si el nuevo usuario no es null y el cuerpo del nuevo usuario posee la key correo se ingresa a la base de datos.
            this.DataBase[newUser.get("correo").toString()] = newUser;
            Log.v("New User" , newUser.toString());
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