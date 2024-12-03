package com.example.smartwattapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartwattapp.Models.Medidor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {
    private lateinit var auth : FirebaseAuth;
    private lateinit var medidorCollection : DatabaseReference;
    private lateinit var medidorTextView: TextView  // Declara aquí


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth;
        medidorCollection = FirebaseDatabase.getInstance().getReference("Medidor");
        medidorTextView = view.findViewById(R.id.medidorCode);

        // Agregar lógica específica para el fragmento home.xml
        obtenerMedidorDelUsuario();
    }
    private fun obtenerMedidorDelUsuario() {
        // Usando launchWhenStarted
        lifecycleScope.launchWhenStarted {
            try {
                // Obtén el usuario actual
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    Log.e("Error", "No hay un usuario autenticado.")
                    medidorTextView.text = "No se encontró un usuario."
                    return@launchWhenStarted
                }

                // Busca el medidor del usuario actual en Firebase
                val snapshot = medidorCollection
                    .orderByChild("user")
                    .equalTo(currentUser.uid)
                    .get()
                    .await()

                if (snapshot.exists()) {
                    // Obtén el primer medidor encontrado
                    val medidor = snapshot.children.firstOrNull()?.getValue(Medidor::class.java)
                    if (medidor != null) {
                        medidorTextView.text = "Codigo de Medidor: ${medidor.code}"
                    } else {
                        medidorTextView.text = "No se encontró el medidor."
                    }
                } else {
                    medidorTextView.text = "No se encontró el medidor."
                }
            } catch (e: Exception) {
                Log.e("Error", "Error obteniendo el medidor: ${e.message}")
                medidorTextView.text = "Ocurrió un error al cargar el medidor."
            }
        }
    }
}