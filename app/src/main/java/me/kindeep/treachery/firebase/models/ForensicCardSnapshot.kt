package me.kindeep.treachery.firebase.models

data class ForensicCardSnapshot(
    var cardName: String = "Untitled",
    var choices: List<String> = listOf(),
    var selectedChoice: Int = -1
) {
    fun isSelected(): Boolean {
        return selectedChoice != -1
    }
}