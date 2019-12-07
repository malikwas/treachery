package me.kindeep.treachery.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects


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

fun createGame(callback: (documentReference: DocumentReference) -> Unit) {
    FirebaseFirestore.getInstance().collection("games").add(GameInstanceSnapshot())
        .addOnSuccessListener { documentReference ->
            documentReference.update("gameId", documentReference.id)
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