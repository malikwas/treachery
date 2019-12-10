package me.kindeep.treachery.forensic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.ForensicGameState
import me.kindeep.treachery.firebase.*
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.LiveGameInstanceSnapshot
import me.kindeep.treachery.forensicGameState

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
            Log.e("Carddds", cardsResourcesSnapshot.toString())
        }
        Log.e("Carddds", cardsResourcesSnapshot.toString())

        addOnGameUpdateListener(gameId) {
            Log.e("FORENSIC", "Updated next Card value thiingy after this shit")
            setNextCardValue()
        }

    }

    fun setNextCardValue() {
        val state = forensicGameState(gameInstance.value!!)
        Log.e("FORENSIC", "Next card state updated with: ${gameInstance.value}")
        when (state) {
            ForensicGameState.CAUSE_CARD -> {
                nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.causeCards
            }
            ForensicGameState.LOCATION_CARD -> {
                nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.locationCards
            }
            ForensicGameState.OTHER_CARD -> {
                val result = mutableListOf<ForensicCardSnapshot>()

                for (card in gameInstance.value!!.otherCards) {
                    if (card.isSelected()) {
                        result.add(card)
                    }
                }
                nextCardSnapshots.value = result
            }
        }
    }

    val nextCardSnapshots: MutableLiveData<List<ForensicCardSnapshot>> = MutableLiveData()
}
