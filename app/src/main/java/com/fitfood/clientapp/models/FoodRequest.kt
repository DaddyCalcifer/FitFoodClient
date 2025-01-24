package com.fitfood.clientapp.models

data class FoodRequest (
    val name: String,
    val mass: Double,
    val kcal: Double,
    val fat: Double,
    val protein: Double,
    val carb: Double
)