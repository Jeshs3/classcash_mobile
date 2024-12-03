package com.example.classcash.dashboardActivity

import androidx.compose.runtime.Composable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.treasurer.AuthViewModel
import com.example.classcash.viewmodels.treasurer.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel : ProfileViewModel = viewModel()
    ) {

    //Data declaration
    val treasurerName = profileViewModel.treasurerName.observeAsState(initial = "Loading...")
    //val treasurerName = authViewModel.name.value.observeAsState("")
    val classroomName = profileViewModel.classroomName.observeAsState(initial = "Loading...")
    profileViewModel.profileImage.observeAsState(initial = null)

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Column(
            modifier = Modifier
                .height(400.dp)
                .width(300.dp)
                .background(Color(0xFFFBFCFE))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar Image
            /*profileImage?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } ?: Text(
                text = "No Profile Image",
                fontFamily = FontFamily(Font(R.font.montserrat))
            )*/

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = treasurerName.value,
                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Classroom Handled
            Text(
                text = "Classroom: $classroomName",
                fontFamily = FontFamily(Font(R.font.montserrat)),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
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
                    text = "Exit",
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat))
                )
            }
        }
    }
}