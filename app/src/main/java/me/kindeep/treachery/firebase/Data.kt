package me.kindeep.treachery.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import me.kindeep.treachery.firebase.models.CardsResourcesSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot


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
    return firestore.collection("games")
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
            Log.e("FIREBASE", "DocumentSnapshot written with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.e("FIREBASE", "Error adding document", e)
        }.addOnCanceledListener {
            Log.e("FIREBASE", "Cancelled?")
        }.addOnCompleteListener {
            Log.e("FIREBASE", "Completed")
        }
}

fun addOnGameUpdateListener(gameId: String, onChange: (GameInstanceSnapshot) -> Unit) {
    getGameReference(gameId)
        .addSnapshotListener { documentSnapshot, _ ->
            Log.e("FIREBASE", "Snapshot changed")
            val newSnapshot: GameInstanceSnapshot = documentSnapshot!!.toObject()!!
            onChange(newSnapshot)
        }
}