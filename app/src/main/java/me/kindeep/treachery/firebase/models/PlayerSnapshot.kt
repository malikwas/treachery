package me.kindeep.treachery.firebase.models

import java.io.Serializable

data class PlayerSnapshot(
    var playerName: String = "Loading...",
    var clueCards: MutableList<CardSnapshot> = mutableListOf(),
    var meansCards: MutableList<CardSnapshot> = mutableListOf(),
    var guessed: Boolean = false
) : Serializable