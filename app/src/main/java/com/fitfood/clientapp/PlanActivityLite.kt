package com.fitfood.clientapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.fitfood.clientapp.ui.theme.ClientAppTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.fitfood.clientapp.models.FitPlan
import com.fitfood.clientapp.models.FitPlanLite


private val fitPlan = FitPlanLite();

@Composable
fun NutritionSummaryScreenLite(plan: FitPlanLite?) {
    if(plan == null) return
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        item {
            Text(
                text = "План питания на сегодня",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Сводка калорий и диаграмма
            SummaryCardLite(plan)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Приемы пищи
            MealsSectionLite(plan)

            Spacer(modifier = Modifier.height(16.dp))
        }
        // Нижняя навигационная панель
        // BottomNavigationBar()
    }
}

@Composable
fun SummaryCardLite(plan: FitPlanLite) {
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
                CircularCaloriesChartLite(plan)
                MacronutrientsColumnLite(plan)
            }
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CalorieInfo("\uD83D\uDCA7 ${plan.waterMl.toInt()} мл.", "Воды")
            }
        }
    }
}

@Composable
fun CalorieInfoLite(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MacronutrientsColumnLite(plan: FitPlanLite) {
    if(plan.protein_g <=0) plan.protein_g = 1.0;
    if(plan.fat_g <=0) plan.fat_g = 1.0;
    if(plan.carb_g <=0) plan.carb_g = 1.0;
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        MacronutrientInfo("\uD83E\uDD69\t\tБелки",
            "${plan.protein_g.toInt()} г",
            Color(0xFF488521),
            1.0f)
        Spacer(Modifier.height(15.dp))

        MacronutrientInfo("\uD83C\uDF54\t\tЖиры",
            "${plan.fat_g.toInt()} г",
            Color(0xFFf7b520),
            1.0f)
        Spacer(Modifier.height(15.dp))

        MacronutrientInfo("\uD83C\uDF5E\t\tУглеводы",
            "${plan.carb_g.toInt()} г",
            Color(0xFF1ba5cc),
           1.0f)
    }
}

@Composable
fun MacronutrientInfoLite(label: String, value: String, color: Color, progress: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            progress = {
                progress
            },
            modifier = Modifier
                .height(16.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = color,
            trackColor = Color.Transparent
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CircularCaloriesChartLite(plan: FitPlanLite) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .size(170.dp)
    ) {
        CircularProgressIndicator(
            progress = {
                1.0f
            },
            strokeWidth = 16.dp,
            modifier = Modifier
                .fillMaxSize()
            ,
            trackColor = Color.Transparent,
            color = Color(0xFF488521)
        )
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "${plan.dayKcal.toInt()} ⚡",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Ккал",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun MealsSectionLite(plan: FitPlanLite) {
    Column(modifier = Modifier
        .fillMaxWidth()) {
        MealRowLite("Завтрак", "${plan.breakfastKcal.toInt()} Ккал", Icons.Default.Blender)
        Spacer(Modifier.height(15.dp))
        MealRowLite("Обед", "${plan.lunchKcal.toInt()} Ккал", Icons.Default.FoodBank)
        Spacer(Modifier.height(15.dp))
        MealRowLite("Ужин", "${plan.dinnerKcal.toInt()} Ккал", Icons.Default.DinnerDining)
        Spacer(Modifier.height(15.dp))
        MealRowLite("Перекусы", "${plan.otherKcal.toInt()} Ккал", Icons.Default.LocalPizza)
    }
}

@Composable
fun MealRowLite(meal: String, calories: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .background(Color(0x18315A16), RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(15.dp))
            Icon(imageVector = icon, contentDescription = meal, Modifier.size(40.dp), tint = Color(0xFF1A300c))
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = meal, style = MaterialTheme.typography.headlineSmall)
                Text(text = calories, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionPreviewLite() {
    fitPlan.dayKcal = 3000.0;
    fitPlan.carb_g = 240.0;
    fitPlan.protein_g = 80.0;
    fitPlan.fat_g = 80.0;
    fitPlan.breakfastKcal = 900.0;
    fitPlan.lunchKcal = 1200.0;
    fitPlan.dinnerKcal = 900.0;
    ClientAppTheme() {
        NutritionSummaryScreenLite(fitPlan);
    }
}
