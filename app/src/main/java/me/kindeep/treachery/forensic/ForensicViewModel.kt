package me.kindeep.treachery.forensic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kindeep.treachery.*
import me.kindeep.treachery.firebase.*
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.LiveGameInstanceSnapshot
import kotlin.collections.HashSet

interface GameViewModel {
    val gameInstance: LiveGameInstanceSnapshot
}

class ForensicViewModel : ViewModel(), GameViewModel {
    companion object {
        var gameId: String = "default"
    }

    val processedOrProcessingGuessIds = HashSet<String>()

    override val gameInstance: LiveGameInstanceSnapshot =
        LiveGameInstanceSnapshot(gameId)

    var cardsResourcesSnapshot: CardsResourcesSnapshot =
        CardsResourcesSnapshot()

    init {
        getCardsResourcesSnapshot {
            cardsResourcesSnapshot = it
            setNextCardValue()
        }

        addOnGameUpdateListener(gameId) {
            setNextCardValue()
        }

        gameInstance.observeForever {
            // A guess cannot be made until the game has started, so the murderer has already been
            // selected and the two cards have been chosen

            if (it.guesses.size >= MIN_PLAYERS_SIZE) {
                if (it.guesses.size == it.players.size) {
                    setGuessesExpired(gameId, true)
                }
            }
        }
    }

    var allowedCards = 0

    fun startTimer() {
        if (!timerStarted) {
            timerStarted = true
            sendForensicMessage(
                gameId,
                "Location and Cause of Death cards set, a new card will be added every ${SINGLE_CARD_TIME / 1000} seconds."
            )
            // Starts after the first two cards have been placed.

            // Add one to allowed cards after fixed time

            val numTimers = gameInstance.value!!.otherCards.size

            for (i in 0 until numTimers) {
                val job = GlobalScope.launch(Dispatchers.Main) {
                    delay(SINGLE_CARD_TIME * i)
                    allowedCards++
                    setNextCardValue()
                    val remainingTime =
                        getRemainingTime(gameInstance.value!!.startedTimestamp) / 1000
//                    sendForensicMessage(gameId, "Another card now available, deciding value...\n Remaining time: $remainingTime")
                }
            }
        }
    }

    fun getRemainingTime(): Long {
        return getRemainingTime(gameInstance.value!!.startedTimestamp) / 1000
    }

    var timerStarted = false

    fun setNextCardValue() {
        val state = forensicGameState(gameInstance.value!!)
        when (state) {
            ForensicGameState.CAUSE_CARD -> {
                nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.causeCards
            }
            ForensicGameState.LOCATION_CARD -> {
                nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.locationCards
            }
            ForensicGameState.OTHER_CARD -> {
                startTimer()
                for (card in gameInstance.value!!.otherCards.subList(0, allowedCards)) {
                    if (!card.isSelected()) {
                        nextCardSnapshots.value = listOf(card)
                        return
                    }
                }

                nextCardSnapshots.value = listOf()
            }
        }
    }

    val nextCardSnapshots: MutableLiveData<List<ForensicCardSnapshot>> = MutableLiveData()
}
