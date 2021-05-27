package com.example.cjproductions.novedades.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cjproductions.R
import com.example.cjproductions.novedades.modelo.Novedades
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_novedad.view.*

class NovedadesAdapter: RecyclerView.Adapter<NovedadesAdapter.NovedadesViewHolder>() {

    private val storage= FirebaseStorage.getInstance().reference
    private var listaNovedades: List<Novedades> = emptyList()

    fun setData(lista:List<Novedades>){
        listaNovedades=lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NovedadesViewHolder {
        return NovedadesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_novedad, parent, false))
    }


    override fun onBindViewHolder(holder: NovedadesViewHolder, position: Int) {
        val perfilReferencia= storage.child(listaNovedades[position].imagen)

        holder.itemView.tvTituloNovedad.text=listaNovedades[position].titulo
        holder.itemView.tvDescripcionNovedad.text=listaNovedades[position].descripcion
        holder.itemView.tvFechaNovedad.text=listaNovedades[position].fecha

        //Coloca la imagen desde el almacenamiento de firebase
        if(perfilReferencia!=null) {
            perfilReferencia.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).resize(600, 400).centerCrop().into(holder.itemView.imagenNovedad)
            }
        }
    }

    override fun getItemCount(): Int {
        return listaNovedades.size
    }

    class NovedadesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}