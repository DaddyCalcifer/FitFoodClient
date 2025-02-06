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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.services.DataService
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.UUID


@Composable
fun FoodListScreen(
    foodItems: List<FeedAct>, // Список продуктов
    onAddFood: () -> Unit,      // Обработчик добавления нового продукта
    onCancel: () -> Unit,
    onDelete: (id: String, token: String) -> Unit
) {
    val token = LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("jwt", "").toString()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Употребленные продукты",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f) // Занимает оставшееся место
        ) {
            items(foodItems) { food ->
                val isDeleted = remember { mutableStateOf(false) }
                if(!isDeleted.value)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        Arrangement.SpaceAround)
                    {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Масса: ${food.mass.toInt()} г",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "\uD83E\uDD69Б: ${
                                    kotlin.math.round(
                                        food.protein100 * (food.mass / 100)
                                    ).toInt()}г\t\uD83C\uDF54Ж: ${
                                    kotlin.math.round(
                                        food.fat100 * (food.mass / 100)
                                    ).toInt()}г\t\uD83C\uDF5EУ: ${kotlin.math.round(food.carb100 * (food.mass / 100)).toInt()}г\n⚡ Ккал: ${
                                    kotlin.math.round(
                                        food.kcal100 * (food.mass / 100)
                                    ).toInt()
                                }",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onDelete(food.id.toString(), token)
                                    isDeleted.value = true
                                }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(70.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0x265E953B))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = onAddFood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text("Добавить продукт")
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Transparent
                    )
                }
            }
        }
    }
}
@Composable
fun AddProductForm(
    onAddProduct: (FoodRequest, String, String) -> Unit,
    onBack: () -> Unit,
    token: String,
    type: String
) {
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var barcodeValue by remember { mutableStateOf("") }
    var product by remember { mutableStateOf<ProductData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val name = remember { mutableStateOf("") }
    val mass = remember { mutableStateOf("") }
    val kcal = remember { mutableStateOf("") }
    val fat = remember { mutableStateOf("") }
    val protein = remember { mutableStateOf("") }
    val carb = remember { mutableStateOf("") }

    // Загружаем продукт при изменении barcodeValue
    val fetchedProduct by produceState<ProductData?>(initialValue = null, barcodeValue) {
        if (barcodeValue.isNotEmpty()) {
            Log.d("Scanner", "Запрос к API для штрих-кода: $barcodeValue")
            isLoading = true
            errorMessage = null
            value = try {
                val fetched = dataService.fetchProduct(barcodeValue)
                if (fetched == null) {
                    Log.e("Scanner", "Продукт не найден")
                    throw Exception("Продукт не найден")
                }
                Log.d("Scanner", "Продукт загружен: ${fetched.name}")
                fetched
            } catch (e: Exception) {
                errorMessage = "Ошибка: ${e.message}"
                Log.e("Scanner", "Ошибка при загрузке: ${e.message}")
                null
            } finally {
                isLoading = false
                showBarcodeScanner = false
            }
        }
    }
    var isSearching = remember { mutableStateOf(false) }
    // Заполняем форму данными, если продукт найден
    LaunchedEffect(fetchedProduct) {
        fetchedProduct?.let {
            Log.d("Scanner", "Заполняем форму данными продукта: ${it.name}")
            name.value = it.name
            mass.value = it.weight.toInt().toString()
            kcal.value = it.calories.toInt().toString()
            fat.value = it.fat.toInt().toString()
            carb.value = it.carbohydrates.toInt().toString()
            protein.value = it.protein.toInt().toString()
        }
    }

    if (showBarcodeScanner) {
        BarcodeScannerScreen { barcode ->
            Log.d("Scanner", "Отсканирован штрих-код: $barcode")
            barcodeValue = barcode
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Добавить продукт",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Поля для ручного ввода с использованием FitTextBox
                Row(Modifier.fillMaxWidth()) {
                    val nameBoxLength = if(name.value.length > 1) {
                        if (isSearching.value) 0.76f else 0.88f
                    } else 1f
                    Box(Modifier.fillMaxWidth(nameBoxLength)) {
                        FitTextBox(
                            content = name,
                            label = "Название продукта",
                            icon = Icons.Default.FoodBank
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(alignment = Alignment.CenterVertically)
                            .clickable {
                                isSearching.value = true
                            }
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(alignment = Alignment.CenterVertically)
                            .clickable {
                                isSearching.value = false
                            }
                    )
                }
                if(isSearching.value && name.value.length > 1) {
                    Spacer(Modifier.height(4.dp))
                    SearchResults(name.value,
                        name = name,
                        fat = fat,
                        carb = carb,
                        protein = protein,
                        kcal = kcal,
                        weight = mass,
                        isSearch = isSearching)
                    Spacer(Modifier.height(4.dp))
                }
                FitTextBox(
                    content = mass,
                    label = "Масса (г)",
                    icon = Icons.Default.Scale,
                    keyboard = KeyboardType.Number
                )
                FitTextBox(
                    content = kcal,
                    label = "Калории (ккал) / 100 г",
                    icon = Icons.Default.LocalFireDepartment,
                    keyboard = KeyboardType.Number
                )
                FitTextBox(
                    content = fat,
                    label = "Жиры (г) / 100 г",
                    icon = Icons.Default.Opacity,
                    keyboard = KeyboardType.Number
                )
                FitTextBox(
                    content = protein,
                    label = "Белки (г) / 100 г",
                    icon = Icons.Default.FitnessCenter,
                    keyboard = KeyboardType.Number
                )
                FitTextBox(
                    content = carb,
                    label = "Углеводы (г) / 100 г",
                    icon = Icons.Default.WaterDrop,
                    keyboard = KeyboardType.Number
                )
                Button(
                    onClick = { showBarcodeScanner = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
                ) {
                    Text("Сканировать штрих-код", style = MaterialTheme.typography.headlineMedium)
                }
            }
            // Кнопка добавления
            Button(
                onClick = {
                    val massValue = mass.value.toDoubleOrNull() ?: 0.0
                    val kcalValue = kcal.value.toDoubleOrNull() ?: 0.0
                    val fatValue = fat.value.toDoubleOrNull() ?: 0.0
                    val proteinValue = protein.value.toDoubleOrNull() ?: 0.0
                    val carbValue = carb.value.toDoubleOrNull() ?: 0.0

                    onAddProduct(
                        FoodRequest(
                            name.value,
                            massValue,
                            kcalValue,
                            fatValue,
                            proteinValue,
                            carbValue
                        ),
                        token,
                        type
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B)),
            ) {
                Text("Добавить", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
@Composable
fun SearchResults(search: String,
                  name: MutableState<String>,
                  kcal: MutableState<String>,
                  fat: MutableState<String>,
                  protein: MutableState<String>,
                  carb: MutableState<String>,
                  weight: MutableState<String>,
                  isSearch: MutableState<Boolean>) {
    val prods = remember { mutableStateListOf<ProductData>() } // Оставляем ссылку неизменной
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(search) {
        isLoading.value = true
        val results = dataService.fetchSearchResults(search) ?: emptyList() // Загружаем данные
        prods.clear() // Очищаем список перед добавлением новых элементов
        prods.addAll(results) // Добавляем элементы
        isLoading.value = false
    }

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .background(Color(0xFFDFE0EA), RoundedCornerShape(20.dp))
            .padding(vertical = 8.dp)
    ) {
        if (isLoading.value) {
            LoadingScreen()
        } else {
            LazyColumn {
                items(prods) { prod ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            prod.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3F424A),
                            modifier = Modifier.clickable{
                                isSearch.value = false
                                name.value = prod.name
                                fat.value = prod.fat.toString()
                                carb.value = prod.carbohydrates.toString()
                                protein.value = prod.protein.toString()
                                kcal.value = prod.calories.toString()
                                weight.value = "100.0"
                            }
                        )
                        Spacer(
                            Modifier
                                .fillMaxWidth(0.88f)
                                .height(2.dp)
                                .background(Color(0x373F424A), RoundedCornerShape(1.dp))
                        )
                    }
                }
            }
        }
    }
}
