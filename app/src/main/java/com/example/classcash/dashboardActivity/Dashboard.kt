package com.example.classcash.dashboardActivity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.addstudent.AddStudentViewModel
import com.example.classcash.viewmodels.collection.CollectionViewModel
import com.example.classcash.viewmodels.dashboard.DashboardViewModel
import java.text.DecimalFormat


@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel : DashboardViewModel,
    topScreenViewModel: TopScreenViewModel,
    addStudentViewModel: AddStudentViewModel,
    collectionViewModel : CollectionViewModel
) {

    val classSize by addStudentViewModel.classSize.collectAsState()

    // Column layout for the screen content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .padding(top = 10.dp)
            .background(Color(0xFFFBFCFE)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

            BalanceBox(navController, collectionViewModel)

            HorizontalDivider(
                color = Color(0xFFADEBB3),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(onClick = { navController.navigate(Routes.studentadd) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search Student",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Text(
                text = "Class Size: $classSize students",
                fontFamily = FontFamily(Font(R.font.montserrat)),
                fontSize = 16.sp
            )

            IconButton(onClick = { navController.navigate(Routes.studentadd) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personadd),
                    contentDescription = "Show Fund Details",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

        }
            Spacer(modifier = Modifier.height(10.dp))

            StudentsList(
                navController,
                dashboardViewModel
            )

        Spacer(modifier = Modifier.height(140.dp))

        AddStudentsButton(navController)
        }
}


@Composable
fun BalanceBox(
    navController: NavController,
    collectionViewModel : CollectionViewModel
) {

    val collectionSettings by collectionViewModel.collectionSettings.observeAsState()

    val duration = collectionSettings?.duration ?: 0
    val dailyFund = collectionSettings?.dailyFund ?: 0.0

    val selectedMonth by collectionViewModel.selectedMonthDetails.observeAsState()

    // Extract details from the selected month
    val monthName = selectedMonth?.monthName ?: "No Month Selected"
    val activeDaysCount = selectedMonth?.activeDays?.size ?: 0
    val monthlyFund = selectedMonth?.monthlyFund ?: 0.0

    val balance by remember { mutableDoubleStateOf(0.0) }
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clickable { navController.navigate(Routes.trview) }
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .background(Color.White)
                .width(200.dp)
                .padding(vertical = 16.dp, horizontal = 10.dp)
                .wrapContentSize(Alignment.Center),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "P ${String.format("%.2f", balance)}",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                    color = Color.Blue
                )
                Text(
                    text = "Total Cash In",
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Normal)),
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        //Display the snippet for the fund
        IconButton(onClick = { showDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.piggy_bank),
                contentDescription = "Show Fund Details",
                modifier = Modifier
                    .size(80.dp)

            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Fund Details") },
                text = {
                    Column {
                        Text(
                            text = "Duration: $duration MONTHS",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Daily Fund: ₱${String.format("%.2f", dailyFund)} per students",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Active Months: $monthName",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Active Days: $activeDaysCount",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                        Text(
                            text = "Estimated Amount to be Collected: $monthlyFund",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Close")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun AddStudentsButton(navController: NavController) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Red)
            .width(200.dp)
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Add Students",
            fontSize = 10.sp,
            color = Color(0xFFFBFCFE),
            fontFamily = FontFamily(Font(R.font.montserrat)),
            modifier = Modifier.clickable { navController.navigate(Routes.studentadd) }
        )
    }
}


@Composable
fun StudentsList(
    navController: NavController,
    dashboardViewModel: DashboardViewModel
) {

    // Collect the student objects as state from the DashboardViewModel
    val students by dashboardViewModel.studentObjects.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        // Trigger the fetch operation once when the composable is first launched
        dashboardViewModel.refreshStudentObjects()
    }


    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (students.isEmpty()) {
            item {
                Text(
                    text = "No students added",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(students) { student ->
                // Calculating progress and current balance for each student
                val progress = student.calculateProgress()
                val amount = student.currentBal

                // Format the balance for display
                val formatter = DecimalFormat("₱0.00")
                formatter.format(amount)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(9.dp)
                        .background(Color(0xFFADEBB3), shape = RoundedCornerShape(16.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Circular Progress Indicator for displaying progress as a circle
                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.trview)
                            }
                            .padding(7.dp)
                            .size(60.dp)
                            .border(width = 2.dp, shape = RoundedCornerShape(50.dp), color = Color.Green)
                            .background(Color(0xFFADEBB3), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Custom Circular Progress using Canvas
                        Canvas(modifier = Modifier.size(50.dp)) {
                            val progressValue = progress / 100f
                            val strokeWidth = 6f
                            val radius = size.minDimension / 2
                            val angle = 360f * progressValue

                            // Draw background circle
                            drawArc(
                                color = Color.Gray,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth)
                            )

                            // Draw progress circle
                            drawArc(
                                color = Color.Blue,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth)
                            )
                        }

                        // Display progress percentage inside the circle
                        Text(
                            "$progress%",
                            color = Color.Blue,
                            fontFamily = FontFamily(Font(R.font.inter, FontWeight.ExtraBold)),
                            fontSize = 12.sp
                        )
                    }



                    // Spacer for adjusting the layout
                    Spacer(modifier = Modifier.width(8.dp))

                    // Student Name centered in the row
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(Routes.trview)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (student.studentName.isBlank()) "No data added" else student.studentName, // Display student name
                            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Amount Box aligned to the right
                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.pbox(student.studentId))
                            }
                            .padding(7.dp)
                            .width(80.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatter.format(amount), // Display formatted balance
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                        )
                    }
                }
            }
        }
    }
}
