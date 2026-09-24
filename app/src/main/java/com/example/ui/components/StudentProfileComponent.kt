package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ResultEntity
import com.example.data.model.StudentEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BdjsoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * StudentProfile Component:
 * Fetches and displays individual student registration details and their participation history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfile(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier,
    studentRegId: String? = null,
    onClose: (() -> Unit)? = null
) {
    StudentProfileComponent(
        viewModel = viewModel,
        modifier = modifier,
        initialStudentRegId = studentRegId,
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileComponent(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier,
    initialStudentRegId: String? = null,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentRegIdState by viewModel.currentStudentRegId.collectAsStateWithLifecycle()
    val allStudents by viewModel.students.collectAsStateWithLifecycle()
    val allExams by viewModel.exams.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var activeRegId by remember(initialStudentRegId, currentRegIdState) {
        mutableStateOf(
            initialStudentRegId
                ?: if (currentRegIdState.isNotBlank()) currentRegIdState
                else allStudents.firstOrNull()?.registrationId ?: "BDJSO-2026-000101"
        )
    }

    // Dynamic fetch for individual student using Flow
    val studentFlow = remember(activeRegId) { viewModel.getStudentFlow(activeRegId) }
    val fetchedStudent by studentFlow.collectAsStateWithLifecycle(initialValue = null)
    val student = fetchedStudent ?: allStudents.find { it.registrationId == activeRegId }

    // Dynamic fetch for individual student's participation history (attempts)
    val attemptsFlow = remember(activeRegId) { viewModel.getStudentAttempts(activeRegId) }
    val fetchedAttempts by attemptsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Dynamic fetch for official merit result
    val resultFlow = remember(activeRegId) { viewModel.getStudentResult(activeRegId) }
    val studentResult by resultFlow.collectAsStateWithLifecycle(initialValue = null)

    var activeTab by remember { mutableIntStateOf(0) } // 0: Details, 1: Participation History, 2: Admit Card
    var showStudentSelector by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showStatusConfirmDialog by remember { mutableStateOf<String?>(null) }

    val examMap = remember(allExams) { allExams.associateBy { it.id } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("student_profile_component")
    ) {
        // Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onClose != null) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("student_profile_close_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ScienceTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column {
                        Text(
                            text = "Student Profile & History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Reg ID: $activeRegId",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ScienceTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Switch Student Dropdown / Lookup (especially useful in Admin or Testing mode)
                    OutlinedButton(
                        onClick = { showStudentSelector = !showStudentSelector },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("switch_student_button")
                    ) {
                        Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Switch", fontSize = 11.sp)
                    }
                }
            }
        }

        // Student Switcher Expansion
        AnimatedVisibility(visible = showStudentSelector) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Select Candidate to View Dossier",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or Reg ID...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val filteredList = allStudents.filter {
                        searchQuery.isBlank() ||
                                it.fullName.contains(searchQuery, ignoreCase = true) ||
                                it.registrationId.contains(searchQuery, ignoreCase = true) ||
                                it.schoolName.contains(searchQuery, ignoreCase = true)
                    }.take(6)

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredList) { st ->
                            val isSelected = st.registrationId == activeRegId
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    activeRegId = st.registrationId
                                    viewModel.setStudentRegId(st.registrationId)
                                    showStudentSelector = false
                                },
                                label = {
                                    Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text(st.fullName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(st.registrationId, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ScienceTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Tab Navigation
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ScienceTeal
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Registration Details", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_registration_details")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Participation History", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        if (fetchedAttempts.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BdjsoEmerald)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${fetchedAttempts.size}",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_participation_history")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Admit Slip", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_admit_slip")
            )
        }

        // Body Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Student Hero Banner Card (Common across views)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(PrimaryDarkNavy, Color(0xFF1E3A5F), ScienceTeal.copy(alpha = 0.8f))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val statusColor = when (student?.status?.uppercase()) {
                                        "VERIFIED" -> BdjsoEmerald
                                        "PENDING" -> OlympiadGold
                                        "REJECTED" -> StatusDanger
                                        else -> BdjsoEmerald
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(statusColor)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = student?.status ?: "VERIFIED",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = student?.categoryId ?: "PRIMARY",
                                            color = ElectricCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = student?.fullName ?: "Unknown Student",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (!student?.banglaName.isNullOrBlank()) {
                                    Text(
                                        text = student?.banglaName ?: "",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("RegID", activeRegId))
                                        Toast.makeText(context, "Copied Reg ID: $activeRegId", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text(
                                        text = activeRegId,
                                        color = OlympiadGold,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy ID",
                                        tint = OlympiadGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Avatar initials
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(2.dp, ElectricCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = (student?.fullName ?: "ST")
                                    .split(" ")
                                    .filter { it.isNotBlank() }
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                    .ifEmpty { "ST" }

                                Text(
                                    text = initials,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Status Actions for Admin Role
            if (currentRole != "STUDENT" && student != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Admin Status Control:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.updateStudentStatus(student.registrationId, "VERIFIED")
                                        Toast.makeText(context, "Student marked as VERIFIED", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("admin_approve_student_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verify", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateStudentStatus(student.registrationId, "REJECTED")
                                        Toast.makeText(context, "Student marked as REJECTED", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                                    border = BorderStroke(1.dp, StatusDanger)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 0: Comprehensive Registration Details
            if (activeTab == 0) {
                if (student == null) {
                    item {
                        EmptyProfileView(regId = activeRegId)
                    }
                } else {
                    // Academic Profile
                    item {
                        ProfileSectionCard(
                            title = "Academic Information",
                            icon = Icons.Default.School,
                            iconTint = ScienceTeal
                        ) {
                            ProfileFieldRow("School / Institution", student.schoolName)
                            ProfileFieldRow("Institution Type", student.institutionType)
                            ProfileFieldRow("Olympiad Category", student.categoryId)
                            ProfileFieldRow("Class Grade", student.className)
                            ProfileFieldRow("Class Roll Number", student.studentRoll)
                        }
                    }

                    // Personal & Identity Profile
                    item {
                        ProfileSectionCard(
                            title = "Personal & Contact Details",
                            icon = Icons.Default.Person,
                            iconTint = BdjsoEmerald
                        ) {
                            ProfileFieldRow("Date of Birth", student.dob)
                            ProfileFieldRow("Gender", student.gender)
                            ProfileFieldRow("Blood Group", student.bloodGroup)
                            ProfileFieldRow("Student Mobile", student.mobile)
                            ProfileFieldRow("Email Address", student.email)
                        }
                    }

                    // Regional & Address Profile
                    item {
                        ProfileSectionCard(
                            title = "Regional & Address Information",
                            icon = Icons.Default.LocationOn,
                            iconTint = OlympiadGold
                        ) {
                            ProfileFieldRow("Division", student.division)
                            ProfileFieldRow("District", student.district)
                            ProfileFieldRow("Upazila / Thana", student.upazila)
                            ProfileFieldRow("Present Address", student.presentAddress)
                            if (student.permanentAddress.isNotBlank()) {
                                ProfileFieldRow("Permanent Address", student.permanentAddress)
                            }
                        }
                    }

                    // Guardian Details
                    item {
                        ProfileSectionCard(
                            title = "Parent / Guardian Information",
                            icon = Icons.Default.SupervisorAccount,
                            iconTint = Color(0xFF8B5CF6)
                        ) {
                            ProfileFieldRow("Guardian Name", student.guardianName)
                            ProfileFieldRow("Relationship", student.guardianRelation)
                            ProfileFieldRow("Guardian Contact", student.guardianMobile)
                        }
                    }

                    // Registration Timestamp & Meta
                    item {
                        val regDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(student.registeredAt))
                        ProfileSectionCard(
                            title = "Registration System Meta",
                            icon = Icons.Default.Info,
                            iconTint = Color(0xFF64748B)
                        ) {
                            ProfileFieldRow("Official Reg ID", student.registrationId)
                            ProfileFieldRow("Verification Status", student.status)
                            ProfileFieldRow("Registration Timestamp", regDate)
                            ProfileFieldRow("Database Record", "Room SQLite Persistent")
                        }
                    }
                }
            }

            // Tab 1: Olympiad Participation History
            if (activeTab == 1) {
                // Summary KPI Row
                item {
                    ParticipationSummaryHeader(
                        attempts = fetchedAttempts,
                        result = studentResult
                    )
                }

                if (fetchedAttempts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Exam Participation History Recorded",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "This candidate has registered but has not completed any scheduled Olympiad rounds or mock exams yet. Participation records will automatically populate upon exam submission.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Examination Records & Detailed Attempts (${fetchedAttempts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(fetchedAttempts) { attempt ->
                        val exam = examMap[attempt.examId]
                        ParticipationAttemptCard(
                            attempt = attempt,
                            exam = exam,
                            result = if (attempt.examId == (studentResult?.examId ?: -1L)) studentResult else null
                        )
                    }
                }

                // Official Result Merit Card (if available)
                if (studentResult != null) {
                    item {
                        OfficialMeritStandingCard(result = studentResult!!)
                    }
                }
            }

            // Tab 2: Printable Admit Slip Preview
            if (activeTab == 2) {
                item {
                    student?.let { s ->
                        PrintableAdmitCardView(
                            student = s,
                            onDownloadPdf = {
                                viewModel.showSnackbar("Admit Card downloaded for ${s.fullName} (${s.registrationId})")
                                Toast.makeText(context, "Admit Card PDF Generated", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } ?: EmptyProfileView(regId = activeRegId)
                }
            }
        }
    }
}

/**
 * Summary cards showing total attempts, top score, and qualification status.
 */
@Composable
private fun ParticipationSummaryHeader(
    attempts: List<ExamAttemptEntity>,
    result: ResultEntity?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Olympiad Track Record",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Attempts
                ParticipationMetricBox(
                    label = "Total Attempts",
                    value = "${attempts.size}",
                    color = ScienceTeal,
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f)
                )

                // Top Score or Latest Result
                val topScore = attempts.maxOfOrNull { it.score } ?: (result?.totalMarks ?: 0.0)
                ParticipationMetricBox(
                    label = "Best Score",
                    value = String.format(Locale.US, "%.1f", topScore),
                    color = BdjsoEmerald,
                    icon = Icons.Default.EmojiEvents,
                    modifier = Modifier.weight(1f)
                )

                // Merit Rank
                val rankDisplay = result?.let { "#${it.rank}" } ?: "Pending"
                ParticipationMetricBox(
                    label = "Merit Rank",
                    value = rankDisplay,
                    color = OlympiadGold,
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ParticipationMetricBox(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

/**
 * Individual examination attempt history card.
 */
@Composable
private fun ParticipationAttemptCard(
    attempt: ExamAttemptEntity,
    exam: ExamEntity?,
    result: ResultEntity?
) {
    val submitTimeStr = if (attempt.submitTime > 0) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(attempt.submitTime))
    } else {
        "In Progress / Saved Session"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Exam Title & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam?.title ?: "BDJSO Olympiad Exam (Session #${attempt.examId})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Submitted: $submitTimeStr",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val statusBg = when (attempt.status.uppercase()) {
                    "EVALUATED" -> BdjsoEmerald
                    "SUBMITTED" -> ScienceTeal
                    "IN_PROGRESS" -> OlympiadGold
                    else -> ScienceTeal
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = attempt.status,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Score & Accuracy Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ScienceTeal.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text("Final Score", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = String.format(Locale.US, "%.1f pts", attempt.score),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ScienceTeal
                        )
                    }
                }

                // Correct
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BdjsoEmerald.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text("Correct", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${attempt.correctCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BdjsoEmerald
                        )
                    }
                }

                // Wrong
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusDanger.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text("Wrong", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${attempt.wrongCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StatusDanger
                        )
                    }
                }

                // Unattempted
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF64748B).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text("Skipped", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${attempt.unattemptedCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Subject breakdown if linked to official result
            if (result != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subject Breakdown: Physics: ${result.physicsMarks} • Chem: ${result.chemistryMarks} • Bio: ${result.biologyMarks} • Math: ${result.mathMarks}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Official Merit Standing card from ResultEntity.
 */
@Composable
private fun OfficialMeritStandingCard(result: ResultEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = OlympiadGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Official Olympiad Standing",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OlympiadGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Rank #${result.rank}",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Marks", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(
                        "${result.totalMarks} pts",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Percentage", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(
                        String.format(Locale.US, "%.1f%%", result.percentage),
                        color = ElectricCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Merit Status", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(
                        result.selectionStatus,
                        color = if (result.selectionStatus == "SELECTED") BdjsoEmerald else OlympiadGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Reusable Section Card for Profile Details.
 */
@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

/**
 * A standard key-value display row for profile fields.
 */
@Composable
private fun ProfileFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.ifBlank { "N/A" },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
    }
}

/**
 * Printable Admit Card View.
 */
@Composable
private fun PrintableAdmitCardView(
    student: StudentEntity,
    onDownloadPdf: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Official Registration Slip Preview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Button(
                    onClick = onDownloadPdf,
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save PDF", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Slip Boundary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ScienceTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BANGLADESH JUNIOR SCIENCE OLYMPIAD 2026",
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryDarkNavy,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Society for the Popularization of Science, Bangladesh (SPSB)",
                                fontSize = 9.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = PrimaryDarkNavy, modifier = Modifier.size(32.dp))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFCBD5E1))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Student Name: ${student.fullName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                            if (student.banglaName.isNotBlank()) {
                                Text("Bangla Name: ${student.banglaName}", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            Text("Registration ID: ${student.registrationId}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = ScienceTeal, fontSize = 11.sp)
                            Text("Category: ${student.categoryId} (${student.className})", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Black)
                            Text("Roll: ${student.studentRoll} • DOB: ${student.dob}", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFCBD5E1))

                    Text("Institution: ${student.schoolName}", fontSize = 11.sp, color = Color.Black)
                    Text("Division: ${student.division} • District: ${student.district}", fontSize = 11.sp, color = Color.DarkGray)
                    Text("Emergency Contact: ${student.guardianName} (${student.guardianMobile})", fontSize = 11.sp, color = Color.DarkGray)

                    HorizontalDivider(color = Color(0xFFCBD5E1))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS: ${student.status}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BdjsoEmerald
                        )
                        Text(
                            text = "OFFICIAL CANDIDATE COPY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Empty view when student record is not found for a given ID.
 */
@Composable
private fun EmptyProfileView(regId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PersonOff, contentDescription = null, tint = OlympiadGold, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Student Record Not Found",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "No registration records found for ID \"$regId\". Please check the registration number or use the switcher above to select a registered participant.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Modal Dialog wrapper to display the Student Profile anywhere in the app.
 */
@Composable
fun StudentProfileDialog(
    viewModel: BdjsoViewModel,
    studentRegId: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            StudentProfileComponent(
                viewModel = viewModel,
                initialStudentRegId = studentRegId,
                onClose = onDismiss
            )
        }
    }
}
