package com.fitfood.clientapp

import android.graphics.drawable.Icon
import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import com.fitfood.clientapp.models.RegisterUser
import com.fitfood.clientapp.models.StringBox
import com.fitfood.clientapp.models.User
import com.fitfood.clientapp.ui.theme.ClientAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientAppTheme() {
                AuthScreen()
            }
        }
    }
}

var userAuth = User()
var userRegister = RegisterUser()

@Composable
fun AuthScreen() {
    var isLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = if (isLogin) 0 else 1,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (isLogin) 0 else 1]),
                    color = Color(0xFF5E953B)
                )
            }
        ) {
            Tab(
                selected = isLogin,
                onClick = { isLogin = true },
                text = { Text("Авторизация") },
                selectedContentColor = Color(0xFF5E953B)
            )
            Tab(
                selected = !isLogin,
                onClick = { isLogin = false },
                text = { Text("Регистрация") },
                selectedContentColor = Color(0xFF5E953B)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLogin) {
            LoginForm()
        } else {
            RegisterForm()
        }
    }
}

@Composable
fun LoginForm() {
    val login = remember { mutableStateOf(userAuth.Login) }
    val password = remember { mutableStateOf(userAuth.Password) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier =
    Modifier
        .fillMaxSize()
        .background(Color.Transparent)
    ) {
        Column(
            Modifier.fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.original),
                contentDescription = "Логотип",
                contentScale = ContentScale.Fit
            )
            Text("Логин", style = MaterialTheme.typography.titleMedium)
            FitTextBox(login, "Введите логин или e-mail",Icons.Default.Face, KeyboardType.Email)

            Text("Пароль", style = MaterialTheme.typography.titleMedium)
            FitPasswordBox(password)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                    userAuth.Login = login.value
                    userAuth.Password = password.value
                    showDialog = true
                          },
                modifier = Modifier.fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
            ) {
                Text("Войти")
            }
        }
        if (showDialog) {
            MessageBoxOk(
                title = "Вход",
                message = "Добро пожаловать, ${userAuth.toString()}!",
                onDismiss = { showDialog = false }
            )
        }
    }
}
@Composable
fun RegisterForm() {
    var login = remember { mutableStateOf(userRegister.Login) }
    var email = remember { mutableStateOf(userRegister.Email) }
    var password = remember { mutableStateOf(userRegister.Password) }
    var confirmPassword = remember { mutableStateOf(userRegister.ConfirmPassword) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Логин", style = MaterialTheme.typography.titleMedium)
        FitTextBox(login, "Введите ваш псевдним" ,Icons.Default.Face)

        Text("Email", style = MaterialTheme.typography.titleMedium)
        FitTextBox(email, "Введите ваш e-mail" ,Icons.Default.Email, KeyboardType.Email)

        Text("Пароль", style = MaterialTheme.typography.titleMedium)
        FitPasswordBox(password)

        Text("Повторите пароль", style = MaterialTheme.typography.titleMedium)
        FitPasswordBox(confirmPassword)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {  },
                modifier = Modifier.fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
            ) {
                Text("Создать аккаунт")
            }
        }
    }
}

@Composable
fun FitTextBox(content: MutableState<String>,
               label: String,
               icon: ImageVector,
               keyboard: KeyboardType = KeyboardType.Text)
{
    TextField(
        value = content.value,
        onValueChange = { content.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {Icon(imageVector = icon,
            contentDescription = "Icon")},
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboard)
    )
}
@Composable
fun FitPasswordBox(password: MutableState<String>) {
    var hidePass by remember { mutableStateOf(true) }
    TextField(
        value = password.value,
        onValueChange = { password.value = it},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        label = { Text("Введите пароль") },
        visualTransformation = if (hidePass) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { hidePass = !hidePass }) {
                if (hidePass) {
                    Icon(Icons.Filled.Visibility, contentDescription = "Показать пароль")
                } else {
                    Icon(Icons.Filled.VisibilityOff, contentDescription = "Скрыть пароль")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {Icon(imageVector = Icons.Default.Lock,
            contentDescription = "Password icon")},
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}
@Composable
fun FitButtonImage(text: String,
                   img: ImageVector,
                   onClick: () -> Unit,
                   background: Color = Color.DarkGray,
                   textColor: Color = Color.White)
{
    Button(onClick = onClick,
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(background)
    ) { Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Icon (imageVector = img, contentDescription = "")
        Text(text, color = textColor)
        Icon (imageVector = img, contentDescription = "", tint = Color.Transparent)
    } }
}
@Composable
fun FitButtonImage(text: String,
                   img: Painter,
                   onClick: () -> Unit,
                   background: Color = Color.DarkGray,
                   textColor: Color = Color.White)
{
    Button(onClick = onClick,
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(background)
    ) { Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = img, contentDescription = "")
        Text(text, color = textColor)
        Text("   ")
    } }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    ClientAppTheme() {
        AuthScreen();
    }
}

@Composable
fun MessageBoxOk(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(Color(0xFF5E953B))
            ) {
                Text("OK")
            }
        }
    )
}
