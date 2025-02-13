package com.fitfood.clientapp.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.fitfood.clientapp.models.FeedAct
import com.fitfood.clientapp.models.FeedStats
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.FoodRequest
import com.fitfood.clientapp.models.ProductData
import com.fitfood.clientapp.models.Sport.Training
import com.fitfood.clientapp.models.Sport.TrainingPlan
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.models.requests.GeneratePlanRequest
import com.fitfood.clientapp.services.AuthService.RegisterRequest
import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID

class DataService {
    private val BASE_URL = "https://fitfoodapi-6x5r.onrender.com/api/"

    fun sendFitData(fitData: FitData, token: String, onResponse: (Boolean) -> Unit) {
        val url = "${BASE_URL}user/data"
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

    fun completeSet(setId: String, reps: Int, weight: Double, token: String, onResponse: (Boolean) -> Unit) {
        // Формируем URL для запроса
        val url = "${BASE_URL}sport/set/$setId/$reps:$weight"

        // Создаем запрос
        val request = Request.Builder()
            .url(url)
            .put(RequestBody.create(null, "")) // Пустое тело запроса, так как данные передаются в URL
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Отправляем запрос асинхронно
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // В случае ошибки сети возвращаем false
                onResponse(false)
            }

            override fun onResponse(call: Call, response: Response) {
                // Возвращаем true, если код ответа 200
                onResponse(response.isSuccessful)
            }
        })
    }

    fun sendFoodData(foodData: FoodRequest, token: String, type: String) {
        val url = "${BASE_URL}food/add/${type}"
        val requestBody = Gson().toJson(foodData)
        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FoodData", "Failed to add data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("FoodData", "Data added successfully")
                } else {
                    Log.e("FoodData", "Failed with status: ${response.code}")
                }
            }
        })
    }
    fun deleteFoodData(id: String, token: String) {
        val url = "${BASE_URL}food/$id/delete"
        val requestBody = Gson().toJson("")
        val request = Request.Builder()
            .url(url)
            .delete(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FoodData", "Failed to delete data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("FoodData", "Data deleted successfully")
                } else {
                    Log.e("FoodData", "Failed with status: ${response.code}")
                }
            }
        })
    }
    suspend fun fetchUser(token: String): User? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "user")
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
    suspend fun fetchProduct(barcode: String): ProductData? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "product/${barcode}")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, ProductData::class.java)
                }
            } else {
                null
            }
        }
    }
    suspend fun fetchTotalStats(token: String): FeedTotalStats? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "food/stats/today/total")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, FeedTotalStats::class.java)
                }
            } else {
                null
            }
        }
    }
    suspend fun fetchTodayFeeds(token: String): List<FeedAct>? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "food")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Array<FeedAct>::class.java).toList()
                }
            } else {
                null
            }
        }
    }


    suspend fun fetchTrainingPlans(token: String): List<TrainingPlan>? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "sport/plans")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Array<TrainingPlan>::class.java).toList()
                }
            } else {
                null
            }
        }
    }

    suspend fun fetchTrainingPlanById(planId: String, userId: String): TrainingPlan? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "sport/plans/$planId")
            .addHeader("Authorization", "Bearer $userId")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, TrainingPlan::class.java)
                }
            } else {
                null
            }
        }
    }

    //Тренировки
    suspend fun fetchTrainings(token: String): List<Training>? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "sport/trainings")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Array<Training>::class.java).toList()
                }
            } else {
                null
            }
        }
    }

    suspend fun fetchTrainingById(trainingId: String, userId: String): Training? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "sport/trainings/$trainingId")
            .addHeader("Authorization", "Bearer $userId")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Training::class.java)
                }
            } else {
                null
            }
        }
    }
    //

    suspend fun fetchTodayFeedsByType(token: String, type: String): List<FeedAct>? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "food/${type}")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Array<FeedAct>::class.java).toList()
                }
            } else {
                null
            }
        }
    }
    suspend fun fetchSearchResults(name: String): List<ProductData>? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL + "food/search?name=${name}")
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    Gson().fromJson(it, Array<ProductData>::class.java).toList()
                }
            } else {
                null
            }
        }
    }

    fun sendGeneratePlanRequest(fitDataId: UUID?, usingType: Int, token: String, context: Context?) {
        val requestObject = GeneratePlanRequest().apply {
            FitDataId = fitDataId
            UsingType = usingType
        }

        val url = "${BASE_URL}fit/default"
        val requestBody = Gson().toJson(requestObject)
        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeneratePlan", "Failed to generate plan", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("GeneratePlan", "Plan generated successfully")
                } else {
                    Log.e("GeneratePlan", "Failed with status: ${response.code}")
                }
            }
        })
    }
}