package com.example.cjproductions.perfilUsuarios

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cjproductions.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_editar_usuarios.*
import kotlinx.android.synthetic.main.dialogo_editar_imagen.*
import java.io.ByteArrayOutputStream


class EditarUsuarios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    private val CAMERA_ACTION_CODE: Int = 201
    private val GALERIA_ACTION_CODE: Int = 501

    private lateinit var nombre: String
    private lateinit var telefono: String

    private lateinit var email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuarios)

        title = resources.getString(R.string.tituloEditarPerfil)
        ponerListeners()

        //--------------------------------------------
        val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
        if (prefs != null) {
            email=prefs.getString("email", "null").toString()
        }
        //--------------------------------------------

        //--------------------------------------------
        val storage= Firebase.storage
        storageReference=storage.reference


        //--------------------------------------------

        cargarImagen()
    }

    private fun cargarImagen() {
        val perfilReferencia = storageReference.child("usuarios/$email/profile.jpg")

        //Coloca la imagen desde el almacenamiento de firebase
        perfilReferencia.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).resize(150, 150).centerCrop().into(editarImagen)
        }
    }

    private fun ponerListeners() {
        btGuardarDatos.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtGuardar, Toast.LENGTH_SHORT).show()
            true
        }

        btGuardarDatos.setOnClickListener {
            val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)

            if (prefs != null) {

                //Comprobar campo Nombre
                //Se hace por si el usuario decide eliminar el nombre
                if (etEditarNombre.text.toString() == "")
                    nombre = resources.getString(R.string.etValorNoProporcionado)
                else
                    nombre = etEditarNombre.text.toString()

                //Comprobar campo Telefono
                //Se hace por si el usuario decide eliminar el telefono
                if (etEditarTelefono.text.toString() == "")
                    telefono = resources.getString(R.string.etValorNoProporcionado)
                else
                    telefono = etEditarTelefono.text.toString()

                db.collection("Usuarios").document(prefs.getString("email", null).toString())
                        .set(hashMapOf("Nombre" to nombre, "Telefono" to telefono))
            }

            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

            finish() //Cierra el activity

        }

        btEditarImagen.setOnLongClickListener {
            Toast.makeText(this, R.string.textoBtCambiarImagen, Toast.LENGTH_SHORT).show()
            true
        }

        btEditarImagen.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_editar_imagen, null)

            val mBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle(resources.getString(R.string.dialogoTitulo))

            val mAlertDialog = mBuilder.show()

            mAlertDialog.dialogoBtCamara.setOnClickListener {
                Toast.makeText(this, "Se ha pulsado camara", Toast.LENGTH_SHORT).show()
                accionCamara()

                mAlertDialog.dismiss()
            }

            mAlertDialog.dialogoBtDefecto.setOnClickListener {
                editarImagen.setImageURI(Uri.parse("android.resource://${packageName}/${R.mipmap.user_default}"))
                val imagen= Uri.parse("android.resource://${packageName}/${R.mipmap.user_default}")

                subirImagen(imagen)

                mAlertDialog.dismiss()
            }

            mAlertDialog.dialogoBtGaleria.setOnClickListener {
                accionGaleria()

                mAlertDialog.dismiss()
            }
        }
    }

    private fun accionGaleria() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(i, GALERIA_ACTION_CODE)
    }

    /**
     * Metodo que llamará a todo lo relacionado con la camara
     */
    private fun accionCamara() {
        permisoCamara() //Cuando termina esto quiere decir que se ha hecho la foto o denegado permisos
    }

    private fun permisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        } else {
            abrirCamara()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Se necesitan los permisos de camara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirCamara() {
        val camara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camara.resolveActivity(packageManager) != null)
            startActivityForResult(camara, CAMERA_ACTION_CODE)
        else
            Toast.makeText(this, "El dispositivo no soporta esta opción", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * DE PARTE DE LA CAMARA
         */
        if (requestCode == CAMERA_ACTION_CODE && resultCode == RESULT_OK) {
            var datos: Bundle? = data?.extras
            var fotoBitmap: Bitmap = datos?.get("data") as Bitmap

            var uri: Uri = getImageUri(this, fotoBitmap)

            editarImagen.setImageURI(uri)

            if(data!=null){
                if(email!="-1"){
                    if (uri != null) {
                        subirImagen(uri)
                    }
                }

            }
        }

        /**
         * DE PARTE DE LA GALERIA
         */
        if(data!=null){
            val uri: Uri? =data.data // mnt/sdacard/images/image.jpg por ejemplo
            if(email!="-1"){
                subirImagen(uri)
            }

        }
    }

    /**
     * Metodo que subirá la imagen que ha elegido el usuario a la base de datos
     */
    private fun subirImagen(uri: Uri?) {
        val referencia = storageReference.child("usuarios/$email/profile.jpg")

        if(referencia!=null){

            if (uri != null) {
                referencia.putFile(uri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        cargarImagen()
                    } else {
                        Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /**
     * Metodo que combierte un objeto de tipo Bitmap a un objeto Uri
     * Retornando dicho objeto
     */
    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}