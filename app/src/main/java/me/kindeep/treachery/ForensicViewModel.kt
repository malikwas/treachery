package me.kindeep.treachery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.firebase.*

class ForensicViewModel : ViewModel() {
    companion object {
        var gameId: String = "default"
    }

    val gameInstance: LiveGameInstanceSnapshot = LiveGameInstanceSnapshot(gameId)
    var cardsResourcesSnapshot: CardsResourcesSnapshot = CardsResourcesSnapshot()

    init {
        getCardsResourcesSnapshot {
            cardsResourcesSnapshot = it
            setNextCardValue()
            Log.e("Carddds", cardsResourcesSnapshot.toString())
        }
        Log.e("Carddds", cardsResourcesSnapshot.toString())
    }

    fun setNextCardValue() {
        if (!gameInstance.value!!.isCauseCardDefined) {
            nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.causeCards
        } else if (!gameInstance.value!!.isLocationCardDefined) {
            nextCardSnapshots.value = cardsResourcesSnapshot.forensicCards.locationCards
        } else {

            val result = mutableListOf<ForensicCardSnapshot>()

            for (card in gameInstance.value!!.otherCards) {
                if (card.isSelected()) {
                    result.add(card)
                }
            }
            nextCardSnapshots.value = result
        }
    }

    val nextCardSnapshots: MutableLiveData<List<ForensicCardSnapshot>> = MutableLiveData()


}
