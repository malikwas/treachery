package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

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

    }
}
