package me.kindeep.treachery.firebase.models

data class CardsResourcesSnapshot(
    val clueCards: List<CardSnapshot> = listOf(),
    val forensicCards: ForensicCardListsSnapshot = ForensicCardListsSnapshot(),
    val meansCards: List<CardSnapshot> = listOf(),
    val totalOtherCards: Int = 0
)