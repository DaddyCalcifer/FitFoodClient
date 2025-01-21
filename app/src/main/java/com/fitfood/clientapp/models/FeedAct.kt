package com.fitfood.clientapp.models

import java.util.UUID


data class FeedAct(
    var userId: UUID,
    var date: String,
    var feedType: Int,
    var name: String,
    var mass: Double,
    var kcal100: Double,
    var fat100: Double,
    var protein100: Double,
    var carb100: Double
)