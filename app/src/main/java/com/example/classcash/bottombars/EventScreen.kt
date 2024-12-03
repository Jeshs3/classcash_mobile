package com.example.classcash.bottombars

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import android.app.DatePickerDialog
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.datetime.LocalDate
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.event.AddEventViewModel
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.event.CalendarViewModel
import kotlinx.datetime.*
import java.util.Calendar


@Composable
fun EventScreen(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel = viewModel(),
    addEventViewModel: AddEventViewModel = viewModel()
) {
    val calendarViewModel: CalendarViewModel = viewModel()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFCFE)), // Background color
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Calendar section
        item {
            CalendarScreen(calendarViewModel)
        }

        // Add Event section
        item {
            AddEventSection(eventId = null, addEventViewModel)
        }
    }
}


@Composable
fun CalendarScreen(calendarViewModel: CalendarViewModel = CalendarViewModel()) {
    val (currentYear, currentMonth) = remember { calendarViewModel.getCurrentMonth() }
    val daysForMonth = remember { calendarViewModel.getDaysForMonth(currentYear, currentMonth) }
    val monthEnum = Month.values().firstOrNull { it.ordinal == currentMonth - 1 }

    // State to track start and end dates
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        // Month and Year Header
        Text(
            text = "${monthEnum?.name ?: "Unknown"} $currentYear",
            fontFamily = FontFamily(Font(R.font.montserrat)),
            fontSize = 24.sp,
            color = Color.Blue
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Day Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(daysForMonth.size) { index ->
                val date = daysForMonth[index]

                // Use local variables to avoid smart cast issues
                val start = startDate
                val end = endDate

                val isInRange = date != null && start != null && end != null &&
                        (date == start || date == end || (date > start && date < end))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isInRange) Color(0xFFE8F5E9) else Color.Transparent
                        )
                        .border(1.dp, Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date?.dayOfMonth?.toString() ?: "",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}




@Composable
fun AddEventSection(
    eventId: Int?,
    addEventViewModel: AddEventViewModel
) {
    var eventName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var isEditable by remember { mutableStateOf(true) } // Toggle between editable and display modes
    var showAddEventIcon by remember { mutableStateOf(false) } // To toggle the visibility of Add Event section

    val inactivityTimeout = 3000L
    var lastInputTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(eventName, startDate, endDate) {
        if (!isEditable) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (now - lastInputTimestamp >= inactivityTimeout) {
            isEditable = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showAddEventIcon) {
            IconButton(
                onClick = { showAddEventIcon = false }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "Add Event",
                    tint = Color.Blue
                )
            }
        } else {
            Text(
                text = "Add Event",
                fontFamily = FontFamily(Font(R.font.montserrat)),
                modifier = Modifier.padding(10.dp)
            )

            if (isEditable) {
                // Editable mode: Allow user input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = Color.Blue)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = eventName,
                        onValueChange = {
                            eventName = it
                            lastInputTimestamp = System.currentTimeMillis()
                        },
                        label = { Text("Event Name", fontFamily = FontFamily(Font(R.font.inter))) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .weight(1f)
                            .padding(end = 8.dp),
                        singleLine = true
                    )

                    IconButton(onClick = { showDatePickerDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.date_range),
                            contentDescription = "Calendar Icon",
                            tint = Color.Blue
                        )
                    }

                    IconButton(
                        onClick = {
                            if (eventName.isNotBlank() && startDate != null && endDate != null) {
                                isEditable = false
                                showAddEventIcon = true
                                addEventViewModel.addEvent(eventName, startDate!!, endDate!!)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.generate_budget),
                            contentDescription = "Generate Budget",
                            tint = Color.Blue
                        )
                    }
                }
            } else {
                // Display mode: Show event details and allow swipe-to-delete
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                        .border(BorderStroke(2.dp, Color.Blue), RoundedCornerShape(12.dp))
                        .background(Color(0xFFFBFCFE), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 100) { // Swipe right threshold
                                    eventName = ""
                                    startDate = null
                                    endDate = null
                                    eventId?.let { addEventViewModel.removeEvent(it) }
                                }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column (
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = eventName,
                                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                            )
                            Text(
                                text = "From ${startDate.toString()} to ${endDate.toString()}",
                                fontFamily = FontFamily(Font(R.font.montserrat)),
                                color = Color.Gray
                            )
                            IconButton(
                                onClick = {
                                    if (eventName.isNotBlank() && startDate != null && endDate != null) {
                                        isEditable = false
                                        showAddEventIcon = true
                                        addEventViewModel.addEvent(eventName, startDate!!, endDate!!)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.generate_budget),
                                    contentDescription = "Generate Budget",
                                    tint = Color.Blue
                                )
                            }

                            Text(
                                text = "Swipe → to delete",
                                fontSize = 8.sp,
                                fontFamily = FontFamily(Font(R.font.inter)),
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }

        // Date Picker Popup
        if (showDatePickerDialog) {
            AlertDialog(
                onDismissRequest = { showDatePickerDialog = false },
                title = { Text("Select Date Range", textAlign = TextAlign.Center) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        DatePickerButton(
                            label = "Start Date",
                            selectedDate = startDate,
                            buttonColors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFADEBB3),
                                contentColor = Color.White
                            ),
                            onDateSelected = { selectedDate -> startDate = selectedDate }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DatePickerButton(
                            label = "End Date",
                            selectedDate = endDate,
                            buttonColors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFADEBB3),
                                contentColor = Color.White
                            ),
                            onDateSelected = { selectedDate -> endDate = selectedDate }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDatePickerDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}


@Composable
fun DatePickerButton(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors() // Add this parameter
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label for the button
        Text(
            label,
            fontFamily = FontFamily(Font(R.font.montserrat))
        )

        // Button with customizable colors
        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = LocalDate(year, month + 1, dayOfMonth)
                        onDateSelected(selectedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            colors = buttonColors // Use the parameter
        ) {
            Text(selectedDate?.toString() ?: "Select Date")
        }
    }
}
