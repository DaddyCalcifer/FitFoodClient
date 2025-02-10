package com.fitfood.clientapp.models.Sport

data class Set(
    val id : String,
    val setNumber : String,
    val exerciseProgressId : String,
    val exerciseProgress : ExerciseProgress,
    val reps : Double,
    val weight : Double,
    val isCompleted : Boolean
)
