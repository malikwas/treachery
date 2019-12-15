package me.kindeep.treachery.forensic

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.shared.SingleForensicCardFragment

class CardsPagerAdapter(
    fm: FragmentManager,
    val viewModel: ForensicViewModel,
    lifecycleOwner: LifecycleOwner
) :
    FragmentStatePagerAdapter(fm) {

    var nextCards: List<ForensicCardSnapshot> = listOf()

    init {
        viewModel.nextCardSnapshots.observe(lifecycleOwner, Observer {
            nextCards = it
            notifyDataSetChanged()
        })
    }

    override fun getCount(): Int {
        return nextCards.size
    }


    override fun getItem(i: Int): Fragment {
        val fragment =
            SingleForensicCardFragment(nextCards[i])
        fragment.arguments = Bundle().apply {
            putInt("cardIndex", i + 1)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Card ${(position + 1)}"
    }
}