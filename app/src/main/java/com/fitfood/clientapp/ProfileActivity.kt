package com.fitfood.clientapp

import FitDataForm
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.fitfood.clientapp.ui.theme.ClientAppTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.FitPlan
import com.fitfood.clientapp.models.Gender
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.services.DataService
import java.util.UUID

val dataService = DataService()

@Composable
fun ParametersScreen(
    user: User,
    context: Context?,
    onAddedData: () -> Unit,
    onBack: () -> Unit,
    drawAsTool: Boolean = false
) {
    var isAddingData by remember { mutableStateOf(false) }
    var isAddingPlan by remember { mutableStateOf(false) }
    var fitData by remember { mutableStateOf<FitData?>(null) }
    var responseSuccess by remember { mutableStateOf<Boolean?>(null) }
    var selectedFitDataId by remember { mutableStateOf<UUID?>(null) }

    if(context!= null) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "").toString()

        if (!isAddingData) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ваши данные",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if(!isAddingPlan) {
                    LazyColumn {
                        items(user.datas) { data ->
                            val itemModifier = if (drawAsTool) {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(
                                        Color(0x18315A16),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        selectedFitDataId = data.id
                                        isAddingPlan = true
                                    }
                                    .padding(16.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(
                                        Color(0x18315A16),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(16.dp)
                            }

                            UserDataItem(data, modifier = itemModifier)
                        }

                        item {
                            Spacer(Modifier.height(10.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                                onClick = { isAddingData = true }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text("Добавить", style = MaterialTheme.typography.headlineMedium)
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.Transparent
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Построение плана питания",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Я хочу...",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                                onClick = {
                                dataService.sendGeneratePlanRequest(
                                    selectedFitDataId,
                                    0,
                                    token,
                                    context
                                )
                                isAddingPlan = false
                                onBack()
                            }) {
                                Text("Сбросить вес", style = MaterialTheme.typography.headlineSmall)
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                                onClick = {
                                dataService.sendGeneratePlanRequest(
                                    selectedFitDataId,
                                    1,
                                    token,
                                    context
                                )
                                isAddingPlan = false
                                    onBack()
                            }) {
                                Text("Поддерживать вес", style = MaterialTheme.typography.headlineSmall)
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                                onClick = {
                                dataService.sendGeneratePlanRequest(
                                    selectedFitDataId,
                                    2,
                                    token,
                                    context
                                )
                                isAddingPlan = false
                                    onBack()
                            }) {
                                Text("Набрать вес", style = MaterialTheme.typography.headlineSmall)
                            }
                            Spacer(Modifier.height(40.dp))
                            Button(modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0x18315A16)),
                                onClick = { isAddingPlan = false
                                onBack()}) {
                                Text("Назад", style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                }
            }
        } else {

            FitDataForm(
                onCancel = { isAddingData = false },
                onSubmit = { data ->
                    fitData = data
                    dataService.sendFitData(data, token) { success ->
                        responseSuccess = success
                        if (success) {
                            onAddedData() // Обновляем данные после успешного добавления
                            isAddingData = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PlansScreen(
    user: User,
    stats: FeedTotalStats,
    context: Context?,
    onAddedData: () -> Unit,
    navController: NavController
) {
    var isAddingPlan by remember { mutableStateOf(false) }
    var isInPlan by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<FitPlan?>(null) }

    if(context!= null) {
        if (!isInPlan) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ваши планы питания",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if(!isAddingPlan) {
                    LazyColumn {
                        items(user.plans) { plan ->
                            val itemModifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(
                                        Color(0x18315A16),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        selectedPlan = plan
                                        isInPlan = true
                                    }
                                    .padding(16.dp)
                            Log.e("plan", "${plan.dayKcal} + ${plan.waterMl}")
                            UserPlanItem(plan, modifier = itemModifier)
                        }

                        item {
                            Spacer(Modifier.height(10.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                                onClick = { isAddingPlan = true }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text("Построить план", style = MaterialTheme.typography.headlineMedium)
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.Transparent
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    ParametersScreen(user, context, onAddedData, {isAddingPlan = false}, true)
                }
            }
        } else {
            selectedPlan?.let { NutritionSummaryScreen(plan = it, stats, navController) }
        }
    }
}

@Composable
fun UserDataItem(data: FitData, modifier: Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Иконка или эмодзи для каждого параметра
        Icon(
            imageVector =
            if(data.gender == 1) Icons.Filled.Male
            else Icons.Filled.Female,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = Color(0xFF1A300c)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = "Вес: ${data.weight} кг", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Рост: ${data.height} см", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Возраст: ${data.age} лет", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Активность: ${data.activity}", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
@Composable
fun UserPlanItem(plan: FitPlan, modifier: Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Иконка или эмодзи для каждого параметра
        Icon(
            imageVector = Icons.Filled.ArtTrack,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = Color(0xFF1A300c)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = "Пища: ${plan.dayKcal} ккал", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Вода: ${plan.waterMl} мл", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun ProfileForm(
    userId: String,
    username: String,
    email: String,
    onSave: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = Color.DarkGray
                )
                Text("Профиль", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.height(70.dp))
            FitTextBox(
                content = remember { mutableStateOf(userId) },
                label = "ID пользователя",
                icon = Icons.Default.Person,
                keyboard = KeyboardType.Text,
                readOnly = true
            )

            FitTextBox(
                content = remember { mutableStateOf(username) },
                label = "Логин",
                icon = Icons.Default.AccountCircle,
                keyboard = KeyboardType.Text,
                readOnly = true
            )

            FitTextBox(
                content = remember { mutableStateOf(email) },
                label = "Email",
                icon = Icons.Default.Email,
                keyboard = KeyboardType.Email,
                readOnly = true
            )

            // Кнопка смены пароля
            Button(
                onClick = { showPasswordDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFDFE0EA))
            ) {
                Row(modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.DarkGray
                    )
                    Text("Сменить пароль", color = Color.DarkGray, style = MaterialTheme.typography.titleMedium)
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Transparent
                    )
                }
            }
        }

        // Кнопка сохранения
        Button(
            onClick = { onSave() },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
        ) {
            Text("Сохранить изменения", style = MaterialTheme.typography.titleLarge)
        }

        // Кнопка выхода
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFDFE0EA))
        ) {
            Row(modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.DarkGray
                )
                Text("Выйти из аккаунта", style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)
                Icon(
                    imageVector = Icons.Filled.LockReset,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.Transparent
                )
            }
        }
    }

    // Всплывающее окно смены пароля
    if (showPasswordDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Смена пароля") },
            text = {
                Column {
                    FitTextBox(
                        content = currentPassword,
                        label = "Текущий пароль",
                        icon = Icons.Default.Lock,
                        keyboard = KeyboardType.Password
                    )
                    FitTextBox(
                        content = newPassword,
                        label = "Новый пароль",
                        icon = Icons.Default.LockOpen,
                        keyboard = KeyboardType.Password
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onChangePassword()
                        showPasswordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(Color(0xFFDFE0EA))
                ) {
                    Text("Отмена", color = Color.DarkGray)
                }
            }
        )
    }
}
