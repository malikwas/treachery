package me.kindeep.treachery.forensic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.kindeep.treachery.*
import me.kindeep.treachery.chat.ChatFragment
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot

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

        var chat = supportFragmentManager.findFragmentById(R.id.chat_fragment) as ChatFragment
        chat.gameId = gameId
        chat.playerName = FORENSIC_NAME
        chat.removeTextBox()
    }

    override fun onDestroy() {
        super.onDestroy()
        fireStartGame(gameId)
    }



    fun startGame(v: View) {
        val startButton = v.findViewById<MaterialButton>(R.id.start_game)
        startButton.isEnabled = false

        // Open Forensic Activity
        fireStartGame(gameId, {
            Log.i("START_GAME_ACTIVITY", "Styaart the game")
            val intent = Intent(this, ForensicActivity::class.java)
            val b = Bundle()
            b.putString("gameId", gameId)
            intent.putExtras(b)
            startActivity(intent)
            finish()
        }, {
            when (it) {
                StartFailureType.NOT_ENOUGH_PLAYERS -> {
                    startButton.isEnabled = true
                    Toast.makeText(
                        this@StartGameActivity,
                        "Not Enough Players! Need at least $MIN_PLAYERS_SIZE to start.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                StartFailureType.WTF -> {
                    startButton.isEnabled = true
                    Toast.makeText(
                        this@StartGameActivity,
                        "Something went terribly wrong, please retry!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}

