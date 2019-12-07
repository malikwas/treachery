package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.firestore.ktx.toObject
import me.kindeep.treachery.firebase.GameInstanceSnapshot
import me.kindeep.treachery.firebase.PlayerSnapshot
import me.kindeep.treachery.firebase.gameReference

class StartGameActivity : AppCompatActivity() {

    lateinit var gameId: String
    lateinit var joinedPlayersRecycler: RecyclerView
    var gameInstance: GameInstanceSnapshot = GameInstanceSnapshot()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        joinedPlayersRecycler = findViewById(me.kindeep.treachery.R.id.joined_players)

        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId

        joinedPlayersRecycler.apply {
            adapter = object : RecyclerView.Adapter<JoinedPlayerHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): JoinedPlayerHolder {
                    return JoinedPlayerHolder(layoutInflater, parent)
                }

                override fun getItemCount(): Int {
                    return gameInstance.players.size
                }

                override fun onBindViewHolder(holder: JoinedPlayerHolder, position: Int) {
                    holder.bind(gameInstance.players[position])
                }
            }

            layoutManager = LinearLayoutManager(this@StartGameActivity)
        }

        gameReference(gameId).addSnapshotListener { documentSnapshot, _ ->
            Log.e("FIREBASE", "Kinda worked eh")
            gameInstance = documentSnapshot!!.toObject()!!
            joinedPlayersRecycler.adapter?.notifyDataSetChanged()
        }
    }


}

class JoinedPlayerHolder(layoutInflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.joined_players_list_item, parent, false)) {
    val playerNameTextView = itemView.findViewById<TextView>(R.id.playerName)

    fun bind(playerSnapshot: PlayerSnapshot) {
        playerNameTextView.text = playerSnapshot.playerName
    }
}
