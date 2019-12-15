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

class SinglePlayerFragment : Fragment() {
    private var cluesFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private var meansFragments: List<CardFragment> =
        listOf(CardFragment(), CardFragment(), CardFragment(), CardFragment())
    private val player: PlayerSnapshot
        get() {
            return viewModel.getPlayerByName(playerName)
        }
    private lateinit var viewModel: PlayerViewModel
    private lateinit var playerNameText: TextView
    private lateinit var playerName: String

    private var selectedClue: Int
        get() {
            return viewModel.getSelectedClue(playerName)
        }
        set(value) {
            viewModel.setSelectedClue(playerName, value)
        }

    private var selectedMeans: Int
        get() {
            return viewModel.getSelectedMeans(playerName)
        }
        set(value) {
            viewModel.setSelectedMeans(playerName, value)
        }

    private fun update() {
        for ((index, fragment) in meansFragments.withIndex()) {
            fragment.bind(player.meansCards.getOrElse(index) { CardSnapshot() })
        }

        for ((index, fragment) in cluesFragments.withIndex()) {
            fragment.bind(player.clueCards.getOrElse(index) { CardSnapshot() })
        }

        cardHighlightUpdate()

        playerNameText.text = player.playerName
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        playerNameText = view.findViewById(R.id.player_name)
        viewModel = activity?.run { ViewModelProviders.of(this)
            .get(PlayerViewModel::class.java) } ?: throw  Exception("you suck")

        arguments?.takeIf { it.containsKey("playerName") }?.apply {
            playerName = getString("playerName")!!
            update()
            viewModel.gameInstance.observe(activity as LifecycleOwner, Observer {
                //                player = it.players.find { it.playerName == playerName } ?: PlayerSnapshot()
                // TODO: Actually check if anything changed before update
                update()
            })
        }

        if (savedInstanceState == null) {
            createFragments()
        } else {
            replaceFragments()
        }
    }

    private fun createFragments() {
        for ((i, fragment) in meansFragments.withIndex()) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.means_layout, fragment, "means$i${playerName}")
                commit()
            }
            log("FRAGMENT TAG ${fragment.tag}")
            fragment.setParentClickListener {
                meansClick(i)
            }
        }

        for ((i, fragment) in cluesFragments.withIndex()) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.clues_layout, fragment, "clues$i${playerName}")
                commit()
            }
            log("FRAGMENT TAG ${fragment.tag}")
            fragment.setParentClickListener {
                clueClick(i)
            }
        }
    }

    private fun replaceFragments() {
        // A Big brain hack to make fragments work when device is rotated, replace all with first,
        // add the rest.
        childFragmentManager.beginTransaction().apply {
            replace(R.id.clues_layout, cluesFragments[0])
            cluesFragments[0].setParentClickListener {
                clueClick(0)
            }
            replace(R.id.means_layout, meansFragments[0])
            meansFragments[0].setParentClickListener {
                meansClick(0)
            }
            commit()
        }

        for (i in 1 until meansFragments.size) {
            val fragment = meansFragments[i]
            childFragmentManager.beginTransaction().apply {
                add(R.id.means_layout, fragment, "means$i${playerName}")
                commit()
            }
            log("FRAGMENT TAG ${fragment.tag}")
            fragment.setParentClickListener {
                meansClick(i)
            }
        }

        for (i in 1 until cluesFragments.size) {
            val fragment = cluesFragments[i]
            childFragmentManager.beginTransaction().apply {
                add(R.id.clues_layout, fragment, "clues$i${playerName}")
                commit()
            }
            log("FRAGMENT TAG ${fragment.tag}")
            fragment.setParentClickListener {
                clueClick(i)
            }
        }
    }

    private fun clueClick(index: Int) {
        selectedClue = if (index in 0..3) {
            index
        } else -1
        cardHighlightUpdate()
    }

    private fun meansClick(index: Int) {
        selectedMeans = if (index in 0..3) {
            index
        } else -1
        cardHighlightUpdate()
    }

    private fun cardHighlightUpdate() {
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

    fun log(umm: Any?) {
        Log.i("SINGLE_PLAYER_FRAGMENT", umm.toString())
    }


}