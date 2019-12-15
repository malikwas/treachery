package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
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

    //    lateinit var testFrag: SinglePlayerFragment
    lateinit var playerPager: ViewPager2

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

        playerPager = findViewById(R.id.player_pager)
        doAdapterStuff()

        viewModel.gameInstance.observe(this, Observer {
            log(it)
            findViewById<TextView>(R.id.gameId).text = it.gameId
//            updateViews()
//            testFrag.player = viewModel.getPlayer()
//            doAdapterStuff()
            playerPager.offscreenPageLimit = viewModel.gameInstance.value!!.players.size
//            doAdapterStuff()
//            playerPager.adapter!!.notifyDataSetChanged()
        })


    }

    fun doAdapterStuff() {
        playerPager.adapter =
            PlayersPagerAdapter(this, viewModel, this@PlayerActivity)

    }

    fun log(umm: Any) {
        Log.e("PLAYER_ACTIVITY", umm.toString())
    }


}