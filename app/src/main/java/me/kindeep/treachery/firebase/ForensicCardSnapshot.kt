package me.kindeep.treachery.firebase

data class ForensicCardSnapshot(
    val cardName: String = "Untitled",
    val choices: List<String> = listOf(),
    val selectedChoice: Int = -1
) {
    fun isSelected(): Boolean {
        return selectedChoice != -1
    }
}