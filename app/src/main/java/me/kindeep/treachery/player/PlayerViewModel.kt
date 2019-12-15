package me.kindeep.treachery.player

import android.util.Log
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.LiveGameInstanceSnapshot
import me.kindeep.treachery.firebase.models.PlayerSnapshot

class PlayerViewModel : ViewModel() {
    companion object {
        var gameId: String = ""
        var playerName: String? = null
    }

    private val selectedMeans = HashMap<String, Int>()
    private val selectedClues = HashMap<String, Int>()

    fun getSelectedClue(playerName: String): Int {
        return selectedClues.getOrElse(playerName) { -1 }
    }

    fun setSelectedClue(playerName: String, clue: Int) {
        selectedClues[playerName] = if (clue in 0..3) clue else -1
    }

    fun getSelectedMeans(playerName: String): Int {
        return selectedMeans.getOrElse(playerName) { -1 }
    }

    fun setSelectedMeans(playerName: String, clue: Int) {
        selectedMeans[playerName] = if (clue in 0..3) clue else -1
    }

    val gameInstance: LiveGameInstanceSnapshot =
        LiveGameInstanceSnapshot(gameId)

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