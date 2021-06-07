package com.example.cjproductions.atencionCliente.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cjproductions.R
import com.example.cjproductions.atencionCliente.adapters.MessageAdapter
import com.example.cjproductions.atencionCliente.models.Message
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private var chatId = ""
    private var user = ""

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var admin = "" //Esta variable almacenará el nombre del administrador seleccionado


        intent.getStringExtra("chatId")?.let { chatId = it }
        intent.getStringExtra("user")?.let { user = it }

        //Esta variable solo será usada si el activity es llamado desde un usuario y no un admin
        intent.getStringExtra("admin")?.let { admin = it }

        /*
            Si el admin es distinto de "" quiere decir que es un usuario es el que ha iniciado sesion,
            y por tanto mostrará en el titulo del activity el nombre del administrador que ha sido
            seleccionado.
         */
        if (admin != "") {
            db.collection("Usuarios").document(admin).get().addOnCompleteListener {
                title = it.result?.get("Nombre").toString()
            }
        }

        if (chatId.isNotEmpty() && user.isNotEmpty()) {
            initViews()
        }
    }

    /**
     * Funcion que inicializa la vista
     */
    private fun initViews() {
        messagesRecylerView.layoutManager = LinearLayoutManager(this)
        messagesRecylerView.adapter = MessageAdapter(user)

        sendMessageButton.setOnClickListener {
            sendMessage()
        }

        val chatRef = db.collection("Chats").document(chatId)

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { messages ->
                    val listMessages = messages.toObjects(Message::class.java)
                    (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
                }

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
                .addSnapshotListener { messages, error ->
                    if (error == null) {
                        messages?.let {
                            val listMessages = it.toObjects(Message::class.java)
                            (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
                        }
                    }
                }
    }

    /**
     * Funcion que envia el mensaje
     */
    private fun sendMessage() {
        val message = Message(
                message = messageTextField.text.toString(),
                from = user
        )

        db.collection("Chats").document(chatId).collection("messages").document().set(message)

        messageTextField.setText("")
    }

    /**
     * Funcion que se ejecuta cuando se pulse el boton de retroceder
     * al momento de finalizar el activity
     */
    override fun onBackPressed() {
        val correosDesarrolladores = arrayListOf<String>("carlosjmsanchez@gmail.com")

        //Este if evitará que aparezca el dialogo de cerrar ventana cuando el correo que hay es de un admin
        if (!correosDesarrolladores.contains(user))
            showAlert(resources.getString(R.string.alertChatUser))
        else
            finish()
    }

    /**
     * Funcion que genera un mensaje de alerta en la pantalla
     * con el texto que se le pase por parametro
     */
    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
        builder.setPositiveButton(resources.getString(R.string.aceptar)) { _, _ ->
            finish()
        }
        builder.setNegativeButton(resources.getString(R.string.cancelar)) { view, _ ->
            view.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}