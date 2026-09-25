package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.model.OlympiadCategory
import com.example.model.PastPaper
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel
import androidx.activity.compose.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen(val titleBn: String, val titleEn: String, val icon: ImageVector) {
    HOME("হোম", "Home", Icons.Default.Home),
    REGISTER("রেজিস্ট্রেশন", "Register", Icons.Default.AppRegistration),
    QUIZ("কুইজ", "Quiz", Icons.Default.Quiz),
    PAPERS("প্রশ্নব্যাংক", "Papers", Icons.Default.MenuBook),
    SYLLABUS("ল্যাব", "Lab", Icons.Default.Science),
    RESULTS("রেজাল্ট", "Results", Icons.Default.EmojiEvents),
    PROFILE("প্রোফাইল", "Profile", Icons.Default.Person),
    ADMIN("এডমিন", "Admin", Icons.Default.Dashboard),
    LOGIN("লগইন", "Login", Icons.Default.AccountCircle),
    GUIDE("নির্দেশিকা", "Guide", Icons.Default.Info)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BDJSOTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: BdjsoViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var activeQuizCategory by remember { mutableStateOf(OlympiadCategory.JUNIOR) }
    var selectedPaperForModal by remember { mutableStateOf<PastPaper?>(null) }
    var useBanglaLanguage by remember { mutableStateOf(true) }

    val currentUser by AuthRepository.currentUser.collectAsState()

    BackHandler(enabled = currentScreen == Screen.ADMIN) {
        if (viewModel.currentScreen.value != AppScreen.ADMIN_DASHBOARD) {
            viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
        } else {
            currentScreen = Screen.HOME
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (currentScreen != Screen.LOGIN && currentScreen != Screen.ADMIN) {
                // Top App Bar matching the reference UI design
                Surface(
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left & Brand: Logos + Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentScreen = Screen.HOME }
                        ) {
                            // 1st: BDJSO Logo
                            Image(
                                painter = painterResource(id = R.drawable.img_bdjso_logo),
                                contentDescription = "BDJSO Logo",
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(68.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // 2nd: SPSB Logo
                            Image(
                                painter = painterResource(id = R.drawable.img_spsb_logo),
                                contentDescription = "SPSB Logo",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "BDJSO",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = PrimaryTealDark
                                )
                                Text(
                                    text = if (currentUser != null) "আইডি: ${currentUser?.username}" else "BFF & SPSB",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Right side: Language Switcher + User Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Language Switch Chip
                            Surface(
                                color = BackgroundClean,
                                border = BorderStroke(1.dp, BorderLight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { useBanglaLanguage = !useBanglaLanguage }
                            ) {
                                Text(
                                    text = if (useBanglaLanguage) "বাংলা" else "EN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Profile or Login Button
                            if (currentUser != null) {
                                Surface(
                                    color = ServiceGreenBg,
                                    border = BorderStroke(1.dp, ServiceGreenBorder),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { currentScreen = Screen.PROFILE }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            tint = PrimaryTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentUser?.name?.substringBefore(" ") ?: "প্রোফাইল",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = PrimaryTealDark
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { currentScreen = Screen.LOGIN },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("top_bar_login_button")
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (useBanglaLanguage) "লগইন" else "Login",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentScreen != Screen.LOGIN) {
                // Bottom Navigation Bar matching the reference UI screenshot
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar")
                ) {
                    val navItems = if (currentUser?.role == UserRole.MANAGER || currentUser?.role == UserRole.ADMIN || currentScreen == Screen.ADMIN) {
                        listOf(Screen.HOME, Screen.QUIZ, Screen.PAPERS, Screen.ADMIN, Screen.PROFILE)
                    } else if (currentUser != null) {
                        listOf(Screen.HOME, Screen.QUIZ, Screen.PAPERS, Screen.RESULTS, Screen.PROFILE)
                    } else {
                        listOf(Screen.HOME, Screen.QUIZ, Screen.PAPERS, Screen.RESULTS, Screen.LOGIN)
                    }

                    navItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = if (useBanglaLanguage) screen.titleBn else screen.titleEn
                                )
                            },
                            label = {
                                Text(
                                    text = if (useBanglaLanguage) screen.titleBn else screen.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                if (screen == Screen.ADMIN) {
                                    viewModel.switchRole("ADMIN")
                                    viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                                }
                                currentScreen = screen
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryTeal,
                                selectedTextColor = PrimaryTealDark,
                                indicatorColor = ServiceGreenBg,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onNavigateToQuiz = { category ->
                        activeQuizCategory = category
                        currentScreen = Screen.QUIZ
                    },
                    onNavigateToQuestionBank = { currentScreen = Screen.PAPERS },
                    onNavigateToSyllabus = { currentScreen = Screen.SYLLABUS },
                    onNavigateToResults = { currentScreen = Screen.RESULTS },
                    onNavigateToGuide = { currentScreen = Screen.GUIDE },
                    onNavigateToRegister = { currentScreen = Screen.REGISTER },
                    onNavigateToAdmin = {
                        viewModel.switchRole("ADMIN")
                        viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                        currentScreen = Screen.ADMIN
                    },
                    onNavigateToDataVisualization = {
                        viewModel.navigateTo(AppScreen.DATA_VISUALIZATION_DASHBOARD)
                        currentScreen = Screen.ADMIN
                    }
                )

                Screen.REGISTER -> StudentRegisterScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.HOME }
                )

                Screen.QUIZ -> QuizScreen(
                    initialCategory = activeQuizCategory,
                    onBack = { currentScreen = Screen.HOME },
                    viewModel = viewModel
                )

                Screen.PAPERS -> QuestionBankScreen(
                    onSelectPaper = { paper ->
                        selectedPaperForModal = paper
                    }
                )

                Screen.SYLLABUS -> SyllabusScreen()

                Screen.RESULTS -> ResultsScreen(
                    viewModel = viewModel,
                    onNavigateToTrends = {
                        viewModel.navigateTo(AppScreen.DATA_VISUALIZATION_DASHBOARD)
                        currentScreen = Screen.ADMIN
                    }
                )

                Screen.GUIDE -> GuideScreen()

                Screen.LOGIN -> LoginScreen(
                    onLoginSuccess = { user ->
                        if (user.role == UserRole.MANAGER || user.role == UserRole.ADMIN) {
                            viewModel.switchRole("ADMIN")
                            viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                            currentScreen = Screen.ADMIN
                        } else {
                            viewModel.switchRole("STUDENT")
                            currentScreen = Screen.PROFILE
                        }
                    },
                    onContinueAsGuest = {
                        currentScreen = Screen.HOME
                    }
                )

                Screen.PROFILE -> {
                    val user = currentUser
                    if (user != null) {
                        ProfileScreen(
                            user = user,
                            onLogout = {
                                AuthRepository.logout()
                                currentScreen = Screen.HOME
                            },
                            onNavigateToAdminDashboard = {
                                viewModel.switchRole("ADMIN")
                                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                                currentScreen = Screen.ADMIN
                            }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = { currentScreen = Screen.PROFILE },
                            onContinueAsGuest = { currentScreen = Screen.HOME }
                        )
                    }
                }

                Screen.ADMIN -> {
                    val adminSubScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                    when (adminSubScreen) {
                        AppScreen.ADMIN_STUDENTS -> AdminStudentsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_QUESTIONS -> AdminQuestionsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_EXAMS -> AdminExamsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_RESULTS -> AdminResultsSelectionScreen(viewModel = viewModel)
                        AppScreen.ADMIN_SCHOOLS -> AdminSchoolsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_VOLUNTEERS -> AdminVolunteersScreen(viewModel = viewModel)
                        AppScreen.ADMIN_ANNOUNCEMENTS -> AdminAnnouncementsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_AUDIT_LOGS -> AdminAuditLogsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_SETTINGS -> AdminSettingsScreen(viewModel = viewModel)
                        AppScreen.ADMIN_USERS -> AdminUsersScreen(viewModel = viewModel)
                        AppScreen.ADMIN_USER_SHOW -> AdminUserShowScreen(viewModel = viewModel)
                        AppScreen.ADMIN_USER_EDIT -> AdminUserEditScreen(viewModel = viewModel)
                        AppScreen.ADMIN_USER_CHANGE_PASSWORD -> AdminUserChangePasswordScreen(viewModel = viewModel)
                        AppScreen.ADMIN_REGISTRATION_STATS -> AdminRegistrationStatsScreen(viewModel = viewModel)
                        AppScreen.DATA_VISUALIZATION_DASHBOARD -> DataVisualizationDashboardScreen(
                            onBack = {
                                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                            },
                            viewModel = viewModel
                        )
                        else -> AdminDashboardScreen(
                            onBack = {
                                currentScreen = Screen.HOME
                                viewModel.navigateTo(AppScreen.HOME)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Paper Preview Dialog
            selectedPaperForModal?.let { paper ->
                AlertDialog(
                    onDismissRequest = { selectedPaperForModal = null },
                    title = { Text("BDJSO ${paper.stage} (${paper.year})") },
                    text = {
                        Column {
                            Text("Category: ${paper.category.displayName} (${paper.category.classes})")
                            Text("Total Marks: ${paper.totalMarks}")
                            Text("Exam Duration: ${paper.timeMinutes} Minutes")
                            Text("Questions: ${paper.questionCount} Questions")
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "This official question paper covers Physics, Chemistry, and Biology problems structured according to BDJSO national standards.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                activeQuizCategory = paper.category
                                selectedPaperForModal = null
                                currentScreen = Screen.QUIZ
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                        ) {
                            Text("Practice In Quiz")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedPaperForModal = null }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
