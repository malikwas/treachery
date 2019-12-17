package me.kindeep.treachery.player

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.chat_view.*
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot
import me.kindeep.treachery.onMurdererCardsDetermined
import me.kindeep.treachery.selectMurderCards
import me.kindeep.treachery.triggerArtificialUpdate
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
    val selectorId = "murdererCardSelector"

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


//        currentPlayerFragment = supportFragmentManager.findFragmentById(R.id.mur) as SinglePlayerFragment
        val fragment = SinglePlayerFragment()
        val bundle = Bundle()
        bundle.putString("playerName", playerName)
        bundle.putString("uid", selectorId)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.murderer_fragment_container, fragment, "murdererFragment")
            commit()
        }



        playerPager = findViewById(R.id.player_pager)
        doAdapterStuff()
        playerPager.offscreenPageLimit = max(viewModel.gameInstance.value!!.players.size, 1)

        viewModel.gameInstance.observe(this, Observer {
            findViewById<TextView>(R.id.gameId).text = it.gameId
            playerPager.offscreenPageLimit = max(viewModel.gameInstance.value!!.players.size, 1)
        })

        onMurdererCardsDetermined(gameId) {
            finish()
        }

        Handler().postDelayed({
            triggerArtificialUpdate(gameId)
        }, 100)

    }

    fun doAdapterStuff() {
        playerPager.adapter =
            PlayersPagerAdapter(this, viewModel, this@MurdererSelectActivity)

    }

    fun log(umm: Any) {
        Log.i("MURDER_SELECT_ACTIVITY", umm.toString())
    }

    private fun getSelectedMeansCard(): CardSnapshot? {
        log("helo ${viewModel.getSelectedMeans(playerName + selectorId)}")
        return viewModel.getPlayerByName(playerName)
            .meansCards.getOrElse(viewModel.getSelectedMeans(playerName + selectorId)) { null }
    }

    private fun getSelectedCluesCard(): CardSnapshot? {
        return viewModel.getPlayerByName(playerName)
            .clueCards.getOrElse(viewModel.getSelectedClue(playerName + selectorId)) { null }
    }

    fun selectCards(view: View) {
        log("Selecting cards")
        getSelectedCluesCard()?.also { cluesCard ->
            log("Selecting cards Clue card: ${cluesCard.name}")
            getSelectedMeansCard()?.also { meansCard ->
                log("Selecting cards Means card: ${meansCard.name}")
                selectMurderCards(
                    gameId,
                    clueCard = cluesCard,
                    meansCard = meansCard
                ) {
                    view.findViewById<MaterialButton>(R.id.murderer_button).isEnabled = false
                    log("Success selecting murder cards, switch")
                }
            }
        }
        viewModel.getSelectedClue(playerName + selectorId)
        finish()
    }


}