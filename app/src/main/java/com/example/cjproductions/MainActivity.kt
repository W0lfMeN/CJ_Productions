package com.example.cjproductions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.atencionCliente.activities.ChatActivity
import com.example.cjproductions.atencionCliente.admin.MenuPrincipalAdmin
import com.example.cjproductions.atencionCliente.models.Chat
import com.example.cjproductions.comprarProductos.MenuPrincipalComprarProductos
import com.example.cjproductions.comprarProductos.activity.ProductosActivity
import com.example.cjproductions.login.InicioSesion
import com.example.cjproductions.login.Registrarse
import com.example.cjproductions.novedades.MenuPrincipalNovedades
import com.example.cjproductions.perfilUsuarios.PerfilUsuarios
import com.example.cjproductions.sobreNosotros.SobreNosotros
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * Variable que almacenará los correos pertenecientes a los desarrolladores de la aplicacion
     * Se usará para saber si la cuenta iniciada es de un desarrollador o no
     * Para saber eso, se comparará el contenido del array con el email perteneciente al archivo de persistencia
     */
    private val correosDesarrolladores = arrayListOf("carlosjmsanchez@gmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(4000)
        setTheme(R.style.Theme_CJProductions)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Llamamos al metodo que se encarga de comprobar si existe contenido en el archivo de preferencias
        comprobarArchivoPreferencias()

        //Llamamos al metodo donde estarán todos los listeners de la ventana principal
        ponerListeners()

        //Llamamos al metodo que se encarga de poner y reproducir el video de fondo del activity
        iniciarBackgroundVideo()
    }

    /**
     * Metodo que añade los listeners que necesitamos en este activity
     */
    private fun ponerListeners() {
        //Boton Saber Mas

        btComprar.setOnClickListener {
            //Aqui se llamará a la pantalla de comprar producto
            //Asignamos los parametros que se van a transferir al activity
            val nombre = "Days Gone"
            val enlace = "https://store.steampowered.com/app/1259420/Days_Gone/"

            val intent: Intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("enlace", enlace)
            intent.putExtra("nombre", nombre)
            startActivity(intent)

        }

        btComprar.setOnLongClickListener {
            Toast.makeText(this, R.string.mainTextoBtSaberMas, Toast.LENGTH_SHORT).show()

            true
        }

        //Boton Ver Perfil

        mainBtPerfil.setOnLongClickListener {
            Toast.makeText(this, R.string.mainTextoBtVerPerfil, Toast.LENGTH_SHORT).show()
            true
        }

        mainBtPerfil.setOnClickListener {
            val intent: Intent = Intent(this, PerfilUsuarios::class.java)
            startActivity(intent)
        }

    }

    /**
     * Funcion que se encarga de buscar e iniciar el video de fondo del activity
     */
    private fun iniciarBackgroundVideo() {
        //Instanciamos la ruta del video de fondo usando la clase Uri
        videoPrincipal.setVideoURI(Uri.parse("android.resource://${packageName}/${R.raw.video_principal}"))

        //con esto se incia el video
        videoPrincipal.start()

        /**
         * Añadimos un listener al video. Este bloque de codigo entrará cuando finalice el video
         * En concreto, cuando termine el video lo que hará será volver a reproducirlo.
         * Haciendo que se reproduzca en bucle
         */
        videoPrincipal.setOnCompletionListener() {
            videoPrincipal.start()
        }
    }

    /**
     * Este metodo se ejecuta cuando se vuelve a la ventana principal
     * desde cualquier otro activity.
     *
     * Lo que hace es reproducir otra vez el video de fondo.
     * De no hacer esto, la pantalla se quedará en blanco
     * cuando se vuelva desde otro activity
     */
    override fun onRestart() {
        super.onRestart()
        iniciarBackgroundVideo()

        //Aqui se va a comprobar si existe o no contenido en el archivo de preferencias
        //(Se hace asi para evitar modificar la visibilidad del boton ver perfil desde otro activity)
        comprobarArchivoPreferencias()
    }

    /**
     * Metodo que añade el icono de las opciones del menu en el activity
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.menu_principal, menu)
        return true
    }

    /**
     * Metodo que les da funcionalidad a los botones del menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            //Al pulsarse se llamará a la clase de Atencion Al cliente
            R.id.AtencionCliente -> {
                val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)

                /**
                 * Si el usuario del archivo de persistencia es distinto de null
                 * quiere decir que hay una cuenta iniciada. En caso contrario no dejará iniciar el activity
                 * mostrando un mensaje informando del problema
                 *
                 * En caso de existir un correo en el archivo de persistencia,
                 * este se comprara con el array en el que están contenidos los correos de los administradores.
                 *
                 * Si el correo coincide con uno de los que hay en el array, quiere decir que es un admin
                 * y por lo tanto iniciará el activity que muestra el menú de chats del admin
                 *
                 * Por el contrario, si no coincide con ninguno, quiere decir que es un usuario normal
                 * y se llamará al metodo gestionChatUsuario para crear el chat
                 */
                if (prefs.getString("email", null) != null) {
                    if (correosDesarrolladores.contains(prefs.getString("email", null).toString())) {
                        val intent = Intent(this, MenuPrincipalAdmin::class.java)
                        startActivity(intent)
                    } else {
                        showAlert(resources.getString(R.string.alertChatUser))
                    }
                } else
                    Toast.makeText(this, R.string.noSesionIniciada, Toast.LENGTH_SHORT).show()
            }

            //Al pulsarse se llamará a la clase de Novedades
            R.id.Novedades -> {
                val intent: Intent = Intent(this, MenuPrincipalNovedades::class.java)
                startActivity(intent)
            }

            //Al pulsarse se llamará a la clase de Comprar productos
            R.id.ComprarProductos -> {
                val intent: Intent = Intent(this, MenuPrincipalComprarProductos::class.java)
                startActivity(intent)
            }

            //Al pulsarse se llamará a la clase de Sobre nosotros
            R.id.SobreNosotros -> {
                val intent: Intent = Intent(this, SobreNosotros::class.java)
                startActivity(intent)
            }

            R.id.mainBtIniciarSesion -> {
                val intent: Intent = Intent(this, InicioSesion::class.java)
                startActivityForResult(intent, 100)
                //Este metodo llama al activity y cuando termine llama a onActivityResult
                //para trabajar con los datos enviados de dicho activity
            }

            R.id.mainBtRegistrarse -> {
                val intent: Intent = Intent(this, Registrarse::class.java)
                startActivityForResult(intent, 100)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Metodo que es llamado automaticamente cuando termina el activity Iniciar sesion
     * Lo que hace es retornar el correo que ha sido introducido en el activity para guardarlo
     * en el archivo de preferencias y si es de google o no
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                editarPersistenciaDatos(data.getStringExtra("email"), data.getStringExtra("google"))

                //Aqui haremos aparecer el boton de Ver Perfil
                mostrarOcultarBtVerPerfil(1)
            }
        }

    }

    /**
     * Metodo que añade el correo al fichero de persistencia
     */
    private fun editarPersistenciaDatos(email: String?, google:String?) {
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE).edit()
        if (prefs != null) {
            prefs.putString("email", email)
            prefs.putString("google", google)
            prefs.apply()
        }
    }

    /**
     * Metodo que muestra u oculta el boton de iniciar sesion segun el parametro
     *
     * 1 para MOSTRAR el botón
     * 0 para OCULTAR el botón
     */
    private fun mostrarOcultarBtVerPerfil(valor: Int) {
        if (valor == 1)
            mainBtPerfil.visibility = View.VISIBLE

        if (valor == 0)
            mainBtPerfil.visibility = View.INVISIBLE
    }

    /**
     * Este metodo nos hará saber si hay una sesion iniciada comprobando el contenido de la persistencia
     *
     * Si por defecto es null, quiere decir que no hay datos, por lo cual no hay sesion iniciada
     * por lo que llama al metodo de ocultar el boton para cerrar sesion pasandole el parametro correcto
     *
     * Por el contrario si es distinto de null, quiere decir que hay una sesion iniciada
     * y mostrará el boton de cerrar sesion llamando al metodo correspondiente
     */
    private fun comprobarArchivoPreferencias() {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)

        if (prefs.getString("email", null) == null) {
            mostrarOcultarBtVerPerfil(0)
        } else {
            mostrarOcultarBtVerPerfil(1)
        }
    }

    /**
     * Metodo que gestiona y crea del chat bajo la vista del usuario
     */
    private fun gestionChatUsuario() {

        val db = Firebase.firestore

        //guardamos en la variable 'user' el correo que se encuentre en la persistencia
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null).toString()

        //Damos valores a las variables que se necesitan para crear el objeto Chat
        val chatId = UUID.randomUUID().toString()
        val otherUser = correosDesarrolladores.random() //se asigna un desarrollador aleatorio
        val users = listOf(user, otherUser)
        val laFecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString()

        //Creamos el objeto Chat con sus valores
        val chat = Chat(
                id = chatId,
                name = "Chat con $user",
                users = users,
                fecha = laFecha
        )

        //Añadimos la coleccion del Chat tanto en una coleccion principal, como en una coleccion dentro de cada usuario que interviene
        db.collection("Chats").document(chatId).set(chat)
        db.collection("Usuarios").document(user).collection("Chats").document(chatId).set(chat)
        db.collection("Usuarios").document(otherUser).collection("Chats").document(chatId).set(chat)


        //Iniciamos el activity pasando los datos importantes como son el id, el usuario y el nombre del administrador
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("user", user)
        intent.putExtra("admin", otherUser)
        startActivity(intent)
    }

    /**
     * Funcion que genera un mensaje de alerta en la pantalla
     * con el texto que se le pase por parametro
     */
    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
        builder.setPositiveButton(resources.getString(R.string.aceptar)) { _, _ ->
            gestionChatUsuario()
        }
        builder.setNegativeButton(resources.getString(R.string.cancelar)) { view, _ ->
            view.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}

