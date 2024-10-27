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

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signin)
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
                val sigin_result : Boolean = validate_datas_inpts(LoginIntent)
                if (sigin_result){
                    returnBtn.isEnabled = false;
                    applyFadeInOutEffect(TextResult , "Cuenta creada exitosamente!\nEspere un momento ..." , R.drawable.bordered_textview_success , visibleDuration = 2000 , CallBack = {startActivity(LoginIntent)});
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
        val NombreInpt = findViewById<TextInputEditText>(R.id.nameinpt).text.toString();
        val EmailInpt = findViewById<TextInputEditText>(R.id.emailinpt).text.toString();
        val CodeInpt = findViewById<TextInputEditText>(R.id.codigoinpt).text.toString();
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

        if (NombreInpt.isEmpty() || EmailInpt.isEmpty() || CodeInpt.isEmpty() || PasswordInpt1.isEmpty()){
            Log.v("Error Sign" , "Te faltan campos por completar");
            applyFadeInOutEffect(TextResult , generalErrors.get("emptyError") , R.drawable.bordered_textview_error);
            return false
        }
        if (NombreInpt.length > 30){ // Caso en el que la longitud del nombre ingresado sea mayor a 30 caracteres
            Log.v("Error Sign" , "El nombre debe poseer una longitud menor a 30 caracteres");
            applyFadeInOutEffect(TextResult , generalErrors.get("nombreError") , R.drawable.bordered_textview_error);
            return false
        }
        if (!NombreInpt.matches(Regex("^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$"))) {
            Log.v("Error Sign", "El nombre solo debe contener letras")
            applyFadeInOutEffect(TextResult , generalErrors.get("nombreError") , R.drawable.bordered_textview_error);
            return false
        }
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

        val newUserData = HashMap<String , String>();
        newUserData["nombre"] = NombreInpt;
        newUserData["codigo"] = CodeInpt;
        newUserData["password"] = PasswordInpt2;
        newUserData["enable"] = "activo"; //Campo para ver que el usuario este activo o no.
        newUserData["rol"] = "user";
        val addOnDb : Boolean = Database.createUser(EmailInpt , newUserData);
        Log.e("Current DB" , "${Database.data}");
        return if (addOnDb){
            return true
        }else{
            applyFadeInOutEffect(TextResult , generalErrors.get("RegisterError") , R.drawable.bordered_textview_error , visibleDuration = 5500);
            return false
        }
        //Enviamos datos al intent
        //intentLogIn.putExtra("NOMBRE" , NombreInpt);
        //intentLogIn.putExtra("CORREO" , EmailInpt);
        //intentLogIn.putExtra("CODIGO" , CodeInpt);
        //intentLogIn.putExtra("PASSWORD" , PasswordInpt);
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
