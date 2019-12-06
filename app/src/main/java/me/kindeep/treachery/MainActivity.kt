package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import me.kindeep.treachery.firebase.GameInstanceSnapshot

class MainActivity : AppCompatActivity() {

    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Document snapshot to POJO

        val snapshot: DocumentSnapshot = ...
        val myObject = snapshot.get<MyClass>("fieldPath")
         */
        Log.e("The", "fuck")

    }

    fun fire(v: View) {
        firestore.collection("games").add(GameInstanceSnapshot())
            .addOnSuccessListener { documentReference ->
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
}
