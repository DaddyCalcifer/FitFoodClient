package com.fitfood.clientapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitfood.clientapp.models.FeedAct
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.FitPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class PlanActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientAppTheme() {

            }
        }
    }
}

private val fitPlan = FitPlan();
private val feedStats = FeedTotalStats();

@Composable
fun NutritionSummaryScreen(plan: FitPlan, stats: FeedTotalStats, navController: NavController) {
    var isFixed by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,8.dp,16.dp,0.dp)
    ) {
        if(!isFixed) {
            plan.dayKcal += stats.burntKcal
            plan.breakfastKcal += stats.burntKcal * 0.275
            plan.lunchKcal += stats.burntKcal * 0.35
            plan.dinnerKcal += stats.burntKcal * 0.275
            plan.otherKcal += stats.burntKcal * 0.1
            isFixed = true
        }
        item {
            Row (verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable
                {

                })
            {
                Text(
                    text = "Сегодня ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Icon(imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    Modifier.size(30.dp))
            }
            SummaryCard(plan, stats)
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(stats.ateKcal > plan.dayKcal * 1.015)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xD3FF0000), RoundedCornerShape(20.dp))
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )
                {
                    val context = LocalContext.current
                    val sharedPreferences: SharedPreferences =
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("jwt", "").toString()

                    Icon(imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        Modifier.size(55.dp), tint = Color(
                        0xFFFFFFFF))
                    Spacer(Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        Text("Перебор калорий!", style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFFFFFFFF))
                        Text("Нужно сжечь ~${(stats.ateKcal - plan.dayKcal).toInt()} ккал", style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFFFFFFF))
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val trainingPlanId = dataService.fetchTrainingPlanByKcal(kcal = (stats.ateKcal - plan.dayKcal).toInt(), token)
                                    trainingPlanId?.let {
                                        navController.navigate("trainingPlan/${it}")
                                        Log.e("TrainingPlan", "План: ${it}")
                                    } ?: run {
                                        Log.e("TrainingPlan", "План не найден")
                                    }
                                }
                                      },
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF)),
                        ) {
                            Text("Подобрать\nтренировку", style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xD3FF0000))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        item {
            MealsSection(plan, stats, navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SummaryCard(plan: FitPlan, stats: FeedTotalStats) {
    val remainingKcal = plan.dayKcal - stats.ateKcal;
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardColors(
            containerColor = Color(0x18315A16),
            contentColor = Color(0xFF1A300c),
            disabledContentColor = Color(0x331A300c),
            disabledContainerColor = Color(0x331A300c))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularCaloriesChart(plan,stats)
                MacronutrientsColumn(plan,stats)
            }
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CalorieInfo("\uD83D\uDE0B ${stats.ateKcal.toInt()}", "Съедено")
                if(remainingKcal.toInt() > 0) {
                    CalorieInfo("\uD83E\uDD0F ${remainingKcal.toInt()}", "Осталось")
                }
                CalorieInfo("\uD83D\uDCA7 ${plan.waterMl.toInt()}", "Вода (мл.)")
            }
        }
    }
}

@Composable
fun CalorieInfo(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MacronutrientsColumn(plan: FitPlan, stats: FeedTotalStats) {
    if(plan.protein_g <=0) plan.protein_g = 1.0;
    if(plan.fat_g <=0) plan.fat_g = 1.0;
    if(plan.carb_g <=0) plan.carb_g = 1.0;
    Column(
        modifier = Modifier
            .padding(6.dp,16.dp,16.dp,6.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        MacronutrientInfo("\uD83E\uDD69\t\tБелки",
            "${stats.ateProtein.toInt()} / ${plan.protein_g.toInt()} г",
            Color(0xFFFFFFFF),
            (stats.ateProtein.toInt() /  plan.protein_g.toInt()).toFloat())
        Spacer(Modifier.height(12.dp))

        MacronutrientInfo("\uD83C\uDF54\t\tЖиры",
            "${stats.ateFat.toInt()} / ${plan.fat_g.toInt()} г",
            Color(0xFFf7b520),
            (stats.ateFat /  plan.fat_g).toFloat())
        Spacer(Modifier.height(12.dp))

        MacronutrientInfo("\uD83C\uDF5E\t\tУглеводы",
            "${stats.ateCarb.toInt()} / ${plan.carb_g.toInt()} г",
            Color(0xFF1ba5cc),
            (stats.ateCarb /  plan.carb_g).toFloat())
    }
}

@Composable
fun MacronutrientInfo(label: String, value: String, color: Color, progress: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.titleSmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .clip(RoundedCornerShape(9.dp))
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxSize(),
                color = color,
                trackColor = Color(0x17488521)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun CircularCaloriesChart(plan: FitPlan, stats: FeedTotalStats) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .size(150.dp)
    ) {
        CircularProgressIndicator(
            progress = {
                (stats.ateKcal / plan.dayKcal).toFloat()
            },
            strokeWidth = 16.dp,
            modifier = Modifier
                .fillMaxSize()
            ,
            trackColor = Color(0x17488521),
            color = Color(0xFF488521)
        )
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "${stats.ateKcal.toInt()}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "/ ${plan.dayKcal.toInt()} ккал",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Съедено",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MealsSection(plan: FitPlan, stats: FeedTotalStats, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MealRow("Завтрак", "${stats.ateBreakfast.toInt()} / ${plan.breakfastKcal.toInt()} Ккал", Icons.Default.Blender, {navController.navigate("mealDetails/Breakfast")})
        Spacer(Modifier.height(15.dp))
        MealRow("Обед", "${stats.ateLunch.toInt()} / ${plan.lunchKcal.toInt()} Ккал", Icons.Default.FoodBank, {navController.navigate("mealDetails/Lunch")})
        Spacer(Modifier.height(15.dp))
        MealRow("Ужин", "${stats.ateDinner.toInt()} / ${plan.dinnerKcal.toInt()} Ккал", Icons.Default.DinnerDining, {navController.navigate("mealDetails/Dinner")})
        Spacer(Modifier.height(15.dp))
        MealRow("Перекусы", "${stats.ateOther.toInt()} / ${plan.otherKcal.toInt()} Ккал", Icons.Default.LocalPizza, {navController.navigate("mealDetails/Other")})
    }
}


@Composable
fun MealRow(meal: String, calories: String, icon: ImageVector, onMealClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color(0x18315A16), RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onMealClick(meal) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(15.dp))
            Icon(imageVector = icon, contentDescription = meal, Modifier.size(40.dp), tint = Color(0xFF1A300c))
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = meal, style = MaterialTheme.typography.titleMedium)
                Text(text = calories, style = MaterialTheme.typography.titleSmall)
            }
        }
        IconButton(onClick = { onMealClick(meal) }, Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Открыть $meal", Modifier.fillMaxSize(0.65f), tint = Color(0xFF1A300c))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NutritionPreview() {
    fitPlan.dayKcal = 3000.0;
    feedStats.ateKcal = 1252.0;
    fitPlan.carb_g = 240.0;
    fitPlan.protein_g = 80.0;
    fitPlan.fat_g = 80.0;
    fitPlan.breakfastKcal = 900.0;
    fitPlan.lunchKcal = 1200.0;
    fitPlan.dinnerKcal = 900.0;
    ClientAppTheme() {
        //NutritionSummaryScreen(fitPlan, feedStats, {});
    }
}
