package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.kindeep.treachery.R

/**
 * Displays every other player, their cards and a messages box for discussion.
 */
class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}
