package com.fitfood.clientapp.models.Sport

import com.fitfood.clientapp.models.User

data class Training(
    val id : String,
    val trainingPlanId : String,
    val trainingPlan : TrainingPlan,
    val exercises : List<ExerciseProgress>,

    val date : String,
    val userId : String,
    val user : User,

    val caloriesBurnt : Double
)
