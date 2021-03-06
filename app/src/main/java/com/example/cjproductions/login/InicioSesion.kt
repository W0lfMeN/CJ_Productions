package com.example.cjproductions.login

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_editar_usuarios.*
import kotlinx.android.synthetic.main.activity_inicio_sesion.*
import java.util.regex.Pattern


class InicioSesion : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        title = resources.getString(R.string.tituloIniciarSesion) //Cambia el titulo de la ventana

        //--------------------------------------------
        val storage= Firebase.storage
        storageReference=storage.reference
        //--------------------------------------------


        ponerListeners()
    }

    private fun ponerListeners() {
        btLogin.setOnClickListener {
            if (!comprobar()) return@setOnClickListener //Se comprueba que todos los campos est??n correctos antes de continuar

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etEmail.text.toString(),
                    etContrasena.text.toString()
            ).addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(this, R.string.inicioSesionCorrecto, Toast.LENGTH_LONG).show()
                    val returnIntent = Intent()
                    returnIntent.putExtra("email", etEmail.text.toString().trim())
                    returnIntent.putExtra("google", "0") //QUIERE DECIR QUE NO ES UN INICIO DE SESION CON GOOGLE
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else {
                    showAlert("Ha ocurrido un error durante el inicio de sesi??n")
                }
            }

        }

        btLogin.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtLogin, Toast.LENGTH_SHORT).show()
            true
        }


        //Boton registrarse con Google

        btRegistrarseGoogle.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtRegistrarseGoogle, Toast.LENGTH_SHORT).show()
            true
        }

        btRegistrarseGoogle.setOnClickListener {
            val googleConf: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)

            googleClient.signOut() //se ejecuta por si acaso hay ya alguna cuenta inicada

            startActivityForResult(googleClient.signInIntent, 200)
        }


        //BOTON RESTABLECER CONTRASE??A

        btRestablecerContrasena.setOnClickListener {
            val intent = Intent(this, RestablecerPassword::class.java)
            startActivity(intent)
        }
    }


    /**
     * Este metodo est?? principalmente por el inicio de sesion mediante google
     * Aqui se realiza el inicio de sesion, controlando que pueda haber un error y capturandolo
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credencial: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {

                        if (it.isSuccessful) {

                            val returnIntent = Intent()
                            returnIntent.putExtra("email", account.email)
                            returnIntent.putExtra("google", "1") //QUIERE DECIR QUE ES UN INICIO DE SESION CON GOOGLE
                            setResult(RESULT_OK, returnIntent)

                            Toast.makeText(this, R.string.inicioSesionCorrecto, Toast.LENGTH_LONG).show()

                            //Creamos el documento en firestore
                            val textoDefault: String = resources.getString(R.string.etValorNoProporcionado)

                            /**
                             * Este bloque de codigo realiza lo siguiente:
                             * Primero obtiene la informacion de la base de datos
                             * correspondiente al email.
                             *
                             * Una vez termina (it.isComplete), si el resultado es distinto de true,
                             * quiere decir que no existe ese correo en la base de datos, por lo que procede a crearlo.
                             *
                             * Por el contrario, si es True, quiere decir que ese email ya est?? en la base de datos
                             * y por lo tanto no hace nada ya que no queremos modificarlo
                             */
                            db.collection("Usuarios").document(account.email.toString()).get().addOnCompleteListener {
                                if (it.isComplete) {

                                    if (it.result?.exists() != true) {

                                        subirImagenDefecto() //Se sube la imagen por defecto


                                        db.collection("Usuarios").document(account.email.toString())
                                                .set(hashMapOf("Nombre" to textoDefault, "Telefono" to textoDefault))

                                        notificacionPush(account.email.toString())
                                    }
                                }
                            }

                            finish()
                        } else {
                            showAlert("Ha ocurrido un error durante durante el inicio de sesi??n")
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert("No se ha podido recuperar la cuenta")
            }
        }
    }

    /**
     * Funcion que comprueba que los campos de texto no est??n vacios
     */
    private fun comprobar(): Boolean {
        var email = etEmail.text.toString().trim()
        var contrasena = etContrasena.text.toString().trim()

        if (!isOk(email)) {
            etEmail.error = resources.getString(R.string.error_campos).format("Email")
            return false
        }
        if (!isOk(contrasena)) {
            etContrasena.error = resources.getString(R.string.error_campos).format("Contrase??a")
            return false
        }

        if (contrasena.length < 6) {
            showAlert(resources.getString(R.string.contrase??aLongitudError))
            return false
        }

        //Comprueba que el email introducido es valido (ojo, puede no existir pero ser valido)
        val patronEmail: Pattern = Patterns.EMAIL_ADDRESS

        if (!patronEmail.matcher(email).matches()) {
            showAlert(resources.getString(R.string.emailNoValido))
            return false
        }

        //Si llega hasta aqui quiere decir que todos los campos son validos
        return true
    }

    /**
     * Funcion que comprueba si una cadena est?? vacia
     * Dicha cadena se pasa como parametro
     */
    private fun isOk(cadena: String): Boolean {
        return !cadena.isEmpty()
    }

    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Metodo que subir?? la imagen que ha elegido el usuario a la base de datos
     */
    private fun subirImagenDefecto() {
        val referencia = storageReference.child("usuarios/"+FirebaseAuth.getInstance().currentUser.email.toString()+"/profile.jpg")
        referencia.putFile(Uri.parse("android.resource://${packageName}/${R.mipmap.user_default}"))

        /*
        .addOnSuccessListener {
            @Override
            fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                referencia.downloadUrl.addOnSuccessListener {
                    @Override
                    fun onSuccess(uri: Uri) {
                        Picasso.get().load(uri).into(editarImagen)
                    }
                }
            }
        }
         */
    }

    /**
     * Funcion que enviar?? una notificacion push al usuario
     * Puesto que ser?? solo una la que se use, se crea de esta forma
     *
     * Si el usuario pulsa la notificacion, le llevar?? al activity IniciarSesion.kt
     */
    fun notificacionPush(correo:String) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(resources.getString(R.string.app_name),
                resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "EL_CANAL_DE_REGISTRO"
        mNotificationManager.createNotificationChannel(channel)
        val mBuilder = NotificationCompat.Builder(applicationContext, resources.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(resources.getString(R.string.tituloNotificacion))
                .setContentText(correo)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(resources.getString(R.string.textoNotificacion)))
                .setAutoCancel(true) // Esto hace que al pulsar la notificacion, esta se borre
        val intent = Intent(applicationContext, InicioSesion::class.java) //El activity que se lanza al pulsar la notificacion
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

}