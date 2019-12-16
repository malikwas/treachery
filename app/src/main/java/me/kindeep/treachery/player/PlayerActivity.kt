package me.kindeep.treachery.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.joined_players_list_item.*
import me.kindeep.treachery.R
import me.kindeep.treachery.chat.ChatFragment
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.getGameFinishIntent
import me.kindeep.treachery.onGameTimerExpire
import me.kindeep.treachery.onPlayerMurdererDetermined
import me.kindeep.treachery.shared.SingleForensicCardFragment
import kotlin.math.max
import kotlin.math.min

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

    fun handleOnGuessClick(v: View) {
        viewModel.makeGuess()
    }

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

        val guessButton = findViewById<MaterialButton>(R.id.guess_button)
        guessButton.isEnabled = viewModel.getIsAbleToGuess()
        viewModel.getEnableChange().changeListeners.add {
            val b = findViewById<MaterialButton>(R.id.guess_button)
            b.isEnabled = !b.isEnabled
        }

        playerPager = findViewById(R.id.player_pager)
        playerPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setCurrentViewedPlayer(
                    viewModel.gameInstance.value!!.players.get(position).playerName
                )
            }
        })
        doAdapterStuff()

        viewModel.gameInstance.observe(this, Observer {
            log(it)
            findViewById<TextView>(R.id.gameId).text = it.gameId
            playerPager.offscreenPageLimit = max(viewModel.gameInstance.value!!.players.size, 1)
        })

        onPlayerMurdererDetermined(gameId, playerName) {
            Toast.makeText(this, "I am the murderer", Toast.LENGTH_LONG).show()
            startMurdererActivity()
        }


        onGameTimerExpire(gameId) {
            startActivity(getGameFinishIntent(this))
        }
    }

    fun doAdapterStuff() {
        playerPager.adapter =
            PlayersPagerAdapter(this, viewModel, this@PlayerActivity)

    }


    fun startMurdererActivity() {
        val intent = Intent(this, MurdererSelectActivity::class.java)
        val b = Bundle()
        b.putString("gameId", gameId)
        b.putString("playerName", playerName)
        intent.putExtras(b)
        startActivity(intent)
    }

    fun log(umm: Any) {
        Log.i("PLAYER_ACTIVITY", umm.toString())
    }
}