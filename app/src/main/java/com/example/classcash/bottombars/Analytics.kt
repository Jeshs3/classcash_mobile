package com.example.classcash.bottombars

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.classcash.recyclable.TopScreenB
import com.example.classcash.viewmodels.TopScreenViewModel

@Composable
fun Analytics(
    navController : NavController,
    topScreenViewModel: TopScreenViewModel
    ){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Text(
            text = "You can see analytics here!"
        )
    }
}

