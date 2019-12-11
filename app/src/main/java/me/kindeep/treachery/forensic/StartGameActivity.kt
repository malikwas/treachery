package me.kindeep.treachery.forensic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_forensic.*
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.shared.JoinedPlayerHolder
import me.kindeep.treachery.shared.JoinedPlayersFragment

class StartGameActivity : AppCompatActivity() {

    lateinit var gameId: String
    lateinit var joinedPlayersRecycler: RecyclerView
    lateinit var joinedPlayersFragment: JoinedPlayersFragment
    var gameInstance: GameInstanceSnapshot =
        GameInstanceSnapshot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        joinedPlayersFragment =
            supportFragmentManager.findFragmentById(R.id.joined_players_fragment) as JoinedPlayersFragment

        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId

        joinedPlayersFragment.gameId = gameId

//        joinedPlayersRecycler = findViewById(R.id.joined_players)
//

//
//        joinedPlayersRecycler.apply {
//            adapter = object : RecyclerView.Adapter<JoinedPlayerHolder>() {
//                override fun onCreateViewHolder(
//                    parent: ViewGroup,
//                    viewType: Int
//                ): JoinedPlayerHolder {
//                    return JoinedPlayerHolder(
//                        layoutInflater,
//                        parent
//                    )
//                }
//
//                override fun getItemCount(): Int {
//                    return gameInstance.players.size
//                }
//
//                override fun onBindViewHolder(holder: JoinedPlayerHolder, position: Int) {
//                    holder.bind(gameInstance.players[position])
//                }
//            }
//
//            layoutManager = LinearLayoutManager(this@StartGameActivity)
//        }
//
//        getGameReference(gameId).addSnapshotListener { documentSnapshot, _ ->
//            Log.e("FIREBASE", "Kinda worked eh")
//            gameInstance = documentSnapshot!!.toObject()!!
//            joinedPlayersRecycler.adapter?.notifyDataSetChanged()
//        }
    }

    fun startGame(v: View) {
        // Open Forensic Activity
        Log.e("ACTIVITY", "Styaart the game")
        val intent = Intent(this, ForensicActivity::class.java)
        val b = Bundle()
        b.putString("gameId", gameId)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

}

