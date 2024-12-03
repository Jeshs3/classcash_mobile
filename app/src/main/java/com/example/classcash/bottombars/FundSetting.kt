package com.example.classcash.bottombars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.collection.CollectionViewModel
import com.example.classcash.viewmodels.collection.Month
import java.util.Calendar



@Composable
fun FundSetting(
    navController:NavController,
    topScreenViewModel : TopScreenViewModel,
    collectionViewModel: CollectionViewModel
) {

    var isEditingDuration by remember { mutableStateOf(true) }
    var isEditingDailyFund by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf("") }
    var dailyFund by remember { mutableStateOf("") }
    val errorMessage by collectionViewModel.errorMessage.observeAsState("")



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .background(Color(0xFFFBFCFE)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Screen label
        Text(
            text = "Set Collection",
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        //Duration Field (editable or label based on state)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                )
                .background(Color(0xFFADEBB3))
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isEditingDuration) {
                TextField(
                    value = duration,
                    onValueChange = { newValue ->
                        val filteredInput = newValue.filter { it.isDigit() }

                        // If the filtered input is valid (not empty), update the ViewModel
                        if (filteredInput.isNotEmpty()) {
                            collectionViewModel.updateDuration(filteredInput)
                            duration = filteredInput // Update duration with the valid numeric input
                        } else {
                            // If input is empty or invalid, you can show error message or handle it accordingly
                            collectionViewModel.updateDuration("") // Or handle error message in VM
                            duration = "" // Clear the input to prevent invalid display
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(
                            text = "Duration: e.g. 6 months",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat)),
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            isEditingDuration = false
                            collectionViewModel.updateDuration(duration)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Save",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                )
            } else {
                // Display formatted label text after check is clicked
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (duration.isNotEmpty()) "Duration: $duration MONTHS" else "Duration: Not Set",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        color = Color.Black
                    )

                    IconButton(onClick = {isEditingDuration = true}) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_text),
                            contentDescription = "Edit Data",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        // Daily Fund Field (editable or label based on state)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                )
                .background(Color(0xFFADEBB3))
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isEditingDailyFund) {
                TextField(
                    value = dailyFund,
                    onValueChange = { newValue ->
                        // Filter out non-numeric characters
                        val filteredInput = newValue.filter { it.isDigit() }

                        // If the filtered input is valid (not empty), update the ViewModel
                        if (filteredInput.isNotEmpty()) {
                            collectionViewModel.updateDailyFund(filteredInput)
                            dailyFund = filteredInput // Update dailyFund with the valid numeric input
                        } else {
                            // If input is empty or invalid, handle error or reset value
                            collectionViewModel.updateDailyFund("") // Optionally handle error message in ViewModel
                            dailyFund = "" // Clear the input to prevent invalid display
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(
                            text = "Daily Fund per student",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            isEditingDailyFund = false
                            collectionViewModel.updateDailyFund(dailyFund)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Save",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                )
            } else {
                // Display formatted label text after check is clicked
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text =  if (duration.isNotEmpty()) "Daily Fund: ₱$dailyFund.00 per student" else "Daily Fund: Not Set",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        color = Color.Black
                    )

                    IconButton(onClick = { isEditingDailyFund = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_text),
                            contentDescription = "Edit Data",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
            Spacer(modifier = Modifier.height(30.dp))

            //Collection Information
            CollectionBox(collectionViewModel, navController)
    }

    if (errorMessage.isNotEmpty()) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
                .background(Color(0xFFFFE5E5), shape = RoundedCornerShape(8.dp)) // Light red background
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = "Error Icon",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp).padding(end = 8.dp)
                )
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat)), // Optional font customization
                )
            }
        }

        LaunchedEffect(key1 = errorMessage) {
            kotlinx.coroutines.delay(2000) // Delay for 2 seconds
            collectionViewModel.clearMessage()
        }
    }
}

@Composable
fun MonthSelectionBox(
    collectionViewModel: CollectionViewModel
) {
    val selectedMonth by collectionViewModel.selectedMonthName.observeAsState("")
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedMonth) {
        if (selectedMonth.isNotEmpty()) {
            collectionViewModel.fetchMonthDetails(selectedMonth)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .background(Color(0xFFFBFCFE), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown Menu for selecting a month
            Box(modifier = Modifier.clickable { expanded = true }) {
                Text(
                    text = if (selectedMonth.isNotEmpty()) selectedMonth else "Select a month",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    color = if (selectedMonth.isNotEmpty()) Color.Black else Color.Gray
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = month,
                                    fontFamily = FontFamily(Font(R.font.montserrat))
                                )
                            },
                            onClick = {
                                expanded = false
                                collectionViewModel.selectMonth(month) // Update ViewModel's selected month
                            }
                        )
                    }
                }
            }

            // Label beside the dropdown
            Text(
                text = "Setup the date",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inter, FontWeight.ExtraBold))
            )
        }
    }
}

@Composable
fun CollectionBox(
    collectionViewModel: CollectionViewModel,
    navController: NavController
) {
    // Observe the selected month and month details
    val monthDetails by collectionViewModel.monthDetailsLiveData.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFADEBB3), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            // Use the refactored MonthSelectionBox
            MonthSelectionBox(collectionViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            if (monthDetails.isEmpty()) {
                // Display Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No details added. Please select a month.",
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Display Month Details in LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(monthDetails) { month ->
                        MonthDetailsRow(
                            month = month,
                            onAddActiveDay = { updatedDays ->
                                collectionViewModel.updateActiveDays(month.monthName, updatedDays)
                            }
                        )
                    }
                }
            }
        }

        // Static Row for buttons at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Align the buttons at the bottom
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { collectionViewModel.deleteCollection() },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(
                    text = "Reset Settings",
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }

            Button(
                onClick = {
                    collectionViewModel.saveCollection()
                    navController.navigate(Routes.dashboard) {
                        popUpTo(Routes.fund) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(
                    text = "Save Settings",
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }
        }
    }
}


@Composable
fun MonthDetailsRow(
    month: Month?,
    onAddActiveDay: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    if (month == null) {
        Text(text = "No month details available.", color = Color.Red)
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Month: ${month.monthName}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Estimated Amount: ${String.format("%.2f", month.monthlyFund)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Blue
            )
            Text(
                text = "Active Days: ${month.activeDays.size}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        IconButton(onClick = {
            android.app.DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                    val updatedActiveDays = month.activeDays.toMutableList().apply {
                        add(selectedDate)
                    }
                    onAddActiveDay(updatedActiveDays)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "Calendar",
                tint = Color.Black
            )
        }
    }
}
