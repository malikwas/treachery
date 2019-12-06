package me.kindeep.treachery.firebase

data class GameInstanceSnapshot(
    val snapshotVersion: String = "0.1",
    val messages: List<MessageSnapshot> = listOf(),
    val players: List<PlayerSnapshot> = listOf()
    )