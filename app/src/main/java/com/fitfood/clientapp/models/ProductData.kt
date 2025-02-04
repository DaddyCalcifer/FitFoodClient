package com.fitfood.clientapp.models

data class ProductData(
    val id          : String,
    val name        : String,
    val manufacture : String,
    val calories    : Double,
    val protein     : Double,
    val fat         : Double,
    val carbohydrates : Double,
    val weight      : Double,
    val ingredients : String
)
