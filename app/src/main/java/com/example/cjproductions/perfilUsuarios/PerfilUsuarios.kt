package com.example.cjproductions.perfilUsuarios

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_perfil_usuarios.*

class PerfilUsuarios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        val dialog: AlertDialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando perfil")
                .setCancelable(false)
                .build()

        dialog.show()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuarios)

        title = resources.getString(R.string.tituloPerfilUsuarios)

        ponerListeners()

        rellenarCampos()

        cargarImagen()


        dialog.dismiss()

    }


    private fun ponerListeners() {

        //BOTON CERRAR SESION

        btCerrarSesion.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtCerrarSesion, Toast.LENGTH_SHORT).show()
            true
        }

        btCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, R.string.sesionCerradaCorrectamente, Toast.LENGTH_SHORT).show()

            borrarPreferencias() //borra el correo que hay en el archivo de preferencias

            finish() //Hace finalizar el activity, volviendo automaticamente al main activity
        }

        //BOTON EDITAR PERFIL

        btEditarImagen.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtEditarPerfil, Toast.LENGTH_SHORT).show()
            true
        }

        btEditarImagen.setOnClickListener {
            val intent: Intent = Intent(this, EditarUsuarios::class.java)
            startActivity(intent)
        }

        //BOTON CAMBIAR CONTRASEÑA

        btCambiarContrasena.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtEditarContrasena, Toast.LENGTH_SHORT).show()
            true
        }

        btCambiarContrasena.setOnClickListener {
            cambiarContrasena()
        }

    }

    /**
     * Funcion que se encarga de rellenar los datos del activity con la informacion correspondiente
     */
    private fun rellenarCampos() {

        val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
        //Rellenar el campo de correo
        if (prefs != null) {
            tvCorreo.setText(prefs.getString("email", null).toString())

            //Ahora rellenamos los demás campos
            db.collection("Usuarios").document(tvCorreo.text.toString()).get().addOnCompleteListener {
                tvTitulo.setText(it.result?.get("Nombre").toString())
                tvTelefono.setText(it.result?.get("Telefono").toString())
            }


        }
    }

    private fun cargarImagen() {

        val perfilReferencia = storage.child("usuarios/" + FirebaseAuth.getInstance().currentUser.uid + "/profile.jpg")

        //Coloca la imagen desde el almacenamiento de firebase
        if (perfilReferencia != null) {
            perfilReferencia.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).resize(150, 150).centerCrop().into(imagenUsuario)
            }
        }
    }

    /**
     * Metodo que borra el contenido del fichero de persistencia
     */
    fun borrarPreferencias() {
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE).edit()
        if (prefs != null) {
            prefs.clear()
            prefs.apply()
        }
    }

    /**
     * Se implementa este metodo para actualizar la información
     * cuando se vuelva desde el activity de editar usuarios
     */
    override fun onRestart() {
        val dialog: AlertDialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando perfil")
                .setCancelable(false)
                .build()

        dialog.show()

        super.onRestart()

        rellenarCampos()
        cargarImagen()

        dialog.dismiss()
    }

    private fun cambiarContrasena() {

        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)

        FirebaseAuth.getInstance().sendPasswordResetEmail(prefs.getString("email", null).toString()).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(this, R.string.mensajeEditarContrasena, Toast.LENGTH_SHORT).show()
            } else {
                showAlert("No se ha podido enviar el correo para restablecer contraseña")
            }
        }

    }

    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }
}