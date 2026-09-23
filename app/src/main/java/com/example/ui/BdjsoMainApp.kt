package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminAnnouncementsScreen
import com.example.ui.screens.AdminAuditLogsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminExamsScreen
import com.example.ui.screens.AdminQuestionsScreen
import com.example.ui.screens.AdminRegistrationStatsScreen
import com.example.ui.screens.AdminResultsSelectionScreen
import com.example.ui.screens.AdminSchoolsScreen
import com.example.ui.screens.AdminSettingsScreen
import com.example.ui.screens.AdminStudentsScreen
import com.example.ui.screens.AdminUserChangePasswordScreen
import com.example.ui.screens.AdminUserEditScreen
import com.example.ui.screens.AdminUserShowScreen
import com.example.ui.screens.AdminUsersScreen
import com.example.ui.screens.AdminVolunteersScreen
import com.example.ui.screens.ExamResultScreen
import com.example.ui.screens.OnlineExamScreen
import com.example.ui.screens.PublicHomeScreen
import com.example.ui.screens.ResultsLookupScreen
import com.example.ui.screens.RulesAndInfoScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.StudentRegisterScreen
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BdjsoMainApp(
    viewModel: BdjsoViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val isStudentRole = currentRole == "STUDENT"
    val isExamScreen = currentScreen == AppScreen.ONLINE_EXAM

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isExamScreen) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryDarkNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = "BDJSO",
                                    tint = OlympiadGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "BDJSO 2026",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = PrimaryDarkNavy
                                )
                                Text(
                                    text = "Junior Science Olympiad",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Role Switcher Button
                        Box {
                            OutlinedButton(
                                onClick = { roleMenuExpanded = true },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            ) {
                                Icon(
                                    if (isStudentRole) Icons.Default.Person else Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isStudentRole) BdjsoEmerald else Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isStudentRole) "Student" else currentRole.replace("_", " "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = roleMenuExpanded,
                                onDismissRequest = { roleMenuExpanded = false }
                            ) {
                                val roles = listOf(
                                    "STUDENT" to "Candidate Portal",
                                    "SUPER_ADMIN" to "Super Administrator",
                                    "REGISTRATION_ADMIN" to "Registration Officer",
                                    "EXAM_ADMIN" to "Online Exam In-Charge",
                                    "QUESTION_ADMIN" to "Question Bank Lead",
                                    "RESULT_ADMIN" to "Results & Evaluation",
                                    "VOLUNTEER_COORDINATOR" to "Volunteer Desk"
                                )

                                roles.forEach { (role, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(role, fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            viewModel.setRole(role)
                                            roleMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (!isExamScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    if (isStudentRole) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { viewModel.navigateTo(AppScreen.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BdjsoEmerald,
                                selectedTextColor = BdjsoEmerald
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.STUDENT_REGISTER,
                            onClick = { viewModel.navigateTo(AppScreen.STUDENT_REGISTER) },
                            icon = { Icon(Icons.Default.HowToReg, contentDescription = "Register") },
                            label = { Text("Register", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BdjsoEmerald,
                                selectedTextColor = BdjsoEmerald
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.STUDENT_DASHBOARD || currentScreen == AppScreen.EXAM_RESULT,
                            onClick = { viewModel.navigateTo(AppScreen.STUDENT_DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("My Profile", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BdjsoEmerald,
                                selectedTextColor = BdjsoEmerald
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.RESULTS_LOOKUP,
                            onClick = { viewModel.navigateTo(AppScreen.RESULTS_LOOKUP) },
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Merit List") },
                            label = { Text("Merit List", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BdjsoEmerald,
                                selectedTextColor = BdjsoEmerald
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.RULES_AND_INFO,
                            onClick = { viewModel.navigateTo(AppScreen.RULES_AND_INFO) },
                            icon = { Icon(Icons.Default.Info, contentDescription = "Rules") },
                            label = { Text("Rules", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BdjsoEmerald,
                                selectedTextColor = BdjsoEmerald
                            )
                        )
                    } else {
                        // Admin Navigation
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                            label = { Text("Control", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ScienceTeal,
                                selectedTextColor = ScienceTeal
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_STUDENTS,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_STUDENTS) },
                            icon = { Icon(Icons.Default.People, contentDescription = "Students") },
                            label = { Text("Students", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ScienceTeal,
                                selectedTextColor = ScienceTeal
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_QUESTIONS,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_QUESTIONS) },
                            icon = { Icon(Icons.Default.QuestionAnswer, contentDescription = "Questions") },
                            label = { Text("Questions", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ScienceTeal,
                                selectedTextColor = ScienceTeal
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_EXAMS,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXAMS) },
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Exams") },
                            label = { Text("Exams", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ScienceTeal,
                                selectedTextColor = ScienceTeal
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_RESULTS,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_RESULTS) },
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Results") },
                            label = { Text("Merit", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ScienceTeal,
                                selectedTextColor = ScienceTeal
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> PublicHomeScreen(viewModel)
                AppScreen.STUDENT_REGISTER -> StudentRegisterScreen(viewModel)
                AppScreen.STUDENT_DASHBOARD -> StudentDashboardScreen(viewModel)
                AppScreen.ONLINE_EXAM -> OnlineExamScreen(viewModel)
                AppScreen.EXAM_RESULT -> ExamResultScreen(viewModel)
                AppScreen.RESULTS_LOOKUP -> ResultsLookupScreen(viewModel)
                AppScreen.RULES_AND_INFO -> RulesAndInfoScreen(viewModel)
                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel)
                AppScreen.ADMIN_STUDENTS -> AdminStudentsScreen(viewModel)
                AppScreen.ADMIN_QUESTIONS -> AdminQuestionsScreen(viewModel)
                AppScreen.ADMIN_EXAMS -> AdminExamsScreen(viewModel)
                AppScreen.ADMIN_RESULTS -> AdminResultsSelectionScreen(viewModel)
                AppScreen.ADMIN_SCHOOLS -> AdminSchoolsScreen(viewModel)
                AppScreen.ADMIN_VOLUNTEERS -> AdminVolunteersScreen(viewModel)
                AppScreen.ADMIN_ANNOUNCEMENTS -> AdminAnnouncementsScreen(viewModel)
                AppScreen.ADMIN_AUDIT_LOGS -> AdminAuditLogsScreen(viewModel)
                AppScreen.ADMIN_SETTINGS -> AdminSettingsScreen(viewModel)
                AppScreen.ADMIN_USERS -> AdminUsersScreen(viewModel)
                AppScreen.ADMIN_USER_SHOW -> AdminUserShowScreen(viewModel)
                AppScreen.ADMIN_USER_EDIT -> AdminUserEditScreen(viewModel)
                AppScreen.ADMIN_USER_CHANGE_PASSWORD -> AdminUserChangePasswordScreen(viewModel)
                AppScreen.ADMIN_REGISTRATION_STATS -> AdminRegistrationStatsScreen(viewModel)
            }
        }
    }
}
