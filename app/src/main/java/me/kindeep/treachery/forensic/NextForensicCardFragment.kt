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
            val selectedCard = cardAdapter.nextCards[pageNumber]
            val state = forensicGameState(viewModel.gameInstance.value!!)

            when (state) {
                ForensicGameState.CAUSE_CARD -> {
                    selectCauseForensicCard(viewModel.gameInstance.value!!.gameId, selectedCard)
                }
                ForensicGameState.LOCATION_CARD -> {
                    selectLocationForensicCard(viewModel.gameInstance.value!!.gameId, selectedCard)
                }
                ForensicGameState.OTHER_CARD -> {
                    selectOtherCard(viewModel.gameInstance.value!!.gameId, selectedCard)
                }
            }


        }
    }
}

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

class SingleForensicCardFragment(var forensicCardSnapshot: ForensicCardSnapshot) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.single_forensic_card_fragment, container, false)
    }

    lateinit var cardNameView: TextView
    lateinit var choice0TextView: TextView
    lateinit var choice1TextView: TextView
    lateinit var choice2TextView: TextView
    lateinit var choice3TextView: TextView
    lateinit var choice4TextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey("cardIndex") }?.apply {
            cardNameView = view.findViewById(R.id.cardName)
            val cardIndexView: TextView = view.findViewById(R.id.cardIndex)
            choice0TextView = view.findViewById(R.id.choice0)
            choice0TextView.setOnClickListener {
                forensicCardSnapshot.selectedChoice = 0
            }
            choice1TextView = view.findViewById(R.id.choice1)
            choice1TextView.setOnClickListener {
                forensicCardSnapshot.selectedChoice = 1
            }
            choice2TextView = view.findViewById(R.id.choice2)
            choice2TextView.setOnClickListener {
                forensicCardSnapshot.selectedChoice = 2
            }
            choice3TextView = view.findViewById(R.id.choice3)
            choice3TextView.setOnClickListener {
                forensicCardSnapshot.selectedChoice = 3
            }
            choice4TextView = view.findViewById(R.id.choice4)
            choice4TextView.setOnClickListener {
                forensicCardSnapshot.selectedChoice = 4
            }

            val cardIndex = getInt("cardIndex")
            cardIndexView.text = cardIndex.toString()
            bind()
        }
    }

    fun bind() {
        cardNameView.text = forensicCardSnapshot.cardName
        choice0TextView.text = forensicCardSnapshot.choices.getOrElse(0) { it.toString() }
        choice1TextView.text = forensicCardSnapshot.choices.getOrElse(1) { it.toString() }
        choice2TextView.text = forensicCardSnapshot.choices.getOrElse(2) { it.toString() }
        choice3TextView.text = forensicCardSnapshot.choices.getOrElse(3) { it.toString() }
        choice4TextView.text = forensicCardSnapshot.choices.getOrElse(4) { it.toString() }
    }
}


