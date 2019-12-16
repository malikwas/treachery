package me.kindeep.treachery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.firebase.activeGamesQuery
import me.kindeep.treachery.firebase.createGame
import me.kindeep.treachery.forensic.StartGameActivity
import me.kindeep.treachery.player.JoinGameActivity
import me.kindeep.treachery.player.PlayerActivity


class MainActivity : AppCompatActivity() {

    lateinit var gamesRecycler: RecyclerView
    lateinit var button: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        gamesRecycler = findViewById(R.id.gamesRecycler)
        val options: FirestoreRecyclerOptions<GameInstanceSnapshot> = FirestoreRecyclerOptions
            .Builder<GameInstanceSnapshot>()
            .setQuery(activeGamesQuery(), GameInstanceSnapshot::class.java)
            .setLifecycleOwner(this)
            .build()
        button = findViewById(R.id.new_game)

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
                        holder.clickView.setOnClickListener {
                            startPlayerActivity(model.gameId)
                        }
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

//        button.isEnabled = true

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        button.isEnabled = true
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun newGame(v: View) {
//        button.isEnabled = false
        createGame {
            val intent = Intent(this, StartGameActivity::class.java)
            val b = Bundle()
            b.putString("gameId", it.id)
            intent.putExtras(b)
            startActivity(intent)
        }
    }

    fun startPlayerActivity(gameId: String) {
        val intent = Intent(this, JoinGameActivity::class.java)
        val b = Bundle()
        b.putString("gameId", gameId)
        intent.putExtras(b)
        startActivity(intent)
    }
}


class GameTileHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.games_list_item, parent, false)) {

    var idTextView: TextView = itemView.findViewById(R.id.idTextView)
    var startTimeTextView: TextView = itemView.findViewById(R.id.created_time)
    var numJoinedPlayersTextView: TextView = itemView.findViewById(R.id.num_joined_players)
    var clickView: View = itemView.findViewById(R.id.click_view)

    fun bind(gameInstanceSnapshot: GameInstanceSnapshot) {
        idTextView.text = gameInstanceSnapshot.gameId
        startTimeTextView.text = gameInstanceSnapshot.createdTimestamp.toDate().toString()
        numJoinedPlayersTextView.text = gameInstanceSnapshot.players.size.toString()

    }
}
