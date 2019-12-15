package me.kindeep.treachery.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot
import me.kindeep.treachery.firebase.models.PlayerSnapshot
import me.kindeep.treachery.shared.CardFragment

class SinglePlayerFragment() : Fragment() {
    private var cluesFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private var meansFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private lateinit var player: PlayerSnapshot
    private lateinit var viewModel: PlayerViewModel

    private fun update() {
        for ((index, fragment) in meansFragments.withIndex()) {
            fragment.bind(player.meansCards.getOrElse(index) { CardSnapshot() })
        }

        for ((index, fragment) in cluesFragments.withIndex()) {
            fragment.bind(player.clueCards.getOrElse(index) { CardSnapshot() })

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        viewModel = ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java)

        arguments?.takeIf { it.containsKey("playerName") }?.apply {
            //            player = getSerializable("player") as PlayerSnapshot
            val playerName = getSerializable("playerName")
//            player = viewModel.gameInstance.value!!.players[getInt("playerIndex")]
            viewModel.gameInstance.observe(activity as LifecycleOwner, Observer {
                player = it.players.find { it.playerName == playerName } ?: PlayerSnapshot()
                // TODO: Actually check if anything changed before update
                update()
            })
        }

        childFragmentManager.beginTransaction().apply {
            for (fragment in meansFragments) {
                add(R.id.means_layout, fragment)
            }

            for (fragment in cluesFragments) {
                add(R.id.clues_layout, fragment)
            }

            commit()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_player, container, false)
    }

    fun log(umm: Any) {
        Log.i("SINGLE_PLAYER_FRAGMENT", umm.toString())
    }


}