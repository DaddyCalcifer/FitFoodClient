package com.fitfood.clientapp.models.requests

data class LoginRequest(
    var login: String = "",
    var password: String = ""
)