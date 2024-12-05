package com.example.classcash.bottombars

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.collection.CollectionViewModel
import java.util.Calendar


@Composable
fun FundSetting(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel,
    collectionViewModel: CollectionViewModel
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isResetting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var isEditingDuration by remember { mutableStateOf(true) }
    var isEditingDailyFund by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf("") }
    var dailyFund by remember { mutableStateOf("") }
    val errorMessage = (collectionViewModel.message.observeAsState(null).value as? CollectionViewModel.MessageType.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .background(Color(0xFFFBFCFE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .background(Color(0xFFFBFCFE)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Collection",
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Editable fields with proper visibility
            EditableField(
                isEditing = isEditingDuration,
                value = duration,
                label = "Duration: e.g. 6 months",
                onValueChange = {
                    duration = it.filter { char -> char.isDigit() }
                    collectionViewModel.updateDuration(duration)
                    collectionViewModel.saveCollection()
                },
                onEditToggle = { isEditingDuration = it },
                displayValue = if (duration.isNotEmpty()) "Duration: $duration MONTHS" else "Duration: Not Set"
            )

            EditableField(
                isEditing = isEditingDailyFund,
                value = dailyFund,
                label = "Daily Fund per student",
                onValueChange = {
                    dailyFund = it.filter { char -> char.isDigit() }
                    collectionViewModel.updateDailyFund(dailyFund)
                    collectionViewModel.saveCollection()
                },
                onEditToggle = { isEditingDailyFund = it },
                displayValue = if (dailyFund.isNotEmpty()) "Daily Fund: ₱$dailyFund.00 per student" else "Daily Fund: Not Set"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Collection Information
            CollectionBox(collectionViewModel)

            Spacer(modifier = Modifier.weight(1f)) // Pushes buttons to the bottom

            // Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    enabled = !isResetting
                ) {
                    Text(
                        text = "Reset Settings",
                        fontSize = 11.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }

                Button(
                    onClick = {
                        val collection = collectionViewModel.collection.value
                        if (collection?.monthName.isNullOrEmpty() || collection?.activeDays.isNullOrEmpty() || collection?.dailyFund == 0.0) {
                            Toast.makeText(
                                context,
                                "Please complete all fields before saving.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showSaveDialog = true
                            collectionViewModel.saveCollection()
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.Green),
                    enabled = !isSaving
                ) {
                    Text(
                        text = "Save Settings",
                        fontSize = 11.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }
            }
        }

        // Error Message
        errorMessage?.let {
            ErrorMessage(it) { collectionViewModel.clearMessage() }
        }

        // Dialogs
        if (showResetDialog) {
            ConfirmationDialog(
                title = "Reset Settings",
                message = "Are you sure you want to reset the collection?",
                onConfirm = {
                    collectionViewModel.collection.value?.collectionId?.let { collectionId ->
                        isResetting = true
                        collectionViewModel.deleteCollection(collectionId)
                        isResetting = false
                    }
                    showResetDialog = false
                },
                onDismiss = { showResetDialog = false }
            )
        }

        if (showSaveDialog) {
            InformationDialog(
                title = "Save Settings",
                message = "Collection settings saved successfully.",
                onDismiss = { showSaveDialog = false }
            )
        }
    }
}

@Composable
fun EditableField(
    isEditing: Boolean,
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onEditToggle: (Boolean) -> Unit,
    displayValue: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
            .background(Color(0xFFADEBB3))
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = label,
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                trailingIcon = {
                    IconButton(onClick = { onEditToggle(false) }) {
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
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayValue,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    color = Color.Black
                )
                IconButton(onClick = { onEditToggle(true) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_text),
                        contentDescription = "Edit Data",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Yes") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("No") } }
    )
}

@Composable
fun InformationDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}

@Composable
fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
            .background(Color(0xFFFFE5E5), shape = RoundedCornerShape(8.dp))
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
                text = message,
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    LaunchedEffect(message) {
        kotlinx.coroutines.delay(2000)
        onDismiss()
    }
}




@Composable
fun MonthSelectionBox(
    collectionViewModel: CollectionViewModel
) {

    var selectedMonth by remember { mutableStateOf<String?>(null) } // Track selected month
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    var expanded by remember { mutableStateOf(false) }



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
                    text = selectedMonth ?: "Select a month",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    color = if (selectedMonth != null) Color.Black else Color.Gray
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
                                selectedMonth = month
                                selectedDays = collectionViewModel.getActiveDaysForMonth(month) ?: emptyList()
                                collectionViewModel.updateSelectedMonth(month, selectedDays)
                                collectionViewModel.saveCollection()
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
    collectionViewModel: CollectionViewModel
) {
    val collection by collectionViewModel.collection.observeAsState()
    val monthDetails by collectionViewModel.monthDetails.observeAsState(emptyMap())
    val selectedMonth = collection?.monthName
    val activeDays = monthDetails[selectedMonth] ?: emptyList() // Retrieve active days for the selected month

    Box(
        modifier = Modifier
            .height(280.dp)
            .background(Color(0xFFADEBB3), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            // Display the MonthSelectionBox (for selecting a month)
            MonthSelectionBox(collectionViewModel)

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedMonth == null) {
                // No month selected
                Text(
                    text = "No month selected. Please select a month.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Render MonthDetails regardless of whether activeDays is empty or not
                MonthDetails(
                    collectionViewModel,
                    selectedMonth = selectedMonth,
                    activeDays = activeDays,
                    onAddActiveDay = { selectedDay ->
                        collectionViewModel.editActiveDays(selectedMonth, selectedDay)
                    }
                )

                if (activeDays.isEmpty()) {
                    // Display additional message if no active days are selected
                    Text(
                        text = "No active days selected for this month. Please select or add days.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MonthDetails(
    collectionViewModel: CollectionViewModel,
    selectedMonth: String?,
    activeDays: List<String>,
    onAddActiveDay: (List<String>) -> Unit
) {
    // If no month is selected, show a message and return
    if (selectedMonth == null) {
        Text(text = "No month details available.", color = Color.Red, modifier = Modifier.fillMaxWidth())
        return
    }

    // Calculate monthly fund and active days count
    val dailyFund = collectionViewModel.collection.value?.dailyFund ?: 0.0
    val activeDaysCount = activeDays.size
    val monthlyFund = activeDaysCount * dailyFund

    // State for the multi-date picker dialog
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDates = remember { mutableStateListOf<String>().apply { addAll(activeDays) } }

    // Show the multi-date picker dialog if needed
    if (showDatePicker) {
        MultiDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            selectedDates = selectedDates,
            onDatesSelected = { newDates ->
                selectedDates.clear()
                selectedDates.addAll(newDates)
                onAddActiveDay(newDates) // Pass selected dates to the parent
                showDatePicker = false
            }
        )
    }

    // Main content of the MonthDetails composable
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month, Amount, and Active Days
        Text(
            text = "Month: $selectedMonth",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Estimated Amount: ${String.format("%.2f", monthlyFund)}",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.Blue
        )
        Text(
            text = "Active Days: $activeDaysCount",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            // Button to view active days
            TextButton(onClick = { showDatePicker = true }) {
                Text(
                    "View Active Days",
                    color = Color.Blue,
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }

            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendar",
                    tint = Color.Black,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}


@Composable
fun MultiDatePickerDialog(
    onDismissRequest: () -> Unit,
    selectedDates: List<String>,
    onDatesSelected: (List<String>) -> Unit
) {
    // Keep track of selected dates in a mutable state
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysList = (1..daysInMonth).toList()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1

    val pickedDates = remember { mutableStateListOf<String>().apply { addAll(selectedDates) } }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pick Dates",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7), // 7 days in a week
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Enough for multiple rows
                ) {
                    items(daysList){ day ->
                        val dateString = "$day/$month/$year"
                        val isPicked = pickedDates.contains(dateString)

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isPicked) Color.LightGray else Color.Transparent)
                                .clickable {
                                    if (isPicked) {
                                        pickedDates.remove(dateString) // Unpick date
                                    } else {
                                        pickedDates.add(dateString) // Pick date
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isPicked) Color.Black else Color.Gray
                            )
                        }
                    }
                }
                    Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onDismissRequest) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            onDatesSelected(pickedDates.toList())
                            onDismissRequest()
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}



