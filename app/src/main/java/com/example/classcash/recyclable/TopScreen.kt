package com.example.classcash.recyclable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import kotlinx.coroutines.CoroutineScope


@Composable

fun TopScreenB(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel,
    drawerState: DrawerState, // Accept the DrawerState
    scope: CoroutineScope
) {
    val currentClassName by topScreenViewModel.currentClassName.collectAsState()
    val isEditing by topScreenViewModel.isEditing.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFADEBB3)) // Set background for the entire top layer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = "≡",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF50404D)
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = currentClassName,
                    onValueChange = { newValue ->
                        topScreenViewModel.onClassNameChange(newValue)
                    },
                    label = { Text(text = "Classroom Name") },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(70.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { topScreenViewModel.onEditClassName() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the class name as text
                    Text(
                        text = currentClassName.ifEmpty { "Classroom Name" },
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF50404D),
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // First icon (Notifications icon)
                    IconButton(
                        onClick = { navController.navigate(Routes.notification) },
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Notifications",
                            tint = Color(0xFF50404D)
                        )
                    }

                    // Second icon (Another action icon)
                    IconButton(
                        onClick = { navController.navigate(Routes.profile)},
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Action",
                            tint = Color(0xFF50404D)
                        )
                    }
                }
            }
        }
    }
}
