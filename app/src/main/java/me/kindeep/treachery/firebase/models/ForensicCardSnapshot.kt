package me.kindeep.treachery.firebase.models

data class ForensicCardSnapshot(
    var cardName: String = "...",
    var choices: List<String> = listOf("...", "...", "...", "...", "...", "..."),
    var selectedChoice: Int = -1
) {
    fun isSelected(): Boolean {
        return selectedChoice != -1
    }
}