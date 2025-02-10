package com.fitfood.clientapp.models.Sport

import com.fitfood.clientapp.models.User

data class TrainingPlan(
    val id : String,
    val name : String,
    val description : String,
    val exercises : List<Exercise>,

    val date : String,
    val userId : String,
    val isDeleted : Boolean,

    val caloriesLoss : Double
)
