package me.kindeep.treachery.shared.finish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.getGame
import me.kindeep.treachery.shared.CardFragment

class FinishActivity : AppCompatActivity() {
    lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        gameId = intent?.extras?.getString("gameId")!!
        val clueFragment = CardFragment()
        val meansFragment = CardFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.means_card, meansFragment, "meansCardFinish")
            replace(R.id.clue_card, clueFragment, "clueCardFinish")
            commit()
        }
        val text = findViewById<TextView>(R.id.win_text)
        getGame(gameId) {

            Log.e(
                "FINISH",
                "${it.gameId} ${it.murdererMeansCard} ${clueFragment.hashCode()} ${meansFragment.hashCode()}"
            )
            findViewById<TextView>(R.id.murderer_name).text = it.murdererName
            clueFragment.bind(it.murdererClueCard.copy())
            meansFragment.bind(it.murdererMeansCard.copy())
            if(it.correctlyGuessed) {
                 text.text = "Correctly guessed by ${it.correctGuess.guesserPlayer}, you win!"
            } else {
                text.text = "No one could determine the murderer, murderer wins!"
            }
        }
    }
}
