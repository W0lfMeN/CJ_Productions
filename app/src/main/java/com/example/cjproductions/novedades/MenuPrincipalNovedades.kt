package com.example.cjproductions.novedades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cjproductions.R
import com.example.cjproductions.novedades.adapter.NovedadesAdapter
import com.example.cjproductions.novedades.modelo.Novedades
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_menu_principal_novedades.*

class MenuPrincipalNovedades : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal_novedades)

        initViews()

    }

    private fun initViews(){
        recyclerNovedades.layoutManager=LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerNovedades.adapter=NovedadesAdapter()

        db.collection("Novedades").get().addOnSuccessListener { novedades ->
            val listaDeNovedades = novedades.toObjects(Novedades::class.java)

            (recyclerNovedades.adapter as NovedadesAdapter).setData(listaDeNovedades)
        }

        //Se añade este listener para capturar cualquier cambio en la base de datos y lo muestre en la pantalla
        db.collection("Novedades")
            .addSnapshotListener { novedades, error ->
                if(error == null){
                    novedades?.let {
                        val listaDeNovedades = it.toObjects(Novedades::class.java)

                        (recyclerNovedades.adapter as NovedadesAdapter).setData(listaDeNovedades)
                    }
                }
            }
    }
}