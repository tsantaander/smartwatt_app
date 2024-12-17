import serial
import json
import firebase_admin
from firebase_admin import credentials, db

# Configuración de Firebase - Asegúrate de que la ruta al archivo JSON y la URL de la base de datos sean correctas
cred = credentials.Certificate(r"C:\Users\Usuario\Documents\Arduino\eva4_iot\sdk_smartwatt.json")
firebase_admin.initialize_app(cred, {
    "databaseURL": "https://smarttwatt-ed730-default-rtdb.firebaseio.com"
})
ref = db.reference('/consumo_energetico')

SERIAL_PORT = 'COM3'  # Asegúrate de usar el puerto correcto
SERIAL_RATE = 9600

def procesar_datos(datos_json):
    try:
        datos = json.loads(datos_json)
        print("Datos procesados:", datos)
        return datos
    except json.JSONDecodeError:
        print("Depuración:", datos_json)
        return None

def enviar_a_firebase(datos):
    if datos:
        try:
            # Usamos .push() para agregar un nuevo registro con un ID único generado por Firebase
            nuevo_registro = ref.push(datos)
            print(f"Datos enviados a Firebase con ID: {nuevo_registro.key}")
        except Exception as e:
            print("Error al enviar datos a Firebase:", e)
    else:
        print("No se enviaron datos a Firebase: datos inválidos")

try:
    with serial.Serial(SERIAL_PORT, SERIAL_RATE, timeout=1) as ser:
        print("Conexión serial establecida en:", SERIAL_PORT)
        
        while True:
            linea = ser.readline().decode('utf-8').strip()
            if linea:  
                if linea.startswith('{') and linea.endswith('}'):
                    datos = procesar_datos(linea)
                    enviar_a_firebase(datos)
                else:
                    print("Mensaje recibido arduino:", linea)

except serial.SerialException as e:
    print(f"Error al conectar con el puerto serial: {e}")
except KeyboardInterrupt:
    print("\nConexión terminada por el usuario.")