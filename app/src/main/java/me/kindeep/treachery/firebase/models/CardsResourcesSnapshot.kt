package me.kindeep.treachery.firebase.models

data class CardsResourcesSnapshot(
    val clueCards: List<ForensicCardSnapshot> = listOf(),
    val forensicCards: ForensicCardListsSnapshot = ForensicCardListsSnapshot(),
    val meansCards: List<ForensicCardSnapshot> = listOf(),
    val totalOtherCards: Int = 0
)