package me.kindeep.treachery

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import me.kindeep.treachery.firebase.ForensicCardSnapshot

/**
 * To aid selecting clue on the next forensic card
 *
 * Displays a ViewPager with multiple cards as next card options, only permits selection of one.
 */
class NextForensicCardFragment : Fragment() {
    lateinit var parent: View

    lateinit var nextCardViewPager: ViewPager

    companion object {
        fun newInstance() = NextForensicCardFragment()
    }

    lateinit var viewModel: ForensicViewModel

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
            nextCardViewPager.adapter =
                CardsPagerAdapter(childFragmentManager, viewModel, activity as LifecycleOwner)
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
        val fragment = SingleForensicCardFragment(nextCards[i])
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-
            putInt("cardIndex", i + 1)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "OBJECT ${(position + 1)}"
    }
}

class SingleForensicCardFragment(val forensicCardSnapshot: ForensicCardSnapshot) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.single_forensic_card_fragment, container, false)
    }

    lateinit var cardNameView: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey("cardIndex") }?.apply {
            cardNameView = view.findViewById(R.id.cardName)
            val cardIndexView: TextView = view.findViewById(R.id.cardIndex)
            val cardIndex = getInt("cardIndex")
            cardIndexView.text = cardIndex.toString()
            bind()
        }
    }

    fun bind() {
        cardNameView.text = forensicCardSnapshot.cardName
    }
}


