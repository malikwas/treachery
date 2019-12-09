package me.kindeep.treachery.firebase.models

import me.kindeep.treachery.firebase.models.CardSnapshot

data class PlayerSnapshot (
    val playerName: String = "",
    val isMurderer: Boolean = false,
    val clueCards: List<CardSnapshot> = listOf(
        CardSnapshot()
    ),
    val meansCards: List<CardSnapshot> = listOf(
        CardSnapshot()
    )
)