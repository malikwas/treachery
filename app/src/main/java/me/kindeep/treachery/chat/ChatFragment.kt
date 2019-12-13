package me.kindeep.treachery.chat

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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import me.kindeep.treachery.*
import me.kindeep.treachery.firebase.models.ForensicCardSnapshot
import me.kindeep.treachery.firebase.models.MessageSnapshot

/**
 * To aid selecting clue on the next forensic card
 *
 * Displays a ViewPager with multiple cards as next card options, only permits selection of one.
 */

class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_view, container, false)
    }
}

/*
class MessageListAdapter(options: FirestoreRecyclerOptions<MessageSnapshot>) :
    FirestoreRecyclerAdapter<MessageSnapshot, BaseMessageHolder>(options) {
    private val SENT_MESSAGE_TYPE = 1
    private val RECEIVED_MESSAGE_TYPE = 2

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)

        return when (message.playerName.equals("somePlayerName")) {
            true -> SENT_MESSAGE_TYPE
            false -> RECEIVED_MESSAGE_TYPE
        }
    }

    override fun onBindViewHolder(
        holder: BaseMessageHolder,
        position: Int,
        model: MessageSnapshot
    ) {
        holder.bind(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMessageHolder {
        if (viewType == SENT_MESSAGE_TYPE) {
            View v = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message,
                parent, false)

            return SentMessageHolder(v)
        } else {
            View v = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message,
                parent, false)

            return ReceivedMessageHolder(v)
        }
    }
}
*/

abstract class BaseMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(messageSnapshot: MessageSnapshot)
}

class SentMessageHolder(itemView: View) : BaseMessageHolder(itemView) {
    val body: TextView = itemView.findViewById(R.id.text_message_body)

    override fun bind(messageSnapshot: MessageSnapshot) {
        body.text = messageSnapshot.message
    }
}

class ReceivedMessageHolder(itemView: View) : BaseMessageHolder(itemView) {
    val body: TextView = itemView.findViewById(R.id.text_message_body)
    val name: TextView = itemView.findViewById(R.id.text_message_name)

    override fun bind(messageSnapshot: MessageSnapshot) {
        body.text = messageSnapshot.message
        name.text = messageSnapshot.playerName
    }
}