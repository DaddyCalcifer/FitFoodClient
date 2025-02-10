package com.fitfood.clientapp.models.Sport

data class ExerciseProgress(
    val id : String,
    val exerciseId : String,
    val trainingId : String,
    val exercise : Exercise,
    val training : Training,
    val sets : List<Set>
)
