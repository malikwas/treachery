package me.kindeep.treachery.shared

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot

class CardFragment : Fragment {

    lateinit var cardImage: ImageView
    lateinit var cardName: TextView

    var card: CardSnapshot

    constructor() : super() {
        card = CardSnapshot()
    }

    constructor(cardSnapshot: CardSnapshot) : super() {
        card = cardSnapshot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardName = view.findViewById(R.id.card_name)
        cardImage = view.findViewById(R.id.card_image)
        bind(card)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

     fun bind(cardSnapshot: CardSnapshot) {
        if (::cardName.isInitialized) {
            Picasso.get().load(cardSnapshot.imgUrl).into(cardImage)
            cardName.text = cardSnapshot.name
        }
    }

    fun log(value: Any) {
        Log.i("CARD_FRAGMENT", value.toString())
    }
}
