package com.example.classcash.dashboardActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classcash.R
import com.example.classcash.Routes
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.recyclable.TopScreenB

@Composable
fun TransactionView(
    navController : NavController,
    topScreenViewModel: TopScreenViewModel = viewModel()
){

    //Transaction Information
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(400.dp)
                .border(width = 2.dp,shape = RoundedCornerShape(20.dp) , color = Color.Green)
        ){

            Column{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Search Button
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search Icon"
                        )
                    }

                    // Text
                    Text(
                        text = "Transaction",
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                    )
                }

                //Divider
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.Green
                )

                //Transaction List
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                )

                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFADEBB3))
                            .border(width = 1.dp, shape = RoundedCornerShape(20.dp), color = Color.Green)
                    ){
                        Row(
                            modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.checklist),
                                contentDescription = "CheckList"
                            )

                            Text(
                                modifier = Modifier
                                    .padding(5.dp),
                                text = "No Transaction yet",
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Normal))
                            )
                        }
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            //Button for external fund
                Button(
                    onClick = {navController.navigate(Routes.extfund)},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADEBB3)),
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                ){
                    Text(
                        text = "Add External",
                        fontSize = 12.sp,
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                    )
                }

            //Button for withdrawal
            Button(
                onClick = {navController.navigate(Routes.withdrawbox)},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADEBB3)),
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.5f),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
            ){
                Text(
                    text = "Withdraw",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontFamily = FontFamily(Font(R.font.montserrat, FontWeight.Bold))
                )
            }
        }


    }

}

