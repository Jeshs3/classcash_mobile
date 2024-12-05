package com.example.classcash.dashboardActivity

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.classcash.R
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.treasurer.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    topScreenViewModel: TopScreenViewModel
) {
    val context = LocalContext.current
    val treasurerName by authViewModel.name
    val profileImage by authViewModel.profileImageUrl

    val classroomName by topScreenViewModel.currentClassName.collectAsState()

    LaunchedEffect(classroomName) {
        authViewModel.classroomName.value = classroomName
    }

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
            // Profile Image
            AsyncImage(
                model = profileImage,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image Picker
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { uri: Uri? ->
                    if (uri != null) {
                        authViewModel.uploadProfileImage(
                            uri,
                            onUploadSuccess = { url ->
                                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onUploadFailure = { error ->
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                ) {
                Text(
                    "Select Profile Image",
                    fontFamily = FontFamily.Monospace,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = "Treasurer's Name: $treasurerName",
                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold)),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Classroom Handled
            Text(
                text = if (classroomName.isBlank()) "Classroom: Unknown Classroom" else "Classroom: $classroomName",
                fontFamily = FontFamily(Font(R.font.inter)),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Exit Button
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
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
