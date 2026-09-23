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
import com.example.ui.components.CentralizedAdminDashboardComponent
import com.example.ui.components.QuestionDiagram
import com.example.ui.theme.*
import com.example.ui.viewmodel.BdjsoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: BdjsoViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stats, 1: Questions, 2: Users
    var questionPage by remember { mutableIntStateOf(1) } // 1: Primary (PDF Page 6-7), 2: Junior (PDF Page 14)
    var userSearchQuery by remember { mutableStateOf("") }
    var expandedAnswers by remember { mutableStateOf(setOf<Int>()) }
    var showTotalInstitutesDialog by remember { mutableStateOf(false) }

    val top15Institutes = remember {
        listOf(
            InstituteStat(1, "Rangdhanu Model School", 504, "4%", "Dinajpur", Color(0xFF6B21A8)),
            InstituteStat(2, "Cantonment Public School and College, Saidpur", 170, "1%", "Nilphamari", Color(0xFF2DD4BF)),
            InstituteStat(3, "Cantonment Public School and College Saidpur", 152, "1%", "Nilphamari", Color(0xFF14B8A6)),
            InstituteStat(4, "Birol Residential Public School", 145, "1%", "Dinajpur", Color(0xFF0F766E)),
            InstituteStat(5, "Savar Cantonment public school and College", 138, "1%", "Dhaka", Color(0xFF115E59)),
            InstituteStat(6, "Rajuk Uttara Model College", 109, "1%", "Dhaka", Color(0xFFA3E635)),
            InstituteStat(7, "Birol city school and college", 104, "1%", "Dinajpur", Color(0xFF99F6E4)),
            InstituteStat(8, "Birol Green School", 91, "1%", "Dinajpur", Color(0xFFFCD34D)),
            InstituteStat(9, "FARAKKABAD NUROL ISLAM SCHOOL AND COLLEGE", 90, "1%", "Dinajpur", Color(0xFFDC2626)),
            InstituteStat(10, "BUNIADPUR IDEAL CADET SCHOOL", 88, "1%", "Dinajpur", Color(0xFF86EFAC)),
            InstituteStat(11, "St. Joseph Higher Secondary School", 78, "1%", "Dhaka", Color(0xFF9333EA)),
            InstituteStat(12, "Cantonment public school and college", 75, "1%", "Bogura", Color(0xFFEA580C)),
            InstituteStat(13, "champion Academy", 72, "1%", "Dhaka", Color(0xFF06B6D4)),
            InstituteStat(14, "Dhaka Residential Model College", 71, "1%", "Dhaka", Color(0xFF334155)),
            InstituteStat(15, "Mymensingh Zilla school", 68, "1%", "Mymensingh", Color(0xFF701A75))
        )
    }

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
                            text = "online.bdjso.org/admin/stats",
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
                .padding(horizontal = 14.dp)
                .testTag("admin_dashboard_content"),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Three Tabs: Tab 0 is Registration Stat (Prominent & Default)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryTeal
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("📊 Registration Stat", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Questions List", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Users (${allUsers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // === TAB 0: REGISTRATION STATS & CENTRALIZED DASHBOARD ===
            if (selectedTab == 0) {
                // Centralized Operational Dashboard (Students, Submissions, Upcoming Events)
                item {
                    CentralizedAdminDashboardComponent(
                        viewModel = viewModel,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Header (online.bdjso.org/admin/stats)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dashboard",
                            fontSize = 12.sp,
                            color = PortalMutedText,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Sunday, August 23, 2026 9:04:43 am",
                            fontSize = 11.sp,
                            color = PortalMutedText
                        )
                    }
                }

                // Title Banner: Total Registration: 11,621 | Total Institutes: 4,740
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, PortalBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Assessment,
                                    contentDescription = "Stats",
                                    tint = PortalDarkText,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Registration Stat",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PortalDarkText
                                )
                            }
                            Text(
                                text = "Sun, August 23, 2026",
                                fontSize = 12.sp,
                                color = PortalMutedText,
                                modifier = Modifier.padding(start = 32.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = PortalBorderColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("• ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PortalDarkText)
                                    Text("Total Registration: ", fontSize = 13.sp, color = PortalDarkText)
                                    Text("11,621", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PortalDarkText)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("• ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PortalDarkText)
                                    Text("Total Institutes: ", fontSize = 13.sp, color = PortalDarkText)
                                    Text("4,740", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PortalDarkText)
                                }
                            }
                        }
                    }
                }

                // 1. Old - New Student Ratio
                item {
                    WebDonutCard(
                        title = "Old - New Student Ratio",
                        slices = listOf(
                            WebDonutSlice("Old", 141f, "1%", ChartBlue),
                            WebDonutSlice("New", 11480f, "99%", ChartYellow)
                        ),
                        totalLabel = "Total Registration: 11,621"
                    )
                }

                // 2. Registration by Gender
                item {
                    WebDonutCard(
                        title = "Registration by Gender",
                        slices = listOf(
                            WebDonutSlice("Male", 7366f, "63%", ChartBlue),
                            WebDonutSlice("Female", 4255f, "37%", ChartYellow)
                        ),
                        totalLabel = "Total Registration: 11,621"
                    )
                }

                // 3. Registration by Category
                item {
                    WebDonutCard(
                        title = "Registration by Category",
                        slices = listOf(
                            WebDonutSlice("Junior", 4900f, "42%", ChartBlue),
                            WebDonutSlice("Secondary", 4511f, "39%", ChartYellow),
                            WebDonutSlice("Primary", 2174f, "19%", ChartPurple),
                            WebDonutSlice("Special", 36f, "0%", ChartPink)
                        ),
                        totalLabel = "Total Registration: 11,621"
                    )
                }

                // 4. Registration by Division
                item {
                    WebDonutCard(
                        title = "Registration by Division",
                        slices = listOf(
                            WebDonutSlice("Dhaka", 3597f, "31%", ChartBlue),
                            WebDonutSlice("Rangpur", 2355f, "20%", ChartYellow),
                            WebDonutSlice("Rajshahi", 1540f, "13%", Color(0xFFC084FC)),
                            WebDonutSlice("Chattagram", 1538f, "13%", ChartRose),
                            WebDonutSlice("Mymensingh", 970f, "8%", ChartTeal),
                            WebDonutSlice("Khulna", 798f, "7%", ChartGreen),
                            WebDonutSlice("Sylhet", 595f, "5%", ChartSkyBlue),
                            WebDonutSlice("Barisal", 228f, "2%", ChartAmber)
                        ),
                        totalLabel = "Total Registration: 11,621"
                    )
                }

                // 5. Registration by Class
                item {
                    WebClassBarChartCard()
                }

                // 6. Registration by Institute (Top: 15 of 4,740) // View Total List
                item {
                    WebInstituteBarChartCard(
                        institutes = top15Institutes,
                        onViewTotalList = { showTotalInstitutesDialog = true }
                    )
                }
            }

            // === TAB 1: QUESTIONS LIST (from PDF Pages 6, 7, 14) ===
            if (selectedTab == 1) {
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

        if (showTotalInstitutesDialog) {
            TotalInstitutesDialog(
                topInstitutes = top15Institutes,
                onDismiss = { showTotalInstitutesDialog = false }
            )
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
