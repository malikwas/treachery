package me.kindeep.treachery.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
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
    return firestore.collection("games").orderBy("createdTimestamp", Query.Direction.DESCENDING)
}

fun getGameReference(gameId: String): DocumentReference {
    val firestore = FirebaseFirestore.getInstance()
    return firestore.collection("games").document(gameId)
}

fun getCardsResourcesSnapshot(onSuccess: (CardsResourcesSnapshot) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val documentReference = firestore.collection("resources").document("cards")
    documentReference.get().addOnSuccessListener {
        onSuccess(it.toObject<CardsResourcesSnapshot>()!!)
    }
}


fun createGame(callback: (documentReference: DocumentReference) -> Unit) {
    FirebaseFirestore.getInstance().collection("games").add(GameInstanceSnapshot())
        .addOnSuccessListener { documentReference ->
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

fun getGame(gameId: String, onSuccess: (GameInstanceSnapshot) -> Unit) {
    getGameReference(gameId).get().addOnSuccessListener {
        onSuccess(it.toObject<GameInstanceSnapshot>()!!)
    }
}