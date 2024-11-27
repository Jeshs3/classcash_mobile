package com.example.classcash.recyclable


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.classcash.R
import com.example.classcash.Routes

sealed class BottomNavScreen(val route: String, val label: String, val icon: Int) {
    data object Home : BottomNavScreen(Routes.dashboard, "Dashboard", R.drawable.ic_home)
    data object Event : BottomNavScreen(Routes.event, "Event", R.drawable.ic_event)
    data object Analytics : BottomNavScreen(Routes.analytics, "Analytics", R.drawable.ic_analytics)
    data object Fund : BottomNavScreen(Routes.fund, "Fund", R.drawable.ic_fund)
    data object Recommend : BottomNavScreen(Routes.recommend, "Recommend", R.drawable.ic_recommend)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    // List of screens for the bottom navigation
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Event,
        BottomNavScreen.Analytics,
        BottomNavScreen.Fund,
        BottomNavScreen.Recommend
    )

    // Get the current route to handle background color changes
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route


    // Horizontal bottom navigation bar
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth() // Fill the width for a horizontal layout
            .clip(RoundedCornerShape(10.dp))
            .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Green),
        containerColor = Color(0xFFADEBB3) // Set the dynamic background color
    ) {
        // Create a NavigationBarItem for each screen
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    }
                },
                icon = {

                        Icon(
                            painterResource(id = screen.icon),
                            contentDescription = screen.label,
                            tint = if (currentRoute == screen.route) Color(0xFF50404D) else Color.Gray
                        )
                },
                label = {
                    Text(
                        text = screen.label,
                        fontSize = 8.sp,
                        fontFamily = FontFamily(Font(R.font.inter, FontWeight.Normal))
                    )
                },
                alwaysShowLabel = true // Ensures the label is always visible
            )
        }
    }
}
