package com.fitfood.clientapp

import FitDataForm
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.fitfood.clientapp.ui.theme.ClientAppTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.fitfood.clientapp.models.FeedAct
import com.fitfood.clientapp.models.FeedTotalStats
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.FitPlan
import com.fitfood.clientapp.models.FoodRequest
import com.fitfood.clientapp.models.Gender
import com.fitfood.clientapp.models.ProductData
import com.fitfood.clientapp.models.Sport.Exercise
import com.fitfood.clientapp.models.Sport.TrainingPlan
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.services.DataService
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.UUID

@Composable
fun TrainingPlanSummaryScreen(plan: TrainingPlan) {
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
            ExercisesList(plan.exercises)
        }
    }
}
@Composable
fun ExercisesList(exercises: List<Exercise>)
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
        Button(
            onClick = { },
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
                    Text("Повторений: ", style = MaterialTheme.typography.bodyLarge)
                    Text("  ${exercise.reps} шт.  ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color(0xFF5E953B), RoundedCornerShape(10.dp)))
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