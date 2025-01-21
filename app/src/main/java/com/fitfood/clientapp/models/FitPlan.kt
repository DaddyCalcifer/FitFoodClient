package com.fitfood.clientapp.models

import java.util.UUID

data class FitPlan (
    var id : UUID? = null,
    var dayKcal : Double = 0.0,
    var durationInDays : Int = 0,
    var waterMl : Double = 0.0,

    var fat_kcal : Double = 0.0,
    var protein_kcal : Double = 0.0,
    var carb_kcal : Double = 0.0,
    var fat_g : Double = 0.0,
    var protein_g : Double = 0.0,
    var carb_g : Double = 0.0,

    var isPublic : Boolean = true,
    var isDeleted : Boolean = false,

    var userId : UUID? = null,
    var user : User? = null,
    var comments : List<Int>? = null,

    var breakfastKcal : Double = 0.0,
    var lunchKcal : Double = 0.0,
    var dinnerKcal : Double = 0.0,
    var otherKcal : Double = 0.0, )