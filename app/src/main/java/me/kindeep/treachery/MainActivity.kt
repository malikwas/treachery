package me.kindeep.treachery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import me.kindeep.treachery.firebase.GameInstanceSnapshot
import me.kindeep.treachery.firebase.activeGamesQuery
import me.kindeep.treachery.firebase.createGame


class MainActivity : AppCompatActivity() {

    val firestore = FirebaseFirestore.getInstance()
    lateinit var gamesRecycler: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gamesRecycler = findViewById(R.id.gamesRecycler)
        val options: FirestoreRecyclerOptions<GameInstanceSnapshot> = FirestoreRecyclerOptions
            .Builder<GameInstanceSnapshot>()
            .setQuery(activeGamesQuery(), GameInstanceSnapshot::class.java)
            .setLifecycleOwner(this)
            .build()


        gamesRecycler.apply {
            // adapter
            adapter =
                object : FirestoreRecyclerAdapter<GameInstanceSnapshot, GameTileHolder>(options) {


                    override fun onBindViewHolder(
                        holder: GameTileHolder,
                        position: Int,
                        model: GameInstanceSnapshot
                    ) {
                        holder.bind(model)
                    }

                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): GameTileHolder {
                        return GameTileHolder(layoutInflater, parent)
                    }
                }
            // Layout Manager
            layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    fun fire(v: View) {
        createGame {
            // Open new activity
        }
    }

    fun newGame(v: View) {
        createGame {
            // Open Forensic Activity
            Log.e("ACTIVITY", "Open the goddamn activity")
            val intent = Intent(this, StartGameActivity::class.java)
             val b: Bundle = Bundle()
            b.putString("gameId", it.id)
            intent.putExtras(b)
            startActivity(intent)
            finish()
        }
    }
}


class GameTileHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.games_list_item, parent, false)) {

    var idTextView: TextView = itemView.findViewById(R.id.idTextView)

    fun bind(gameInstanceSnapshot: GameInstanceSnapshot) {
        Log.e("FIRE", gameInstanceSnapshot.createdTimestamp.toString())
        idTextView.text = gameInstanceSnapshot.gameId
    }
}
