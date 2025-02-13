package com.fitfood.clientapp

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.fitfood.clientapp.ui.theme.ClientAppTheme

@Composable
fun ExercisesProgressList(exercises: List<ExerciseProgress>)
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
                ExerciseProgressCard(e)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ExerciseProgressCard(exProgress : ExerciseProgress)
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
                    for(set in exProgress.sets)
                    {

                    }
                }
            }
        }
    }
}
@Composable
fun SetCard(set: Set)
{
    var setStarted = remember { mutableStateOf(true) }
    var reps = remember { mutableStateOf(set.reps.toString()) }
    var weight = remember { mutableStateOf(set.weight.toString()) }

    Column(modifier = Modifier.fillMaxWidth()
        .background(color = Color.Gray, RoundedCornerShape(20.dp))) {
        Column(modifier = Modifier.fillMaxWidth()
            .background(Color(0xFF1A300c),
            RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(5.dp))
            Text(
                "Подход ${set.setNumber}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(Modifier.height(5.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            if(!setStarted.value) {
                Column(modifier = Modifier.padding(10.dp))
                {
                    Text(
                        "Как только будете готовы, начинайте подход.",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = { setStarted.value = true },
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                    ) {
                        Text("Начать подход", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                Column(modifier = Modifier.padding(10.dp))
                {
                    ValuePicker(reps, "Повторения")
                    Spacer(Modifier.height(16.dp))
                    ValuePicker(weight, "Вес")
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                    ) {
                        Text("Завершить подход", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
@Composable
fun ValuePicker(value: MutableState<String>, label: String="")
{
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()) {
        if(label.isNotEmpty())
        {
            Row {
                Text(label, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
            }
        }
        Row {
            Button(
                onClick = { value.value = (value.value.toInt() - 1).toString() },
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
                onValueChange = { value.value = it },
                modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 8.dp)
                    .height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = { value.value = (value.value.toInt() + 1).toString() },
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
        //SetCard()
    }
}