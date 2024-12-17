#include <SoftwareSerial.h>

// Pines y configuración de sensores
const int sensorPin = A0;  // Pin analógico para sensor ACS712

// Parámetros de calibración mejorados
const float FACTOR_CONVERSION = 5.0 / 1023.0;  // Factor de conversión ADC
const float SENSIBILIDAD_SENSOR = 0.185;       // Sensibilidad para ACS712 5A
const float VOLTAJE_REFERENCIA = 2.5;          // Voltaje de referencia del sensor

// Variables de medición con mayor precisión
float sensorVoltaje = 0;   
float corriente = 0;       
float offsetSensor = 2.5;  // Offset inicial ajustable

// Configuración para comunicación con ESP8266
SoftwareSerial espSerial(2, 3); // RX, TX para el ESP8266

// Variables para estadísticas adicionales
float potenciaInstantanea = 0;
float energiaAcumulada = 0;
unsigned long tiempoAnterior = 0;

void setup() {
  // Inicialización de comunicaciones
  Serial.begin(9600);         // Monitor serial a mayor velocidad
  espSerial.begin(9600);      // Comunicación con ESP8266 a mayor velocidad

  // Mensaje de inicio más informativo
  Serial.println("--- SISTEMA DE MONITOREO ENERGÉTICO ---");
  Serial.println("Inicializando sensores y calibración...");

  // Calibración dinámica mejorada
  calibrarSensor();

  // Mensaje de confirmación
  Serial.println("Sistema listo para monitoreo.");
  
  // Inicializar tiempo de referencia
  tiempoAnterior = millis();
}

void loop() {
  // Leer promedio de voltajes para reducir ruido
  sensorVoltaje = leerPromedioVoltaje(50);

  // Calcular corriente con mayor precisión
  corriente = calcularCorriente(sensorVoltaje);

  // Calcular potencia y energía
  calcularMetricas();

  // Mostrar resultados con formato mejorado
  mostrarResultados();

  // Enviar datos al ESP8266
  enviarDatos();

  delay(1000); // Intervalo de muestreo
}

// Función de lectura de promedio de voltajes
float leerPromedioVoltaje(int numMuestras) {
  float totalVoltaje = 0.0;
  float valorMaximo = 0;
  float valorMinimo = 5.0;

  for (int i = 0; i < numMuestras; i++) {
    int lecturaRaw = analogRead(sensorPin);
    float voltaje = lecturaRaw * FACTOR_CONVERSION;
    
    // Filtrar valores extremos
    valorMaximo = max(valorMaximo, voltaje);
    valorMinimo = min(valorMinimo, voltaje);
    
    totalVoltaje += voltaje;
    delay(1);
  }

  // Eliminar valores máximo y mínimo para más precisión
  totalVoltaje -= (valorMaximo + valorMinimo);
  return (totalVoltaje / (numMuestras - 2));
}

// Función mejorada de calibración
void calibrarSensor() {
  long total = 0;
  int numCalibraciones = 500; // Más muestras para mayor precisión

  Serial.println("Realizando calibración de sensor...");
  Serial.println("Asegúrese de que no haya corriente circulando");

  for (int i = 0; i < numCalibraciones; i++) {
    total += analogRead(sensorPin);
    delay(2);
  }

  // Calcular offset con mayor precisión
  offsetSensor = (total / numCalibraciones) * FACTOR_CONVERSION;
  
  Serial.print("Offset calibrado: ");
  Serial.print(offsetSensor, 4);
  Serial.println(" V");
}

// Cálculo de corriente con filtrado
float calcularCorriente(float voltaje) {
  float corrienteCalculada = (voltaje - offsetSensor) / SENSIBILIDAD_SENSOR;
  
  // Filtrar valores cercanos a cero
  if (abs(corrienteCalculada) < 0.05) {
    corrienteCalculada = 0.0;
  }

  return corrienteCalculada;
}

// Calcular métricas energéticas
void calcularMetricas() {
  unsigned long tiempoActual = millis();
  float tiempoTranscurrido = (tiempoActual - tiempoAnterior) / 1000.0; // Convertir a segundos

  // Calcular potencia instantánea (V * I)
  potenciaInstantanea = sensorVoltaje * corriente;

  // Acumular energía (watios-hora)
  energiaAcumulada += (potenciaInstantanea * tiempoTranscurrido) / 3600.0;

  // Actualizar tiempo
  tiempoAnterior = tiempoActual;
}

// Mostrar resultados con formato mejorado
void mostrarResultados() {
  Serial.println("--- DATOS DE CONSUMO ---");
  Serial.print("Voltaje Sensor: ");
  Serial.print(sensorVoltaje, 3);
  Serial.print(" V | Corriente: ");
  Serial.print(corriente, 3);
  Serial.print(" A | Potencia: ");
  Serial.print(potenciaInstantanea, 3);
  Serial.print(" W | Energía Acumulada: ");
  Serial.print(energiaAcumulada, 3);
  Serial.println(" Wh");
}

// Envío de datos en formato JSON
void enviarDatos() {
  String jsonData = "{";
  jsonData += "\"voltaje\":" + String(sensorVoltaje, 3);
  jsonData += ",\"corriente\":" + String(corriente, 3);
  jsonData += ",\"potencia\":" + String(potenciaInstantanea, 3);
  jsonData += ",\"energia_acumulada\":" + String(energiaAcumulada, 3);
  jsonData += "}";

  // Enviar el mensaje al ESP8266 usando SoftwareSerial
  Serial.println(jsonData);  // Enviar los datos en formato JSON
  
}