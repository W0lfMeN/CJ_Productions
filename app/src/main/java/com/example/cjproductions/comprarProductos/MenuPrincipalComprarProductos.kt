package com.example.cjproductions.comprarProductos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cjproductions.R
import com.example.cjproductions.comprarProductos.activity.ProductosActivity
import com.example.cjproductions.comprarProductos.adapter.ProductosAdapter
import com.example.cjproductions.comprarProductos.modelo.Productos
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_menu_principal_comprar_productos.*

class MenuPrincipalComprarProductos : AppCompatActivity() {
    private var db =FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal_comprar_productos)

        title = resources.getString(R.string.tituloMenuComprarProductos)


        initViews()
    }

    private fun initViews() {
        listComprarRecycler.layoutManager= LinearLayoutManager(this)
        listComprarRecycler.adapter=
                ProductosAdapter { productos ->
                    productosSelected(productos)
                }


        db.collection("Productos").get().addOnSuccessListener { productos ->
            val listaDeProductos = productos.toObjects(Productos::class.java)

            (listComprarRecycler.adapter as ProductosAdapter).setData(listaDeProductos)
        }

        //Se añade este listener para capturar cualquier cambio en la base de datos y lo muestre en la pantalla
        db.collection("Productos")
                .addSnapshotListener { productos, error ->
                    if(error == null){
                        productos?.let {
                            val listaDeProductos = it.toObjects(Productos::class.java)

                            (listComprarRecycler.adapter as ProductosAdapter).setData(listaDeProductos)
                        }
                    }
                }

    }

    private fun productosSelected(productos: Productos) {
        val intent = Intent(this, ProductosActivity::class.java)
        intent.putExtra("enlace", productos.enlace)
        intent.putExtra("nombre",productos.nombre)
        startActivity(intent)

    }
}