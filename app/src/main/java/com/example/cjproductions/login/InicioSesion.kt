package com.example.cjproductions.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_inicio_sesion.*
import kotlinx.android.synthetic.main.activity_main.*


class InicioSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        title="Iniciar Sesion" //Cambia el titulo de la ventana
        ponerListeners()
    }

    private fun ponerListeners(){
        btLogin.setOnClickListener{
            if (!comprobar()) return@setOnClickListener //Se comprueba que todos los campos están correctos antes de continuar

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etEmail.text.toString(),
                    etContrasena.text.toString()
            ).addOnCompleteListener{

                if(it.isSuccessful){
                    Toast.makeText(this, R.string.inicioSesionCorrecto, Toast.LENGTH_LONG).show()
                    val returnIntent = Intent()
                    returnIntent.putExtra("email", etEmail.text.toString().trim())
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }else{
                    showAlert("Ha ocurrido un error durante el inicio de sesión")
                }
            }

        }

        btLogin.setOnLongClickListener{
            Toast.makeText(this, R.string.textoBtLogin, Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Funcion que comprueba que los campos de texto no estén vacios
     */
    private fun comprobar(): Boolean{
        var email = etEmail.text.toString().trim()
        var contrasena= etContrasena.text.toString().trim()

        if(!isOk(email)){
            etEmail.error=resources.getString(R.string.error_campos).format("Email")
            return false
        }
        if(!isOk(contrasena)){
            etContrasena.error=resources.getString(R.string.error_campos).format("Contraseña")
            return false
        }

        if(contrasena.length<6){
            showAlert(resources.getString(R.string.contraseñaLongitudError))
            return false
        }

        //Si llega hasta aqui quiere decir que todos los campos son validos
        return true
    }

    /**
     * Funcion que comprueba si una cadena está vacia
     * Dicha cadena se pasa como parametro
     */
    private fun isOk(cadena: String): Boolean{
        return !cadena.isEmpty()
    }

    private fun showAlert(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog= builder.create()
        dialog.show()
    }

}