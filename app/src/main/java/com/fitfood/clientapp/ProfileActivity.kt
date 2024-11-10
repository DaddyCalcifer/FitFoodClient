package com.fitfood.clientapp

import FitDataForm
import android.os.Bundle
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Male
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.fitfood.clientapp.models.FitData
import com.fitfood.clientapp.models.FitPlan
import com.fitfood.clientapp.models.Gender
import com.fitfood.clientapp.models.User

@Composable
fun ParametersScreen(user: User) {
    var isAddingData by remember { mutableStateOf(false) }
    if(!isAddingData) {
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

            LazyColumn {
                items(user.datas) { data ->
                    UserDataItem(data)
                }
                item {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                        onClick = {isAddingData = true})
                    {
                        Row(modifier = Modifier.
                            fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween)
                        {
                            Icon(imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp))
                            Text("Добавить")
                            Icon(imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.Transparent)
                        }
                    }
                }
            }
        }
    }
    else
    {
        FitDataForm(onCancel = {isAddingData = false}, onSubmit = {})
    }
}

@Composable
fun UserDataItem(data: FitData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0x18315A16), shape = RoundedCornerShape(20.dp))
            .clickable { /* Обработать нажатие */ }
            .padding(16.dp),
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
            Text(text = "Вес: ${data.weight} кг", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Рост: ${data.height} см", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Возраст: ${data.age} лет", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Активность: ${data.activity}", style = MaterialTheme.typography.bodyLarge)
        }
        //Column() {
        //    Icon(
        //        imageVector = Icons.Filled.Delete,
        //        contentDescription = null,
        //        modifier = Modifier.size(50.dp),
        //        tint = Color.Red
        //    )
        //}
    }
}
