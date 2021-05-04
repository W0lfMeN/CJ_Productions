package com.example.cjproductions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cjproductions.login.InicioSesion
import com.example.cjproductions.login.Registrarse
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(4000)
        setTheme(R.style.Theme_CJProductions)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Llamamos al metodo donde estarán todos los listeners de la ventana principal
        ponerListeners()

        //Llamamos al metodo que se encarga de poner y reproducir el video de fondo del activity
        iniciarBackgroundVideo()

        //Esta linea oculta el boton INICIAR SESION
        //btIniciarSesion.setVisibility(View.INVISIBLE)
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

        //Boton Iniciar Sesion

        mainBtIniciarSesion.setOnClickListener{
            val intent: Intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }

        mainBtIniciarSesion.setOnLongClickListener{
            Toast.makeText(this,R.string.mainTextoBtIniciarSesion, Toast.LENGTH_SHORT).show()

            true
        }

        //Boton Registrarse

        mainBtRegistrarse.setOnLongClickListener{
            Toast.makeText(this,R.string.mainTextoBtRegistrarse, Toast.LENGTH_SHORT).show()

            true
        }

        mainBtRegistrarse.setOnClickListener{
            val intent: Intent = Intent(this, Registrarse::class.java)
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
                //val intent: Intent = Intent(this, "ClaseJava"::class.java)
                //startActivity(intent)
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
        }
        return super.onOptionsItemSelected(item)
    }
}
