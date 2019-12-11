package me.kindeep.treachery.shared

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject

import me.kindeep.treachery.R
import me.kindeep.treachery.firebase.getGameReference
import me.kindeep.treachery.firebase.models.GameInstanceSnapshot

class JoinedPlayersFragment : Fragment() {

    var gameId: String? = null
        set(value) {
            field = value
            if (value != null) {
                getGameReference(value).addSnapshotListener { documentSnapshot, _ ->
                    Log.e("JOINEDPLAYERS", "Game id set and document found, notifying adapter")
                    gameInstance = documentSnapshot!!.toObject()!!
                    joinedPlayersRecycler.adapter?.notifyDataSetChanged()
                }
            }
        }

    lateinit var joinedPlayersRecycler: RecyclerView
    private var gameInstance: GameInstanceSnapshot = GameInstanceSnapshot()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        joinedPlayersRecycler = view.findViewById(R.id.joined_players)

        joinedPlayersRecycler.apply {
            adapter = object : RecyclerView.Adapter<JoinedPlayerHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): JoinedPlayerHolder {
                    return JoinedPlayerHolder(
                        layoutInflater,
                        parent
                    )
                }

                override fun getItemCount(): Int {
                    return gameInstance.players.size
                }

                override fun onBindViewHolder(holder: JoinedPlayerHolder, position: Int) {
                    holder.bind(gameInstance.players[position])
                }
            }

            layoutManager = LinearLayoutManager(activity)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_joined_players, container, false)
    }
}
