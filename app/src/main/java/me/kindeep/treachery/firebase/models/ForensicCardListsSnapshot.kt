package me.kindeep.treachery.firebase.models

data class ForensicCardListsSnapshot(
    val causeCards: List<ForensicCardSnapshot> = listOf(),
    val locationCards: List<ForensicCardSnapshot> = listOf(),
    val otherCards: List<ForensicCardSnapshot> = listOf()
)