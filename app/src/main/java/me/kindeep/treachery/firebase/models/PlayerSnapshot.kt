package me.kindeep.treachery.firebase.models

data class PlayerSnapshot (
    var playerName: String = "Loading...",
    var isMurderer: Boolean = false,
    var clueCards: MutableList<CardSnapshot> = mutableListOf(),
    var meansCards: MutableList<CardSnapshot> = mutableListOf()
)