package com.example.smartwattapp.Models

data class ConsumoEnergetico(
    val corriente: Double = 0.0,
    val energia_acumulada: Double = 0.0,
    val potencia: Double = 0.0,
    val voltaje: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)