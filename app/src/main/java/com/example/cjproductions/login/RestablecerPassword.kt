package com.example.cjproductions.login

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_restablecer_password.*
import java.util.regex.Pattern

class RestablecerPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_password)

        title = resources.getString(R.string.tituloRestablecerPassword)


        ponerListeners()
    }

    private fun ponerListeners() {
        btRestablecerContrasena.setOnClickListener {
            if (!comprobarEmail()) return@setOnClickListener

            cambiarContrasena()
        }
    }

    /**
     * Funcion que comprueba que el campo del email no esté vacío y sea valido
     * (Puede no existir pero si ser valido)
     */
    private fun comprobarEmail(): Boolean {

        if (!isOk(etRestablecerContrasena.text.toString())) {
            etRestablecerContrasena.error = resources.getString(R.string.error_campos).format("Email")
            return false
        }

        //Comprueba que el email introducido es valido (ojo, puede no existir pero ser valido)
        val patronEmail: Pattern = Patterns.EMAIL_ADDRESS

        if (!patronEmail.matcher(etRestablecerContrasena.text.toString()).matches()) {
            showAlert(resources.getString(R.string.emailNoValido))
            return false
        }

        return true
    }

    /**
     * Funcion que comprueba si una cadena está vacia
     * Dicha cadena se pasa como parametro
     */
    private fun isOk(cadena: String): Boolean {
        return !cadena.isEmpty()
    }

    /**
     * Funcion que genera un mensaje de alerta en la pantalla
     * con el texto que se le pase por parametro
     */
    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Funcion que se encarga de enviar un correo al email introducido
     */
    private fun cambiarContrasena() {

        FirebaseAuth.getInstance().sendPasswordResetEmail(etRestablecerContrasena.text.toString()).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(this, R.string.mensajeEditarContrasena, Toast.LENGTH_SHORT).show()
            } else {
                showAlert("No se ha podido enviar el correo para restablecer contraseña")
            }
        }

    }
}