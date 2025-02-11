import android.content.Context
import android.content.SharedPreferences
import android.util.JsonToken
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitfood.clientapp.AddProductForm
import com.fitfood.clientapp.FoodListScreen
import com.fitfood.clientapp.LoadingScreen
import com.fitfood.clientapp.NutritionSummaryScreen
import com.fitfood.clientapp.ParametersScreen
import com.fitfood.clientapp.PlansScreen
import com.fitfood.clientapp.ProfileForm
import com.fitfood.clientapp.SportSummaryScreen
import com.fitfood.clientapp.TrainingPlanSummaryScreen
import com.fitfood.clientapp.models.ActivityType
import com.fitfood.clientapp.models.FeedAct
import com.fitfood.clientapp.models.FeedStats
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.FitPlan
import com.fitfood.clientapp.models.FoodRequest
import com.fitfood.clientapp.models.Gender
import com.fitfood.clientapp.models.Sport.TrainingPlan
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.services.DataService
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen(val title: String, val icon: ImageVector) {
    object PhysicalData : Screen("Тренировки", Icons.Filled.FitnessCenter)
    object Nutrition : Screen("Питание", Icons.Filled.Restaurant)
    object Profile : Screen("Профиль", Icons.Filled.AccountCircle)
}

val dataService = DataService()

@Composable
fun MainScreen(navController_: NavController?, context: Context?) {
    val navController = rememberNavController()


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "Nutrition") {
                composable("Profile") {
                    ProfileScreen(navController_, LocalContext.current)
                }
                composable("PhysicalData") {
                    SportSummaryScreen(navController)
                }
                composable("Nutrition") {
                    NutritionScreen(navController, context)
                }
                composable("trainingPlan/{id}",
                    arguments = listOf(navArgument("id")
                    { type = NavType.StringType }
                    )) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("id") ?: "Unknown"
                    val sharedPreferences: SharedPreferences =
                        LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("jwt", "").orEmpty()

                    var plan by remember { mutableStateOf<TrainingPlan?>(null) }
                    var isLoading by remember { mutableStateOf(true) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(planId) {
                        try {
                            plan = dataService.fetchTrainingPlanById(planId, token)
                            errorMessage = null
                        } catch (e: Exception) {
                            errorMessage = "Ошибка загрузки плана: ${e.message}"
                            plan = null
                        } finally {
                            isLoading = false
                        }
                    }

                    if (isLoading) {
                        LoadingScreen()
                    } else if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else if (plan != null) {
                        TrainingPlanSummaryScreen(plan!!)
                    } else {
                        Text("План тренировки не найден")
                    }
                }
                composable("Nutrition/Detail") {

                    NutritionScreen(navController, context)
                }
                composable("Nutrition/Add/{mealType}",
                    arguments = listOf(navArgument("mealType") { type = NavType.StringType })) {
                    backStackEntry ->
                    val mealType = backStackEntry.arguments?.getString("mealType") ?: "Unknown"
                    val sharedPreferences: SharedPreferences = LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token_ = sharedPreferences.getString("jwt", "").orEmpty()

                    AddProductForm(onAddProduct = {data, token, type ->
                        dataService.sendFoodData(data, token, type)
                    },{navController.popBackStack()}, token_, mealType)
                }

                composable(
                    route = "mealDetails/{mealType}",
                    arguments = listOf(navArgument("mealType") { type = NavType.StringType })
                ) { backStackEntry ->
                    val mealType = backStackEntry.arguments?.getString("mealType") ?: "Unknown"

                    val context = LocalContext.current // Получение контекста безопасным способом
                    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("jwt", "").orEmpty()

                    // Состояние для списка продуктов
                    val foodItemsState = remember { mutableStateOf<List<FeedAct>>(emptyList()) }

                    // Загрузка данных
                    LaunchedEffect(Unit) {
                        val fetchedFoodItems = dataService.fetchTodayFeedsByType(token, mealType)
                        foodItemsState.value = fetchedFoodItems ?: emptyList() // Обработка null
                    }

                    // Отображение экрана
                    FoodListScreen(
                        foodItems = foodItemsState.value,
                        onAddFood = { navController.navigate("Nutrition/Add/${mealType}") },
                        onCancel = { navController.popBackStack() },
                        onDelete = {id, tokenz -> dataService.deleteFoodData(id, tokenz) }
                    )
                }

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFF5E953B),
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {

        NavigationBarItem(
            icon = { Icon(Screen.PhysicalData.icon, contentDescription = null) },
            label = { Text(Screen.PhysicalData.title) },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "PhysicalData",
            onClick = {
                navController.navigate("PhysicalData")
            }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Nutrition.icon, contentDescription = null) },
            label = { Text(Screen.Nutrition.title) },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "Nutrition",
            onClick = {
                navController.navigate("Nutrition")
            }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Profile.icon, contentDescription = null) },
            label = { Text(Screen.Profile.title) },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "Profile",
            onClick = {
                navController.navigate("Profile")
            }
        )
    }
}

@Composable
fun ProfileScreen(navController: NavController?, context: Context?) {
    val token = LocalContext.current
        .getSharedPreferences("app_prefs",
            Context.MODE_PRIVATE)
        .getString("jwt", "").orEmpty()

    // Состояние для данных пользователя
    var user by remember { mutableStateOf<User?>(null) }

    // Загрузка данных
    LaunchedEffect(Unit) {
        user = dataService.fetchUser(token)
    }

    user?.let {
        ProfileForm(userId = it.id.toString(),
            username = it.username,
            email = it.email,
            {},
            {},
            {
                if (context != null && navController != null) {
                    val sharedPreferences: SharedPreferences =
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("jwt", "").apply()
                    (context as? ComponentActivity)?.runOnUiThread {
                        navController.navigate("auth") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                }
            })
    }?: run {
        LoadingScreen()
    }
}

@Composable
fun FitDataForm(onSubmit: (FitData) -> Unit, onCancel: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(1) }
    var activity by remember { mutableStateOf(0) }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Добавление данных",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        when (currentStep) {
            0 -> {
                TextField(
                    value = weight,
                    onValueChange = { if (it.all { char -> char.isDigit() }) weight = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Введите ваш вес (кг)") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {Icon(imageVector = Icons.Filled.Balance,
                        contentDescription = "Icon")},
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
            1 -> {
                TextField(
                    value = height,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) height = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Введите ваш рост (см)") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {Icon(imageVector = Icons.Filled.CoPresent,
                        contentDescription = "Icon")},
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
            2 -> {
                TextField(
                    value = age,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) age = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Введите ваш возраст (от 14 до 65)") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {Icon(imageVector = Icons.Filled.Face,
                        contentDescription = "Icon")},
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
            3 -> {
                Text("Ваш пол")
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    GenderButton(
                        selected = gender == 1,
                        text = "Мужчина",
                        icon = Icons.Default.Male,
                        onClick = { gender = 1 }
                    )
                    GenderButton(
                        selected = gender == 0,
                        text = "Женщина",
                        icon = Icons.Default.Female,
                        onClick = { gender = 0 }
                    )
                }
            }
            4 -> {
                Text("Ваш уровень физической активности", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                Text("Где 1 - отсутствие активности, 5 - экстремальные нагрузки",
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(10.dp))
                Text("(людям с малоактивным образом жизни рекомендуется выбирать 1)",
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ActivityType.entries.forEach { level ->
                        Button(onClick = { activity = level.lvl },
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                if (activity == level.lvl) Color(0xFF5E953B)
                                else Color.Gray
                            )
                        )
                        {
                                Text((level.lvl+1).toString(), style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (currentStep == 0) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                )
                { Text("Отмена") }
            }
            if (currentStep > 0) {
                Button(
                    onClick = { currentStep -= 1 },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                )
                { Text("Назад") }
            }
            if (currentStep < 4) {
                Button(
                    onClick = { currentStep += 1 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                )
                { Text("Далее") }
            } else {
                Button(
                    onClick = {
                        val fitData = FitData(
                            weight = weight.toFloatOrNull() ?: 0f,
                            height = height.toFloatOrNull() ?: 0f,
                            age = age.toIntOrNull() ?: 0,
                            gender = gender,
                            activity = activity
                        )
                        onSubmit(fitData)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
                ) { Text("Сохранить") }
            }
        }
    }
}
@Composable
fun GenderButton(selected: Boolean, text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            if (selected) Color(0xFF5E953B) else Color.Gray
        ),
        modifier = Modifier
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun PhysicalDataScreen(context: Context?) {
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }

    if (context != null) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "").toString()

        fun refreshUser() {
            coroutineScope.launch {
                user = dataService.fetchUser(token)
                isLoading = false
            }
        }

        LaunchedEffect(Unit) {
            refreshUser()
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            user?.let {
                ParametersScreen(it, context, {refreshUser()}, {})
            } ?: run {
                Text("Ошибка загрузки данных")
            }
        }
    }
}

@Composable
fun NutritionScreen(navController: NavController, context: Context?) {
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    var stats by remember { mutableStateOf<FeedTotalStats?>(null) }

    if (context != null) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "").toString()

        fun refreshUser() {
            coroutineScope.launch {
                user = dataService.fetchUser(token)
                stats = dataService.fetchTotalStats(token)
                isLoading = false
            }
        }

        LaunchedEffect(Unit) {
            refreshUser()
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            user?.let { user_ ->
                stats?.let {
                    PlansScreen(user_, it, context, {refreshUser()}, navController)
                }
            } ?: run {
                Text("Ошибка загрузки данных")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(null, null)
    //FitDataForm {  }
}
