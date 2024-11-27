package com.example.classcash.dashboardActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.classcash.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentBox(navController: NavController) {

    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    var amount by remember { mutableStateOf("") }
    var showError by remember {mutableStateOf(false)}
    //var studentName by remember {mutableStateOf("")}

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFFADEBB3), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row with title and close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grizzly",
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
                        /*val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble != null) {
                            paymentPresenter.onPayButtonClicked(studentName, amountDouble)
                            navController.popBackStack()
                        } else {
                            showError = true
                        }*/
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
            }
        }
    }

    if(showError){
        Text(
            text = "Please enter valid amount",
            color = Color.Red,
            fontFamily = FontFamily(Font(R.font.inter))
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
