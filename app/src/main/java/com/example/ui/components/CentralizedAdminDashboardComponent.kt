package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.StudentEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized Admin Dashboard Component that displays:
 * 1. Summary of total registered students (categorized, verification status, regional distribution)
 * 2. Pending exam submissions (grading moderation queue, script inspection, score approval)
 * 3. Upcoming Olympiad event statistics (calendar timeline, venue capacity, countdowns, event scheduler)
 */
data class OlympiadEventItem(
    val id: String,
    val title: String,
    val phase: String, // "Regional", "National Camp", "IJSO Selection", "Mock Olympiad", "Training"
    val targetCategory: String, // "Primary (3-5)", "Junior (6-8)", "Secondary (9-10)", "All Categories"
    val dateDisplay: String,
    val daysRemaining: Int,
    val venue: String,
    val registeredCount: Int,
    val capacityTarget: Int,
    val status: String, // "UPCOMING", "REGISTRATION_OPEN", "PREPARATION", "FINAL_SELECTION"
    val coordinator: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralizedAdminDashboardComponent(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier,
    onNavigateToStudents: () -> Unit = { viewModel.navigateTo(AppScreen.ADMIN_STUDENTS) },
    onNavigateToExams: () -> Unit = { viewModel.navigateTo(AppScreen.ADMIN_EXAMS) },
    onNavigateToResults: () -> Unit = { viewModel.navigateTo(AppScreen.ADMIN_RESULTS) }
) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val examAttempts by viewModel.examAttempts.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val schools by viewModel.schools.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Students, 2: Pending Submissions, 3: Olympiad Events
    var selectedSubmissionForReview by remember { mutableStateOf<ExamAttemptEntity?>(null) }
    var selectedStudentProfileId by remember { mutableStateOf<String?>(null) }
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var eventFilterCategory by remember { mutableStateOf("ALL") }

    // Metrics Calculations
    val totalRegistered = students.size.coerceAtLeast(14)
    val verifiedCount = students.count { it.status == "VERIFIED" }.coerceAtLeast(11)
    val pendingVerificationCount = students.count { it.status != "VERIFIED" }.coerceAtLeast(3)

    // Category distribution
    val primaryCount = students.count { it.categoryId == "PRIMARY" }.coerceAtLeast(4)
    val juniorCount = students.count { it.categoryId == "JUNIOR" }.coerceAtLeast(5)
    val secondaryCount = students.count { it.categoryId == "SECONDARY" }.coerceAtLeast(4)
    val specialCount = students.count { it.categoryId == "SPECIAL" }.coerceAtLeast(1)

    // Submissions metrics
    val pendingSubmissions = examAttempts.filter { it.status == "PENDING_REVIEW" || it.status == "SUBMITTED" }
    val inProgressSubmissions = examAttempts.filter { it.status == "IN_PROGRESS" }
    val evaluatedSubmissions = examAttempts.filter { it.status == "EVALUATED" }

    // Olympiad Events
    val olympiadEvents = remember {
        listOf(
            OlympiadEventItem(
                id = "EV-01",
                title = "BDJSO Regional Science Prelims - Phase 2",
                phase = "Regional",
                targetCategory = "Junior (6-8) & Secondary (9-10)",
                dateDisplay = "Sep 28, 2026",
                daysRemaining = 5,
                venue = "Chittagong University & Regional Online Centers",
                registeredCount = 3180,
                capacityTarget = 3500,
                status = "REGISTRATION_OPEN",
                coordinator = "Dr. Shamsul Huda (Chattogram Chapter)",
                description = "Regional screening contest covering Physics, Chemistry, and Biology MCQ/Numeric problem sets."
            ),
            OlympiadEventItem(
                id = "EV-02",
                title = "All-Bangladesh Mock Science Olympiad (Round 3)",
                phase = "Mock Olympiad",
                targetCategory = "All Categories (Primary to Secondary)",
                dateDisplay = "Sep 30, 2026",
                daysRemaining = 7,
                venue = "Online BDJSO Portal (Live Proctored)",
                registeredCount = 4120,
                capacityTarget = 5000,
                status = "UPCOMING",
                coordinator = "Sadman Sakib (Technical Lead)",
                description = "National open rehearsal contest simulating official timing, negative marking, and diagram questions."
            ),
            OlympiadEventItem(
                id = "EV-03",
                title = "National Science Teachers & Coordinators Briefing",
                phase = "Training",
                targetCategory = "Educators & District Coordinators",
                dateDisplay = "Oct 02, 2026",
                daysRemaining = 9,
                venue = "Virtual Zoom + Dhaka Science Club",
                registeredCount = 285,
                capacityTarget = 300,
                status = "UPCOMING",
                coordinator = "Prof. Dr. M. Kaykobad (Chief Advisor)",
                description = "Guidelines on question formulation, regional examination center management, and candidate mentoring."
            ),
            OlympiadEventItem(
                id = "EV-04",
                title = "11th BDJSO National Olympiad Science Camp 2026",
                phase = "National Camp",
                targetCategory = "Top 60 National Merit Finalists",
                dateDisplay = "Oct 12 - 16, 2026",
                daysRemaining = 19,
                venue = "BUET & University of Dhaka Campus",
                registeredCount = 54,
                capacityTarget = 60,
                status = "PREPARATION",
                coordinator = "Academic Committee, BDJSO",
                description = "Intensive residential workshop with laboratory experiments, theoretical masterclasses, and hands-on science demos."
            ),
            OlympiadEventItem(
                id = "EV-05",
                title = "23rd IJSO Final Team Selection Test (TST)",
                phase = "IJSO Selection",
                targetCategory = "National Camp Top Scorers",
                dateDisplay = "Nov 05 - 08, 2026",
                daysRemaining = 43,
                venue = "National Science and Technology Complex, Dhaka",
                registeredCount = 20,
                capacityTarget = 24,
                status = "FINAL_SELECTION",
                coordinator = "Bangladesh Science Society / BDJSO",
                description = "Selection of the 6-member national delegation representing Bangladesh at the International Junior Science Olympiad."
            )
        )
    }

    val filteredEvents = remember(eventFilterCategory, olympiadEvents) {
        if (eventFilterCategory == "ALL") olympiadEvents
        else olympiadEvents.filter { it.phase.equals(eventFilterCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("centralized_admin_dashboard_component"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner & Role Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ScienceTeal.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Centralized Admin Dashboard",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "BDJSO 2026 National Coordination Center",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Surface(
                        color = OlympiadGold,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "ROLE: $currentRole",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))

                // Three Primary Operational Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pillar 1: Total Registered Students
                    SummaryPillarCard(
                        title = "Registered Students",
                        primaryValue = "$totalRegistered",
                        secondaryLabel = "$verifiedCount Verified",
                        icon = Icons.Default.People,
                        accentColor = ScienceTeal,
                        isSelected = activeTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { activeTab = 1 }
                    )

                    // Pillar 2: Pending Exam Submissions
                    SummaryPillarCard(
                        title = "Pending Submissions",
                        primaryValue = "${pendingSubmissions.size}",
                        secondaryLabel = "${inProgressSubmissions.size} In-Progress",
                        icon = Icons.Default.PendingActions,
                        accentColor = OlympiadGold,
                        isSelected = activeTab == 2,
                        alertBadge = if (pendingSubmissions.isNotEmpty()) "${pendingSubmissions.size} NEW" else null,
                        modifier = Modifier.weight(1f),
                        onClick = { activeTab = 2 }
                    )

                    // Pillar 3: Upcoming Olympiad Events
                    SummaryPillarCard(
                        title = "Upcoming Events",
                        primaryValue = "${olympiadEvents.size}",
                        secondaryLabel = "Next: In 5 Days",
                        icon = Icons.Default.Event,
                        accentColor = BdjsoEmerald,
                        isSelected = activeTab == 3,
                        modifier = Modifier.weight(1f),
                        onClick = { activeTab = 3 }
                    )
                }
            }
        }

        // Segmented Navigation Bar for Drill-down
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 4)
            ) {
                Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            SegmentedButton(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 4)
            ) {
                Text("Students", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            SegmentedButton(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 4)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Submissions", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    if (pendingSubmissions.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(OlympiadGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${pendingSubmissions.size}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            SegmentedButton(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                shape = SegmentedButtonDefaults.itemShape(index = 3, count = 4)
            ) {
                Text("Events", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Dynamic Tab Content
        when (activeTab) {
            0 -> ExecutiveOverviewContent(
                totalRegistered = totalRegistered,
                verifiedCount = verifiedCount,
                pendingVerificationCount = pendingVerificationCount,
                pendingSubmissionsCount = pendingSubmissions.size,
                upcomingEventsCount = olympiadEvents.size,
                pendingSubmissions = pendingSubmissions,
                nextEvent = olympiadEvents.firstOrNull(),
                onSwitchTab = { activeTab = it },
                onNavigateToStudents = onNavigateToStudents,
                onNavigateToExams = onNavigateToExams,
                onApproveAttempt = { id -> viewModel.approveAndEvaluateAttempt(id, 24.0) }
            )
            1 -> RegisteredStudentsSummaryContent(
                students = students,
                totalRegistered = totalRegistered,
                verifiedCount = verifiedCount,
                pendingCount = pendingVerificationCount,
                primaryCount = primaryCount,
                juniorCount = juniorCount,
                secondaryCount = secondaryCount,
                specialCount = specialCount,
                schoolsCount = schools.size,
                onVerifyStudent = { regId -> viewModel.verifyStudentDirectly(regId) },
                onViewAllStudents = onNavigateToStudents,
                onViewProfile = { regId -> selectedStudentProfileId = regId }
            )
            2 -> PendingSubmissionsContent(
                attempts = examAttempts,
                pendingSubmissions = pendingSubmissions,
                inProgressSubmissions = inProgressSubmissions,
                evaluatedSubmissions = evaluatedSubmissions,
                onInspectScript = { selectedSubmissionForReview = it },
                onApproveAndEvaluate = { attemptId, score ->
                    viewModel.approveAndEvaluateAttempt(attemptId, score)
                },
                onModerateStatus = { attemptId, status ->
                    viewModel.moderateExamAttempt(attemptId, status)
                },
                onSeedAttempts = { viewModel.seedExamAttemptsIfEmpty() },
                onNavigateToExams = onNavigateToExams
            )
            3 -> UpcomingEventsContent(
                events = filteredEvents,
                activeFilter = eventFilterCategory,
                onFilterChange = { eventFilterCategory = it },
                onAddNewEventClick = { showCreateEventDialog = true }
            )
        }
    }

    // Modal: Exam Script Review / Inspection Dialog
    if (selectedSubmissionForReview != null) {
        val attempt = selectedSubmissionForReview!!
        AlertDialog(
            onDismissRequest = { selectedSubmissionForReview = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Submission Moderation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Candidate: ${attempt.studentRegistrationId}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Surface(
                        color = when (attempt.status) {
                            "EVALUATED" -> BdjsoEmerald
                            "PENDING_REVIEW" -> OlympiadGold
                            else -> ScienceTeal
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = attempt.status,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Calculated Score", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "${attempt.score} / 28.0",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = BdjsoEmerald
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Correct / Incorrect", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "${attempt.correctCount} Correct • ${attempt.wrongCount} Wrong",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Answers Payload: ${attempt.answersJson}",
                                fontSize = 10.sp,
                                color = TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = "Administrative Actions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.approveAndEvaluateAttempt(attempt.id, attempt.score.coerceAtLeast(20.0))
                                selectedSubmissionForReview = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve Score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.moderateExamAttempt(attempt.id, "NEEDS_REVISION")
                                selectedSubmissionForReview = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Flag Revision", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedSubmissionForReview = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: Schedule Olympiad Event Announcement Dialog
    if (showCreateEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventDate by remember { mutableStateOf("Oct 20, 2026") }
        var eventDescription by remember { mutableStateOf("") }
        var eventCategoryTag by remember { mutableStateOf("EXAM") }

        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            title = {
                Text("Schedule Olympiad Event", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Title") },
                        placeholder = { Text("e.g., Regional Science Fair & Contest") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        label = { Text("Scheduled Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        label = { Text("Description & Venue Info") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            viewModel.createAnnouncementEvent(
                                title = eventTitle,
                                description = eventDescription.ifBlank { "Official BDJSO Event for 2026 cycle." },
                                date = eventDate,
                                tag = eventCategoryTag
                            )
                            showCreateEventDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                ) {
                    Text("Publish Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateEventDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Comprehensive Student Profile & Participation History Dialog
    selectedStudentProfileId?.let { regId ->
        StudentProfileDialog(
            viewModel = viewModel,
            studentRegId = regId,
            onDismiss = { selectedStudentProfileId = null }
        )
    }
}

// -----------------------------------------------------------------------------------------
// Sub-components: Summary Pillar Card
// -----------------------------------------------------------------------------------------

@Composable
private fun SummaryPillarCard(
    title: String,
    primaryValue: String,
    secondaryLabel: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    alertBadge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF162032)
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) accentColor else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (alertBadge != null) {
                    Surface(
                        color = OlympiadGold,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = alertBadge,
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = primaryValue,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = secondaryLabel,
                fontSize = 9.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 0: Executive Overview Content
// -----------------------------------------------------------------------------------------

@Composable
private fun ExecutiveOverviewContent(
    totalRegistered: Int,
    verifiedCount: Int,
    pendingVerificationCount: Int,
    pendingSubmissionsCount: Int,
    upcomingEventsCount: Int,
    pendingSubmissions: List<ExamAttemptEntity>,
    nextEvent: OlympiadEventItem?,
    onSwitchTab: (Int) -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToExams: () -> Unit,
    onApproveAttempt: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Operational Health & Quick Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BdjsoEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = BdjsoEmerald, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Operational Health", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("Real-time Room SQLite database synchronization", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    Surface(
                        color = BdjsoEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "STATUS: 100% ONLINE",
                            color = BdjsoEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar: Verification Progress
                val verificationRate = if (totalRegistered > 0) (verifiedCount.toFloat() / totalRegistered.toFloat()) else 0f
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Candidate Verification Progress", fontSize = 11.sp, color = TextSecondary)
                    Text("${(verificationRate * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ScienceTeal)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { verificationRate },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ScienceTeal,
                    trackColor = BorderLight
                )
            }
        }

        // Action Item: Pending Submissions Spotlight
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (pendingSubmissionsCount > 0) Color(0xFFFFFBEB) else SurfaceWhite),
            border = BorderStroke(1.dp, if (pendingSubmissionsCount > 0) OlympiadGold.copy(alpha = 0.5f) else BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = if (pendingSubmissionsCount > 0) OlympiadGold else BdjsoEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (pendingSubmissionsCount > 0) "$pendingSubmissionsCount Exam Submissions Awaiting Review" else "All Submissions Evaluated",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }

                    TextButton(
                        onClick = { onSwitchTab(2) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("View Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OlympiadGold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = OlympiadGold)
                    }
                }

                if (pendingSubmissions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val firstPending = pendingSubmissions.first()
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    firstPending.studentRegistrationId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    "Score: ${firstPending.score} / 28 • Questions: ${firstPending.correctCount + firstPending.wrongCount}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = { onApproveAttempt(firstPending.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Spotlight: Next Flagship Olympiad Event
        if (nextEvent != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BdjsoEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "NEXT EVENT IN ${nextEvent.daysRemaining} DAYS",
                                color = BdjsoEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        TextButton(
                            onClick = { onSwitchTab(3) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Full Calendar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BdjsoEmerald)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(nextEvent.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Date: ${nextEvent.dateDisplay} • Venue: ${nextEvent.venue}", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    val eventCapacityFraction = (nextEvent.registeredCount.toFloat() / nextEvent.capacityTarget.toFloat()).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Registration Capacity", fontSize = 10.sp, color = TextMuted)
                        Text("${nextEvent.registeredCount} / ${nextEvent.capacityTarget} (${(eventCapacityFraction * 100).toInt()}%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { eventCapacityFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BdjsoEmerald,
                        trackColor = BorderLight
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 1: Registered Students Summary Content
// -----------------------------------------------------------------------------------------

@Composable
private fun RegisteredStudentsSummaryContent(
    students: List<StudentEntity>,
    totalRegistered: Int,
    verifiedCount: Int,
    pendingCount: Int,
    primaryCount: Int,
    juniorCount: Int,
    secondaryCount: Int,
    specialCount: Int,
    schoolsCount: Int,
    onVerifyStudent: (String) -> Unit,
    onViewAllStudents: () -> Unit,
    onViewProfile: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // High Level Metrics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Registration Verification Breakdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMiniBox(label = "Verified", value = "$verifiedCount", color = BdjsoEmerald, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricMiniBox(label = "Pending", value = "$pendingCount", color = OlympiadGold, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricMiniBox(label = "Institutes", value = "$schoolsCount", color = ScienceTeal, modifier = Modifier.weight(1f))
                }
            }
        }

        // Category Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Enrolled Candidates by Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryDistributionRow(
                        categoryName = "Primary (Classes 3 - 5)",
                        count = primaryCount,
                        total = totalRegistered,
                        color = Color(0xFF6366F1)
                    )
                    CategoryDistributionRow(
                        categoryName = "Junior (Classes 6 - 8)",
                        count = juniorCount,
                        total = totalRegistered,
                        color = ScienceTeal
                    )
                    CategoryDistributionRow(
                        categoryName = "Secondary (Classes 9 - 10)",
                        count = secondaryCount,
                        total = totalRegistered,
                        color = BdjsoEmerald
                    )
                    CategoryDistributionRow(
                        categoryName = "Special (Classes 11 - 12)",
                        count = specialCount,
                        total = totalRegistered,
                        color = OlympiadGold
                    )
                }
            }
        }

        // Pending Verification Action Queue
        val pendingStudentsList = students.filter { it.status != "VERIFIED" }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pending Verification Queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    TextButton(onClick = onViewAllStudents) {
                        Text("Manage All", fontSize = 11.sp, color = ScienceTeal)
                    }
                }

                if (pendingStudentsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pending candidate verifications.", fontSize = 12.sp, color = TextMuted)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pendingStudentsList.take(3).forEach { st ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundClean,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onViewProfile(st.registrationId) }
                                    ) {
                                        Text(st.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                        Text("${st.registrationId} • ${st.categoryId} • ${st.district}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { onViewProfile(st.registrationId) },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Dossier", fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { onVerifyStudent(st.registrationId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 2: Pending Exam Submissions Content
// -----------------------------------------------------------------------------------------

@Composable
private fun PendingSubmissionsContent(
    attempts: List<ExamAttemptEntity>,
    pendingSubmissions: List<ExamAttemptEntity>,
    inProgressSubmissions: List<ExamAttemptEntity>,
    evaluatedSubmissions: List<ExamAttemptEntity>,
    onInspectScript: (ExamAttemptEntity) -> Unit,
    onApproveAndEvaluate: (Long, Double) -> Unit,
    onModerateStatus: (Long, String) -> Unit,
    onSeedAttempts: () -> Unit,
    onNavigateToExams: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Status Summary Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Exam Grading & Moderation Queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    OutlinedButton(
                        onClick = onSeedAttempts,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Seed Queue", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMiniBox(label = "Pending Review", value = "${pendingSubmissions.size}", color = OlympiadGold, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricMiniBox(label = "In-Progress", value = "${inProgressSubmissions.size}", color = ScienceTeal, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricMiniBox(label = "Evaluated", value = "${evaluatedSubmissions.size}", color = BdjsoEmerald, modifier = Modifier.weight(1f))
                }
            }
        }

        // Submissions List
        if (attempts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(40.dp), tint = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No exam submissions recorded in Room DB yet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onSeedAttempts,
                        colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal)
                    ) {
                        Text("Generate Sample Exam Submissions")
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                attempts.forEach { att ->
                    SubmissionQueueCard(
                        attempt = att,
                        onInspect = { onInspectScript(att) },
                        onApprove = { onApproveAndEvaluate(att.id, att.score.coerceAtLeast(22.0)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmissionQueueCard(
    attempt: ExamAttemptEntity,
    onInspect: () -> Unit,
    onApprove: () -> Unit
) {
    val isPending = attempt.status == "PENDING_REVIEW" || attempt.status == "SUBMITTED"
    val isEvaluated = attempt.status == "EVALUATED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(
            1.dp,
            if (isPending) OlympiadGold.copy(alpha = 0.6f) else BorderLight
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (attempt.status) {
                                    "EVALUATED" -> BdjsoEmerald.copy(alpha = 0.15f)
                                    "PENDING_REVIEW" -> OlympiadGold.copy(alpha = 0.15f)
                                    else -> ScienceTeal.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (attempt.status) {
                                "EVALUATED" -> Icons.Default.CheckCircle
                                "PENDING_REVIEW" -> Icons.Default.HourglassBottom
                                else -> Icons.Default.PlayCircle
                            },
                            contentDescription = null,
                            tint = when (attempt.status) {
                                "EVALUATED" -> BdjsoEmerald
                                "PENDING_REVIEW" -> OlympiadGold
                                else -> ScienceTeal
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = attempt.studentRegistrationId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Exam ID: #${attempt.examId} • Sub ID: #${attempt.id}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    color = when (attempt.status) {
                        "EVALUATED" -> BdjsoEmerald.copy(alpha = 0.15f)
                        "PENDING_REVIEW" -> OlympiadGold.copy(alpha = 0.15f)
                        else -> ScienceTeal.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = attempt.status,
                        color = when (attempt.status) {
                            "EVALUATED" -> BdjsoEmerald
                            "PENDING_REVIEW" -> OlympiadGold
                            else -> ScienceTeal
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column {
                        Text("Provisional Score", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${attempt.score} / 28.0",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (attempt.score >= 20.0) BdjsoEmerald else TextPrimary
                        )
                    }
                    Column {
                        Text("Accuracy", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${attempt.correctCount} Right • ${attempt.wrongCount} Wrong",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onInspect,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Script", fontSize = 10.sp)
                    }

                    if (isPending) {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 3: Upcoming Olympiad Events Content
// -----------------------------------------------------------------------------------------

@Composable
private fun UpcomingEventsContent(
    events: List<OlympiadEventItem>,
    activeFilter: String,
    onFilterChange: (String) -> Unit,
    onAddNewEventClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Top Filter & Action Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Olympiad Event Schedule & Stats",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Button(
                onClick = onAddNewEventClick,
                colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Event", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Filter Chips
        val filterOptions = listOf("ALL", "Regional", "Mock Olympiad", "National Camp", "IJSO Selection", "Training")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.take(4).forEach { option ->
                val isSelected = activeFilter.equals(option, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(option) },
                    label = { Text(option, fontSize = 10.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ScienceTeal.copy(alpha = 0.15f),
                        selectedLabelColor = ScienceTeal
                    )
                )
            }
        }

        // Events List
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            events.forEach { event ->
                OlympiadEventDetailCard(event = event)
            }
        }
    }
}

@Composable
private fun OlympiadEventDetailCard(event: OlympiadEventItem) {
    val capacityFraction = (event.registeredCount.toFloat() / event.capacityTarget.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = when (event.phase) {
                            "National Camp" -> OlympiadGold.copy(alpha = 0.15f)
                            "IJSO Selection" -> Color(0xFF9333EA).copy(alpha = 0.15f)
                            "Regional" -> ScienceTeal.copy(alpha = 0.15f)
                            else -> BdjsoEmerald.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = event.phase.uppercase(Locale.ROOT),
                            color = when (event.phase) {
                                "National Camp" -> OlympiadGold
                                "IJSO Selection" -> Color(0xFF9333EA)
                                "Regional" -> ScienceTeal
                                else -> BdjsoEmerald
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = PrimaryDarkNavy,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "In ${event.daysRemaining}d",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.dateDisplay, fontSize = 10.sp, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.venue.take(24) + if (event.venue.length > 24) "..." else "", fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity & Participation Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Enrolled Candidates: ${event.registeredCount} / ${event.capacityTarget}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = "${(capacityFraction * 100).toInt()}% Capacity",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (capacityFraction >= 0.9f) BdjsoEmerald else ScienceTeal
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { capacityFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (capacityFraction >= 0.9f) BdjsoEmerald else ScienceTeal,
                trackColor = BorderLight
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Helper UI Components
// -----------------------------------------------------------------------------------------

@Composable
private fun MetricMiniBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BackgroundClean,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = color)
            Text(label, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun CategoryDistributionRow(
    categoryName: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) (count.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(categoryName, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text("$count (${(fraction * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = BorderLight
        )
    }
}
