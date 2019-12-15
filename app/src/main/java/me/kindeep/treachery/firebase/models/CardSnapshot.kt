package me.kindeep.treachery.firebase.models

data class CardSnapshot (
    val name: String = "Loading...",
    val imgUrl: String = "https://picsum.photos/800",
    val altImgUrl: String = "https://picsum.photos/800",
    val guessedBy: MutableList<String> = mutableListOf()
)