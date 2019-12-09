package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class ForensicActivity : AppCompatActivity() {

    lateinit var gameId: String
    lateinit var viewModel: ForensicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forensic)
        gameId = intent?.extras?.getString("gameId")!!

        ForensicViewModel.gameId = gameId
        viewModel = ViewModelProviders.of(this)
            .get(ForensicViewModel::class.java)

        viewModel.gameInstance.observe(this, Observer {
            findViewById<TextView>(R.id.gameId).text = it.gameId
        })
    }
}
