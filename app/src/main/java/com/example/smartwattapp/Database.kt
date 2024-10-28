package com.example.smartwattapp

object Database {
    // La estructura de la pequeña base de datos en memoria va a ser la siguiente
    // {
    //  'EMAIL_USER' : {
    //      'nombre' : 'NOMBRE_USER',
    //      'codigo' : 'CODIGO_ARDUINO',
    //      'password' : 'PASSWORD',
    //      'enable' : 'activo' | 'deshabilitado',
    //      'rol' : 'user' | 'admin'
    //  },
    //  'EMAIL_USER' : {
    //      'nombre' : 'NOMBRE_USER',
    //      'codigo' : 'CODIGO_ARDUINO',
    //      'password' : 'PASSWORD',
    //      'rol' : 'user' | 'admin'
    //      'enable' : 'activo' | 'deshabilitado',
    //  }
    // }
    //("admin@gmail.com" to hashMapOf(
    //"nombre" to "Administrador" ,
    //"password" to "admin123." ,
    //"rol" to "admin"
    //))
    val data : HashMap<String , HashMap<String , String>> = hashMapOf(
        ("admin@gmail.com" to hashMapOf(
            "nombre" to "Administrador" ,
            "password" to "admin123." ,
            "rol" to "admin",
            "codigo" to "",
            "enable" to "activo"
            ))
    );

    fun verifyUniqueCode(registerCode : String): Boolean{
        // Si la funcion retorna false el codigo no es el unico en la base de datos.
        // Si retorna verdadero el codigo es el unico en la base de datos.
        for ((email , user) in data){
            //Recorremos en todos los usuarios para encontrar un codigo similar al que se quiere registrar
            val codeCurrentUser : String = user.get("codigo") ?: return false;
            if (codeCurrentUser == registerCode){
                return false //Existe un codigo de arduino similar en la base de datos.
            }
        }
        return true
    }
    fun createUser(email : String , userDatas : HashMap<String , String>): Boolean{

        // Si el rol que se intenta asignar es admin no se permite el registro
        if (userDatas["rol"]=="admin") {
            return false
        }
        //Si existe un email en la DB igual al con el que se intenta asociar el usuario
        //Entonces la funcion retornara false debido a que no fue correcta la inserción
        //si no existe un email asociado a un usuario en la db se podra agregar el usuario al hashmap
        if (data.containsKey(email)){
            return false
        }
        else {
            val arduinCode : String = userDatas.get("codigo") ?: return false;
            val uniqueCode = verifyUniqueCode(arduinCode);
            if (!uniqueCode){
                return false
            }
            data[email] = userDatas
            return true
        }
    }
    fun getUser(email : String): HashMap<String , String>?{
        //Si existe un usuario asociado a un email entonces se retornara dicho HASHMAP correspondiente al usuario
        //Si no entonces se retornara null
        if (data.containsKey(email)){
            return data.get(email)
        }
        return null
    }

    fun changePassword(email: String , currentPassword : String , newPassword : String): Boolean{
        val userDatas = data.get(email) ?: return false;
        val passwordInDb = userDatas.get("password") ?: return false;
        return if (passwordInDb == currentPassword){
            userDatas["password"] = newPassword
            true
        }else{
            false
        }
    }

    // Función para eliminar a un usuario
    fun deleteUser(email: String): Boolean {
        // No permitir eliminar al administrador
        if (email == "admin@gmail.com") {
            return false
        }
        // Si el usuario existe, elimínalo y retorna true
        return if (data.containsKey(email)) {
            data.remove(email)
            true
        } else {
            false
        }
    }


    // Función para cambiar el estado de un usuario (activo/deshabilitado) por su email
    fun toggleUserStatus(email: String): Boolean {
        // No permitir cambiar estado del admin
        if (email == "admin@gmail.com") {
            return false
        }

        val userDatas = data[email] ?: return false
        val currentStatus = userDatas["enable"] ?: "activo"
        userDatas["enable"] = if (currentStatus == "activo") "deshabilitado" else "activo"
        return true
    }

    fun updateUserField(email: String, field: String, value: String): Boolean {
        // No permitir modificar al admin
        if (email == "admin@gmail.com") {
            return false
        }
        val userDatas = data[email] ?: return false
        // No permitir cambiar el rol a admin
        if (field == "rol" && value == "admin") {
            return false
        }
        // Verificación especial para códigos de Arduino
        if (field == "codigo" && value != userDatas["codigo"]) {
            if (!verifyUniqueCode(value)) {
                return false
            }
        }
        userDatas[field] = value
        return true
    }

    // Función auxiliar para obtener usuarios activos
    fun getActiveUsers(): List<Pair<String, HashMap<String, String>>> {
        return data.filter { (_, userData) ->
            userData["enable"] == "activo"
        }.toList()
    }

    // Función auxiliar para obtener usuarios por rol
    fun getUsersByRole(role: String): List<Pair<String, HashMap<String, String>>> {
        return data.filter { (_, userData) ->
            userData["rol"] == role
        }.toList()
    }
}