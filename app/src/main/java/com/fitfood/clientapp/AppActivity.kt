import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.fitfood.clientapp.NutritionSummaryScreen
import com.fitfood.clientapp.fitPlan
import com.fitfood.clientapp.models.FoodPlan

sealed class Screen(val title: String, val icon: ImageVector) {
    object PhysicalData : Screen("Параметры", Icons.Filled.FitnessCenter)
    object Nutrition : Screen("План питания", Icons.Filled.Restaurant)
    object Profile : Screen("Профиль", Icons.Filled.AccountCircle)
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Profile) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedScreen) { screen ->
                selectedScreen = screen
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreen) {
                is Screen.Profile -> ProfileScreen()
                is Screen.PhysicalData -> PhysicalDataScreen()
                is Screen.Nutrition -> NutritionScreen()
                else -> {}
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF5E953B),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Screen.Nutrition.icon, contentDescription = null) },
            label = { Text(Screen.Nutrition.title) },
            selected = selectedScreen is Screen.Nutrition,
            onClick = { onScreenSelected(Screen.Nutrition) }
        )
        NavigationBarItem(
            icon = { Icon(Screen.PhysicalData.icon, contentDescription = null) },
            label = { Text(Screen.PhysicalData.title) },
            selected = selectedScreen is Screen.PhysicalData,
            onClick = { onScreenSelected(Screen.PhysicalData) }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Profile.icon, contentDescription = null) },
            label = { Text(Screen.Profile.title) },
            selected = selectedScreen is Screen.Profile,
            onClick = { onScreenSelected(Screen.Profile) }
        )
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile Screen")
    }
}

@Composable
fun PhysicalDataScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Physical Data Screen")
    }
}

@Composable
fun NutritionScreen() {
    val fitPlan = FoodPlan();
    com.fitfood.clientapp.fitPlan.DayKcal = 3000.0;
    com.fitfood.clientapp.fitPlan.AteKcal = 1252.0;
    com.fitfood.clientapp.fitPlan.Carb_g = 240.0;
    com.fitfood.clientapp.fitPlan.Protein_g = 80.0;
    com.fitfood.clientapp.fitPlan.Fat_g = 80.0;
    com.fitfood.clientapp.fitPlan.BreakfastKcal = 900.0;
    com.fitfood.clientapp.fitPlan.LunchKcal = 1200.0;
    com.fitfood.clientapp.fitPlan.DinnerKcal = 900.0;

    NutritionSummaryScreen(fitPlan);
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
