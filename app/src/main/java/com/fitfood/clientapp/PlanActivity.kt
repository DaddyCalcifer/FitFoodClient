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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FoodBank
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
@Composable
fun NutritionSummaryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Сегодня",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Сводка калорий и диаграмма
        SummaryCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Приемы пищи
        MealsSection()

        Spacer(modifier = Modifier.height(16.dp))

        // Нижняя навигационная панель
        // BottomNavigationBar()
    }
}

@Composable
fun SummaryCard() {
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
                CircularCaloriesChart(remainingCalories = 826, 2000f)
                MacronutrientsColumn()
            }
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CalorieInfo("\uD83D\uDE0B1,291", "Съедено")
                CalorieInfo("\uD83E\uDD0F826", "Осталось")
                CalorieInfo("\uD83D\uDD25244", "Сожжено")
            }
        }
    }
}

@Composable
fun CalorieInfo(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MacronutrientsColumn() {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        MacronutrientInfo("\uD83E\uDD69\t\tБелки", "206 / 258 г", Color(0xFF488521))
        Spacer(Modifier.height(15.dp))
        MacronutrientInfo("\uD83C\uDF54\t\tЖиры", "35 / 103 г", Color(0xFFf7b520))
        Spacer(Modifier.height(15.dp))
        MacronutrientInfo("\uD83C\uDF5E\t\tУглеводы", "32 / 68 г", Color(0xFF1ba5cc))
    }
}

@Composable
fun MacronutrientInfo(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            progress = {
                0.5f // Для примера, нужно будет динамически вычислять
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
fun CircularCaloriesChart(remainingCalories: Int, totalCalories: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .size(170.dp)
    ) {
        CircularProgressIndicator(
            progress = {
                remainingCalories / totalCalories
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
                text = "$remainingCalories",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Осталось",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun MealsSection() {
    Column(modifier = Modifier
        .fillMaxWidth()) {
        MealRow("Завтрак", "56 / 635 Ккал", Icons.Default.Face)
        Spacer(Modifier.height(15.dp))
        MealRow("Обед", "856 / 847 Ккал", Icons.Default.FoodBank)
        Spacer(Modifier.height(15.dp))
        MealRow("Ужин", "379 / 529 Ккал", Icons.Default.DinnerDining)
        Spacer(Modifier.height(15.dp))
        MealRow("Перекусы", "0 / 106 Ккал", Icons.Default.LocalPizza)
    }
}

@Composable
fun MealRow(meal: String, calories: String, icon: ImageVector) {
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
        IconButton(onClick = { /* TODO: Add meal */ },
            Modifier.size(60.dp)) {
            Icon(imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Добавить $meal",
                Modifier.fillMaxSize(0.65f),
                tint = Color(0xFF1A300c))
        }
    }
}

/*@Composable
fun BottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Blue
    ) {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Diary") },
            selected = true,
            onClick = { /* TODO: Diary screen */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Book, contentDescription = "Recipes") },
            selected = false,
            onClick = { /* TODO: Recipes screen */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Fasting") },
            selected = false,
            onClick = { /* TODO: Fasting screen */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { /* TODO: Profile screen */ }
        )
    }
}*/

@Preview(showBackground = true)
@Composable
fun NutritionPreview() {
    ClientAppTheme() {
        NutritionSummaryScreen();
    }
}
