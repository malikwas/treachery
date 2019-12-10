package me.kindeep.treachery.firebase.models

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import me.kindeep.treachery.firebase.getGameReference


data class GameInstanceSnapshot(
    val gameId: String = "Undefined",
    val snapshotVersion: String = "0.1",
    val messages: List<MessageSnapshot> = listOf(
        MessageSnapshot()
    ),
    val players: List<PlayerSnapshot> = listOf(
        PlayerSnapshot()
    ),
    val started: Boolean = false,
    val createdTimestamp: Timestamp = Timestamp.now(),
    val startedTimestamp: Timestamp = Timestamp.now(),
    val murdererSelected: Boolean = false,
    val causeCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val causeCardDefined: Boolean = false,
    val locationCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val locationCardDefined: Boolean = false,
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
        value =
            GameInstanceSnapshot(gameId = gameId)
        gameReference = getGameReference(gameId)
        // Listen for changes
        getGameReference(gameId)
            .addSnapshotListener { documentSnapshot, _ ->
                val newSnapshot: GameInstanceSnapshot = documentSnapshot!!.toObject()!!
                Log.e("FIREBASE", "Snapshot changed to $documentSnapshot " +
                        "\nparsed as \n $newSnapshot")
                value = newSnapshot
            }
    }

    init {
        initialize()
    }

}