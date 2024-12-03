package com.example.classcash.dashboardActivity.sidepanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.classcash.R

@Composable
fun About(
    navController : NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Terms and Conditions Section

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 10.dp)
                .background(Color(0xFFADEBB3)),
            contentAlignment = Alignment.Center
        ){
            Row(){
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow),
                        contentDescription = "Arrow Back"
                    )
                }
                Text(
                    text = "ClassCash",
                    fontFamily = FontFamily(Font(R.font.irishgrover_regular)),
                    fontSize = 30.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0F7FA))
                .padding(16.dp)
        ) {
            Text(
                text = """
                    Terms and Conditions:
                    
                    1. This app is designed for classroom treasurers to manage sinking funds efficiently.
                    2. The app ensures privacy and security for all entered data.
                    3. The developer is not liable for any misuse of the application or inaccuracies in data entries.
                    4. Use of this app signifies your agreement to these terms.
                """.trimIndent(),
                fontSize = 14.sp,
                color = Color.DarkGray,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Developer Info Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF3E0))
                .padding(16.dp)
        ) {
            Text(
                text = """
                    Developer Info:
                    
                    Email: test@gmail.com
                    Organization: jan555@
                    Feedback: Reach out for feedback or inquiries via the email provided above.
                """.trimIndent(),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}
