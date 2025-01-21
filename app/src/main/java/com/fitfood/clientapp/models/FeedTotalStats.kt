package com.fitfood.clientapp.models

data class FeedTotalStats (
    var ateKcal     : Double = 0.0,
    var ateProtein  : Double = 0.0,
    var ateCarb     : Double = 0.0,
    var ateFat      : Double = 0.0,
    var burntKcal   : Double = 0.0,
    var ateBreakfast: Double = 0.0,
    var ateDinner   : Double = 0.0,
    var ateLunch    : Double = 0.0,
    var ateOther    : Double = 0.0,
)