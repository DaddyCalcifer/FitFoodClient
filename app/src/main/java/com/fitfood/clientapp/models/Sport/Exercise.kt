package com.fitfood.clientapp.models.Sport

data class Exercise(
    val id : String,
    val trainingPlanId : String,
    val trainingPlan : TrainingPlan,
    val name : String,
    val description : String,
    val sets : Int,
    val weight : Double,
    val reps : Int,
    val repCaloriesLoss : Double,
    val repsIsSeconds : Boolean,

    val totalCaloriesLoss : Double
)
