package me.kindeep.treachery.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot
import me.kindeep.treachery.firebase.models.PlayerSnapshot
import me.kindeep.treachery.shared.CardFragment

class SinglePlayerFragment : Fragment() {
    private val cluesFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private val meansFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private lateinit var viewModel: PlayerViewModel
    private lateinit var meansLayout: LinearLayout
    private lateinit var cluesLayout: LinearLayout

    var player: PlayerSnapshot = PlayerSnapshot()
        set(value) {
            log("player value set to $value")
            field = value
            bind()
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fm: FragmentManager = fragmentManager!!
        val transcation = fm.beginTransaction()

        for (i in 0..3) {
            transcation.add(R.id.means_layout, meansFragments[i])
        }

        for (i in 0..3) {
            transcation.add(R.id.clues_layout, cluesFragments[i])
        }

        transcation.commit()

        meansLayout = view.findViewById(R.id.means_layout)
        cluesLayout = view.findViewById(R.id.clues_layout)
        viewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)

        viewModel.gameInstance.observe(this, Observer {
            bind()
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_player, container, false)
    }

    fun bind() {
        log(player.toString())
        for (i in 0..3) {
            cluesFragments[i].card = player.clueCards.getOrElse(i) { CardSnapshot() }
            cluesFragments[i].bind()
            meansFragments[i].card = player.meansCards.getOrElse(i) { CardSnapshot() }
            cluesFragments[i].bind()
        }
    }

    fun log(umm: Any) {
        Log.i("SINGLE_PLAYER_FRAGMENT", umm.toString())
    }


}