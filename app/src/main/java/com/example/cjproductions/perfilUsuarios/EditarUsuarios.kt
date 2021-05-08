package com.example.cjproductions.perfilUsuarios

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_editar_usuarios.*
import java.util.regex.Pattern

class EditarUsuarios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuarios)

        ponerListeners()
    }

    private fun ponerListeners(){
        btGuardarDatos.setOnLongClickListener {
            Toast.makeText(this,R.string.textoBtGuardar, Toast.LENGTH_SHORT).show()
            true
        }

        btGuardarDatos.setOnClickListener {
            val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
            if (prefs != null) {

                var nombre:String
                var telefono:String

                //Comprobar campo Nombre
                //Se hace por si el usuario decide eliminar el nombre
                if(etEditarNombre.text.toString()=="")
                    nombre= resources.getString(R.string.etValorNoProporcionado)
                else
                    nombre= etEditarNombre.text.toString()

                //Comprobar campo Telefono
                //Se hace por si el usuario decide eliminar el telefono
                if(etEditarTelefono.text.toString()=="")
                    telefono=resources.getString(R.string.etValorNoProporcionado)
                else
                    telefono= etEditarTelefono.text.toString()



                db.collection("Usuarios").document(prefs.getString("email", null).toString())
                    .set(hashMapOf("Nombre" to nombre, "Telefono" to telefono))
            }

            finish() //Cierra el activity
        }
    }
}