package com.example.cjproductions.login

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registrarse.*
import java.util.regex.Pattern


class Registrarse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        title = "Registrarse"

        ponerListeners()
    }

    private fun ponerListeners(){
        //Boton registrarse

        btRegistrarse.setOnLongClickListener{
            Toast.makeText(this, R.string.textoBtLogin, Toast.LENGTH_SHORT).show()
            true
        }

        btRegistrarse.setOnClickListener{
            if (!comprobar()) return@setOnClickListener

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                etEmail.text.toString(),
                etContrasena.text.toString()
            ).addOnCompleteListener{

                if(it.isSuccessful){
                    Toast.makeText(this, R.string.registroCorrecto, Toast.LENGTH_LONG).show()
                    onBackPressed()
                }else{
                    showAlert("Ha ocurrido un error durante la creacion del usuario")
                }
            }
        }
    }


    /**
     * Funcion que comprueba que los campos de texto no estén vacios
     */
    private fun comprobar(): Boolean{
        var email = etEmail.text.toString().trim()
        var contrasena= etContrasena.text.toString().trim()
        var contrasena2= etContrasena2.text.toString().trim()

        if(!isOk(email)){
            etEmail.error=resources.getString(R.string.error_campos).format("Email")
            return false
        }
        if(!isOk(contrasena)){
            etContrasena.error=resources.getString(R.string.error_campos).format("Contraseña")
            return false
        }
        if(!isOk(contrasena2)){
            etContrasena2.error=resources.getString(R.string.error_campos).format("Confirmar Contraseña")
            return false
        }

        //Comprueba que los dos campos de las contraseñas son iguales
        if(!(contrasena == contrasena2)){
            showAlert(resources.getString(R.string.errorContraseña))
            return false
        }

        if(contrasena.length<6){
            showAlert(resources.getString(R.string.contraseñaLongitudError))
            return false
        }

        //Comprueba que el email introducido es valido (ojo, puede no existir pero ser valido)
        val patronEmail: Pattern = Patterns.EMAIL_ADDRESS

        if(!patronEmail.matcher(email).matches()){
            showAlert(resources.getString(R.string.emailNoValido))
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

    /**
     * Funcion que genera un mensaje de alerta en la pantalla
     * con el texto que se le pase por parametro
     */
    private fun showAlert(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog= builder.create()
        dialog.show()
    }
}