package me.kindeep.treachery.player

import android.util.Log
import androidx.lifecycle.ViewModel
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.GuessSnapshot
import me.kindeep.treachery.firebase.models.LiveGameInstanceSnapshot
import me.kindeep.treachery.firebase.models.PlayerSnapshot
import me.kindeep.treachery.sendGuess

class PlayerViewModel : ViewModel() {
    companion object {
        var gameId: String = ""
        var playerName: String? = null
    }

    val gameInstance: LiveGameInstanceSnapshot =
        LiveGameInstanceSnapshot(gameId)

    fun makeGuess(guessedPlayer: String, clueCard: String, meansCard: String) {
        sendGuess(GuessSnapshot(
            guesserPlayer = playerName!!,
            guessedPlayer = guessedPlayer,
            clueCard = clueCard,
            meansCard = meansCard
        ), gameId)
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