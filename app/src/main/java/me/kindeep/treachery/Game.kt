package me.kindeep.treachery

import android.util.Log
import me.kindeep.treachery.firebase.getCardsResourcesSnapshot
import me.kindeep.treachery.firebase.getGame
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.firebase.models.GuessSnapshot
import me.kindeep.treachery.firebase.models.PlayerSnapshot
import me.kindeep.treachery.firebase.sendProcessedGuessMessage


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

fun processGuess(guessSnapshots: List<GuessSnapshot>, guess: GuessSnapshot, murdererName: String, murdererClueCard: String,
                 murdererMeansCard: String, players: List<PlayerSnapshot>, gameId: String) {
    if (guess.guessedPlayer == murdererName && guess.clueCard == murdererClueCard
        && guess.meansCard == murdererMeansCard) {
        // You win?
    } else {
        val player = players.find {
            it.playerName == guess.guessedPlayer
        }

        player!!.meansCards.find { it.name == guess.meansCard }!!.guessedBy.add(guess.guesserPlayer)
        player!!.clueCards.find { it.name == guess.clueCard }!!.guessedBy.add(guess.guesserPlayer)
        guess.processed = true

        sendProcessedGuessMessage(gameId, guess)

        getGameReference(gameId).update("players", players)
        getGameReference(gameId).update("guesses", guessSnapshots)
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

enum class StartFailureType {
    NOT_ENOUGH_PLAYERS,
    WTF // What the terrible Failure
}


fun fireStartGame(
    gameId: String,
    onSuccess: () -> Unit = {},
    onFailure: (StartFailureType) -> Unit = {}
) {
    // TODO: This should be a transaction.
    getGame(gameId) {
        if (it.players.size > 3) {
            // Player size ok, start
            getGameReference(gameId).update("started", true).addOnSuccessListener {
                // new players should not be added after this update, initialize player cards
                getGame(gameId) { gameInstance ->
                    getCardsResourcesSnapshot { cardResources ->
                        val shuffledClues = cardResources.clueCards.shuffled().toMutableList()
                        val shuffledMeans = cardResources.meansCards.shuffled().toMutableList()
                        val murdererIndex = gameInstance.players.indices.random()

                        for ((index, player) in gameInstance.players.withIndex()) {
                            if (index == murdererIndex) {
                                player.murderer = true
                            }
                            // Deal 4 red and blue cards to each player
                            for (i in 1..4) {
                                if (shuffledClues.isNotEmpty()) {
                                    player.clueCards.add(shuffledClues.removeAt(0))
                                }
                                if (shuffledMeans.isNotEmpty()) {
                                    player.meansCards.add(shuffledMeans.removeAt(0))
                                }
                            }
                        }
                        getGameReference(gameId).update("players", gameInstance.players)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                    }
                }
            }
        } else {
            onFailure(StartFailureType.NOT_ENOUGH_PLAYERS)
        }
    }.addOnFailureListener {
        onFailure(StartFailureType.WTF)
    }
}

enum class PlayerAddFailureType {
    DUPLICATE_NAME,
    WTF // What a Terrible Failure
}


fun addPlayer(
    gameId: String,
    playerSnapshot: PlayerSnapshot,
    onSuccess: () -> Unit = {},
    onFailure: (PlayerAddFailureType) -> Unit = {}
) {
    getGame(gameId) {
        if (it.players.map { it.playerName }.contains(playerSnapshot.playerName)) {
            onFailure(PlayerAddFailureType.DUPLICATE_NAME)

        } else {
            it.players.add(playerSnapshot)
            getGameReference(gameId).update("players", it.players).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure(PlayerAddFailureType.WTF)
            }
        }
    }
}
