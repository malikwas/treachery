package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Displays every other player, their cards and a messages box for discussion.
 */
class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}
