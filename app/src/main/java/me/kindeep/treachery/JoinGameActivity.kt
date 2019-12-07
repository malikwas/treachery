package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Click a game from MainActivity, from this activity select a player name and Proceed to PlayerActivity
 *
 */
class JoinGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)
    }
}
