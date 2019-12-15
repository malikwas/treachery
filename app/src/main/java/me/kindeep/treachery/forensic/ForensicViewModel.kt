package me.kindeep.treachery.forensic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.ForensicGameState
import me.kindeep.treachery.firebase.*
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.GuessSnapshot
import me.kindeep.treachery.firebase.models.LiveGameInstanceSnapshot
import me.kindeep.treachery.forensicGameState
import me.kindeep.treachery.processGuess

class ForensicViewModel : ViewModel() {
    companion object {
        var gameId: String = "default"
    }

    val gameInstance: LiveGameInstanceSnapshot =
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
            for (guess in it.guesses) {
                if (!guess.processed) processGuess(it.guesses, guess, it.murdererName!!,
                    it.murdererClueCard!!, it.murdererMeansCard!!, it.players, it.gameId)
            }

            var numGuesses = 0;
            for (player in it.players) {
                if (player.guessed) numGuesses++
            }

            if (numGuesses == it.players.size) {} // Game Over?
        }
    }

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

                for (card in gameInstance.value!!.otherCards) {
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
