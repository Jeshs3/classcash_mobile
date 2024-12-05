package com.example.classcash.dashboardActivity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.dashboard.DashboardViewModel
import com.example.classcash.viewmodels.payment.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentBox(
    navController: NavController,
    paymentViewModel: PaymentViewModel,
    dashboardViewModel : DashboardViewModel,
    studentId: Int
) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    var amount by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var studentData by remember { mutableStateOf<Student?>(null) }


    // Fetch student data when studentId changes
    LaunchedEffect(studentId) {
        Log.d("PaymentBox", "Fetching data for studentId: $studentId")
        paymentViewModel.fetchStudentData(studentId) { fetchedStudent ->
            if (fetchedStudent != null) {
                Log.d("PaymentBox", "Student fetched: ${fetchedStudent.studentName}")
            } else {
                Log.e("PaymentBox", "No data found for studentId: $studentId")
            }
            studentData = fetchedStudent
            Log.d("PaymentBox", "Updated studentData: $studentData")
            isLoading = false
        }
    }



    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFFADEBB3), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            if (isLoading) {
                // Display a loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Blue
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header row with title and close icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Log.d("PaymentBox", "Student ID: ${studentData?.studentId}")
                        Log.d("PaymentBox", "Student Name: ${studentData?.studentName}")
                        Text(
                            text = "ID: ${studentData?.studentId ?: "No ID found"}\n" +
                                    "Name: ${studentData?.studentName.takeIf { !it.isNullOrEmpty() } ?: "No name found"}",
                            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Close"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display date
                    Text(
                        text = date,
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount input field
                    Text(
                        text = "Input Balance:",
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Medium)),
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { input ->
                            // Validate input to allow only numbers
                            if (input.all { it.isDigit() || it == '.' }) {
                                amount = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = {
                            Text(
                                text = "Please enter amount",
                                fontFamily = FontFamily(Font(R.font.inter)),
                                color = Color.Red.copy(alpha = 0.8f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enter button
                    Button(
                        onClick = {
                            if (amount.isNotBlank()) {
                                isLoading = true
                                paymentViewModel.processPayment(studentId, amount) { success ->
                                    Log.d("PaymentBox", "Payment success: $success") // Debugging line
                                    if (success) {
                                        isLoading = false
                                        dashboardViewModel.refreshStudentObjects()
                                        //paymentViewModel.fetchClassBalance()
                                        showSuccessDialog = true
                                    } else {
                                        showError = true
                                    }
                                }
                            } else {
                                showError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(50))
                    ) {
                        Text(
                            text = "Enter",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }

                    // Error message display
                    if (showError) {
                        Text(
                            text = "Please enter a valid amount",
                            color = Color.Red,
                            fontFamily = FontFamily(Font(R.font.montserrat)),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {

        AlertDialog(
            onDismissRequest = {showSuccessDialog = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            },
            text = {
                Text(
                    text = "ID: ${studentData?.studentId}\n" +
                            "Student Name: ${studentData?.studentName}\n" +
                            "Payment Added: ₱$amount on $date",
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    fontSize = 14.sp
                )
            },
            title = { Text("Payment Confirmation") }
        )
    }
}





@Composable
fun WithdrawBox(navController: NavController){


    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    var amount by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("")}

    Dialog(onDismissRequest = { navController.popBackStack()}) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFFADEBB3), RoundedCornerShape(8.dp))
                .padding(16.dp)  // Add padding to the dialog content
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header row with title and close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Withdrawal",
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Text(
                    text = date,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Field for withdrawal purpose
                Text(
                    text = "What is the purpose?",
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Medium)),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    placeholder = {
                        Text(
                            text = "What is the purpose",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = Color.Red.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                //Field for amount
                Text(
                    text = "Input Balance:",
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Medium)),
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = {
                        Text(
                            text = "Please enter amount",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = Color.Red.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Enter button
                Button(
                    onClick = { /* Handle payment action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                ) {
                    Text(
                        text = "Enter",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }
            }
        }
    }

}

@Composable
fun ExternalFundBox(navController: NavController){

    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    var amount by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("")}

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFFADEBB3), RoundedCornerShape(8.dp))
                .padding(16.dp)  // Add padding to the dialog content
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header row with title and close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add External Funds",
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { navController.popBackStack()}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Text(
                    text = date,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount input field

                Text(
                    text = "Fund Source:",
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Medium)),
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    placeholder = {
                        Text(
                            text = "Enter source e.g Booth",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = Color.Red.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Input Balance:",
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Medium)),
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = {
                        Text(
                            text = "Please enter amount",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = Color.Red.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Enter button
                Button(
                    onClick = { /* Handle payment action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                ) {
                    Text(
                        text = "Enter",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }
            }
        }
    }

}
