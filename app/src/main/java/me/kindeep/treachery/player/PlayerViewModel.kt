package me.kindeep.treachery.player

import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.*
import me.kindeep.treachery.forensic.GameViewModel
import me.kindeep.treachery.processGuess
import me.kindeep.treachery.sendGuess
import me.kindeep.treachery.sendGuessMessage
import kotlin.properties.Delegates

class EnableChange(state: Boolean) {
    var changeListeners = ArrayList<() -> Unit>()

    var bool: Boolean by Delegates.observable(state) { _, _, newValue ->
        changeListeners.forEach {
            it()
        }
    }
}

class PlayerViewModel : ViewModel(), GameViewModel {
    companion object {
        var gameId: String = ""
        var playerName: String? = null
    }

    private val selectedMeans = HashMap<String, Int>()
    private val selectedClues = HashMap<String, Int>()
    private var currentViewedPlayer: String? = null
    private var hasGuessed = false
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

    fun setSelectedClue(cluePlayer: String, clue: Int) {
        if (!hasGuessed && currentViewedPlayer != playerName) {
            if (selectedClues[cluePlayer] == clue) selectedClues.remove(cluePlayer)
            else selectedClues[cluePlayer] = clue

            isAbleToGuess = !hasGuessed && currentViewedPlayer != playerName && selectedClues.containsKey(cluePlayer)
                    && selectedMeans.containsKey(cluePlayer) && currentViewedPlayer.equals(
                cluePlayer
            )
        }
    }

    fun getSelectedMeans(playerName: String): Int {
        return selectedMeans.getOrElse(playerName) { -1 }
    }

    fun setSelectedMeans(meansPlayer: String, means: Int) {
        if (!hasGuessed && currentViewedPlayer != playerName) {
            if (selectedMeans[meansPlayer] == means) selectedMeans.remove(meansPlayer)
            else selectedMeans[meansPlayer] = means

            isAbleToGuess = !hasGuessed && currentViewedPlayer != playerName && selectedClues.containsKey(meansPlayer)
                    && selectedMeans.containsKey(meansPlayer) && currentViewedPlayer.equals(
                meansPlayer
            )
        }
    }

    fun getCurrentViewedPlayer(): String? {
        return currentViewedPlayer
    }

    fun setCurrentViewedPlayer(currentPlayer: String) {
        currentViewedPlayer = currentPlayer
        isAbleToGuess = !hasGuessed && currentViewedPlayer != playerName && selectedClues.containsKey(playerName)
                && selectedMeans.containsKey(playerName) && currentViewedPlayer.equals(playerName)
    }

    fun getIsAbleToGuess(): Boolean {
        return isAbleToGuess
    }

    override val gameInstance: LiveGameInstanceSnapshot =
        LiveGameInstanceSnapshot(gameId)

    fun makeGuess() {
        val clueCard = gameInstance.value!!.players.find { it.playerName == currentViewedPlayer }!!
            .clueCards.get(selectedClues[currentViewedPlayer!!]!!).name

        selectedClues.clear()

        val meansCard = gameInstance.value!!.players.find { it.playerName == currentViewedPlayer }!!
            .meansCards.get(selectedMeans[currentViewedPlayer!!]!!).name

        selectedMeans.clear()

        val g = GuessSnapshot(
            guesserPlayer = playerName!!,
            guessedPlayer = currentViewedPlayer!!,
            clueCard = clueCard,
            meansCard = meansCard
        )

        sendGuessMessage(gameId, g)
        processGuess(
            guess = g,
            murdererName = gameInstance.value!!.murdererName!!,
            murdererClueCard = gameInstance.value!!.murdererClueCard.name,
            murdererMeansCard = gameInstance.value!!.murdererMeansCard.name,
            players = gameInstance.value!!.players,
            gameId = gameId
        )


        isAbleToGuess = false
        hasGuessed = true
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