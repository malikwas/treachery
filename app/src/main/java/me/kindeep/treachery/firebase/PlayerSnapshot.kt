package me.kindeep.treachery.firebase

data class PlayerSnapshot (
    val playerName: String = "",
    val isMurderer: Boolean = false,
    val clueCards: List<CardSnapshot> = listOf(CardSnapshot()),
    val meansCards: List<CardSnapshot> = listOf(CardSnapshot())
)