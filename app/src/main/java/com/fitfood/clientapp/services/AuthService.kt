package com.fitfood.clientapp.services

import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AuthService {

    // Базовый URL API
    private val BASE_URL = "http://10.0.2.2:5059/api/user/"

    // Клиент OkHttp и объект Gson для сериализации
    private val client = OkHttpClient()
    private val gson = Gson()

    // Модель для запроса авторизации
    data class LoginRequest(val login: String, val password: String)
    data class RegisterRequest(val username: String, val password: String, val email: String)

    // Функция для авторизации
    fun authorizeUser(login: String, password: String, callback: (String) -> Unit) {
        val url = "${BASE_URL}authorize"
        val loginRequest = LoginRequest(login, password)

        val requestBody = gson.toJson(loginRequest)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { callback(it) }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback("Server error: ${e.message}")
            }
        })
    }

    // Функция для регистрации
    fun registerUser(username: String, password: String, email: String, callback: (String) -> Unit) {
        val url = "${BASE_URL}create"
        val registerRequest = RegisterRequest(username, password, email)

        val requestBody = gson.toJson(registerRequest)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { callback(it) }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback("Server error: ${e.message}")
            }
        })
    }

    suspend fun checkToken(token: String): Response {
        val request = Request.Builder()
            .url("${BASE_URL}user")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
    }


}