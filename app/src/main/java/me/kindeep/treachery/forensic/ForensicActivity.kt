package me.kindeep.treachery.forensic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import me.kindeep.treachery.*
import me.kindeep.treachery.chat.ChatFragment
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.player.PlayerViewModel
import me.kindeep.treachery.player.PlayersPagerAdapter
import me.kindeep.treachery.player.SinglePlayerFragment
import me.kindeep.treachery.shared.CardFragment
import me.kindeep.treachery.shared.SingleForensicCardFragment
import kotlin.math.max

class ForensicActivity : AppCompatActivity() {

    lateinit var gameId: String
    lateinit var viewModel: ForensicViewModel
    lateinit var frag1: SingleForensicCardFragment
    lateinit var frag2: SingleForensicCardFragment
    lateinit var frag3: SingleForensicCardFragment
    lateinit var frag4: SingleForensicCardFragment
    lateinit var frag5: SingleForensicCardFragment
    lateinit var frag6: SingleForensicCardFragment
    lateinit var murdererNameText: TextView
    lateinit var playerPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetGameFinisher()
        setContentView(R.layout.activity_forensic)
        gameId = intent?.extras?.getString("gameId")!!
        murdererNameText = findViewById(R.id.murderer_name)
        frag1 = supportFragmentManager.findFragmentById(R.id.card1) as SingleForensicCardFragment
        frag2 = supportFragmentManager.findFragmentById(R.id.card2) as SingleForensicCardFragment
        frag3 = supportFragmentManager.findFragmentById(R.id.card3) as SingleForensicCardFragment
        frag4 = supportFragmentManager.findFragmentById(R.id.card4) as SingleForensicCardFragment
        frag5 = supportFragmentManager.findFragmentById(R.id.card5) as SingleForensicCardFragment
        frag6 = supportFragmentManager.findFragmentById(R.id.card6) as SingleForensicCardFragment

        var chat = supportFragmentManager.findFragmentById(R.id.chat_fragment) as ChatFragment
        chat.gameId = gameId
        chat.playerName = FORENSIC_NAME
        chat.removeTextBox()

        ForensicViewModel.gameId = gameId
        viewModel = ViewModelProviders.of(this)
            .get(ForensicViewModel::class.java)


        PlayerViewModel.gameId = gameId

        val playerViewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)

        playerPager = findViewById(R.id.player_pager)
        val adapter = PlayersPagerAdapter(this, playerViewModel, this)
        playerPager.adapter = adapter

        val clueFragment = CardFragment()
        val meansFragment = CardFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.means_card, meansFragment, "meansCardForensic")
            replace(R.id.clue_card, clueFragment, "clueCardForensic")
            commit()
        }


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
            clueFragment.bind(it.murdererClueCard)
            meansFragment.bind(it.murdererMeansCard)
            murdererNameText.text = it.murdererName
        }


        onGameFinish(gameId) {
            startActivity(getGameFinishIntent(this, gameId))
            finish()
            finishAndRemoveTask()
        }

        // All players view pager

        viewModel.gameInstance.observe(this, Observer {
            findViewById<TextView>(R.id.gameId).text = it.gameId
            playerPager.offscreenPageLimit = max(viewModel.gameInstance.value!!.players.size, 1)
            playerPager.adapter?.notifyDataSetChanged()
        })

        Handler().postDelayed({
            triggerArtificialUpdate(gameId)
        }, 100)
    }

    fun remindTime(v: View) {
        sendForensicMessage(gameId, "Time remaining: ${viewModel.getRemainingTime()}")
    }

    fun log(umm: Any) {
        Log.d("FORENSIC_ACTIVITY", umm.toString())
    }

}
