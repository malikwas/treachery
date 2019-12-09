package me.kindeep.treachery.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject


data class GameInstanceSnapshot(
    val gameId: String = "Undefined",
    val snapshotVersion: String = "0.1",
    val messages: List<MessageSnapshot> = listOf(MessageSnapshot()),
    val players: List<PlayerSnapshot> = listOf(PlayerSnapshot()),
    val isStarted: Boolean = false,
    val createdTimestamp: Timestamp = Timestamp.now(),
    val startedTimestamp: Timestamp = Timestamp.now(),
    val isMurdererSelected: Boolean = false,
    val causeCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val isCauseCardDefined: Boolean = false,
    val locationCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val isLocationCardDefined: Boolean = false,
    val otherCards: List<ForensicCardSnapshot> = listOf()
)

class LiveGameInstanceSnapshot(gameId: String) : LiveData<GameInstanceSnapshot>() {
    private lateinit var gameReference: DocumentReference

    var gameId = gameId
        set(value) {
            field = value
            initialize()
        }

    private fun initialize() {
        // Initialize a default value
        value = GameInstanceSnapshot(gameId = gameId)
        gameReference = gameReference(gameId)
        // Listen for changes
        gameReference(gameId).addSnapshotListener { documentSnapshot, _ ->
            Log.e("FIREBASE", "Kinda worked eh")
            val newSnapshot: GameInstanceSnapshot = documentSnapshot!!.toObject()!!
            value = newSnapshot
        }
    }

    init {
        initialize()
    }

}