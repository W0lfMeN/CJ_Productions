package com.example.cjproductions.login

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_editar_usuarios.*
import kotlinx.android.synthetic.main.activity_registrarse.*
import java.util.regex.Pattern


class Registrarse : AppCompatActivity() {

    private val db=FirebaseFirestore.getInstance()
    private val storage= FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        title = "Registrarse"

        ponerListeners()
    }

    private fun ponerListeners(){
        //Boton registrarse

        btRegistrarse.setOnLongClickListener{
            Toast.makeText(this, R.string.textoBtRegistrarse, Toast.LENGTH_SHORT).show()
            true
        }

        btRegistrarse.setOnClickListener{
            if (!comprobar()) return@setOnClickListener

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                etEmail.text.toString(),
                etContrasena.text.toString()
            ).addOnCompleteListener{

                if(it.isSuccessful){
                    val textoDefault:String=resources.getString(R.string.etValorNoProporcionado)
                    val rutaImagen = FirebaseAuth.getInstance().currentUser.uid

                    subirImagenDefecto() //se sube la imagen por defecto

                    Toast.makeText(this, R.string.registroCorrecto, Toast.LENGTH_LONG).show()

                    //Se crea la coleccion junto con el registro
                    db.collection("Usuarios").document(etEmail.text.toString()).
                    set(hashMapOf("Nombre" to textoDefault, "Telefono" to textoDefault, "Imagen" to rutaImagen))

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

    /**
     * Metodo que subirá la imagen que ha elegido el usuario a la base de datos
     */
    private fun subirImagenDefecto(){
        val referencia = storage.child("usuarios/"+FirebaseAuth.getInstance().currentUser.uid+"/profile.jpg")
        referencia.putFile(Uri.parse("android.resource://${packageName}/${R.mipmap.user_default}")).addOnSuccessListener {
            @Override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot){
                referencia.downloadUrl.addOnSuccessListener {
                    @Override fun onSuccess(uri: Uri){
                        Picasso.get().load(uri).into(editarImagen)
                    }
                }
            }
        }
    }
}