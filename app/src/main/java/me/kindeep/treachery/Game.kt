package me.kindeep.treachery

import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot

/**
 *
 */
fun pickRandomMurderer(gameId: String, callback: (murdererPlayerId: String) -> Unit) {

}

fun selectRandomForensicCards(gameId: String, callback: () -> Unit) {

}

fun selectCauseForensicCard(
    gameId: String,
    forensicCardSnapshot: ForensicCardSnapshot,
    onSuccess: () -> Unit
) {
    getGameReference(gameId).update("causeCard", forensicCardSnapshot).addOnSuccessListener {
        getGameReference(gameId).update("isCauseCardDefined", true)
            .addOnSuccessListener {
                onSuccess()
            }
    }

}