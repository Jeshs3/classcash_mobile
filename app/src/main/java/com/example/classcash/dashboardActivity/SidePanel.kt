package com.example.classcash.dashboardActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.addstudent.AddStudentViewModel
import com.example.classcash.viewmodels.treasurer.AuthViewModel

@Composable
fun SidePanel(
    navController : NavController,
    onNavigationClick: (String) -> Unit
) {

    val addStudentViewModel : AddStudentViewModel = viewModel()
    val authViewModel : AuthViewModel = viewModel()
    var isClassDialogOpen by remember { mutableStateOf(false) }
    var isLogoutDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(color = Color(0xFFFFD700)), // Light gray background
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Navigation Items
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            SidePanelItem(
                title = "Add Student",
                iconPainter = painterResource(id = R.drawable.ic_personadd)
            ) {
                onNavigationClick(Routes.studentadd)
            }
            SidePanelItem(
                title = "Student Files",
                iconPainter = painterResource(id = R.drawable.folder)
            ) {
                onNavigationClick("StudentFiles")
            }
            SidePanelItem(
                title = "Delete Class",
                iconPainter = painterResource(id = R.drawable.delete_btn)
            ) {
                isClassDialogOpen = true
            }
            SidePanelItem(
                title = "About",
                iconPainter = painterResource(id = R.drawable.info)
            ) {
                onNavigationClick("About")
            }

            Spacer(modifier = Modifier.height(80.dp))
            // Logout Button
            SidePanelItem(
                title = "Logout",
                iconPainter = painterResource(id = R.drawable.ic_close)
            ) {
                isLogoutDialogOpen = true
            }
        }
    }


    //Dialog for deleting class
    if (isClassDialogOpen) {
        DeleteClassDialog(
            isClassDialogOpen = isClassDialogOpen,
            onConfirm = {
                isClassDialogOpen = false
                addStudentViewModel.deleteClass() // Call deleteClass when confirmed
                onNavigationClick(Routes.dashboard) // Navigate after deletion
            },
            onDismiss = {
                isClassDialogOpen = false // Simply close the dialog without action
            }
        )
    }

    //For Logging Out
    if (isLogoutDialogOpen) {
        LogoutDialog(
            isLogoutDialogOpen = isLogoutDialogOpen,
            onConfirm = {
                isLogoutDialogOpen = false
                authViewModel.logout() // Call the logout function
                onNavigationClick("Login") // Navigate back to login UI
            },
            onDismiss = {
                isLogoutDialogOpen = false // Simply close the dialog
            }
        )
    }
}


@Composable
fun SidePanelItem(
    title: String,
    iconPainter: Painter,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun DeleteClassDialog(
    isClassDialogOpen: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isClassDialogOpen) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "Delete Class")
            },
            text = {
                Text(text = "Are you sure you want to delete class?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun LogoutDialog(
    isLogoutDialogOpen : Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
   if(isLogoutDialogOpen){
       AlertDialog(
           onDismissRequest = { onDismiss() },
           title = { Text("Logout") },
           text = { Text("Are you sure you want to leave?") },
           confirmButton = {
               TextButton(onClick = { onConfirm() }) {
                   Text("Logout", color = Color.Red)
               }
           },
           dismissButton = {
               TextButton(onClick = { onDismiss() }) {
                   Text("Cancel")
               }
           }
       )
   }
}


