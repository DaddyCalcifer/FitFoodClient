package com.fitfood.clientapp.models.requests

data class RegisterRequest(
    val username: String = "",
    val password: String = "",
    val email: String = ""
)