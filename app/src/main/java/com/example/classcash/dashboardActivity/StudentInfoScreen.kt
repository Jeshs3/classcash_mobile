package com.example.classcash.dashboardActivity

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.dashboard.DashboardViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentInfoScreen(
    navController: NavController,
    dashboardViewModel : DashboardViewModel,
    studentId: Int
) {

    val student = dashboardViewModel.getStudentById(studentId)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .background(Color(0xFFFBFCFE))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFADEBB3))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Name: ${student?.studentName ?: "No name found"}",
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f) // Pushes the percentage box to the right
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0%", // Replace with the actual progress
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp)) // Adds space between the box and text below
                    Text(
                        text = "Total Percent Semestral",
                        fontSize = 8.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }
            }
        }

        // End of the first box
        Spacer(modifier = Modifier.height(10.dp))

        // Transaction Details Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFADEBB3))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search Transaction"
                        )
                    }

                    Text(
                        text = "Transaction",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        modifier = Modifier.weight(1f) // Pushes calendar icon to the right
                    )

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Transaction Dates"
                        )
                    }
                    IconButton(onClick = {
                        dashboardViewModel.downloadReport(context, studentId) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Report saved to $message", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_import),
                            contentDescription = "Transaction Download"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFADEBB3))
                ) {
                    // LazyColumn for transactions
                    LazyColumn(
                        modifier = Modifier.height(180.dp)
                    ) {
                        items(10) { index ->
                            Text(
                                text = "Transaction $index",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Divider below LazyColumn and above Amount Box
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(bottom = 60.dp)
                    )

                    // Amount Box at Bottom-Right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .width(80.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(Color.Blue),
                        contentAlignment = Alignment.Center
                    ) {
                        val formatter = DecimalFormat("₱0.00")
                        val formattedBalance = student?.currentBal?.let { formatter.format(it) } ?: "₱0.00"

                        Text(
                            text = formattedBalance,
                            color = Color(0xFFFBFCFE),
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Analytics Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFADEBB3))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Circular Progress Bar with Percentage Inside
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val progress = 0.75f // Replace with actual progress (0.0f to 1.0f)
                        val strokeWidth = 8.dp.toPx()

                        drawCircle(
                            color = Color.LightGray,
                            style = Stroke(width = strokeWidth)
                        )
                        drawArc(
                            color = Color.Green,
                            startAngle = -90f,
                            sweepAngle = 360 * progress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                    Text(
                        text = "100%", // "${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Analytics Details
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Month Name", // Replace with the selected month
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                    Text(
                        text = "Amount Collected: P0.00", // Replace with actual amount
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter))
                    )
                    Text(
                        text = "Number of Days: ", // Replace with actual number of active days
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter))
                    )
                    Text(
                        text = "Date Completed: ", // Replace with the date the target amount is reached
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter))
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptComposable(
    studentName: String,
    transactionDetails: String,
    totalAmount: String,
    transactionDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Placeholder for a logo or header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.LightGray) // Replace with an actual image if available
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Logo/Organization Name",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Receipt title
        Text(
            text = "Receipt",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Student Name
        Text(
            text = "Student Name: $studentName",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Transaction Details header
        Text(
            text = "Transaction Details:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Transaction Details content with dynamic overflow handling
        Text(
            text = transactionDetails,
            fontSize = 14.sp,
            maxLines = 5, // Adjustable for longer details
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Total Amount
        Text(
            text = "Total Amount: $totalAmount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32), // Highlighted in green for emphasis
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Date of Transaction
        Text(
            text = "Date: $transactionDate",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thank You message
        Text(
            text = "Thank you!",
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


