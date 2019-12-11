package me.kindeep.treachery.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot

class SingleForensicCardFragment() : Fragment() {
    var forensicCardSnapshot: ForensicCardSnapshot = ForensicCardSnapshot()

    constructor(forensicCardSnapshot: ForensicCardSnapshot) : this() {
        this.forensicCardSnapshot = forensicCardSnapshot
    }


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
        cardNameView = view.findViewById(R.id.cardName)
        val cardIndexView: TextView = view.findViewById(R.id.cardIndex)
        choice0TextView = view.findViewById(R.id.choice0)
        choice1TextView = view.findViewById(R.id.choice1)
        choice2TextView = view.findViewById(R.id.choice2)
        choice3TextView = view.findViewById(R.id.choice3)
        choice4TextView = view.findViewById(R.id.choice4)
        arguments?.takeIf { it.containsKey("cardIndex") }?.apply {
            
            for (i in 0..4) {
                getChoiceViewAt(i).setOnClickListener {
                    forensicCardSnapshot.selectedChoice = i
                    bind()
                }
            }

            val cardIndex = getInt("cardIndex")
            cardIndexView.text = cardIndex.toString()
            bind()
        }
    }

    fun getChoiceViewAt(at: Int): TextView {
        return when (at) {
            0 -> choice0TextView
            1 -> choice1TextView
            2 -> choice2TextView
            3 -> choice3TextView
            4 -> choice4TextView
            else -> choice0TextView
        }
    }

    fun bind() {
        cardNameView.text = forensicCardSnapshot.cardName

        for (i in 0..4) {
            getChoiceViewAt(i).text = forensicCardSnapshot.choices.getOrElse(i) { it.toString() }
            getChoiceViewAt(i).setBackgroundColor(resources.getColor(
                R.color.forensicDefaultListBg))
        }

        getChoiceViewAt(forensicCardSnapshot.selectedChoice).setBackgroundColor(resources.getColor(
            R.color.forensicListHighlight))
    }
}
