package me.kindeep.treachery.chat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import me.kindeep.treachery.*
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot
import me.kindeep.treachery.firebase.models.MessageSnapshot
import me.kindeep.treachery.firebase.sendMessage

/**
 * Fully featured chat, with different views for sent messages on the right, and received messages
 * on the left (refer to layouts). Text box can be hidden programmatically.
 */

class ChatFragment : Fragment() {

    var gameId: String? = null
        set(value) {
            field = value
            if (value != null) {
                getGameReference(value).addSnapshotListener { documentSnapshot, _ ->
                    Log.e("CHAT FRAGMENT", "Game id set and document found, notifying adapter")
                    val gameInstance = documentSnapshot!!.toObject<GameInstanceSnapshot>()!!
                    messages = gameInstance.messages.sortedBy { it.timestamp }
                    messageRecycler.adapter?.notifyDataSetChanged()
                }
            }
        }

    lateinit var messageRecycler: RecyclerView
    private var isTextBoxVisible = true
    private var messages: List<MessageSnapshot> = mutableListOf()
    private var textBoxContainer: LinearLayout? = null

    fun removeTextBox() {
        textBoxContainer!!.visibility = View.GONE
        isTextBoxVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textBoxContainer = view.findViewById(R.id.layout_textbox)

        val sendButton: TextView = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {

            val textBox = textBoxContainer!!.findViewById<EditText>(R.id.editable_textbox)
            val input = textBox.text.toString().trim()

            if (input != "" && gameId != null) {
                var m = MessageSnapshot(message=input, playerName="SomeDude")
                sendMessage(m, gameId!!)
                textBox.setText("")
            }

        }

        messageRecycler = view.findViewById(R.id.message_recycler)

        messageRecycler.apply {
            adapter = object : RecyclerView.Adapter<BaseMessageHolder>() {
                private val SENT_MESSAGE_TYPE = 1
                private val RECEIVED_MESSAGE_TYPE = 2

                override fun getItemViewType(position: Int): Int {
                    val message = messages[position]

                    return when (message.playerName.equals("SomeDude")) {
                        true -> SENT_MESSAGE_TYPE
                        else -> RECEIVED_MESSAGE_TYPE
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMessageHolder {
                    if (viewType == SENT_MESSAGE_TYPE) {
                        return SentMessageHolder(
                            layoutInflater,
                            parent
                        )
                    } else {
                        return ReceivedMessageHolder(
                            layoutInflater,
                            parent
                        )
                    }
                }

                override fun getItemCount(): Int {
                    return messages.size
                }

                override fun onBindViewHolder(holder: BaseMessageHolder, position: Int) {
                    holder.bind(messages[position])
                }
            }

            layoutManager = LinearLayoutManager(activity)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_view, container, false)
    }
}

abstract class BaseMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(messageSnapshot: MessageSnapshot)
}

class SentMessageHolder(layoutInflater: LayoutInflater, parent: ViewGroup) :
    BaseMessageHolder(layoutInflater.inflate(R.layout.item_sent_message, parent, false))
{
    val body: TextView = itemView.findViewById(R.id.text_message_body)

    override fun bind(messageSnapshot: MessageSnapshot) {
        body.text = messageSnapshot.message
        body.setBackgroundColor(
            if (messageSnapshot.type == 1) Color.parseColor("#009688")
            else Color.parseColor("#FFFF9800") )
    }
}

class ReceivedMessageHolder(layoutInflater: LayoutInflater, parent: ViewGroup) :
    BaseMessageHolder(layoutInflater.inflate(R.layout.item_received_message, parent, false))
{
    val body: TextView = itemView.findViewById(R.id.text_message_body)
    val name: TextView = itemView.findViewById(R.id.text_message_name)

    override fun bind(messageSnapshot: MessageSnapshot) {
        body.text = messageSnapshot.message
        body.setBackgroundColor(
            if (messageSnapshot.type == 1) Color.parseColor("#3F51B5")
            else Color.parseColor("#FFFF9800") )
        name.text = messageSnapshot.playerName
    }
}