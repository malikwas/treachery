package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import me.kindeep.treachery.R

/**
 * Displays every other player, their cards and a messages box for discussion.
 */
class PlayerActivity : AppCompatActivity() {
    lateinit var gameId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId

    }
}
