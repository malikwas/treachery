package me.kindeep.treachery

import android.util.Log
import me.kindeep.treachery.firebase.getGame
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot


fun pickRandomMurderer(gameId: String, callback: (murdererPlayerId: String) -> Unit) {

}

fun selectRandomForensicCards(gameId: String, callback: () -> Unit) {

}

fun selectCauseForensicCard(
    gameId: String,
    forensicCardSnapshot: ForensicCardSnapshot,
    onSuccess: (() -> Unit) = {}
) {
    getGameReference(gameId).update("causeCard", forensicCardSnapshot).addOnSuccessListener {
        getGameReference(gameId).update("causeCardDefined", true)
            .addOnSuccessListener {
                onSuccess()
            }
    }
}

enum class ForensicGameState {
    CAUSE_CARD,
    LOCATION_CARD,
    OTHER_CARD
}

fun forensicGameState(gameInstance: GameInstanceSnapshot): ForensicGameState {
    Log.e("FORENSIC", "Getting game state for: $gameInstance")
    if (!gameInstance.causeCardDefined) {
        return ForensicGameState.CAUSE_CARD
    } else if (!gameInstance.locationCardDefined) {
        return ForensicGameState.LOCATION_CARD
    } else {
        return ForensicGameState.OTHER_CARD
    }
}

fun selectLocationForensicCard(
    gameId: String,
    forensicCardSnapshot: ForensicCardSnapshot,
    onSuccess: (() -> Unit) = {}
) {
    getGameReference(gameId).update("locationCard", forensicCardSnapshot).addOnSuccessListener {
        getGameReference(gameId).update("locationCardDefined", true)
            .addOnSuccessListener {
                onSuccess()
            }
    }
}

fun updateOtherForensicCards(
    gameId: String,
    otherCards: List<ForensicCardSnapshot>,
    onSuccess: (() -> Unit) = {}
) {
    getGameReference(gameId).update("otherCards", otherCards).addOnSuccessListener {
        onSuccess()
    }
}

fun selectOtherCard(
    gameId: String,
    forensicCardSnapshot: ForensicCardSnapshot,
    onSuccess: () -> Unit = {}
) {
    getGame(gameId) {
        for ((index: Int, card) in it.otherCards.withIndex()) {
            if (!card.isSelected()) {
                it.otherCards[index] = forensicCardSnapshot
                break
            }
        }

        getGameReference(gameId).update("otherCards", it.otherCards)
    }
}