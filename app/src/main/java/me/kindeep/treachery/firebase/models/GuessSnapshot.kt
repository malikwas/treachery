package me.kindeep.treachery.firebase.models

import java.util.*

data class GuessSnapshot(
    val guesserPlayer: String = "GuesserPlayer",
    val guessedPlayer: String = "GuessedPlayer",
    val meansCard: String = "MeansCard",
    val clueCard: String = "ClueCard",
    var processed: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)