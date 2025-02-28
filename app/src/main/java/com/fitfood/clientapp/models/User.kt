package com.fitfood.clientapp.models

import com.fitfood.clientapp.models.Sport.Training
import java.util.UUID

data class User(
    val id: UUID? = null,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var plans: List<FitPlan> = emptyList(),
    var datas: List<FitData> = emptyList()
)