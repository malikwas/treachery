package me.kindeep.treachery.forensic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.kindeep.treachery.R
import me.kindeep.treachery.chat.ChatFragment
import me.kindeep.treachery.firebase.addOnGameUpdateListener
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.shared.SingleForensicCardFragment

class ForensicActivity : AppCompatActivity() {

    lateinit var gameId: String
    lateinit var viewModel: ForensicViewModel
    lateinit var frag1: SingleForensicCardFragment
    lateinit var frag2: SingleForensicCardFragment
    lateinit var frag3: SingleForensicCardFragment
    lateinit var frag4: SingleForensicCardFragment
    lateinit var frag5: SingleForensicCardFragment
    lateinit var frag6: SingleForensicCardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forensic)
        gameId = intent?.extras?.getString("gameId")!!

        frag1 = supportFragmentManager.findFragmentById(R.id.card1) as SingleForensicCardFragment
        frag2 = supportFragmentManager.findFragmentById(R.id.card2) as SingleForensicCardFragment
        frag3 = supportFragmentManager.findFragmentById(R.id.card3) as SingleForensicCardFragment
        frag4 = supportFragmentManager.findFragmentById(R.id.card4) as SingleForensicCardFragment
        frag5 = supportFragmentManager.findFragmentById(R.id.card5) as SingleForensicCardFragment
        frag6 = supportFragmentManager.findFragmentById(R.id.card6) as SingleForensicCardFragment

//        var chat = supportFragmentManager.findFragmentById(R.id.chat) as ChatFragment
//        chat.removeTextBox()
//        chat.gameId = gameId

        ForensicViewModel.gameId = gameId
        viewModel = ViewModelProviders.of(this)
            .get(ForensicViewModel::class.java)

        viewModel.gameInstance.observe(this, Observer {
            findViewById<TextView>(R.id.gameId).text = it.gameId
        })

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


    }
}
