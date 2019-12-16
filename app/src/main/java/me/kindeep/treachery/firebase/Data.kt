package me.kindeep.treachery.firebase

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.firebase.models.GuessSnapshot
import me.kindeep.treachery.firebase.models.MessageSnapshot
import kotlin.math.min


fun getActiveGames(callback: (List<GameInstanceSnapshot>) -> Unit) {
    activeGamesQuery().get()
        .addOnSuccessListener {
            val activeGames: List<GameInstanceSnapshot> = it.toObjects()
            callback(activeGames)
        }
        .addOnFailureListener {

        }
}

fun activeGamesQuery(): Query {
    val firestore = FirebaseFirestore.getInstance()
    return firestore.collection("games").whereEqualTo("started", false)
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
}

fun getGameReference(gameId: String): DocumentReference {
    var gameIdIn = gameId
    if(gameIdIn.isNullOrEmpty()) {
        gameIdIn = "default"
    }
    val firestore = FirebaseFirestore.getInstance()
    return firestore.collection("games").document(gameIdIn)

}

fun getCardsResourcesSnapshot(onSuccess: (CardsResourcesSnapshot) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val documentReference = firestore.collection("resources").document("cards")
    documentReference.get().addOnSuccessListener {
        onSuccess(it.toObject<CardsResourcesSnapshot>()!!)
    }
}


fun createGame(callback: (documentReference: DocumentReference) -> Unit) {
    val id = customRandomString()
    FirebaseFirestore.getInstance().collection("games").document(id).set(GameInstanceSnapshot())
        .addOnSuccessListener {
            val documentReference = getGameReference(id)
            documentReference.update("gameId", documentReference.id)
            callback(documentReference)
            Log.i("FIREBASE", "DocumentSnapshot written with ID: ${documentReference.id}")

            // Get random other cards
            getCardsResourcesSnapshot {
                documentReference.update(
                    "otherCards",
                    it.forensicCards.otherCards.shuffled().subList(
                        0, min(it.totalOtherCards, it.forensicCards.otherCards.size)
                    )
                )
            }
        }
        .addOnFailureListener { e ->
            Log.e("FIREBASE", "Error adding document", e)
        }.addOnCanceledListener {
            Log.i("FIREBASE", "Cancelled?")
        }.addOnCompleteListener {
            Log.i("FIREBASE", "Completed")
        }
}

fun addOnGameUpdateListener(gameId: String, onChange: (GameInstanceSnapshot) -> Unit) {
    getGameReference(gameId)
        .addSnapshotListener { documentSnapshot, _ ->
            Log.i("FIREBASE", "Snapshot changed")
            val newSnapshot: GameInstanceSnapshot = documentSnapshot!!.toObject()!!
            onChange(newSnapshot)
        }
}

fun getGame(
    gameId: String,
    onSuccess: (GameInstanceSnapshot) -> Unit
): Task<DocumentSnapshot> {
    return getGameReference(gameId)
        .get().addOnSuccessListener {
            onSuccess(it.toObject<GameInstanceSnapshot>()!!)
        }
}


fun customRandomString(): String {
    val allowedChars = ('a'..'z') + ('1'..'9')
    var string = ""
    for (i in 1..10) {
        string += allowedChars.random()
    }
    return string
}