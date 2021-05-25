package com.example.cjproductions.atencionCliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cjproductions.R
import com.example.cjproductions.atencionCliente.models.Chat
import kotlinx.android.synthetic.main.item_chat.view.*

/**
 * Clase que gestiona el recycler view de la lista de chats
 */
class ChatAdapter(val chatClick: (Chat) -> Unit): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var chats: List<Chat> = emptyList()

    fun setData(list: List<Chat>){
        chats = list
        notifyDataSetChanged()
    }

    /**
     * Metodo que crea la vista del recycler pasandole el layout correspondiente
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent,false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        holder.itemView.chatNameText.text = chats[position].name
        holder.itemView.usersTextView.text = chats[position].fecha

        holder.itemView.setOnClickListener {
            chatClick(chats[position])
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}