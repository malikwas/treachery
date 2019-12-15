package me.kindeep.treachery.player

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import me.kindeep.treachery.R
import kotlin.math.max


/**
 * For murderer to select his card. Displays every player cards too.
 */
class MurdererSelectActivity : AppCompatActivity() {
    lateinit var gameId: String
    lateinit var playerName: String
    lateinit var viewModel: PlayerViewModel
    lateinit var playerPager: ViewPager2
    lateinit var currentPlayerFragment: SinglePlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_murderer_select)
        playerName = intent?.extras?.getString("playerName")!!
        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId
        findViewById<TextView>(R.id.player_name).text = playerName

        PlayerViewModel.gameId = gameId
        PlayerViewModel.playerName = playerName

        viewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)

        playerPager = findViewById(R.id.player_pager)
        doAdapterStuff()

//        currentPlayerFragment = supportFragmentManager.findFragmentById(R.id.mur) as SinglePlayerFragment
        val fragment = SinglePlayerFragment()
        val bundle = Bundle()
        bundle.putString("playerName", playerName)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.murderer_fragment_container, fragment, "murdererFragment")
            commit()
        }




        viewModel.gameInstance.observe(this, Observer {
            log(it)
            findViewById<TextView>(R.id.gameId).text = it.gameId
            playerPager.offscreenPageLimit = max(viewModel.gameInstance.value!!.players.size, 1)
        })

    }

    fun doAdapterStuff() {
        playerPager.adapter =
            PlayersPagerAdapter(this, viewModel, this@MurdererSelectActivity)

    }

    fun log(umm: Any) {
        Log.e("PLAYER_ACTIVITY", umm.toString())
    }


}