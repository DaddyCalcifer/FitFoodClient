package com.fitfood.clientapp

import android.widget.Space
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitfood.clientapp.models.Sport.Exercise
import com.fitfood.clientapp.models.Sport.ExerciseProgress
import com.fitfood.clientapp.models.Sport.Set
import com.fitfood.clientapp.models.Sport.Training
import com.fitfood.clientapp.models.Sport.TrainingPlan
import com.fitfood.clientapp.ui.theme.ClientAppTheme

@Composable
fun TrainingSummaryScreen(train: Training, token: String) {
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
                    text = "${train.trainingPlan.name} ",
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
            Text(train.trainingPlan.description,
                style = MaterialTheme.typography.titleLarge)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ExercisesProgressList(train.exercises, token)
        }
    }
}

@Composable
fun ExercisesProgressList(exercises: List<ExerciseProgress>, token: String)
{
    Column()
    {
        Text("Список упражнений", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        for (e in exercises) {
            ExerciseProgressCard(e, token)
            Spacer(Modifier.height(16.dp))

        }
    }
}

@Composable
fun ExerciseProgressCard(exProgress : ExerciseProgress, token: String)
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
                Text(exProgress.exercise.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(exProgress.exercise.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Row{
                    Text("Сжигает калорий: ", style = MaterialTheme.typography.bodyLarge)
                    Text("  ${exProgress.exercise.totalCaloriesLoss.toInt()} ⚡ ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp)))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column{
                    Text("Подходы: ", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    for(set in exProgress.sets)
                    {
                        SetCard(set, token, exProgress.exercise.repsIsSeconds)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun SetCard(set: Set, token: String="", repIsSecs: Boolean=false)
{
    var setStarted = remember { mutableStateOf(false) }
    var fullSized = remember { mutableStateOf(false) }
    var isCompleted = remember { mutableStateOf(false) }
    var reps = remember { mutableStateOf(set.reps.toString()) }
    var weight = remember { mutableStateOf(set.weight.toString()) }

    var headerColor = if(set.isCompleted || fullSized.value) Color(0xFF3C5015) else Color(0xFFB1B2B2)

    Column(modifier = Modifier.fillMaxWidth()
        .background(color = Color(0x564B4B4B), RoundedCornerShape(20.dp))) {
        Column(modifier = Modifier.fillMaxWidth()
            .background(headerColor,
            RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                Spacer(Modifier.height(5.dp))
                Row(Modifier.fillMaxWidth()
                    .clickable{
                        fullSized.value = !fullSized.value
                    },
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        Spacer(Modifier.width(10.dp))
                        Box(
                            Modifier.size(26.dp)
                                .background(color = Color.White, RoundedCornerShape(13.dp)),
                        )
                        {
                            if (set.isCompleted)
                                Row(Modifier.padding(4.dp)) {
                                    Box(
                                        Modifier.size(18.dp)
                                            .background(
                                                color = Color(0xFF3C5015),
                                                RoundedCornerShape(9.dp)
                                            )
                                    )
                                }
                        }
                    }
                    Text(
                        "Подход ${set.setNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Row() {
                        val iconArrow = if(fullSized.value)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown
                        Icon(imageVector = iconArrow,
                            contentDescription = null,
                            Modifier.size(26.dp),
                            tint = Color.White)
                        Spacer(Modifier.width(10.dp))
                    }
                }
                Spacer(Modifier.height(5.dp))
        }
        if(fullSized.value) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (!setStarted.value && !set.isCompleted) {
                    Column(modifier = Modifier.padding(10.dp))
                    {
                        Text(
                            "Как только будете готовы, начинайте подход.",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(73.dp))
                        Button(
                            onClick = { setStarted.value = true },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF3C5015)),
                        ) {
                            Text("Начать подход", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(10.dp)) {
                        val isLoading = remember { mutableStateOf(false) }
                        val showErrorDialog = remember { mutableStateOf(false) }
                        val repsText = if(repIsSecs) "Время (сек.)" else "Повторения"
                        ValuePicker(reps, repsText, hasButtons = !set.isCompleted)
                        Spacer(Modifier.height(16.dp))
                        ValuePicker(weight, "Вес (кг.)", hasButtons = !set.isCompleted)
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
                                    Text("Не удалось завершить подход. Пожалуйста, попробуйте снова.")
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

                        if (!isCompleted.value && !set.isCompleted) {
                            Button(
                                onClick = {
                                    // Запускаем загрузку
                                    isLoading.value = true

                                    // Вызываем метод завершения подхода
                                    dataService.completeSet(set.id,
                                        reps.value.toInt(),
                                        weight.value.toDouble(),
                                        token)
                                    {
                                        isSuccessful ->
                                        isLoading.value = false // Завершаем загрузку

                                        if (isSuccessful) {
                                            isCompleted.value = true
                                            set.isCompleted = true
                                            fullSized.value = true
                                        } else {
                                            // Ошибка: показываем всплывающее окно
                                            showErrorDialog.value = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF3C5015)),
                                enabled = !isLoading.value // Кнопка неактивна во время загрузки
                            ) {
                                if (isLoading.value) {
                                    // Показываем индикатор загрузки
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text("Завершить подход", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ValuePicker(value: MutableState<String>, label: String="", hasButtons: Boolean = true)
{
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()) {
        if(label.isNotEmpty())
        {
            Row {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(10.dp))
            }
        }
        Row {
            if(hasButtons)
            Button(
                onClick = {
                    if(value.value.toDouble() > 0){
                    value.value =
                        if(!value.value.contains("."))
                            (value.value.toInt() - 1).toString()
                        else
                            (value.value.toDouble() - 0.5).toString()
                }},
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
            ) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            TextField(
                value = value.value,
                onValueChange = {
                    if(it.toInt() in 1..600)
                    value.value = it
                                },
                modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 8.dp)
                    .height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                readOnly = true
            )
            if(hasButtons)
            Button(
                onClick = {
                    if(value.value.toDouble() < 600.0){
                    value.value =
                    if(!value.value.contains("."))
                        (value.value.toInt() + 1).toString()
                    else
                        (value.value.toDouble() + 0.5).toString() }},
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    ClientAppTheme {
        SetCard(Set(
            id = "id",
            setNumber = 1,
            reps = 10,
            weight = 10.0,
            exerciseProgressId = "ddsdds",
            exerciseProgress = null,
            isCompleted = false), "")
    }
}