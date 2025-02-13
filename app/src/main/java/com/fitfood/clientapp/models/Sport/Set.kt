package com.fitfood.clientapp.models.Sport

data class Set(
    val id : String,
    val setNumber : Int,
    val exerciseProgressId : String,
    val exerciseProgress : ExerciseProgress?,
    val reps : Int,
    val weight : Double,
    var isCompleted : Boolean
)
