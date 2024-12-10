package com.example.classcash.dashboardActivity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.classcash.R
//import com.example.classcash.viewmodels.notifications. NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Notifications(
    navController: NavController
) {
    // Observe notifications from ViewModel
    //val notifications = notificationsViewModel.notifications.observeAsState(emptyList())

    val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp)
            .padding(horizontal = 10.dp)
            .background(Color(0xFFFBFCFE)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .border(width = 4.dp, shape = RoundedCornerShape(40.dp), color = Color.Green)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentDate,
                    color = Color.Red,
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )

                /*if (notifications.value.isEmpty()) {
                    Text(
                        text = "No notifications",
                        fontFamily = FontFamily(Font(R.font.montserrat))
                    )
                } else {
                    notifications.value.forEach { notification ->
                        Text(
                            text = notification.message,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                }*/
            }
        }
    }
}
