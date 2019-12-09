package me.kindeep.treachery.firebase.models

import com.google.firebase.Timestamp

data class MessageSnapshot (
    val playerName: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)