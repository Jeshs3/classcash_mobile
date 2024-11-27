package com.example.classcash.bottombars

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.TopScreenViewModel

@Composable
fun Budget(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel
) {

    // Content
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp) // Add padding around the entire content
    ) {
        // Box for the Event Data
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Green)
                .height(200.dp)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTH/YEAR",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )

                    // Amount Info
                    Box(
                        modifier = Modifier
                            .border(width = 3.dp, shape = RoundedCornerShape(20.dp), color = Color.Blue)
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box for text alignment
                    ) {
                        Text(
                            text = "P0.00",
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Event: e.g Acquaintance Party",
                    fontFamily = FontFamily(Font(R.font.inter))
                )
                Text(
                    text = "Expenditures: e.g more than P1000",
                    fontFamily = FontFamily(Font(R.font.inter))
                )

                // Data representation for the expenses breakdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Empty data",
                        modifier = Modifier.padding(14.dp)
                    )
                    Text(
                        text = "No expenses data",
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expenses Breakdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFADEBB3), Color(0xFFFBFCFE))
                    )
                )
                .padding(16.dp) // Add padding inside for content spacing
        ) {
            Column {
                // Top Row showing the Suggested Amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Suggested Amount: ",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter))
                    )

                    Box(
                        modifier = Modifier
                            .border(width = 3.dp, shape = RoundedCornerShape(40.dp), color = Color.Blue)
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box for text alignment
                    ) {
                        Text(
                            text = "P0.00",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                }

                ExpenseBreakdownRow("Food", "0%", "P0.00")
                ExpenseBreakdownRow("Fee", "0%", "P0.00")
                ExpenseBreakdownRow("Other Expenses", "0%", "P0.00")

                // Icon for generating budget again
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_regenerate),
                        contentDescription = "Regenerate Icon"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Button
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp)
                .height(40.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
        ) {
            Text(
                text = "Delete",
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.montserrat))
            )
        }
    }
}


/*
* UI FOR EXPENSE BREAKDOWN
* */
@Composable
fun ExpenseBreakdownRow(expenseName: String, percentage: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(expenseName, fontSize = 14.sp)
        Text(percentage, fontSize = 14.sp)
        Text(amount, fontSize = 14.sp)
    }
}

/*@Composable
fun PieChart(expenses: List<Expense>) {
    val total = expenses.sumOf { it.amount }
    val angles = expenses.map { 360 * (it.amount / total) }

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = 0f

        expenses.zip(angles).forEach { (expense, sweepAngle) ->
            drawArc(
                color = getCategoryColor(expense.category),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Venue" -> Color.Blue
        "Catering" -> Color.Red
        "Decor" -> Color.Green
        "Misc" -> Color.Yellow
        else -> Color.Gray
    }
}
*/