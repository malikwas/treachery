package me.kindeep.treachery.shared

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot

class CardFragment : Fragment() {
    var card: CardSnapshot = CardSnapshot()
        set(value) {
            field = value
            // Update views
            bind()
        }

    lateinit var cardImage: ImageView
    lateinit var cardName: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardName = view.findViewById(R.id.card_name)
        cardImage = view.findViewById(R.id.card_image)
        bind()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    fun bind() {
        if(::cardName.isInitialized) {
            cardName.text = card.name
            Picasso.get().load(card.imgUrl).into(cardImage)
        }
    }
}
