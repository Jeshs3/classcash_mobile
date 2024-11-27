package com.example.classcash.dashboardActivity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.viewmodels.addstudent.AddStudentViewModel
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.addstudent.StudentRepository
import com.example.classcash.viewmodels.addstudent.AddStudentViewModelFactory

@Composable
fun AddStudentScreen(
    navController: NavController,
    topScreenViewModel: TopScreenViewModel = viewModel(),
    studentRepository: StudentRepository
) {
    val addStudentViewModel: AddStudentViewModel = viewModel(
        factory = AddStudentViewModelFactory(studentRepository)
    )
    val context = LocalContext.current
    val uiState by addStudentViewModel.uiState.collectAsState()
    val studentNames by addStudentViewModel.studentNames.collectAsState(initial = emptyList())
    val inputState by addStudentViewModel.inputState.collectAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFCFE)),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Class Size Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFADEBB3), Color(0xFFFBFCFE))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${studentNames.size} Students",
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.SemiBold)),
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                IconButton(
                    onClick = { /* Handle import functionality */ },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_import),
                        contentDescription = "Import",
                        modifier = Modifier.alpha(0.5f)
                    )
                }
            }
        }

        // Input and List Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(15.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFADEBB3), Color(0xFFFBFCFE))
                    )
                )
        ) {
            // Input Field
            OutlinedTextField(
                value = inputState,
                onValueChange = { addStudentViewModel.updateInput(it) },
                placeholder = { Text("Input Student Name") },
                trailingIcon = {
                    IconButton(
                        onClick = { addStudentViewModel.addStudent(inputState) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Add Student",
                            tint = Color.Green.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFBFCFE),
                    unfocusedContainerColor = Color(0xFFFBFCFE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Students in the Class",
                fontFamily = FontFamily(Font(R.font.montserrat)),
                modifier = Modifier.padding(10.dp)
            )
            // Dynamic List of Students
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                itemsIndexed(studentNames) { index, studentName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = studentName,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { addStudentViewModel.removeStudent(index) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Remove Student",
                                tint = Color.Red.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Save Button
        Button(
            onClick = {
                addStudentViewModel.saveAll()
                Toast.makeText(context, "Students Saved", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                addStudentViewModel.clearInputFields()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(50))
                .width(200.dp),
            enabled = studentNames.isNotEmpty()
        ) {
            Text(
                text = "Save",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.montserrat))
            )
        }
    }

    // UI State Feedback
    LaunchedEffect(uiState) {
        when (uiState) {
            is AddStudentViewModel.UiState.Success -> {
                Toast.makeText(context, (uiState as AddStudentViewModel.UiState.Success).message, Toast.LENGTH_SHORT)
                    .show()
            }
            is AddStudentViewModel.UiState.Error -> {
                Toast.makeText(context, (uiState as AddStudentViewModel.UiState.Error).message, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> Unit
        }
    }
}







