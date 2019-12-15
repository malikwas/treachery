package me.kindeep.treachery.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private lateinit var playerNameText: TextView

    var selectedClue = -1
    var selectedMeans = -1

    private fun update() {
        for ((index, fragment) in meansFragments.withIndex()) {
            fragment.bind(player.meansCards.getOrElse(index) { CardSnapshot() })
        }

        for ((index, fragment) in cluesFragments.withIndex()) {
            fragment.bind(player.clueCards.getOrElse(index) { CardSnapshot() })
        }

        playerNameText.text = player.playerName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        playerNameText = view.findViewById(R.id.player_name)
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
            for ((i, fragment) in meansFragments.withIndex()) {
                add(R.id.means_layout, fragment)
                fragment.setParentClickListener {
                    meansClick(i)
                }
            }

            for ((i, fragment) in cluesFragments.withIndex()) {
                add(R.id.clues_layout, fragment)
                fragment.setParentClickListener {
                    clueClick(i)
                }
            }

            commit()
        }
    }

    fun clueClick(index: Int) {
        selectedClue = if (index in 0..3) {
            index
        } else -1
        cardHighlightUpdate()
    }

    fun meansClick(index: Int) {
        selectedMeans = if (index in 0..3) {
            index
        } else -1
        cardHighlightUpdate()
    }

    fun cardHighlightUpdate() {
        for ((i, fragment) in meansFragments.withIndex()) {
            fragment.highlighted = (i == selectedMeans)
        }

        for ((i, fragment) in cluesFragments.withIndex()) {
            fragment.highlighted = (i == selectedClue)
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