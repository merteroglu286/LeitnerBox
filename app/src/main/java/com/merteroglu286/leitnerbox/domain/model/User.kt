package com.merteroglu286.leitnerbox.domain.model

data class User(
    var id: String = "",
    val email: String = "",
    val photoCredit: Int = -1,
    val isPremium: Boolean = false,
    val boxes: Boxes = Boxes()
)