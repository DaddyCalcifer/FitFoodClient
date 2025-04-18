package com.fitfood.clientapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.fitfood.clientapp.ui.theme.ClientAppTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.fitfood.clientapp.models.Sport.Exercise
import com.fitfood.clientapp.models.Sport.TrainingPlan
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun TrainingPlanSummaryScreen(plan: TrainingPlan, navController: NavController) {
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
                    text = "${plan.name} ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    Modifier.size(30.dp)
                )
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text(plan.description,
                style = MaterialTheme.typography.titleLarge)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ExercisesList(plan.exercises, plan.id, navController)
        }
    }
}
@Composable
fun ExercisesList(exercises: List<Exercise>, trainPlanId: String?, navController: NavController)
{
    var openedExercises = remember { mutableStateOf(false) }
    Column()
    {
        Row(modifier = Modifier.clickable{
            openedExercises.value = !openedExercises.value
        }) {
            if(openedExercises.value){
                Icon(imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    Modifier.size(32.dp))
            }
            else {
                Icon(imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    Modifier.size(32.dp))
            }
            Text("Список упражнений", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(10.dp))
        if(openedExercises.value) {
            for(e in exercises) {
                ExerciseCard(e)
                Spacer(Modifier.height(16.dp))
            }
        }
        Spacer(Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "").toString()

        Button(
            onClick = {
                coroutineScope.launch {
                    val training = dataService.sendCreateTraining(trainPlanId, token)
                    training?.let {
                        // Обработка объекта Training
                        Log.d("Training", "Тренировка началась: ${it.id}")

                        // Пример действия: переход на экран тренировки или обновление UI
                        navController.navigate("training/${it.id}")
                    } ?: run {
                        Toast.makeText(context, "Ошибка при создании тренировки", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(70.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
        ) {
            Text("Начать тренировку", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(16.dp))
    }
}
@Composable
fun ExerciseCard(exercise : Exercise)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp)))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        )
        {
            Column {
                Text(exercise.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Row{
                    Text("Сжигает калорий: ", style = MaterialTheme.typography.bodyLarge)
                    Text("  ${exercise.totalCaloriesLoss.toInt()} ⚡ ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp)))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row{
                    Text("Подходов: ", style = MaterialTheme.typography.bodyLarge)
                    Text("  ${exercise.sets} шт.  ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp)))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row{
                    if(!exercise.repsIsSeconds) {
                        Text("Повторений: ", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "  ${exercise.reps} шт.  ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier
                                .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp))
                        )
                    }
                    else {
                        Text("Подходы по: ", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "  ${exercise.reps} сек.  ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier
                                .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(exercise.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    ClientAppTheme {
        //TrainingPlanSummaryScreen()
    }
}