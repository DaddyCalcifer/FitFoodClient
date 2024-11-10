package com.fitfood.clientapp.services

import android.util.Log
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.services.AuthService.RegisterRequest
import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class DataService {
    private val BASE_URL = "http://10.0.2.2:5059/api/user/"

    fun sendFitData(fitData: FitData, token: String, onResponse: (Boolean) -> Unit) {
        val url = "${BASE_URL}data"
        val requestBody = Gson().toJson(fitData)
        val request = Request.Builder()
            .url(url)
            .put(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResponse(false)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(response.isSuccessful)
            }
        })
    }
    suspend fun fetchUser(token: String): User? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, User::class.java)
                }
            } else {
                null
            }
        }
    }

}