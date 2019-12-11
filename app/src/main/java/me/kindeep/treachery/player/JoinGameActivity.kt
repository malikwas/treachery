package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import me.kindeep.treachery.R
import me.kindeep.treachery.shared.JoinedPlayersFragment

/**
 * Click a game from MainActivity, from this activity select a player name and Proceed to PlayerActivity
 *
 */
class JoinGameActivity : AppCompatActivity() {
    lateinit var gameId: String
    lateinit var playerNameEditText: EditText
    lateinit var joinedPlayersFragment: JoinedPlayersFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)
        joinedPlayersFragment =
            supportFragmentManager.findFragmentById(R.id.joined_players_fragment) as JoinedPlayersFragment
        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId

        joinedPlayersFragment.gameId = gameId


        playerNameEditText = findViewById(R.id.player_name_edit_text)


    }

    fun joinGame(v: View) {
        Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
    }
}
