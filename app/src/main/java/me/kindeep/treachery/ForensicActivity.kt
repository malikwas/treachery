package me.kindeep.treachery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ForensicActivity : AppCompatActivity() {

    lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forensic)
        gameId = savedInstanceState!!["id"].toString()
    }
}
