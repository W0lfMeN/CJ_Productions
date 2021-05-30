package com.example.cjproductions.atencionCliente.models

import java.util.*

/**
 * Clase que va a contener todos los datos de un mensaje
 */
data class Message(
        var message: String = "",
        var from: String = "",
        var dob: Date = Date()
)