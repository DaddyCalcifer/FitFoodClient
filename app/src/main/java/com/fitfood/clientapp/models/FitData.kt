package com.fitfood.clientapp.models

import java.util.UUID

data class FitData(
    val id: UUID? = null,
    val weight: Float,
    val height: Float,
    val age: Int,
    val gender: Int,
    val activity: Int,
    val userId: UUID? = null,
    val updatedAt: String = "",
    val createdAt: String = "",
    val isDeleted: Boolean = false
)