package com.example.classcash

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.classcash.bottombars.Analytics
import com.example.classcash.bottombars.Budget
import com.example.classcash.bottombars.EventScreen
import com.example.classcash.bottombars.FundSetting
import com.example.classcash.dashboardActivity.AddStudentScreen
import com.example.classcash.dashboardActivity.DashboardScreen
import com.example.classcash.dashboardActivity.Notifications
import com.example.classcash.dashboardActivity.TransactionView
import com.example.classcash.dashboardActivity.PaymentBox
import com.example.classcash.dashboardActivity.WithdrawBox
import com.example.classcash.dashboardActivity.ExternalFundBox
import com.example.classcash.dashboardActivity.LoginScreen
import com.example.classcash.dashboardActivity.ProfileScreen
import com.example.classcash.dashboardActivity.SidePanel
import com.example.classcash.dashboardActivity.StudentInfoScreen
import com.example.classcash.dashboardActivity.sidepanel.About
import com.example.classcash.dashboardActivity.sidepanel.StudentFiles
import com.example.classcash.recyclable.BottomNavigationBar
import com.example.classcash.recyclable.TopScreenB
import com.example.classcash.viewmodels.addstudent.AddStudentViewModel
import com.example.classcash.viewmodels.TopScreenViewModel
import com.example.classcash.viewmodels.addstudent.AddStudentViewModelFactory
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.Student.TransactionLog
import com.example.classcash.viewmodels.addstudent.StudentRepository
import com.example.classcash.viewmodels.collection.CollectionRepository
import com.example.classcash.viewmodels.collection.CollectionViewModel
import com.example.classcash.viewmodels.collection.CollectionViewModelFactory
import com.example.classcash.viewmodels.dashboard.DashboardViewModel
import com.example.classcash.viewmodels.dashboard.DashboardViewModelFactory
import com.example.classcash.viewmodels.event.AddEventViewModel
import com.example.classcash.viewmodels.event.AddEventViewModelFactory
import com.example.classcash.viewmodels.event.BudgetViewModel
import com.example.classcash.viewmodels.event.EventRepository
import com.example.classcash.viewmodels.notifications.NotificationsRepository
import com.example.classcash.viewmodels.notifications.NotificationsViewModel
import com.example.classcash.viewmodels.notifications.NotificationsViewModelFactory
import com.example.classcash.viewmodels.payment.PaymentRepository
import com.example.classcash.viewmodels.payment.PaymentViewModel
import com.example.classcash.viewmodels.payment.PaymentViewModelFactory
import com.example.classcash.viewmodels.payment.TransactionViewModel
import com.example.classcash.viewmodels.treasurer.AuthViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        FirebaseAuth.getInstance().useAppLanguage()
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                Log.d("Auth", "User is signed out")
            } else {
                Log.d("Auth", "User is signed in: ${auth.currentUser?.email}")
            }
        }
        enableEdgeToEdge()
        setContent {
            ClassCash()
        }
    }
}


@Composable
fun ClassCash() {
    val navController = rememberNavController()
    val topScreenViewModel: TopScreenViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val firebaseFirestore = FirebaseFirestore.getInstance()
    val studentRepository = StudentRepository(firebaseFirestore)
    val collectionRepository = CollectionRepository(firebaseFirestore)
    val eventRepository = EventRepository(firebaseFirestore)
    val paymentRepository = PaymentRepository(firebaseFirestore)

    val addStudentViewModel: AddStudentViewModel = viewModel(
        factory = AddStudentViewModelFactory(studentRepository, paymentRepository)
    )

    val transactionViewModel : TransactionViewModel = viewModel(
        factory = PaymentViewModelFactory(paymentRepository)

    )
    val addEventViewModel : AddEventViewModel = viewModel(
        factory = AddEventViewModelFactory(eventRepository)
    )

    val budgetViewModel : BudgetViewModel = viewModel(
        factory = AddEventViewModelFactory(eventRepository)
    )

    // Fund Setup
    val collectionViewModel : CollectionViewModel = viewModel(
        factory = CollectionViewModelFactory(collectionRepository)
    )

    //Payment
    val paymentViewModelFactory = PaymentViewModelFactory(paymentRepository)
    val paymentViewModel : PaymentViewModel = viewModel(factory = paymentViewModelFactory)


    //val notificationsRepository = NotificationsRepository()
    //val notificationsViewModelFactory = NotificationsViewModelFactory(notificationsRepository)
    //val notificationsViewModel : NotificationsViewModel = viewModel(factory = notificationsViewModelFactory)

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(studentRepository, paymentRepository)
    )
    // Routes where TopBar and SidePanel are visible
    val topBarRoutes = listOf(
        Routes.dashboard,
        Routes.event,
        Routes.analytics,
        Routes.fund,
        Routes.recommend,
        Routes.studentadd,
        Routes.trview,
        Routes.notification,
        Routes.studentinfo
    )

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Determine if TopBar and SidePanel should be visible
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val isTopBarVisible = currentRoute in topBarRoutes

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isTopBarVisible) {
                SidePanel(
                    navController = navController,
                    onNavigationClick = { route ->
                        scope.launch {
                            drawerState.close() // Close the drawer when a navigation item is clicked
                        }
                        navController.navigate(route)
                    }
                )
            }
        }
    ) {
        // Main Content with Scaffold
        Box {
            Scaffold(
                topBar = {
                    if (isTopBarVisible) {
                        TopScreenB(
                            navController,
                            topScreenViewModel,
                            drawerState = drawerState, // Pass the DrawerState
                            scope = scope
                        )
                    }
                },
                bottomBar = {
                    if (currentRoute in topBarRoutes) {
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.splashscreen,
                    Modifier.padding(innerPadding)
                )  {
                    composable(Routes.splashscreen) {
                        SplashScreen(navController)
                    }
                    composable(Routes.login) {
                        LoginScreen(
                            authViewModel,
                            onNavigateToDashboard = {
                                navController.navigate(Routes.dashboard) {
                                    popUpTo(Routes.login) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Routes.dashboard) {
                        DashboardScreen(
                            navController,
                            dashboardViewModel,
                            topScreenViewModel,
                            addStudentViewModel,
                            collectionViewModel,
                            paymentViewModel
                        )
                    }
                    composable(Routes.event) {
                        EventScreen(
                            navController,
                            topScreenViewModel,
                            addEventViewModel,
                            budgetViewModel,
                            paymentViewModel
                        )
                    }
                    composable(Routes.analytics) {
                        Analytics(
                            navController,
                            topScreenViewModel
                        )
                    }
                    composable(Routes.fund) {
                        FundSetting(
                            navController,
                            topScreenViewModel,
                            collectionViewModel
                        )
                    }
                    composable(Routes.recommend) {
                        Budget(
                            navController,
                            topScreenViewModel,
                            budgetViewModel,
                            paymentViewModel
                        )
                    }
                    composable(Routes.studentadd) {
                        AddStudentScreen(
                            navController,
                            topScreenViewModel,
                            addStudentViewModel,
                            studentRepository
                        )
                    }
                    composable(Routes.trview) {
                        TransactionView(navController)
                    }
                    /*composable(Routes.notification) {
                        Notifications(
                            navController,
                            notificationsViewModel
                        )
                    }*/
                    composable(Routes.pbox) { backStackEntry ->
                        // Extract `studentId` from the navigation arguments or pass it directly
                        val studentId = backStackEntry.arguments?.getString("studentId")?.toIntOrNull() ?: 0

                        Log.d("Navigation", "Student ID received: $studentId")

                        PaymentBox(
                            navController,
                            paymentViewModel,// Make sure this is initialized in your Activity or ViewModel scope
                            dashboardViewModel,
                            studentId
                        )

                    }
                    composable(Routes.withdrawbox) {
                        WithdrawBox(
                            navController,
                            transactionViewModel
                        )
                    }
                    composable(Routes.extfund) {
                        ExternalFundBox(
                            navController,
                            transactionViewModel
                        )
                    }
                    composable(Routes.profile) {
                        ProfileScreen(
                            navController,
                            authViewModel,
                            topScreenViewModel
                        )
                    }
                    composable(Routes.files) {
                        StudentFiles()
                    }
                    composable(Routes.aboutsection) {
                        About(navController)
                    }
                    composable(Routes.studentinfo) { backStackEntry ->
                        val studentId = backStackEntry.arguments?.getString("studentId")?.toIntOrNull() ?: 0
                        StudentInfoScreen(navController, dashboardViewModel, studentId)
                    }
                }
            }

        }
    }
}



