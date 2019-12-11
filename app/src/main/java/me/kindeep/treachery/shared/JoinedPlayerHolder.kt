package me.kindeep.treachery.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.models.PlayerSnapshot

class JoinedPlayerHolder(layoutInflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(
        layoutInflater.inflate(
            R.layout.joined_players_list_item,
            parent,
            false
        )
    ) {
    private val playerNameTextView = itemView.findViewById<TextView>(R.id.playerName)

    fun bind(playerSnapshot: PlayerSnapshot) {
        playerNameTextView.text = playerSnapshot.playerName
    }
}
