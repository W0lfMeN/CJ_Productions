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
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.login.InicioSesion
import com.example.cjproductions.login.Registrarse
import com.example.cjproductions.perfilUsuarios.PerfilUsuarios
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(4000)
        setTheme(R.style.Theme_CJProductions)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
        /**
         * Este if nos hará saber si hay una sesion iniciada comprobando el contenido de la persistencia
         *
         * Si por defecto es null, quiere decir que no hay datos, por lo cual no hay sesion iniciada
         * por lo que llama al metodo de ocultar el boton para cerrar sesion pasandole el parametro correcto
         *
         * Por el contrario si es distinto de null, quiere decir que hay una sesion iniciada
         * y mostrará el boton de cerrar sesion llamando al metodo correspondiente
         */
        if(prefs.getString("email",null)==null){
            mostrarOcultarBtCerrarSesion(0)
            mostrarOcultarBtVerPerfil(0)
        }else{
            mostrarOcultarBtCerrarSesion(1)
            mostrarOcultarBtVerPerfil(1)
        }

        //Llamamos al metodo donde estarán todos los listeners de la ventana principal
        ponerListeners()

        //Llamamos al metodo que se encarga de poner y reproducir el video de fondo del activity
        iniciarBackgroundVideo()

    }

    /**
     * Metodo que añade los listeners que necesitamos en este activity
     */
    private fun ponerListeners(){
        //Boton Saber Mas

        btSaberMas.setOnClickListener{
            //Aqui se llamará a la pantalla de comprar producto
        }

        btSaberMas.setOnLongClickListener{
            Toast.makeText(this,R.string.mainTextoBtSaberMas, Toast.LENGTH_SHORT).show()

            true
        }

        //Boton Cerrar Sesion

        mainBtCerrarSesion.setOnLongClickListener{
            Toast.makeText(this,R.string.mainTextoBtCerrarSesion, Toast.LENGTH_SHORT).show()
            true
        }

        mainBtCerrarSesion.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,R.string.sesionCerradaCorrectamente, Toast.LENGTH_SHORT).show()

            borrarPreferencias() //borra el correo que hay en el archivo de preferencias

            mostrarOcultarBtCerrarSesion(0) //oculta el boton de cerrar sesion
            mostrarOcultarBtVerPerfil(0) //Oculta el boton de ver perfil
        }

        //Boton Ver Perfil

        mainBtPerfil.setOnLongClickListener{
            Toast.makeText(this,R.string.mainTextoBtVerPerfil, Toast.LENGTH_SHORT).show()
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
    private fun iniciarBackgroundVideo(){
        //Instanciamos la ruta del video de fondo usando la clase Uri
        videoPrincipal.setVideoURI(Uri.parse("android.resource://${packageName}/${R.raw.video_principal}"))

        //con esto se incia el video
        videoPrincipal.start()

        /**
         * Añadimos un listener al video. Este bloque de codigo entrará cuando finalice el video
         * En concreto, cuando termine el video lo que hará será volver a reproducirlo.
         * Haciendo que se reproduzca en bucle
         */
        videoPrincipal.setOnCompletionListener(){
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
        when (item.itemId){

            //Al pulsarse se llamará a la clase de Atencion Al cliente
            R.id.AtencionCliente ->{

            }

            //Al pulsarse se llamará a la clase de Novedades
            R.id.Novedades ->{
                //val intent: Intent = Intent(this, "ClaseJava"::class.java)
                //startActivity(intent)
            }

            //Al pulsarse se llamará a la clase de Comprar productos
            R.id.ComprarProductos ->{
                //val intent: Intent = Intent(this, "ClaseJava"::class.java)
                //startActivity(intent)
            }

            //Al pulsarse se llamará a la clase de Sobre nosotros
            R.id.SobreNosotros ->{
                //val intent: Intent = Intent(this, "ClaseJava"::class.java)
                //startActivity(intent)
            }

            R.id.mainBtIniciarSesion ->{
                val intent: Intent = Intent(this, InicioSesion::class.java)
                startActivityForResult(intent, 100)
                //Este metodo llama al activity y cuando termine llama a onActivityResult
                //para trabajar con los datos enviados de dicho activity
            }

            R.id.mainBtRegistrarse ->{
                val intent: Intent = Intent(this, Registrarse::class.java)
                startActivityForResult(intent, 100)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Metodo que es llamado automaticamente cuando termina el activity Iniciar sesion
     * Lo que hace es retornar el correo que ha sido introducido en el activity para guardarlo
     * en el archivo de preferencias
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && resultCode == RESULT_OK){
            if (data != null) {
                editarPersistenciaDatos(data.getStringExtra("email"))

                //Aqui haremos aparecer al boton de Cerrar sesion y el boton de Ver Perfil
                mostrarOcultarBtCerrarSesion(1)
                mostrarOcultarBtVerPerfil(1)
            }
        }

    }

    /**
     * Metodo que añade el correo al fichero de persistencia
     */
    fun editarPersistenciaDatos(email: String?) {
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE).edit()
        if (prefs != null) {
            prefs.putString("email", email)
            prefs.apply()
        }
    }


    /**
     * Metodo que borra el contenido del fichero de persistencia
     */
    fun borrarPreferencias(){
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE).edit()
        if (prefs != null) {
            prefs.clear()
            prefs.apply()
        }
    }

    /**
     * Metodo que muestra u oculta el boton de iniciar sesion segun el parametro
     *
     * 1 para MOSTRAR el botón
     * 0 para OCULTAR el botón
     */
    fun mostrarOcultarBtCerrarSesion(valor:Int){
        if(valor==1)
            mainBtCerrarSesion.setVisibility(View.VISIBLE)

        if(valor==0)
            mainBtCerrarSesion.setVisibility(View.INVISIBLE)
    }

    /**
     * Metodo que muestra u oculta el boton de iniciar sesion segun el parametro
     *
     * 1 para MOSTRAR el botón
     * 0 para OCULTAR el botón
     */
    fun mostrarOcultarBtVerPerfil(valor:Int){
        if(valor==1)
            mainBtPerfil.setVisibility(View.VISIBLE)

        if(valor==0)
            mainBtPerfil.setVisibility(View.INVISIBLE)
    }
}
