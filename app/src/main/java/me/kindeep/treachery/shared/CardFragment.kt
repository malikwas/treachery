package me.kindeep.treachery.shared

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.CardSnapshot
import java.lang.Exception
import java.util.*


class CardFragment : Fragment {

    lateinit var cardImage: ImageView
    lateinit var cardName: TextView
    lateinit var clickableParent: View

    var card: CardSnapshot
    var highlighted: Boolean = false
        set(value) {
            field = value
            if (isInitialized()) {
                if (field) {
                    cardName.background =
                        resources.getDrawable(R.drawable.card_header_gradient_selected)
                } else {
                    cardName.background = resources.getDrawable(R.drawable.card_header_gradient)
                }
            }
        }

    private var pClickListener: View.OnClickListener = View.OnClickListener { }

    fun setParentClickListener(listener: (View) -> Unit) {
        pClickListener = View.OnClickListener(listener)
    }

    constructor() : super() {
        log("FRAGMENT CREATED $tag $this")
        card = CardSnapshot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardName = view.findViewById(R.id.card_name)
        cardImage = view.findViewById(R.id.card_image)
        clickableParent = view.findViewById(R.id.click_parent)
        clickableParent.setOnClickListener(pClickListener)
        bind(card)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    fun bind(cardSnapshot: CardSnapshot) {
        if (isInitialized()) {
            Picasso.get()
                .load(cardSnapshot.imgUrl)
                .resize(170, 200)
                .into(cardImage, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Picasso.get().load(cardSnapshot.altImgUrl)
                            .resize(170, 200)
                            .into(cardImage, object : Callback {
                                override fun onSuccess() {
                                }

                                override fun onError(e: Exception?) {
                                    Picasso.get().load("https://picsum.photos/200")
                                        .resize(170, 200)
                                        .into(cardImage)
                                }
                            })
                    }
                })
            cardName.text = cardSnapshot.name
        }
    }

    fun isInitialized(): Boolean {
        return ::cardName.isInitialized
    }

    fun log(value: Any) {
        Log.i("CARD_FRAGMENT", value.toString())
    }
}
