package me.kindeep.treachery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.system.Os.remove
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.getCardsResourcesSnapshot
import me.kindeep.treachery.firebase.getGame
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.*
import me.kindeep.treachery.forensic.ForensicActivity
import me.kindeep.treachery.player.PlayerActivity
import me.kindeep.treachery.shared.finish.FinishActivity
import java.sql.Time
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.max
import kotlin.math.min


const val MIN_PLAYERS_SIZE = 3
const val MURDER_SELECT_CARDS_TIMEOUT: Long = 3000
const val FORENSIC_NAME: String = "Forensic Scientist"
const val SINGLE_CARD_TIME: Long = 30000
const val TOTAL_GAME_TIME: Long = 400000

//const val MIN_PLAYERS_SIZE = 3
//const val MURDER_SELECT_CARDS_TIMEOUT: Long = 30000
//const val FORENSIC_NAME: String = "Forensic Scientist"
//const val SINGLE_CARD_TIME: Long = 30000
//const val TOTAL_GAME_TIME: Long = 400000


// Type 2: Game information
// Type 1: Chat message
// Just 1 and 2

fun sendProcessedGuessMessage(gameId: String, guessSnapshot: GuessSnapshot) {
    sendMessage(
        MessageSnapshot(
            playerName = FORENSIC_NAME,
            type = 2,
            message = "No ${guessSnapshot.guesserPlayer}, that guess is incorrect.",
            timestamp = Timestamp(Timestamp.now().seconds + 1, Timestamp.now().nanoseconds)
        ), gameId
    )
}

fun sendForensicMessage(gameId: String, message: String) {
    sendMessage(
        MessageSnapshot(
            playerName = FORENSIC_NAME,
            type = 2,
            message = message
        ), gameId
    )
}

fun sendGuessMessage(gameId: String, guessSnapshot: GuessSnapshot) {
    sendMessage(
        MessageSnapshot(
            playerName = guessSnapshot.guesserPlayer,
            type = 2,
            message = "I believe that ${guessSnapshot.guessedPlayer} is the murderer, and that the" +
                    " means was ${guessSnapshot.meansCard} and the clue is ${guessSnapshot.clueCard}."
        ), gameId
    )
}

fun sendMessage(message: MessageSnapshot, gameId: String) {
    getGame(gameId) {
        val gameRef = getGameReference(gameId)
        gameRef.update("messages", FieldValue.arrayUnion(message))
    }
}

fun sendGuess(guessSnapshot: GuessSnapshot, gameId: String) {
    getGameReference(gameId).update("guesses", FieldValue.arrayUnion(guessSnapshot))
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
    OTHER_CARD,
    ROUND_2,
    ROUND_3
}

fun forensicGameState(gameInstance: GameInstanceSnapshot): ForensicGameState {
    log("Getting game state for: $gameInstance")
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
        sendForensicMessage(gameId, "Added another card")
        onSuccess()
    }
}


fun startRound(
    gameId: String,
    roundNum: Int,
    replaceCardName: String,
    newCard: ForensicCardSnapshot,
    onSuccess: () -> Unit = {}
) {
    sendForensicMessage(gameId, "ROUND 2")

    getGame(gameId) { gameInstanceSnapshot ->
        for ((i, card) in gameInstanceSnapshot.otherCards.withIndex()) {
            if (card.cardName == newCard.cardName) {
                gameInstanceSnapshot.otherCards[i] = newCard
                break
            }
        }
        updateOtherForensicCards(gameId, gameInstanceSnapshot.otherCards) {
            sendForensicMessage(gameId, "I've replaced the $replaceCardName with $newCard")
            onSuccess()
        }
    }
}

fun onPlayerMurdererDetermined(gameId: String, playerName: String, callback: () -> Unit) {
    gameChangeListener(gameId) { prev, new ->
        log("GAME CHANGE LISTENER TRIGGEERD FROM MURDERER DETERMINED $playerName $gameId ${new.murdererName} ${new.murdererSelected} ${prev.murdererSelected}")
        if (new.murdererSelected && !prev.murdererSelected) {
            if (new.murdererName == playerName) {
                log("YES MURDERER OPEN ACTIVITY PLEASE $playerName $gameId ${new.murdererName} ${new.murdererSelected} ${prev.murdererSelected}")
                callback()
                return@gameChangeListener
            }
        }
    }
}

fun onMurdererCardsDetermined(
    gameId: String,
    callback: () -> Unit
) {
    gameChangeListener(gameId) { prev, new ->
        if (new.murdererCardsDetermined && !prev.murdererCardsDetermined) {
            callback()
            return@gameChangeListener
        }
    }
}

fun gameChangeListener(
    gameId: String,
    onChange: (prev: GameInstanceSnapshot, new: GameInstanceSnapshot) -> Unit
) {
    var prevGameInstanceSnapshot = GameInstanceSnapshot()
    getGameReference(gameId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
        val new = documentSnapshot?.toObject<GameInstanceSnapshot>()
        if (new != null) {
            onChange(prevGameInstanceSnapshot, new)
            prevGameInstanceSnapshot = new
        }
    }
}


fun processGuess(
    guess: GuessSnapshot,
    murdererName: String,
    murdererClueCard: String,
    murdererMeansCard: String,
    players: List<PlayerSnapshot>,
    gameId: String
) {
    sendForensicMessage(gameId, "Processing guess $murdererName ${guess.guessedPlayer} $murdererMeansCard $murdererClueCard $guess")
    if (guess.guessedPlayer == murdererName && guess.clueCard == murdererClueCard
        && guess.meansCard == murdererMeansCard
    ) {
        sendForensicMessage(gameId, "That guess was correct")
        updateField(gameId, "correctlyGuessed", true) {
            updateField(gameId, "correctGuess", guess) {

            }
        }
    } else {
        val player = players.find {
            it.playerName == guess.guessedPlayer
        }!!

        player.meansCards.find { it.name == guess.meansCard }!!.guessedBy.add(guess.guesserPlayer)
        player.clueCards.find { it.name == guess.clueCard }!!.guessedBy.add(guess.guesserPlayer)
        guess.processed = true

        sendProcessedGuessMessage(gameId, guess)

        getGameReference(gameId).update("players", players)
        getGameReference(gameId).update("guesses", FieldValue.arrayUnion(guess))
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

        getGameReference(gameId).update("otherCards", it.otherCards).addOnSuccessListener {
            onSuccess()
        }
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
        if (it.players.size >= MIN_PLAYERS_SIZE) {
            // Player size ok, start
            getGameReference(gameId).update("started", true).addOnSuccessListener {
                // new players should not be added after this update, initialize player cards
                getGame(gameId) { gameInstance ->
                    getCardsResourcesSnapshot { cardResources ->
                        val shuffledClues = cardResources.clueCards.shuffled().toMutableList()
                        val shuffledMeans = cardResources.meansCards.shuffled().toMutableList()
                        val murdererIndex = gameInstance.players.indices.random()
                        val murdererPlayer = gameInstance.players[murdererIndex]
                        for ((index, player) in gameInstance.players.withIndex()) {
                            if (index == murdererIndex) {
                                gameInstance.murdererName = player.playerName
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
                                updateField(gameId, "murdererName", murdererPlayer.playerName) {
                                    updateField(gameId, "murdererSelected", true) {
                                        sendForensicMessage(gameId, "Murderer, select your cards.")
                                        Timer("Murderer Card Select Delay", false).schedule(
                                            MURDER_SELECT_CARDS_TIMEOUT
                                        ) {
                                            log("TIMER FINISHED AAAAARGH")
                                            selectMurderCards(
                                                gameId = gameId,
                                                clueCard = murdererPlayer.clueCards.random(),
                                                meansCard = murdererPlayer.meansCards.random()
                                            ) {
                                                sendForensicMessage(
                                                    gameId,
                                                    "Timeout, selecting random cards."
                                                )
                                            }
                                        }
                                        onMurdererCardsDetermined(gameId) {
                                            sendForensicMessage(
                                                gameId,
                                                "Murderer selected their cards."
                                            )
                                            onSuccess()
                                            return@onMurdererCardsDetermined
                                        }
                                    }
                                }
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

fun getMurderer(gameInstance: GameInstanceSnapshot, murdererName: String): PlayerSnapshot? {
    return gameInstance.players.find { it.playerName == murdererName }
}

fun selectMurderCards(
    gameId: String,
    clueCard: CardSnapshot,
    meansCard: CardSnapshot,
    onSuccess: () -> Unit
) {
    getGame(gameId) {
        it.murdererClueCard = clueCard
        it.murdererMeansCard = meansCard
        log("Try selecting murder card ${it.murdererCardsDetermined}")
        if (!it.murdererCardsDetermined) {
            log("try updating related fields.")
            updateField(gameId, "murdererClueCard", it.murdererClueCard) {
                updateField(gameId, "murdererMeansCard", it.murdererMeansCard) {
                    updateField(gameId, "murdererCardsDetermined", true) {
                        log("Success updating all fields")
                        onSuccess()
                    }
                }
            }
        }
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
                sendForensicMessage(gameId, "${playerSnapshot.playerName} joined the game.")
                onSuccess()
            }.addOnFailureListener {
                onFailure(PlayerAddFailureType.WTF)
            }
        }
    }
}

fun updateField(
    gameId: String,
    fieldName: String,
    fieldValue: Any,
    onUpdateSuccess: () -> Unit = {}
) {
    getGameReference(gameId).update(fieldName, fieldValue).addOnSuccessListener {
        onUpdateSuccess()
    }
}

fun log(toLog: Any) {
    Log.i("GAME_KT", toLog.toString())
}

fun getRemainingTime(startedTimestamp: Timestamp): Long {
    val currentTime = Timestamp.now()
    return max(0, (startedTimestamp.toDate().time + TOTAL_GAME_TIME) - currentTime.toDate().time)

}

/**
 * Only ever set this once. ONCE. ONLY.
 */
var bigBrainSingleCallback: () -> Unit = {}
var hackBoolBigBrain = true
fun onGameFinish(gameId: String, callback: () -> Unit) {
    bigBrainSingleCallback = callback
    getGame(gameId) {
        if (hackBoolBigBrain) {
            if (it.correctlyGuessed) {
                hackBoolBigBrain = false
                bigBrainSingleCallback()
                return@getGame
            } else if (it.guessesExpired) {
                hackBoolBigBrain = false
                bigBrainSingleCallback()
                return@getGame
            }
            val currentTime = Timestamp.now()
            val remainingTime =
                max(
                    0,
                    (it.startedTimestamp.toDate().time + TOTAL_GAME_TIME) - currentTime.toDate().time
                )

            val job = GlobalScope.launch(Dispatchers.Main) {
                delay(remainingTime)
                if (hackBoolBigBrain) {
                    hackBoolBigBrain = false
                    bigBrainSingleCallback()
                }
                return@launch
            }
        }

        if (hackBoolBigBrain) {
            gameChangeListener(gameId) { prev, new ->
                if (hackBoolBigBrain) {
                    if (new.correctlyGuessed) {
                        hackBoolBigBrain = false
                        bigBrainSingleCallback()
                        return@gameChangeListener
                    } else if (new.guessesExpired) {
                        hackBoolBigBrain = false
                        bigBrainSingleCallback()
                        return@gameChangeListener
                    }
                }
            }
        }
    }

}

fun getGameFinishIntent(from: Context, gameId: String): Intent {
    val intent = Intent(from, FinishActivity::class.java)
    val b = Bundle()
    b.putString("gameId", gameId)
    intent.putExtras(b)
    return intent
}
