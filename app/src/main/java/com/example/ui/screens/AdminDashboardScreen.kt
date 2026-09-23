package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthRepository
import com.example.data.BDJSOData
import com.example.model.OlympiadCategory
import com.example.model.QuestionType
import com.example.model.QuizQuestion
import com.example.model.UserProfile
import com.example.ui.components.QuestionDiagram
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Questions, 1: Stats, 2: Users
    var questionPage by remember { mutableIntStateOf(1) } // 1: Primary (PDF Page 6-7), 2: Junior (PDF Page 14)
    var userSearchQuery by remember { mutableStateOf("") }
    var expandedAnswers by remember { mutableStateOf(setOf<Int>()) }

    val allUsers = remember { AuthRepository.getAllUsers() }
    val filteredUsers = remember(userSearchQuery) {
        if (userSearchQuery.isBlank()) allUsers
        else allUsers.filter {
            it.name.contains(userSearchQuery, ignoreCase = true) ||
            it.username.contains(userSearchQuery, ignoreCase = true) ||
            it.email.contains(userSearchQuery, ignoreCase = true) ||
            it.mobile.contains(userSearchQuery, ignoreCase = true)
        }
    }

    val pageQuestions = remember(questionPage) {
        if (questionPage == 1) {
            BDJSOData.sampleQuestions.filter { it.category == OlympiadCategory.PRIMARY }
        } else {
            BDJSOData.sampleQuestions.filter { it.category == OlympiadCategory.JUNIOR }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BDJSO Online Admin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = PrimaryTealDark
                        )
                        Text(
                            text = "online.bdjso.org/admin",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundClean)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .testTag("admin_dashboard_content"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Three Tabs matching PDF portal
            item {
                Spacer(modifier = Modifier.height(8.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryTeal
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Questions List", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Registration Stat", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Users (${allUsers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // === TAB 0: QUESTIONS LIST (from PDF Pages 6, 7, 14) ===
            if (selectedTab == 0) {
                // Header and Pagination (like "Questions List" & page buttons 1, 2)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "❓ Questions List",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTealDark
                                )
                                Text(
                                    text = "Total 45 Questions • Page $questionPage",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            // Pagination Pills (1, 2)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = questionPage == 1,
                                    onClick = { questionPage = 1 },
                                    label = { Text("Page 1 (Primary)") }
                                )
                                FilterChip(
                                    selected = questionPage == 2,
                                    onClick = { questionPage = 2 },
                                    label = { Text("Page 2 (Junior)") }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(pageQuestions) { q ->
                    val isExpanded = expandedAnswers.contains(q.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Question header row with ID and green action box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = ServiceTealBg,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, ServiceTealBorder)
                                    ) {
                                        Text(
                                            text = q.code,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            color = PrimaryTealDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${q.subject}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = ServiceGreen,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.RemoveRedEye,
                                            contentDescription = "View",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bengali Question
                            if (q.questionBangla.isNotBlank()) {
                                Text(
                                    text = q.questionBangla,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    lineHeight = 19.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // English Question
                            Text(
                                text = q.questionEnglish.ifEmpty { q.question },
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            // Diagram if available
                            if (q.diagramType != "NONE") {
                                Spacer(modifier = Modifier.height(6.dp))
                                QuestionDiagram(diagramType = q.diagramType)
                            }

                            // MCQ Options if present
                            if (q.questionType == QuestionType.MCQ && q.options.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    q.options.forEachIndexed { i, opt ->
                                        Text(
                                            text = "• ${('A'.code + i).toChar()}. $opt",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Official Answer Box (Matching PDF style "Answer: 30")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ServiceTealBg,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, ServiceTealBorder)
                                ) {
                                    Text(
                                        text = q.answerLabel.ifEmpty { "Answer: ${q.correctNumericAnswer}" },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = PrimaryTealDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandedAnswers = if (isExpanded) expandedAnswers - q.id else expandedAnswers + q.id
                                    }
                                ) {
                                    Text(
                                        text = if (isExpanded) "লুকান" else "ব্যাখ্যা",
                                        fontSize = 11.sp,
                                        color = PrimaryTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = q.explanation,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp)
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // === TAB 1: REGISTRATION STATS (from PDF Pages 2, 3, 4, 5) ===
            if (selectedTab == 1) {
                item {
                    // Total Registration Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ServiceGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, tint = ServiceGreen, modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Total Registrations", fontSize = 12.sp, color = TextSecondary)
                                Text("11,621", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTealDark)
                                Text("Total Institutes: 4,740 across 64 Districts", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Category & Gender Stats
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Registration by Category", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
                            Spacer(modifier = Modifier.height(10.dp))
                            StatProgressBar("Junior (Class 6 - 8)", "4,900 (42%)", 0.42f, ServiceGreen)
                            StatProgressBar("Secondary (Class 9 - 10)", "4,511 (39%)", 0.39f, ServiceBlue)
                            StatProgressBar("Primary (Class 3 - 5)", "2,174 (19%)", 0.19f, ServiceAmber)
                            StatProgressBar("Special (Class 11 - 12)", "36 (0%)", 0.01f, Color.Gray)

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Registration by Gender", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
                            Spacer(modifier = Modifier.height(10.dp))
                            StatProgressBar("Male", "7,366 (63%)", 0.63f, ServiceBlue)
                            StatProgressBar("Female", "4,255 (37%)", 0.37f, Color(0xFFE91E63))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Divisional Breakdown (Page 3 & 4)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Top Divisions Registration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
                            Spacer(modifier = Modifier.height(10.dp))
                            StatProgressBar("Dhaka", "3,597 (31%)", 0.31f, PrimaryTeal)
                            StatProgressBar("Rangpur", "2,355 (20%)", 0.20f, ServiceBlue)
                            StatProgressBar("Rajshahi", "1,540 (13%)", 0.13f, ServiceGreen)
                            StatProgressBar("Chattogram", "1,538 (13%)", 0.13f, ServiceAmber)
                            StatProgressBar("Mymensingh", "973 (8%)", 0.08f, Color(0xFF8B5CF6))
                            StatProgressBar("Khulna", "798 (7%)", 0.07f, Color(0xFFEC4899))
                            StatProgressBar("Sylhet", "595 (5%)", 0.05f, Color(0xFF06B6D4))
                            StatProgressBar("Barisal", "225 (2%)", 0.02f, Color(0xFFF97316))
                        }
                    }
                }
            }

            // === TAB 2: USERS LIST (from PDF Pages 1, 8, 10, 11, 12, 13) ===
            if (selectedTab == 2) {
                item {
                    OutlinedTextField(
                        value = userSearchQuery,
                        onValueChange = { userSearchQuery = it },
                        placeholder = { Text("Search by Name, Username, Email or Mobile...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(filteredUsers) { u ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = u.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Username: ${u.username} • ${u.classGrade}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = if (u.role == com.example.model.UserRole.MANAGER) ServiceAmberBg else ServiceGreenBg,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (u.role == com.example.model.UserRole.MANAGER) ServiceAmberBorder else ServiceGreenBorder)
                                ) {
                                    Text(
                                        text = if (u.role == com.example.model.UserRole.MANAGER) "Manager" else "Student",
                                        color = if (u.role == com.example.model.UserRole.MANAGER) Color(0xFFB45309) else ServiceGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Email: ${u.email}", fontSize = 11.sp, color = TextSecondary)
                            Text("Mobile: ${u.mobile}", fontSize = 11.sp, color = TextSecondary)
                            Text("Institute: ${u.instituteName}", fontSize = 11.sp, color = TextSecondary)
                            Text("Location: ${u.upazila}, ${u.district}", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatProgressBar(label: String, value: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = TextPrimary)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
