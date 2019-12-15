package me.kindeep.treachery.player

import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.*
import me.kindeep.treachery.sendGuess
import me.kindeep.treachery.sendGuessMessage
import kotlin.properties.Delegates

class EnableChange(state: Boolean) {
    var changeListeners = ArrayList<() -> Unit>()

    var bool: Boolean by Delegates.observable(state) {_, _, newValue ->
        changeListeners.forEach {
            it()
        }
    }
}

class PlayerViewModel : ViewModel() {
    companion object {
        var gameId: String = ""
        var playerName: String? = null
    }

    private val selectedMeans = HashMap<String, Int>()
    private val selectedClues = HashMap<String, Int>()
    private var currentViewedPlayer: String? = null
    private var isAbleToGuess = false
        set(value) {
            field = value

            log(value)

            if (enableChange.bool != value)
                enableChange.bool = value
        }
    private var enableChange: EnableChange = EnableChange(isAbleToGuess)

    fun getEnableChange(): EnableChange {
        return enableChange
    }

    fun getSelectedClue(playerName: String): Int {
        return selectedClues.getOrElse(playerName) { -1 }
    }

    fun setSelectedClue(playerName: String, clue: Int) {
        if (selectedClues[playerName] == clue) selectedClues.remove(playerName)
        else selectedClues[playerName] = clue

        isAbleToGuess = selectedClues.containsKey(playerName)
                && selectedMeans.containsKey(playerName) && currentViewedPlayer.equals(playerName)
    }

    fun getSelectedMeans(playerName: String): Int {
        return selectedMeans.getOrElse(playerName) { -1 }
    }

    fun setSelectedMeans(playerName: String, means: Int) {
        if (selectedMeans[playerName] == means) selectedMeans.remove(playerName)
        else selectedMeans[playerName] = means

        isAbleToGuess = selectedClues.containsKey(playerName)
                && selectedMeans.containsKey(playerName) && currentViewedPlayer.equals(playerName)
    }

    fun getCurrentViewedPlayer(): String? {
        return currentViewedPlayer
    }

    fun setCurrentViewedPlayer(playerName: String) {
        currentViewedPlayer = playerName
        isAbleToGuess = selectedClues.containsKey(playerName)
                && selectedMeans.containsKey(playerName) && currentViewedPlayer.equals(playerName)
    }

    fun getIsAbleToGuess(): Boolean {
        return isAbleToGuess
    }

    val gameInstance: LiveGameInstanceSnapshot =
        LiveGameInstanceSnapshot(gameId)

    fun makeGuess() {
        val clueCard = gameInstance.value!!.players.find { it.playerName == currentViewedPlayer }!!
            .clueCards.get(selectedClues[currentViewedPlayer!!]!!).name

        setSelectedClue(currentViewedPlayer!!, selectedClues[currentViewedPlayer!!]!!)

        val meansCard = gameInstance.value!!.players.find { it.playerName == currentViewedPlayer }!!
            .meansCards.get(selectedMeans[currentViewedPlayer!!]!!).name

        setSelectedMeans(currentViewedPlayer!!, selectedMeans[currentViewedPlayer!!]!!)

        val g = GuessSnapshot(
            guesserPlayer = playerName!!,
            guessedPlayer = currentViewedPlayer!!,
            clueCard =  clueCard,
            meansCard = meansCard
        )

        sendGuess(g, gameId)
        sendGuessMessage(gameId, g)
    }

    fun getPlayer(): PlayerSnapshot {
        if (!playerName.isNullOrBlank()) {
            val player: PlayerSnapshot = gameInstance.value!!.players.find {
                log("$it $playerName")
                it.playerName == playerName
            } ?: PlayerSnapshot()
            return player
        }
        return PlayerSnapshot()
    }

    fun getPlayerByName(pName: String): PlayerSnapshot {
        return gameInstance.value?.players?.find { it.playerName == pName } ?: PlayerSnapshot()
    }

    init {
        addOnGameUpdateListener(gameId) {
        }
    }

    fun log(umm: Any) {
        Log.e("PLAYER_VIEW_MODEL", umm.toString())
    }
}