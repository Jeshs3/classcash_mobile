@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.classcash.bottombars

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.event.AddEventViewModel
import com.example.classcash.viewmodels.event.BudgetViewModel
import com.example.classcash.viewmodels.event.EventSaveStatus
import com.example.classcash.viewmodels.payment.PaymentViewModel
import kotlinx.datetime.*
import java.util.Calendar


@Composable
fun EventScreen(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel,
    addEventViewModel: AddEventViewModel,
    budgetViewModel : BudgetViewModel,
    paymentViewModel : PaymentViewModel
) {

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFCFE)), // Background color
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Calendar Section
        item {
            CalendarScreen(navController, addEventViewModel,paymentViewModel, budgetViewModel)
        }

        // Add Event Section
        item {
            AddEventSection(
                addEventViewModel,
                onEventSaved = {
                    Toast.makeText(context, "Event saved successfully! See the details on the shaded dates", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun CalendarScreen(
    navController : NavController,
    addEventViewModel: AddEventViewModel,
    paymentViewModel: PaymentViewModel,
    budgetViewModel : BudgetViewModel
) {
    val startDate by addEventViewModel.startDate.observeAsState(
        initial = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    val endDate by addEventViewModel.endDate.observeAsState(
        initial = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    val (currentYear, currentMonth) = remember { addEventViewModel.getCurrentMonth() }
    val daysForMonth = remember { addEventViewModel.getDaysForMonth(currentYear, currentMonth) }
    val monthEnum = Month.values().firstOrNull { it.ordinal == currentMonth - 1 }

    // State for dialog visibility and selected date
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEventId by remember { mutableStateOf(-1) }

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

        // Calendar Grid with Dates
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(daysForMonth.size) { index ->
                val date = daysForMonth[index]
                val isShaded = date != null && startDate != null && endDate != null &&
                        date in startDate..endDate

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isShaded) Color.Blue else Color.Gray
                        )
                        .border(1.dp, Color.LightGray)
                        .clickable(enabled = isShaded) {
                            selectedDate = date
                            showEventDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date?.dayOfMonth?.toString() ?: "",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Show Event Details Dialog
    if (showEventDialog) {
        EventDetailsDialog(
            eventId = selectedEventId,
            budgetViewModel,
            paymentViewModel,
            addEventViewModel,
            navController,
            onDismiss = { showEventDialog = false }
        )
    }
}



@Composable
fun AddEventSection(
    addEventViewModel: AddEventViewModel,
    onEventSaved: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showDescriptionDialog by remember { mutableStateOf(false) }

    val saveStatus by addEventViewModel.eventSaveStatus.observeAsState(EventSaveStatus.Success)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Event Name Input with Trailing Icons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Event Name",
                        color = Color.Gray.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    TextField(
                        value = eventName,
                        onValueChange = {
                            eventName = it
                            addEventViewModel.updateEventDetails(mapOf("eventName" to it))
                        },
                        placeholder = {
                            Text(
                                "Enter Event Name",
                                fontFamily = FontFamily(Font(R.font.inter))
                                )
                                      },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
                IconButton(onClick = { showDescriptionDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_text),
                        contentDescription = "Edit Description",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { showDateRangePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.date_range),
                        contentDescription = "Select Date Range",
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Event Button
        Button(
            onClick = {
                addEventViewModel.addEvent()
            },
            colors = ButtonDefaults.buttonColors(Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Event")
        }

        // Save Status Handling
        when (saveStatus) {
            is EventSaveStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            is EventSaveStatus.Success -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Event saved successfully!", Toast.LENGTH_SHORT).show()
                    onEventSaved()
                }
            }
            is EventSaveStatus.Error -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, (saveStatus as EventSaveStatus.Error).message, Toast.LENGTH_LONG).show()
                }
            }
            else -> {}
        }
    }

    // Description Dialog
    if (showDescriptionDialog) {
        AlertDialog(
            onDismissRequest = { showDescriptionDialog = false },
            title = { Text("Edit Description") },
            text = {
                TextField(
                    value = eventDescription,
                    onValueChange = {
                        eventDescription = it
                        addEventViewModel.updateEventDetails(mapOf("eventDescription" to it))
                    },
                    placeholder = { Text("Enter Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { showDescriptionDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Date Range Picker Dialog
    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDatesSelected = { selectedStartDate, selectedEndDate ->
                startDate = selectedStartDate
                endDate = selectedEndDate
                addEventViewModel.updateEventDetails(
                    mapOf(
                        "startDate" to selectedStartDate.toString(),
                        "endDate" to selectedEndDate.toString()
                    )
                )
                showDateRangePicker = false
            },
            onDismissRequest = { showDateRangePicker = false }
        )
    }
}


@Composable
fun DatePickerDialog(
    initialDate: kotlinx.datetime.LocalDate,
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(initialDate.year, initialDate.monthNumber - 1, initialDate.dayOfMonth)
    }

    val datePickerState = rememberDatePickerState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
                            Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        }
                        selectedDate?.let { onDateSelected(it) }
                        onDismissRequest()
                    },
                        colors = ButtonDefaults.buttonColors(Color.Green)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun DateRangePickerDialog(
    onDatesSelected: (startDate: kotlinx.datetime.LocalDate, endDate: kotlinx.datetime.LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(kotlinx.datetime.LocalDate(2024, 1, 1)) }
    var endDate by remember { mutableStateOf(kotlinx.datetime.LocalDate(2024, 1, 2)) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Select Date Range") },
        text = {
            Column {
                Button(
                    onClick = { showStartDatePicker = true }
                ) {
                    Text(
                        "Pick Start Date: ${startDate.toString()}",
                        fontFamily = FontFamily(Font(R.font.inter))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showEndDatePicker = true }) {
                    Text(
                        "Pick End Date: ${endDate.toString()}",
                        fontFamily = FontFamily(Font(R.font.inter))
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDatesSelected(startDate, endDate)
                onDismissRequest()
            },
                colors = ButtonDefaults.buttonColors(Color(0xFFADEBB3))
            ) {
                Text(
                    "Confirm",
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() },
                colors = ButtonDefaults.buttonColors(Color.Red),
            ) {
                Text(
                    "Cancel",
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }
        }
    )

    // Show Start DatePicker Dialog
    if (showStartDatePicker) {
        DatePickerDialog(
            initialDate = startDate,
            onDateSelected = { selectedDate ->
                startDate = selectedDate
                showStartDatePicker = false
            },
            onDismissRequest = { showStartDatePicker = false }
        )
    }

    // Show End DatePicker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            initialDate = endDate,
            onDateSelected = { selectedDate ->
                endDate = selectedDate
                showEndDatePicker = false
            },
            onDismissRequest = { showEndDatePicker = false }
        )
    }
}

@Composable
fun ExpenseDialog(
    expenseList: MutableList<Double>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Add Expenses", fontFamily = FontFamily(Font(R.font.inter)))
                Spacer(modifier = Modifier.height(8.dp))

                val newExpense = remember { mutableStateOf("") }

                TextField(
                    value = newExpense.value,
                    onValueChange = { newExpense.value = it },
                    label = { Text("Enter Expense") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        newExpense.value.toDoubleOrNull()?.let { expense ->
                            expenseList.add(expense)
                            newExpense.value = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add to List")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Expenses: ${expenseList.joinToString(", ")}")

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun EventDetailsDialog(
    eventId: Int,
    budgetViewModel: BudgetViewModel,
    paymentViewModel: PaymentViewModel,
    addEventViewModel: AddEventViewModel,
    navController : NavController,
    onDismiss: () -> Unit
) {
    val classBalance by paymentViewModel.classBalance.observeAsState(0.0)
    val event = addEventViewModel.event.observeAsState().value

    // Fetch event details when dialog is displayed
    LaunchedEffect(eventId) {
        addEventViewModel.fetchEventDetails(eventId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Event Details")
        },
        text = {
            Column {
                Text(text = "Name: ${event?.eventName ?: "Untitled Event"}")
                Text(text = "From: ${event?.startDate ?: "N/A"} To: ${event?.endDate ?: "N/A"}")
                Text(text = "Description: ${event?.eventDescription ?: "No Description"}")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                event?.let { budgetViewModel.computeBudget(it, classBalance) }
                navController.navigate(Routes.recommend)
            }) {
                Text("Generate Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                event?.let { addEventViewModel.removeEvent(it.eventId) }
                onDismiss()
            }) {
                Text("Delete Event")
            }
        }
    )
}


