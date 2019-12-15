package me.kindeep.treachery.firebase.models

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import me.kindeep.treachery.firebase.getGameReference


data class GameInstanceSnapshot(
    val gameId: String = "Loading...",
    val snapshotVersion: String = "0.1",
    val messages: List<MessageSnapshot> = mutableListOf(),
    val guesses: List<GuessSnapshot> = listOf(),
    val players: MutableList<PlayerSnapshot> = mutableListOf(
        PlayerSnapshot("Mr. Player"),
        PlayerSnapshot("Ms. Bot"),
        PlayerSnapshot("Mr. Robot"),
        PlayerSnapshot("Quality Analyst")
    ),
    val started: Boolean = false,
    val createdTimestamp: Timestamp = Timestamp.now(),
    val startedTimestamp: Timestamp = Timestamp.now(),
    val murdererSelected: Boolean = false,
    var murdererName: String? = null,
    var murdererMeansCard: String? = null,
    var murdererClueCard: String? = null,
    var murdererCardsDetermined: Boolean = false,
    val causeCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val causeCardDefined: Boolean = false,
    val locationCard: ForensicCardSnapshot = ForensicCardSnapshot(),
    val locationCardDefined: Boolean = false,
    val otherCards: MutableList<ForensicCardSnapshot> = mutableListOf(
        ForensicCardSnapshot(),
        ForensicCardSnapshot(),
        ForensicCardSnapshot(),
        ForensicCardSnapshot()
    )
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
        Log.e("FIREBASE", " LiveGameInstanceSnapshot initialized with $gameId")
        value =
            GameInstanceSnapshot(gameId = gameId)
        gameReference = getGameReference(gameId)
        // Listen for changes
        getGameReference(gameId)
            .addSnapshotListener { documentSnapshot, _ ->
                val newSnapshot: GameInstanceSnapshot = documentSnapshot!!.toObject()!!
                value = newSnapshot
            }
    }

    init {
        initialize()
    }

}