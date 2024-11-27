package com.example.classcash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.*

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000L) // Delay for 2 seconds
        navController.navigate(Routes.login)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFBFCFE), Color(0xFFADEBB3))
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Image(
                painter = painterResource(id = R.drawable.classlogo),
                contentDescription = "logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ClassCash",
                fontSize = 40.sp,
                fontFamily = FontFamily(Font(R.font.irishgrover_regular, FontWeight.Bold))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Make your funds accounted automatically",
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.montagaregular, FontWeight.Normal))
            )

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "LOADING...",
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Normal))
            )
    }
}
