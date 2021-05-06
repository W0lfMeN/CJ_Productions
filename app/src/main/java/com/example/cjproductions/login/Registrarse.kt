package com.example.cjproductions.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

        //Boton registrarse con Google

        btRegistrarseGoogle.setOnLongClickListener{
            Toast.makeText(this, R.string.textoBtRegistrarseGoogle, Toast.LENGTH_SHORT).show()
            true
        }

        btRegistrarseGoogle.setOnClickListener{
            val googleConf: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient: GoogleSignInClient= GoogleSignIn.getClient(this, googleConf)

            googleClient.signOut() //se ejecuta por si acaso hay ya alguna cuenta inicada

            startActivityForResult(googleClient.signInIntent, 200)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==200){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if(account != null) {
                    val credencial: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {

                        if(it.isSuccessful){

                            val returnIntent = Intent()
                            returnIntent.putExtra("email", account.email)
                            setResult(RESULT_OK, returnIntent)

                            Toast.makeText(this, R.string.registroCorrecto, Toast.LENGTH_LONG).show()

                            finish()
                        }else{
                            showAlert("Ha ocurrido un error durante la creacion del usuario")
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert("No se ha podido recuperar la cuenta")
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