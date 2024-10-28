package com.example.smartwattapp

import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartwattapp.Database;
import android.graphics.Color
import android.content.res.ColorStateList

class AdminPanelActivity : AppCompatActivity() {
    // Funciones para mantener a los usuarios seleccionados
    private val selectedUsers = mutableSetOf<String>()
    private lateinit var tableLayout: TableLayout
    private val ADMIN_EMAIL = "admin@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge();
        setContentView(R.layout.admin_panel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tableLayout = findViewById(R.id.tableLayout)

        // Agregar botones de acción
        val editButton = findViewById<MaterialButton>(R.id.editButton)
        val toggleButton = findViewById<MaterialButton>(R.id.toggleButton)
        val deleteButton = findViewById<MaterialButton>(R.id.deleteButton)

        editButton.setOnClickListener { editSelectedUsers() }
        toggleButton.setOnClickListener { toggleSelectedUsers() }
        deleteButton.setOnClickListener { deleteSelectedUsers() }

        refreshTable()
    }

    private fun refreshTable() {
        tableLayout.removeAllViews() // Limpia la tabla
        addHeaderRow() // Agrega encabezado

        var index = 0
        for ((email, userDetails) in Database.data) {
            addUserRow(email, userDetails, index++)
        }
    }
    private fun addHeaderRow() {
        val headerRow = TableRow(this)
        val headers = listOf("Seleccionar", "Email", "Nombre", "Código", "Contraseña", "Estado")

        headers.forEach { headerText ->
            TextView(this).apply {
                text = headerText
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#1E293B"))
                setBackgroundResource(R.drawable.custom_celdas)
                setPadding(15, 30, 15, 30)
                headerRow.addView(this)
            }
        }

        tableLayout.addView(headerRow)
    }

    private fun addUserRow(email: String, userDetails: HashMap<String, String>, index: Int) {
        val tableRow = TableRow(this)

        // Checkbox
        val checkbox = MaterialCheckBox(this).apply {
            isEnabled = email != ADMIN_EMAIL
            isChecked = selectedUsers.contains(email)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUsers.add(email)
                } else {
                    selectedUsers.remove(email)
                }
            }
            // Manejo de colores del checkbox (Considera 3 estados)
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf()
                ),
                intArrayOf(
                    Color.parseColor("#B3B3B3"), // Color cuando está deshabilitado
                    Color.parseColor("#f7c559"), // Color cuando está seleccionado
                    Color.parseColor("#1E293B")  // Color cuando no está seleccionado
                )
            )
            buttonTintList = colorStateList
        }
        tableRow.addView(checkbox)

        // Datos del usuario
        listOf(
            email,
            userDetails["nombre"] ?: "N/A",
            userDetails["codigo"] ?: "N/A",
            userDetails["password"] ?: "N/A",
            userDetails["enable"] ?: "N/A"
        ).forEach { text ->
            TextView(this).apply {
                this.text = text
                gravity = Gravity.CENTER
                setTextColor(if (userDetails["enable"] == "deshabilitado") Color.GRAY else Color.BLACK)
                setBackgroundResource(R.drawable.custom_celdas)
                setPadding(15, 30, 15, 30)
                tableRow.addView(this)
            }
        }

        tableLayout.addView(tableRow)
    }

    private fun editSelectedUsers() {
        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val email = selectedUsers.first()
        val user = Database.getUser(email) ?: return

        val view = layoutInflater.inflate(R.layout.dialogo_usuario, null)
        val nombreEdit = view.findViewById<TextInputEditText>(R.id.nombreEdit)
        val codigoEdit = view.findViewById<TextInputEditText>(R.id.codigoEdit)
        val passwordEdit = view.findViewById<TextInputEditText>(R.id.passwordEdit)

        nombreEdit.setText(user["nombre"])
        codigoEdit.setText(user["codigo"])
        passwordEdit.setText(user["password"])

        MaterialAlertDialogBuilder(this)
            .setTitle("Editar usuario seleccionado")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val isUpdated = Database.updateUserField(email, "nombre", nombreEdit.text.toString()) &&
                        Database.updateUserField(email, "codigo", codigoEdit.text.toString()) &&
                        Database.updateUserField(email, "password", passwordEdit.text.toString())

                if (isUpdated) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    selectedUsers.clear()
                    refreshTable()
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun toggleSelectedUsers() {
        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Deshabilitar / Habilitar de Usuario/s")
            .setMessage("¿Desea cambiar el estado de los usuarios seleccionados?")
            .setPositiveButton("Confirmar") { _, _ ->
                selectedUsers.forEach { email ->
                    Database.toggleUserStatus(email)
                }
                selectedUsers.clear()
                refreshTable()
                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteSelectedUsers() {
        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Eliminación de Usuario/s")
            .setMessage("¿Desea cambiar eliminar los usuarios seleccionados?")
            .setPositiveButton("Confirmar") { _, _ ->
                selectedUsers.forEach { email ->
                    Database.toggleUserStatus(email)
                }
                selectedUsers.clear()
                refreshTable()
                Toast.makeText(this, "Operación de eliminación exitosa", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
        refreshTable()
    }
}