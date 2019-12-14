package me.kindeep.treachery.firebase.models

data class PlayerSnapshot (
    var playerName: String = "Loading...",
    var murderer: Boolean = false,
    var clueCards: MutableList<CardSnapshot> = mutableListOf(),
    var meansCards: MutableList<CardSnapshot> = mutableListOf()
)