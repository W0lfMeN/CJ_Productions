package com.example.cjproductions.atencionCliente.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cjproductions.R
import com.example.cjproductions.atencionCliente.activities.ChatActivity
import com.example.cjproductions.atencionCliente.adapters.ChatAdapter
import com.example.cjproductions.atencionCliente.models.Chat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu_principal_admin.*

class MenuPrincipalAdmin : AppCompatActivity() {
    private var user = ""

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal_admin)

        title = resources.getString(R.string.tituloMenuSoporte)

        //guardamos en la variable 'user' el correo que se encuentre en la persistencia
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.preferenciasFile), Context.MODE_PRIVATE)
        user = prefs.getString("email", null).toString()

        //si el usuario no es nulo, iniciamos la vista
        if (user.isNotEmpty()) {
            initViews()
        }
    }

    /**
     * Funcion que inicializa la vista de la ventana
     * Mostrandonos los chats que tendremos disponibles
     *
     * El admin no puede iniciar conversación, solo pueden hacerlo
     * los usuarios que no sean admins
     */
    private fun initViews() {
        listChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listChatsRecyclerView.adapter =
                ChatAdapter { Chat ->
                    chatSelected(Chat)
                }

        val userRef = db.collection("Usuarios").document(user)

        userRef.collection("Chats").get().addOnSuccessListener { Chats ->
            val listChats = Chats.toObjects(Chat::class.java)

            (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
        }


        //Se añade este listener para capturar cualquier cambio en la base de datos y lo muestre en la pantalla
        userRef.collection("Chats")
                .addSnapshotListener { Chats, error ->
                    if (error == null) {
                        Chats?.let {
                            val listChats = it.toObjects(Chat::class.java)

                            (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                        }
                    }
                }
    }

    /**
     * Funcion que inicia un nuevo activity con el chat que hemos seleccionado
     * Pasando como parametros el id del chat y el usuario
     */
    private fun chatSelected(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("user", user)
        startActivity(intent)
    }
}