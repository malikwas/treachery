package me.kindeep.treachery.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.joined_players_list_item.*
import me.kindeep.treachery.PlayerAddFailureType
import me.kindeep.treachery.R
import me.kindeep.treachery.addPlayer
import me.kindeep.treachery.firebase.models.PlayerSnapshot
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
        val joinButton = v.findViewById<MaterialButton>(R.id.join_game)
        joinButton.isEnabled = false

        val playerSnapshot: PlayerSnapshot =
            PlayerSnapshot(playerName = playerNameEditText.text.toString())
        addPlayer(gameId, playerSnapshot, {
            val intent = Intent(this, PlayerActivity::class.java)
            val b = Bundle()
            b.putString("gameId", gameId)
            b.putString("playerName", playerSnapshot.playerName)
            intent.putExtras(b)
            startActivity(intent)
            finish()
        }, {
            when (it) {
                PlayerAddFailureType.DUPLICATE_NAME -> {
                    joinButton.isEnabled = true
                    Toast.makeText(
                        this@JoinGameActivity,
                        "Player with name ${playerSnapshot.playerName} already exists! Please choose a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                PlayerAddFailureType.WTF -> {
                    joinButton.isEnabled = true
                    Toast.makeText(
                        this@JoinGameActivity,
                        "Something went terribly wrong, please retry!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
