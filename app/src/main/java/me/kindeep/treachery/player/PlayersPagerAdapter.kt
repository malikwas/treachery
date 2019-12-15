package me.kindeep.treachery.player

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.kindeep.treachery.firebase.models.PlayerSnapshot

class PlayersPagerAdapter(
    f: FragmentActivity,
    val viewModel: PlayerViewModel,
    lifecycleOwner: LifecycleOwner
) :
    FragmentStateAdapter(f) {


    var players: List<PlayerSnapshot> = listOf()

    init {
        players = viewModel.gameInstance.value?.players ?: listOf()
        viewModel.gameInstance.observe(lifecycleOwner, Observer {
            players = viewModel.gameInstance.value?.players ?: listOf()
            notifyDataSetChanged()
            log("\n\n Notify change \n\n")
        })
    }



    override fun getItemCount(): Int {
        return players.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = SinglePlayerFragment()
        fragment.arguments = Bundle().apply {
//            putInt("playerIndex", position)
            putSerializable("playerName", players[position].playerName)
        }

        return fragment
    }


    fun log(umm: Any) {
        Log.e("PLAYERS_PAGER_ADAPTER", umm.toString())
    }
}