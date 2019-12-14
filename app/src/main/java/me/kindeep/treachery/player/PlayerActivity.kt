package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.forensic.ForensicViewModel
import me.kindeep.treachery.shared.CardFragment

/**
 * Displays every other player, their cards and a messages box for discussion.
 */
class PlayerActivity : AppCompatActivity() {
    lateinit var gameId: String

    lateinit var viewModel: PlayerViewModel

    lateinit var testFrag: SinglePlayerFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        gameId = intent?.extras?.getString("gameId")!!
//        findViewById<TextView>(R.id.gameId).text = gameId

        val playerName = intent?.extras?.getString("playerName")!!

        PlayerViewModel.gameId = gameId
        PlayerViewModel.playerName = playerName

        viewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        testFrag =
            supportFragmentManager.findFragmentById(R.id.test_player_fragment) as SinglePlayerFragment
        testFrag.player = viewModel.getPlayer()
        updateViews()

        viewModel.gameInstance.observe(this, Observer {
            log(it)
            findViewById<TextView>(R.id.gameId).text = it.gameId
            updateViews()
            testFrag.player = viewModel.getPlayer()
        })


    }

    fun updateViews() {
        log("update")
        testFrag.bind()
    }

    fun log(umm: Any) {
        Log.e("PLAYER_ACTIVITY", umm.toString())
    }


}