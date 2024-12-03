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
import com.example.smartwattapp.Models.Medidor
import com.google.android.material.textfield.TextInputEditText
import com.example.smartwattapp.databinding.SigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: SigninBinding;
    private lateinit var auth: FirebaseAuth;
    private lateinit var database : DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SigninBinding.inflate(layoutInflater);
        auth = Firebase.auth;
        database = FirebaseDatabase.getInstance().getReference("Medidor"); // Instanciamos la colección Medidor
        setContentView(binding.root);
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            val SiginBtn = findViewById<Button>(R.id.siginbtn);
            val TextResult = findViewById<TextView>(R.id.TextResultSigIn);
            val returnBtn = findViewById<Button>(R.id.returnbtn);

            SiginBtn.setOnClickListener{
                val LoginIntent = Intent(this , LogInActivity::class.java);
                LoginIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                val sigin_result : Boolean = validate_datas_inpts(LoginIntent);
                if (sigin_result){ // Los inputs son validos por ende podemos crear el usuario en FireBase
                    returnBtn.isEnabled = false;
                    val EmailInpt = findViewById<TextInputEditText>(R.id.emailinpt).text.toString();
                    val PasswordInpt1 = findViewById<TextInputEditText>(R.id.passwordinpt1).text.toString();
                    val CodeInpt = findViewById<TextInputEditText>(R.id.codigoinpt).text.toString();// Se debe crear una colección Medidor con Un nuevo medidor y su codigo.
                    auth.createUserWithEmailAndPassword(EmailInpt , PasswordInpt1).addOnCompleteListener(this) {task ->
                        if (task.isSuccessful){
                            val userId : String? = auth.currentUser?.uid;
                            val idMedidor : String? = database.child("Medidor").push().key;
                            val newMedidor : Medidor = Medidor(id = idMedidor , user = userId , code = CodeInpt);
                            database.child(idMedidor!!).setValue(newMedidor).addOnCompleteListener(this){ task ->
                                if (task.isSuccessful){
                                    applyFadeInOutEffect(TextResult , "La cuenta y el medidor se han registrado correctamente." , R.drawable.bordered_textview_success , visibleDuration = 2000 , CallBack = {startActivity(LoginIntent)});
                                }else{
                                    applyFadeInOutEffect(TextResult , task.exception.toString() , R.drawable.bordered_textview_error , visibleDuration = 2000);
                                }
                            }
                        }else{
                            applyFadeInOutEffect(TextResult , task.exception.toString() , R.drawable.bordered_textview_error , visibleDuration = 2000);
                        }
                    }
                }
            }
            returnBtn.setOnClickListener{
                val intent = Intent(this , MainActivity::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent);
            }

        }catch (e : Exception){
            Toast.makeText(this , "Hubo un error :(" , Toast.LENGTH_SHORT).show() // Mostramos un Toast en caso que exista un error en la aplicación
        }

    }

    private fun validate_datas_inpts(intentLogIn : Intent) : Boolean {
        val EmailInpt = findViewById<TextInputEditText>(R.id.emailinpt).text.toString();
        val CodeInpt = findViewById<TextInputEditText>(R.id.codigoinpt).text.toString();// Se debe crear una colección Medidor con Un nuevo medidor y su codigo.
        val PasswordInpt1 = findViewById<TextInputEditText>(R.id.passwordinpt1).text.toString();
        val PasswordInpt2 = findViewById<TextInputEditText>(R.id.passwordinpt2).text.toString();
        val TextResult = findViewById<TextView>(R.id.TextResultSigIn);

        val generalErrors : Map<String , String> = mapOf(
            "emptyError" to "Debe llenar todos los campos!",
            "nombreError" to "➤ El nombre debe poseer menos de 30 caracteres y solo poseer letras!",
            "emailError" to "➤ El email es inválido!",
            "codeError" to "➤ El codigo es invalido debe poseer 10 numeros!",
            "passwordError" to "➤ La contraseña debe tener \n una longitud mayor a 8 caracteres \n\n➤ Debe contener letras mayusculas \n\n➤ Debe contener letras minuscular \n\n➤ Debe contener números \n\n➤ Debe contener caracteres \n especiales como & , . , ! , etc.",
            "passwordIncoincidenceError" to "➤ Las contraseñas no coinciden",
            "RegisterError" to "➤ El email con el que te intentas \n registrar ya existe en nuestro sistema \n O el codigo del medidor ya se encuentra registrado"
        )

        if (!EmailInpt.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$"))){ //Condicion en caso que el correo ingresado no haga match con la expresión regular propuesta.
            Log.v("Error Sign", "El Correo es invalido")
            applyFadeInOutEffect(TextResult , generalErrors.get("emailError") , R.drawable.bordered_textview_error);
            return false
        }
        if (CodeInpt.length != 10 || !CodeInpt.matches(Regex("^[0-9]{10}$"))) {
            Log.v("Error Sign", "El código debe ser numérico y tener 10 dígitos")
            applyFadeInOutEffect(TextResult , generalErrors.get("codeError") , R.drawable.bordered_textview_error);
            return false
        }
        // Validar si la contraseña contiene al menos una letra mayúscula, una minúscula, un número y un carácter especial
        if (PasswordInpt1.length < 8 || !PasswordInpt1.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.,#$-+¿¡~])[A-Za-z\\d@\$!%*?&.,#$-+¿¡~]{8,}$"))) {
            Log.v("Error Sign", "La contraseña debe tener al menos una letra mayúscula, una minúscula, un número y un carácter especial")
            applyFadeInOutEffect(TextResult , generalErrors.get("passwordError") , R.drawable.bordered_textview_error , visibleDuration = 5500);
            return false
        }
        if (PasswordInpt1 != PasswordInpt2){
            Log.v("Error Sign", "La contraseña debe tener al menos una letra mayúscula, una minúscula, un número y un carácter especial")
            applyFadeInOutEffect(TextResult , generalErrors.get("passwordIncoincidenceError") , R.drawable.bordered_textview_error , visibleDuration = 5500);
            return false
        }
        return true
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
