package me.kindeep.treachery.firebase

import com.google.firebase.Timestamp


data class GameInstanceSnapshot(
    val gameId: String = "Undefined",
    val snapshotVersion: String = "0.1",
    val messages: List<MessageSnapshot> = listOf(MessageSnapshot()),
    val players: List<PlayerSnapshot> = listOf(PlayerSnapshot()),
    val isStarted: Boolean = false,
    val createdTimestamp: Timestamp = Timestamp.now(),
    val startedTimestamp: Timestamp = Timestamp.now(),
    val isMurdererSelected: Boolean = false
    )