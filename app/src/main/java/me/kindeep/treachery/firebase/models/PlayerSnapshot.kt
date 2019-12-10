package me.kindeep.treachery.firebase.models

data class PlayerSnapshot (
    val playerName: String = "NoName",
    val isMurderer: Boolean = false,
    val clueCards: List<CardSnapshot> = listOf(),
    val meansCards: List<CardSnapshot> = listOf()
)