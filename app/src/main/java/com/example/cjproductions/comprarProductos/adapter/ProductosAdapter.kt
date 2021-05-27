package com.example.cjproductions.comprarProductos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cjproductions.R
import com.example.cjproductions.comprarProductos.modelo.Productos
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_producto.view.*

class ProductosAdapter(val productoClick: (Productos) -> Unit): RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder>() {

    var listProductos: List<Productos> = emptyList()
    private val storage= FirebaseStorage.getInstance().reference

    fun setData(list: List<Productos>){
        listProductos= list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        return ProductosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false))
    }

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        val perfilReferencia= storage.child(listProductos[position].imagen)

        holder.itemView.tvTitulo.text=listProductos[position].nombre
        holder.itemView.tvDescripcion.text=listProductos[position].descripcion

        //Coloca la imagen desde el almacenamiento de firebase
        if(perfilReferencia!=null) {
            perfilReferencia.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).resize(300, 300).centerCrop().into(holder.itemView.logoImageView)
            }
        }

        holder.itemView.setOnClickListener {
            productoClick(listProductos[position])
        }
    }

    override fun getItemCount(): Int {
        return listProductos.size
    }

    class ProductosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}