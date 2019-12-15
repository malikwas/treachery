package me.kindeep.treachery.forensic

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import me.kindeep.treachery.*
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.shared.SingleForensicCardFragment

/**
 * To aid selecting clue on the next forensic card
 *
 * Displays a ViewPager with multiple cards as next card options, only permits selection of one.
 */
class NextForensicCardFragment : Fragment() {
    lateinit var parent: View
    lateinit var nextCardViewPager: ViewPager
    lateinit var selectCardButton: Button
    lateinit var viewModel: ForensicViewModel
    var pageNumber = 0


    companion object {
        fun newInstance() =
            NextForensicCardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = inflater.inflate(R.layout.next_forensic_card_fragment, container, false)
        return parent
    }

    /**
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextCardViewPager = view.findViewById(R.id.next_card_pager)
        selectCardButton = view.findViewById(R.id.select_card)
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(this).get(ForensicViewModel::class.java)
            viewModel.gameInstance.observe(context as LifecycleOwner, Observer {
                parent.findViewById<TextView>(
                    R.id.gameIdNextFrag
                ).text = viewModel.gameInstance.value!!.gameId
            })

            doAdapterStuff()

            viewModel.nextCardSnapshots.observe(context as LifecycleOwner, Observer {
                doAdapterStuff()
            })
        }
    }

    fun doAdapterStuff() {
        val cardAdapter = CardsPagerAdapter(
            childFragmentManager,
            viewModel,
            activity as LifecycleOwner
        )
        nextCardViewPager.adapter = cardAdapter


        nextCardViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                pageNumber = position
            }
        })

        selectCardButton.setOnClickListener {
            if (cardAdapter.nextCards.isNotEmpty()) {
                val selectedCard = cardAdapter.nextCards[pageNumber]
                val state = forensicGameState(viewModel.gameInstance.value!!)

                when (state) {
                    ForensicGameState.CAUSE_CARD -> {
                        selectCauseForensicCard(viewModel.gameInstance.value!!.gameId, selectedCard)
                    }
                    ForensicGameState.LOCATION_CARD -> {
                        selectLocationForensicCard(
                            viewModel.gameInstance.value!!.gameId,
                            selectedCard
                        )
                    }
                    ForensicGameState.OTHER_CARD -> {
                        selectOtherCard(viewModel.gameInstance.value!!.gameId, selectedCard)
                    }
                }
            }
        }
    }
}



