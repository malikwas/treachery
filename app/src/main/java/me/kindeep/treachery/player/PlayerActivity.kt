package me.kindeep.treachery.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.joined_players_list_item.*
import me.kindeep.treachery.R
import me.kindeep.treachery.chat.ChatFragment
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.onPlayerMurdererDetermined
import me.kindeep.treachery.shared.SingleForensicCardFragment

/**
 * Displays every other player, their cards and a messages box for discussion.
 */
class PlayerActivity : AppCompatActivity() {
    lateinit var gameId: String
    lateinit var playerName: String

    lateinit var viewModel: PlayerViewModel

    //    lateinit var testFrag: SinglePlayerFragment
    lateinit var playerPager: ViewPager2

    lateinit var frag1: SingleForensicCardFragment
    lateinit var frag2: SingleForensicCardFragment
    lateinit var frag3: SingleForensicCardFragment
    lateinit var frag4: SingleForensicCardFragment
    lateinit var frag5: SingleForensicCardFragment
    lateinit var frag6: SingleForensicCardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerName = intent?.extras?.getString("playerName")!!
        gameId = intent?.extras?.getString("gameId")!!
        findViewById<TextView>(R.id.gameId).text = gameId
        findViewById<TextView>(R.id.player_name).text = playerName

        var chat = supportFragmentManager.findFragmentById(R.id.chat_fragment) as ChatFragment
        chat.gameId = gameId
        chat.playerName = playerName

        frag1 = supportFragmentManager.findFragmentByTag("card1") as SingleForensicCardFragment
        frag2 = supportFragmentManager.findFragmentByTag("card2") as SingleForensicCardFragment
        frag3 = supportFragmentManager.findFragmentByTag("card3") as SingleForensicCardFragment
        frag4 = supportFragmentManager.findFragmentByTag("card4") as SingleForensicCardFragment
        frag5 = supportFragmentManager.findFragmentByTag("card5") as SingleForensicCardFragment
        frag6 = supportFragmentManager.findFragmentByTag("card6") as SingleForensicCardFragment

        addOnGameUpdateListener(gameId) {
            frag1.forensicCardSnapshot = it.otherCards.getOrElse(0) { ForensicCardSnapshot() }
            frag1.bind()
            frag4.forensicCardSnapshot = it.otherCards.getOrElse(1) { ForensicCardSnapshot() }
            frag4.bind()
            frag5.forensicCardSnapshot = it.otherCards.getOrElse(2) { ForensicCardSnapshot() }
            frag5.bind()
            frag6.forensicCardSnapshot = it.otherCards.getOrElse(3) { ForensicCardSnapshot() }
            frag6.bind()

            frag2.forensicCardSnapshot = it.causeCard
            frag2.bind()
            frag3.forensicCardSnapshot = it.locationCard
            frag3.bind()
        }

        PlayerViewModel.gameId = gameId
        PlayerViewModel.playerName = playerName

        viewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)
//        setContentView(R.layout.activity_player)

        playerPager = findViewById(R.id.player_pager)
        doAdapterStuff()

        viewModel.gameInstance.observe(this, Observer {
            log(it)
            findViewById<TextView>(R.id.gameId).text = it.gameId
            playerPager.offscreenPageLimit = viewModel.gameInstance.value!!.players.size
        })

        onPlayerMurdererDetermined(gameId, playerName) {
            Toast.makeText(this, "I am the murderer", Toast.LENGTH_LONG).show()
        }

    }

    fun doAdapterStuff() {
        playerPager.adapter =
            PlayersPagerAdapter(this, viewModel, this@PlayerActivity)

    }

    fun log(umm: Any) {
        Log.e("PLAYER_ACTIVITY", umm.toString())
    }


}