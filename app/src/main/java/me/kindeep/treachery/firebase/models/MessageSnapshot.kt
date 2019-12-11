package me.kindeep.treachery.firebase.models

import com.google.firebase.Timestamp

data class MessageSnapshot (
    val playerName: String = "Loading...",
    val message: String = "Loading...",
    val timestamp: Timestamp = Timestamp.now()
)