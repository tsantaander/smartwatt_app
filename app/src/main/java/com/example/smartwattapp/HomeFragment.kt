package com.example.smartwattapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartwattapp.Models.ConsumoEnergetico
import com.example.smartwattapp.Models.Medidor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var medidorCollection: DatabaseReference
    private lateinit var consumoEnergeticoListener: ValueEventListener
    private lateinit var consumoColecction: DatabaseReference

    // TextViews para actualizar en tiempo real
    private lateinit var medidorTextView: TextView
    private lateinit var consumoValueTextView: TextView
    private lateinit var voltageValueTextView: TextView
    private lateinit var currentValueTextView: TextView
    private lateinit var powerValueTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        medidorTextView = view.findViewById(R.id.medidorCode)
        consumoValueTextView = view.findViewById(R.id.tv_consumo_value)
        voltageValueTextView = view.findViewById(R.id.tv_voltage_value)
        currentValueTextView = view.findViewById(R.id.tv_current_value)
        powerValueTextView = view.findViewById(R.id.tv_power_value)

        // Configurar Firebase
        auth = Firebase.auth
        medidorCollection = FirebaseDatabase.getInstance().getReference("Medidor")
        consumoColecction = FirebaseDatabase.getInstance().getReference("consumo_energetico")

        // Obtener datos del medidor y consumo
        obtenerMedidorDelUsuario()
        obtenerDatosConsumoEnTiempoReal()
    }

    private fun obtenerDatosConsumoEnTiempoReal() {
        consumoEnergeticoListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val consumoList = snapshot.children.mapNotNull { dataSnapshot ->
                            try {
                                val data = dataSnapshot.value as? Map<String, Any>
                                if (data != null) {
                                    ConsumoEnergetico(
                                        corriente = data["corriente"]?.toString()?.toDoubleOrNull() ?: 0.0,
                                        energia_acumulada = data["energia_acumulada"]?.toString()?.toDoubleOrNull() ?: 0.0,
                                        potencia = data["potencia"]?.toString()?.toDoubleOrNull() ?: 0.0,
                                        voltaje = data["voltaje"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    )
                                } else {
                                    Log.w("Firebase", "Nodo no válido: ${dataSnapshot.key}")
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("Firebase", "Error al procesar nodo: ${dataSnapshot.key} - ${e.message}")
                                null
                            }
                        }

                        if (consumoList.isNotEmpty()) {
                            val consumo = consumoList.last()
                            Log.d("Consumo", "Datos de consumo actualizados: $consumo")
                            actualizarInterfazConDatosConsumo(consumo)
                        } else {
                            Log.d("Consumo", "No se encontraron datos de consumo para este usuario.")
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error procesando datos en tiempo real: ${e.message}")
                    }
                } else {
                    Log.d("Consumo", "No existe la colección consumo_energetico o está vacía.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error en la escucha de tiempo real: ${error.message}")
            }
        }

        consumoColecction.addValueEventListener(consumoEnergeticoListener)
    }




    private fun actualizarInterfazConDatosConsumo(consumo: ConsumoEnergetico) {
        view?.apply {
            findViewById<TextView>(R.id.tv_consumo_value)?.text = "Energía Acumulada: ${consumo.energia_acumulada}W"
            findViewById<TextView>(R.id.tv_voltage_value)?.text = "${consumo.voltaje}V"
            findViewById<TextView>(R.id.tv_current_value)?.text = "${consumo.corriente}A"
            findViewById<TextView>(R.id.tv_power_value)?.text = "${consumo.potencia}W"
        }
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
    // Importante: remover el listener cuando el fragmento se destruya
    override fun onDestroyView() {
        super.onDestroyView()
        consumoColecction.removeEventListener(consumoEnergeticoListener)
    }
}