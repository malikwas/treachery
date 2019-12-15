package me.kindeep.treachery.firebase.models

data class GuessSnapshot(
    val guesserPlayer: String = "GuesserPlayer",
    val guessedPlayer: String = "GuessedPlayer",
    val meansCard: String = "MeansCard",
    val clueCard: String = "ClueCard",
    var processed: Boolean = false
)