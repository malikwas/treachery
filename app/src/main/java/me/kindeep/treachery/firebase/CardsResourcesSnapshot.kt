package me.kindeep.treachery.firebase

data class CardsResourcesSnapshot(
    val clueCards: List<ForensicCardSnapshot> = listOf(),
    val forensicCards: ForensicCardListsSnapshot = ForensicCardListsSnapshot(),
    val meansCards: List<ForensicCardSnapshot> = listOf()
)