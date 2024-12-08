package com.example.classcash.bottombars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.classcash.R
import androidx.navigation.NavController
import com.example.classcash.viewmodels.TopScreenViewModel

@Composable
fun Analytics(
    navController : NavController,
    topScreenViewModel: TopScreenViewModel
    ){

    Column(
        modifier = Modifier
            .fillMaxSize()
        .padding(10.dp)
    ){

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFADEBB3))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Left Column for Collection Details
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = "August 2024", // Replace with the actual selected month
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat)),
                            color = Color.Blue
                        )

                        IconButton(
                            onClick = { /* Action */ }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_import),
                                contentDescription = "Download"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Total Students: 0", // Replace with the actual class size
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 8.sp
                    )
                    Text(
                        text = "Total Amount Collected: 0", // Replace with the actual amount
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 8.sp
                    )
                    Text(
                        text = "Number of Days: 0", // Replace with the actual number of days
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 8.sp
                    )
                    Text(
                        text = "Collection Ended: 00/00/0000", // Replace with the last active day
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 8.sp
                    )
                }

                    // Progress Circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(top = 16.dp)
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

                    // "Collection Completed" Text
                    Text(
                        text = "Collection Completed",
                        fontSize = 8.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        modifier = Modifier.padding(top = 8.dp)
                    )
            }
        }

        //the end for ended month details

        Spacer(modifier= Modifier.height(10.dp))
        //Display for ongoing month collection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFADEBB3))
                .padding(16.dp)
        ) {
            Column {
                // Top Row: Month and IconButton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "September 2024", // Replace with actual month
                        fontFamily = FontFamily(Font(R.font.montserrat)),
                        fontSize = 15.sp,
                        color = Color.Blue
                    )

                    IconButton(
                        onClick = { /* Download action */ }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_import),
                            contentDescription = "Download"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar Section
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Background Progress (shaded part)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray.copy(alpha = 0.2f))
                        )

                        // Foreground Progress (colored part)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f) // Replace 0.75f with your progress percentage (e.g., 75%)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Blue)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Amount Texts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$1,500", // Replace with the current collected amount
                            fontFamily = FontFamily(Font(R.font.inter)),
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Target: $2,000", // Replace with the target amount
                            fontFamily = FontFamily(Font(R.font.inter)),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Remaining Days Text
                Text(
                    text = "Remaining Days: 0 days", // Replace with the actual remaining days
                    color = Color.Red,
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(R.font.inter))
                )
            }
        }
    }
}

