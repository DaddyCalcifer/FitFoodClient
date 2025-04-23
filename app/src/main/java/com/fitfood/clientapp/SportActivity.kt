package com.fitfood.clientapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.Sport.Set
import com.fitfood.clientapp.models.Sport.Training
import com.fitfood.clientapp.models.Sport.TrainingPlan
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.ui.theme.ClientAppTheme
import kotlinx.coroutines.launch

@Composable
fun SportSummaryScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var plans by remember { mutableStateOf<List<TrainingPlan>?>(null) }
    var trainings by remember { mutableStateOf<List<Training>?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var stats by remember { mutableStateOf<FeedTotalStats?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt", "").orEmpty()

    // Функция для загрузки планов
    fun refreshPlans() {
        coroutineScope.launch {
            try {
                plans = dataService.fetchTrainingPlans(token)
                trainings = dataService.fetchTrainings(token)
                user = dataService.fetchUser(token)
                progress = dataService.fetchTrainingProgress(token)
                if (user?.plans?.isNotEmpty() == true) {
                    stats = dataService.fetchTotalStats(token)
                }
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки данных: ${e.message}"
                Log.e("SportSummaryScreen", "Ошибка при загрузке данных", e)
                plans = null
                user = null
                stats = null
            } finally {
                isLoading = false
            }
        }
    }

    // Запускаем загрузку планов при первом запуске Composable
    LaunchedEffect(Unit) {
        refreshPlans()
    }

    // Отображение UI в зависимости от состояния
    if (isLoading) {
        LoadingScreen() // Показываем индикатор загрузки
    } else if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 8.dp, 16.dp, 0.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        text = "Сегодня ",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        Modifier.size(30.dp)
                    )
                }
            }
            item {
                stats?.let { safeStats ->
                    user?.let { safeUser ->
                        progress?.let {
                            HealthStatsScreen(
                                safeStats,
                                safeUser,
                                trainings,
                                navController,
                                progress!!
                            )
                        }
                    }
                } ?: run {
                    HealthStatsPlaceholder(navController)
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                plans?.let { trainingPlans ->
                    TrainingPlansList(navController, trainingPlans)
                } ?: run {
                    StatsImgText(Icons.Default.SearchOff,"Нет доступных планов тренировок")
                }
            }
        }
    }
}

@Composable
fun StatsImgText(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 5.dp)
        )
    }
}

@Composable
fun HealthStatsScreen(stats: FeedTotalStats, user: User, trains: List<Training>?, navController: NavController, progress: Int) {
    val hasTrain = trains?.isNotEmpty() ?: false

    val needAte = if (user.plans.isNotEmpty()) {
        ((user.plans.last().dayKcal + stats.burntKcal) - stats.ateKcal).toInt()
    } else {
        0
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        if (user.plans.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth(0.49f)
                    .fillMaxHeight()
            ) {
                if (needAte > 0) {
                    StatsImgText(Icons.Default.Cake, "Съедено: ${stats.ateKcal.toInt()} ккал")
                } else {
                    StatsImgText(Icons.Default.Warning, "Перебор: ${needAte * -1} ккал")
                }
                Spacer(modifier = Modifier.height(20.dp))
                StatsImgText(Icons.Default.LocalFireDepartment, "Сожжено: ${stats.burntKcal.toInt()} ккал")
                Spacer(modifier = Modifier.height(25.dp))
                Box(Modifier.background(color = Color(0xFFE5E7E7), RoundedCornerShape(20.dp))
                    .padding(10.dp)
                    .fillMaxWidth(0.95f)
                    .clickable{
                        navController.navigate("trainings")
                    }) {
                    StatsImgText(
                        Icons.Default.AutoStories,
                        "История занятий"
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Постройте свой план питания прямо сейчас!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Spacer(Modifier.height(25.dp))
                Button(
                    onClick = { navController.navigate("Nutrition") },
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(40.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                ) {
                    Text("Открыть", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .background(Color.DarkGray, RoundedCornerShape(2.dp))
        )
        // Правая колонка с прогрессом тренировки
        val context = LocalContext.current
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val last = sharedPreferences.getString("training", "").orEmpty()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        ) {
            if (hasTrain && progress > -1) {
                Text(
                    text = "Прогресс\nтренировок:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                TrainingProgressGrid(progress)
                Button(
                    onClick = {
                        if (trains != null) {
                            Log.i("TrainingID", "Navigating to training with ID: ${trains.last().id}")
                        }
                        if (last != "") {
                            navController.navigate("training/${last}")
                        }
                              },
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(40.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                ) {
                    Text("Открыть", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Text(
                    text = "Выбирайте программу тренировок и начинайте заниматься сейчас!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Spacer(Modifier.height(15.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    Modifier
                        .size(45.dp)
                )
            }
        }
    }
}

@Composable
fun HealthStatsPlaceholder(navController: NavController) {

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "Постройте свой план питания прямо сейчас!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Spacer(Modifier.height(25.dp))
            Button(
                onClick = { navController.navigate("Nutrition") },
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(40.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
            ) {
                Text("Открыть", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxHeight()
            .height(16.dp)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        Text(
            text = "Выбирайте программу тренировок и начинайте заниматься сейчас!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Spacer(Modifier.height(15.dp))
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            Modifier
                .size(45.dp)
        )
    }
}


@Composable
fun TrainingProgressGrid(progress: Int) {
    val filledCells = (progress / 1).coerceAtMost(100)
    val gridSize = 10

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until gridSize) {
            Row {
                for (col in 0 until gridSize) {
                    val cellIndex = row * gridSize + col
                    val cellColor = if (cellIndex < filledCells) Color(0xFF5E953B) else Color.Gray

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(1.dp)
                            .background(cellColor, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun TrainingPlansList(navController: NavController, plans: List<TrainingPlan>) {
    Column {
        StatsImgText(Icons.Default.FitnessCenter, "Программы тренировок")
        Spacer(Modifier.height(10.dp))
        for (p in plans) {
            PlanCard(navController, p)
            Spacer(Modifier.height(16.dp))
            Log.i("TPlan", "${p.name} был загружен")
        }
    }
}

@Composable
fun PlanCard(navController: NavController, plan: TrainingPlan) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
            .clickable { navController.navigate("trainingPlan/${plan.id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.AddReaction,
                contentDescription = null,
                Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(plan.name, style = MaterialTheme.typography.titleLarge)
                Row {
                    Text("От: ", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "  FitFood  ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp))
                    )
                }
                Row {
                    Text("Сжигает калорий: ", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "  ⚡ ${(plan.caloriesLoss).toInt()}  ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(plan.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun TrainingInListCard(training: Training, navController: NavController)
{
    Column(modifier = Modifier.fillMaxWidth()
        .background(color = Color(0x564B4B4B), RoundedCornerShape(20.dp))
        .clickable{navController.navigate("training/${training.id}")}) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(
                    Color(0xFF3C5015),
                    RoundedCornerShape(20.dp)
                ).height(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(5.dp))
            Row(
                Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "   ${training.trainingPlan.name} [${training.date}]",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(5.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp,0.dp,10.dp,10.dp)) {
                val isLoading = remember { mutableStateOf(false) }
                val showErrorDialog = remember { mutableStateOf(false) }
                Spacer(Modifier.height(16.dp))

                if (showErrorDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showErrorDialog.value = false // Закрываем диалог
                        },
                        title = {
                            Text("Ошибка")
                        },
                        text = {
                            Text("Возникла ошибка при загрузке")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showErrorDialog.value = false // Закрываем диалог
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                            ) {
                                Text("OK", color = Color.White)
                            }
                        }
                    )

                }
                if(isLoading.value)
                {
                    Box(modifier = Modifier.fillMaxWidth().height(70.dp)) {
                        LoadingScreen()
                    }
                }
                else{
                    Row {
                        Column(modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(100.dp),
                            verticalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                training.trainingPlan.description,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF3C5015)
                            )
                        }
                        var doneSets = 0
                        var leftSets = 0
                        for(ex in training.exercises)
                        {
                            for(set in ex.sets)
                            {
                                if(set.isCompleted)
                                    doneSets++
                                else
                                    leftSets++
                            }
                        }
                        val prog = (doneSets.toDouble()/(doneSets+leftSets).toDouble()*100)
                        TrainingProgressGrid(prog.toInt())
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingsHistory(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var trainings by remember { mutableStateOf<List<Training>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt", "").orEmpty()

    // Функция для загрузки планов
    fun refreshTrains() {
        coroutineScope.launch {
            try {
                trainings = dataService.fetchTrainings(token)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки данных: ${e.message}"
                Log.e("SportSummaryScreen", "Ошибка при загрузке данных", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshTrains()
    }

    // Отображение UI в зависимости от состояния
    if (isLoading) {
        LoadingScreen() // Показываем индикатор загрузки
    } else if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 8.dp, 16.dp, 0.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Мои тренировки ",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        Modifier.size(30.dp)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                trainings?.let { trainings ->
                    for(tra in trainings)
                    {
                        TrainingInListCard(tra, navController)
                        Spacer(Modifier.height(15.dp))
                    }
                } ?: run {
                    StatsImgText(Icons.Default.SearchOff,"История пуста")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListingPreview() {
    ClientAppTheme {
    }
}