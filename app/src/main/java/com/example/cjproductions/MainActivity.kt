package com.example.cjproductions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(4000)
        setTheme(R.style.Theme_CJProductions)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Llamamos al metodo donde estarán todos los listeners de la ventana principal
        ponerListeners()

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

    private fun ponerListeners(){
        btSaberMas.setOnClickListener{
            //Aqui se llamará a la pantalla de comprar producto
        }

        btSaberMas.setOnLongClickListener{
            Toast.makeText(this,R.string.textoBtSaberMas, Toast.LENGTH_SHORT).show()

            true
        }

        btIniciarSesion.setOnClickListener{

        }

        btIniciarSesion.setOnLongClickListener{
            Toast.makeText(this,R.string.textoBtIniciarSesion, Toast.LENGTH_SHORT).show()

            true
        }
    }

    //Crea las opciones del menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.menu_principal, menu)
        return true
    }
    //Les damos funcionalidad al boton del menu
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
