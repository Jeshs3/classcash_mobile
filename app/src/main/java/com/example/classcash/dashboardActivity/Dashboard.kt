package com.example.classcash.dashboardActivity

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
            Spacer(modifier = Modifier.height(20.dp))

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
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .clickable { navController.navigate(Routes.trview) }
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .background(Color.White)
                .width(200.dp)
                .padding(vertical = 16.dp, horizontal = 16.dp)
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


        //Display the snippet for the fund
        IconButton(onClick = { showDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.piggy_bank),
                contentDescription = "Show Fund Details",
                tint = Color.Blue,
                modifier = Modifier
                    .size(50.dp)

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
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                // Integrating StudentItem into the StudentsList composable
                val progress = student.progress // Fetch progress dynamically from the student object
                val amount = student.balance // Fetch balance dynamically from the student object
                val formatter = DecimalFormat("₱#.00")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(9.dp)
                        .background(Color(0xFFADEBB3), shape = RoundedCornerShape(16.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Text(
                            "$progress%", // Display progress as a percentage
                            color = Color.Blue,
                            fontFamily = FontFamily(Font(R.font.inter, FontWeight.ExtraBold))
                        )
                    }

                    Spacer(modifier = Modifier.fillMaxWidth(0.2f))

                    Text(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.trview)
                            },
                        text = if (student.name.isBlank()) "No data added" else student.name, // Use student object here
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.fillMaxWidth(0.2f))

                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("pbox/${student.name}") // Pass the student's name or ID to the next screen
                            }
                            .padding(7.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatter.format(amount), // Display formatted balance
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                        )
                    }
                }
            }
        }
    }
}
