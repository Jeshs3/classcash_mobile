
package com.example.classcash.dashboardActivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import com.example.classcash.R
import com.example.classcash.viewmodels.treasurer.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onNavigateToDashboard: () -> Unit) {
    val scope = rememberCoroutineScope()
    val name by authViewModel.name
    val email by authViewModel.email
    val password by authViewModel.password
    val errorMessage by authViewModel.errorMessage
    val successMessage by authViewModel.successMessage
    var loginMode by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) } // Loading state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFADEBB3)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .width(350.dp)
                    .height(600.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBFCFE), Color(0xFFADEBB3))
                        )
                    )
                    .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Green)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = CenterHorizontally
                ) {
                    // Your existing login/register UI goes here
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.classlogo),
                            contentDescription = "logo",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "ClassCash",
                            fontWeight = FontWeight.Black,
                            fontSize = 30.sp,
                            color = Color(0xFF50404D),
                            fontFamily = FontFamily(Font(R.font.irishgrover_regular, FontWeight.Normal))
                        )
                    }

                    HorizontalDivider(
                        color = Color(0xFFADEBB3),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (loginMode) "Login" else "Sign Up",
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                    )


                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = Color(0xFFADEBB3),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!loginMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { authViewModel.onNameChange(it) },
                            label = {
                                Text(
                                    "Name",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Green,
                                unfocusedBorderColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { authViewModel.onEmailChange(it) },
                        label = {
                            Text(
                                text = "Email",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                                ) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        onValueChange = { authViewModel.onPasswordChange(it) },
                        label = {
                            Text(
                                text = "Password",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (isPasswordVisible) R.drawable.isopen else R.drawable.islock),
                                    contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                                    tint = Color.Red.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    //If REGISTRATION IS SUCCESSFUL
                    // Handle both success and error messages
                    if (!successMessage.isNullOrEmpty()) {
                        SuccessDialog(message = successMessage!!) {
                            authViewModel.clearMessages()
                        }
                    } else if (!errorMessage.isNullOrEmpty()) {
                        ErrorDialog(message = errorMessage!!) {
                            authViewModel.clearMessages()
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            authViewModel.errorMessage.value
                            loading = true // Show loading indicator
                            scope.launch {
                                if (loginMode) {
                                    authViewModel.login(
                                        onSuccess = {
                                            loading = false // Hide loading indicator
                                            onNavigateToDashboard()
                                        },
                                        onFailure = { error ->
                                            loading = false // Hide loading indicator
                                            authViewModel.errorMessage.value
                                        }
                                    )
                                } else {
                                    authViewModel.register(
                                        onSuccess = {
                                            loading = false // Hide loading indicator
                                            onNavigateToDashboard()
                                        },
                                        onFailure = { error ->
                                            loading = false // Hide loading indicator
                                            authViewModel.errorMessage.value
                                        }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .align(CenterHorizontally)
                    ) {
                        Text(
                            text = if (loginMode) "Login" else "Continue",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                        )
                    }


                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "or",
                        fontFamily = FontFamily(Font(R.font.inter))
                    )


                    TextButton(
                        onClick = {loginMode = !loginMode}
                    ){
                        Text(
                            text = if(loginMode) "Switch to Register" else "Switch to Login",
                            fontSize = 15.sp,
                            color = Color.Red,
                            fontFamily = FontFamily(Font(R.font.montserrat))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Sinking Fund Management System",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraLight,
                color = Color.Gray
            )

            Text(
                text = "All rights reserved 2024",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraLight,
                color = Color.Gray
            )
        }

        // Loading overlay
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Please wait...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}




@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                textAlign = TextAlign.Center)
        }

        // Auto-dismiss the dialog after a delay
        LaunchedEffect(Unit) {
            delay(3000L) // Adjust the delay as needed (e.g., 2 seconds)
            onDismiss()
        }
    }
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = { Text("Error") },
        text = { Text(message) }
    )
}
