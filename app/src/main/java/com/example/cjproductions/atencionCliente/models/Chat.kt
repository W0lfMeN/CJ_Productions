package com.example.cjproductions.atencionCliente.models

/**
 * Clase de los datos que va a contener todos los chats
 */
data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList(),
    var fecha: String = ""
)