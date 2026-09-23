package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolunteerActivism
import com.example.ui.components.QuestionDiagramViewer
import com.example.ui.components.StudentPerformanceDashboardComponent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudentEntity
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val schools by viewModel.schools.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val results by viewModel.allResults.collectAsStateWithLifecycle()
    val volunteers by viewModel.volunteers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    val verifiedStudents = students.count { it.status == "VERIFIED" }
    val selectedStudents = results.count { it.selectionStatus == "SELECTED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BDJSO Control Center",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active Role: $currentRole",
                                color = OlympiadGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BdjsoEmerald)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SYSTEM ONLINE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Manager Profile Bar (Screenshots 1 & 9)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectUserByUsername("600032", AppScreen.ADMIN_USER_SHOW)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ScienceTeal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Md. Mursaline Parvez (600032)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "mursalineparvez@gmail.com • Manager",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(OlympiadGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Manager", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action Buttons (Screenshot 2: Clear Cache & Registration Stats)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.clearCache() },
                            colors = ButtonDefaults.buttonColors(containerColor = OlympiadGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Cache", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_REGISTRATION_STATS) },
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_USERS) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Users", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Text(
                text = "Key Operational Metrics",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Registrations",
                        value = "${students.size}",
                        subtitle = "$verifiedStudents Verified",
                        icon = Icons.Default.People,
                        color = ScienceTeal,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_STUDENTS) }
                    )
                    AdminMetricCard(
                        title = "Institutions",
                        value = "${schools.size}",
                        subtitle = "Across 8 Divisions",
                        icon = Icons.Default.School,
                        color = BdjsoEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_SCHOOLS) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Question Bank",
                        value = "${questions.size}",
                        subtitle = "Physics, Chem, Bio, Math",
                        icon = Icons.Default.QuestionAnswer,
                        color = OlympiadGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_QUESTIONS) }
                    )
                    AdminMetricCard(
                        title = "Exams Scheduled",
                        value = "${exams.size}",
                        subtitle = "1 Active Session",
                        icon = Icons.Default.Assignment,
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXAMS) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Merit Results",
                        value = "${results.size}",
                        subtitle = "$selectedStudents Selected",
                        icon = Icons.Default.EmojiEvents,
                        color = BdjsoEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_RESULTS) }
                    )
                    AdminMetricCard(
                        title = "Volunteers",
                        value = "${volunteers.size}",
                        subtitle = "Campus Coordinators",
                        icon = Icons.Default.VolunteerActivism,
                        color = Color(0xFFF43F5E),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_VOLUNTEERS) }
                    )
                }
            }
        }

        // Module Quick Access Navigation
        item {
            Text(
                text = "Administrative Modules",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminModuleRow("User Directory & Permission Roles", "Manage 11,621 users, permissions, profiles, password resets", Icons.Default.ManageAccounts) {
                    viewModel.navigateTo(AppScreen.ADMIN_USERS)
                }
                AdminModuleRow("Registration Analytics & Statistics", "Donut charts, category breakdown, division & daily trends", Icons.Default.ShowChart) {
                    viewModel.navigateTo(AppScreen.ADMIN_REGISTRATION_STATS)
                }
                AdminModuleRow("Student Database & Verification", "Approve applicants, verify schools, view admit cards", Icons.Default.People) {
                    viewModel.navigateTo(AppScreen.ADMIN_STUDENTS)
                }
                AdminModuleRow("Question Bank Management", "Create, edit, categorize questions with negative marking", Icons.Default.QuestionAnswer) {
                    viewModel.navigateTo(AppScreen.ADMIN_QUESTIONS)
                }
                AdminModuleRow("Online Exam Operations", "Control exam states: Scheduled, Live, Ended, Auto-timer", Icons.Default.PlayArrow) {
                    viewModel.navigateTo(AppScreen.ADMIN_EXAMS)
                }
                AdminModuleRow("Merit List & Camp Selection", "Category/district quotas, tie-breaking, publish toggle", Icons.Default.EmojiEvents) {
                    viewModel.navigateTo(AppScreen.ADMIN_RESULTS)
                }
                AdminModuleRow("School & Institution Registry", "Manage partner schools, EIIN numbers, campus focal points", Icons.Default.School) {
                    viewModel.navigateTo(AppScreen.ADMIN_SCHOOLS)
                }
                AdminModuleRow("Volunteer Coordinator Roster", "Coordinate campus ambassadors & exam invigilators", Icons.Default.VolunteerActivism) {
                    viewModel.navigateTo(AppScreen.ADMIN_VOLUNTEERS)
                }
                AdminModuleRow("Announcements & News Feed", "Publish official notices, contest dates, press releases", Icons.Default.Announcement) {
                    viewModel.navigateTo(AppScreen.ADMIN_ANNOUNCEMENTS)
                }
                AdminModuleRow("Security Audit Trail", "Immutable logs of administrative changes and submissions", Icons.Default.Security) {
                    viewModel.navigateTo(AppScreen.ADMIN_AUDIT_LOGS)
                }
                AdminModuleRow("System & Olympiad Settings", "Toggle registration open/closed, year, pass criteria", Icons.Default.Settings) {
                    viewModel.navigateTo(AppScreen.ADMIN_SETTINGS)
                }
            }
        }

        // Recent Audit Stream
        item {
            Text(
                text = "Live Audit Stream",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                auditLogs.take(4).forEach { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ScienceTeal)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${log.action} • ${log.targetId}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = log.details,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = log.userId,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdminModuleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ------------------- SUB-MODULE: STUDENT MANAGEMENT -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudentsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ALL") }
    var selectedStudentDossier by remember { mutableStateOf<StudentEntity?>(null) }

    val filtered = students.filter { s ->
        val matchStatus = selectedStatus == "ALL" || s.status.equals(selectedStatus, ignoreCase = true)
        val matchQuery = searchQuery.isBlank() ||
                s.fullName.contains(searchQuery, ignoreCase = true) ||
                s.registrationId.contains(searchQuery, ignoreCase = true) ||
                s.schoolName.contains(searchQuery, ignoreCase = true) ||
                s.district.contains(searchQuery, ignoreCase = true)
        matchStatus && matchQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Registration Roster", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by Name, Reg ID, School or District") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "VERIFIED", "PENDING", "REJECTED").forEach { st ->
                        item {
                            FilterChip(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st },
                                label = { Text(st) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ScienceTeal, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }

            items(filtered) { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedStudentDossier = s },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = s.registrationId,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScienceTeal
                            )
                            val statusBg = if (s.status == "VERIFIED") BdjsoEmerald else StatusWarning
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(s.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = s.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "${s.schoolName} • ${s.className} (${s.categoryId})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${s.district}, ${s.division} • Mobile: ${s.mobile}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.updateStudentStatus(s.registrationId, "VERIFIED") },
                                colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.updateStudentStatus(s.registrationId, "REJECTED") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusDanger)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reject", fontSize = 11.sp, color = StatusDanger)
                            }
                        }
                    }
                }
            }
        }
    }

    // Student Detailed Dossier Modal
    selectedStudentDossier?.let { s ->
        AlertDialog(
            onDismissRequest = { selectedStudentDossier = null },
            confirmButton = {
                Button(onClick = { selectedStudentDossier = null }) {
                    Text("Close")
                }
            },
            title = { Text(s.fullName, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Reg ID: ${s.registrationId}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text("Bangla Name: ${s.banglaName}")
                    Text("DOB: ${s.dob} • Gender: ${s.gender} • Blood: ${s.bloodGroup}")
                    Text("School: ${s.schoolName} (${s.institutionType})")
                    Text("Class: ${s.className} • Roll: ${s.studentRoll} • Category: ${s.categoryId}")
                    Text("Location: ${s.upazila}, ${s.district}, ${s.division}")
                    Text("Mobile: ${s.mobile} • Email: ${s.email}")
                    Text("Guardian: ${s.guardianName} (${s.guardianRelation}) - ${s.guardianMobile}")
                    Text("Status: ${s.status}", fontWeight = FontWeight.Bold, color = BdjsoEmerald)
                }
            }
        )
    }
}

// ------------------- SUB-MODULE: QUESTION BANK -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuestionsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    var selectedSubject by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = questions.filter { q ->
        selectedSubject == "ALL" || q.subject.equals(selectedSubject, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Olympiad Question Bank", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BdjsoEmerald,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Question")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "Physics", "Chemistry", "Biology", "Mathematics", "Science").forEach { sub ->
                        item {
                            FilterChip(
                                selected = selectedSubject == sub,
                                onClick = { selectedSubject = sub },
                                label = { Text(sub) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ScienceTeal, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }

            items(filtered) { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (q.questionCode.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(OlympiadGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(q.questionCode, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ScienceTeal)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(q.subject, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(q.categoryId, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteQuestion(q.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusDanger, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = q.questionText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                        // Render Scientific / Olympiad Diagram if present
                        if (q.diagramType.isNotBlank()) {
                            QuestionDiagramViewer(diagramType = q.diagramType)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("A. ${q.optionA}", fontSize = 12.sp)
                        Text("B. ${q.optionB}", fontSize = 12.sp)
                        Text("C. ${q.optionC}", fontSize = 12.sp)
                        Text("D. ${q.optionD}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correct Answer: Option ${q.correctAnswer} (+4.0 / -1.0)",
                            fontWeight = FontWeight.Bold,
                            color = BdjsoEmerald,
                            fontSize = 12.sp
                        )
                        if (q.explanation.isNotBlank()) {
                            Text(
                                text = "Explanation: ${q.explanation}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var text by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf("Physics") }
        var category by remember { mutableStateOf("PRIMARY") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf("A") }
        var exp by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (text.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            viewModel.addQuestion(
                                text = text.trim(),
                                category = category,
                                subject = subject,
                                topic = "General Science",
                                diff = "MEDIUM",
                                optA = optA.trim(),
                                optB = optB.trim(),
                                optC = optC.trim().ifBlank { "None of above" },
                                optD = optD.trim().ifBlank { "All of above" },
                                correct = correct,
                                explanation = exp.trim()
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                ) {
                    Text("Save Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            },
            title = { Text("Add Olympiad Question", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Question Text *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = correct, onValueChange = { correct = it }, label = { Text("Correct Key (A/B/C/D)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = exp, onValueChange = { exp = it }, label = { Text("Scientific Explanation") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }
}

// ------------------- SUB-MODULE: EXAM MANAGEMENT -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExamsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.exams.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Management & Scheduling", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(exams) { exam ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                text = exam.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            val statusColor = when (exam.status) {
                                "LIVE" -> Color(0xFFDC2626)
                                "SCHEDULED" -> ScienceTeal
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(exam.status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Category: ${exam.categoryId} • Date: ${exam.examDate}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Window: ${exam.startTime} - ${exam.endTime} • Duration: ${exam.durationMinutes} mins",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Total Questions: ${exam.totalQuestions} • Total Marks: ${exam.totalMarks}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ScienceTeal
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setExamStatus(exam.id, "LIVE") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Go Live", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.setExamStatus(exam.id, "ENDED") },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("End Exam", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.setExamStatus(exam.id, "SCHEDULED") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reset", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- SUB-MODULE: RESULTS & SELECTION -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResultsSelectionScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val results by viewModel.allResults.collectAsStateWithLifecycle()
    val isAnyPublished = results.any { it.isPublished }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selection & Merit Moderation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Evaluation Analytics & Ranking Summary Dashboard
            item {
                StudentPerformanceDashboardComponent(
                    currentStudentResult = results.firstOrNull { it.rank == 1 },
                    allPublishedResults = results,
                    onViewFullLeaderboard = { /* in results admin view */ }
                )
            }

            // Master Publish Toggle Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Publish Results Officially",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isAnyPublished) "Results are currently public on website" else "Results are private / draft mode",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isAnyPublished,
                            onCheckedChange = { checked ->
                                viewModel.setResultsPublished(checked)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BdjsoEmerald)
                        )
                    }
                }
            }

            items(results) { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${res.rank} • ${res.studentName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${res.totalMarks} Marks (${String.format("%.1f", res.percentage)}%)",
                                fontWeight = FontWeight.Bold,
                                color = ScienceTeal,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "Reg: ${res.studentRegistrationId} • ${res.categoryId} • ${res.district}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.updateSelectionStatus(res.id, "SELECTED") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (res.selectionStatus == "SELECTED") BdjsoEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (res.selectionStatus == "SELECTED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Selected", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.updateSelectionStatus(res.id, "WAITING_LIST") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (res.selectionStatus == "WAITING_LIST") OlympiadGold else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (res.selectionStatus == "WAITING_LIST") PrimaryDarkNavy else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Waiting List", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.updateSelectionStatus(res.id, "NOT_SELECTED") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (res.selectionStatus == "NOT_SELECTED") StatusDanger else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (res.selectionStatus == "NOT_SELECTED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Not Selected", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- SUB-MODULE: SCHOOLS DIRECTORY -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSchoolsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val schools by viewModel.schools.collectAsStateWithLifecycle()
    var showAddModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School & Institution Registry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = ScienceTeal,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add School")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(schools) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = s.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Code: ${s.code} • ${s.upazila}, ${s.district}, ${s.division}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Contact: ${s.contactPerson} (${s.mobile})", fontSize = 11.sp, color = ScienceTeal)
                        }
                        IconButton(onClick = { viewModel.deleteSchool(s.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusDanger)
                        }
                    }
                }
            }
        }
    }

    if (showAddModal) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var division by remember { mutableStateOf("Dhaka") }
        var district by remember { mutableStateOf("Dhaka") }
        var upazila by remember { mutableStateOf("Sadar") }
        var contact by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddModal = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addSchool(
                                name = name.trim(),
                                code = code.ifBlank { "SCH-${System.currentTimeMillis() % 10000}" },
                                division = division,
                                district = district,
                                upazila = upazila,
                                contact = contact.ifBlank { "Principal" },
                                phone = mobile.ifBlank { "01700000000" },
                                email = "school@edu.bd"
                            )
                            showAddModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal)
                ) {
                    Text("Add School")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModal = false }) { Text("Cancel") }
            },
            title = { Text("Register Institution", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("School Name *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("EIIN / School Code") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("District") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Focal Teacher Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }
}

// ------------------- SUB-MODULE: VOLUNTEERS -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVolunteersScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val volunteers by viewModel.volunteers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Volunteer Roster", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(volunteers) { v ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(v.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            val isApproved = v.status == "APPROVED"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isApproved) BdjsoEmerald else OlympiadGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(v.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text("${v.university} • ${v.district}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Role: ${v.role} • Phone: ${v.mobile}", fontSize = 11.sp, color = ScienceTeal)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.updateVolunteerStatus(v.id, "APPROVED") },
                                colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Approve", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.updateVolunteerStatus(v.id, "REJECTED") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Reject", fontSize = 11.sp, color = StatusDanger)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- SUB-MODULE: ANNOUNCEMENTS -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Announcements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = BdjsoEmerald,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Notice")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(announcements) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(ann.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Tag: ${ann.tag} • Date: ${ann.publishDate}", fontSize = 11.sp, color = ScienceTeal)
                        }
                        IconButton(onClick = { viewModel.deleteAnnouncement(ann.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var tag by remember { mutableStateOf("Notice") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && desc.isNotBlank()) {
                            viewModel.addAnnouncement(title.trim(), desc.trim(), tag)
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                ) {
                    Text("Publish Notice")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
            title = { Text("New Announcement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag (e.g. Schedule, Result)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }
}

// ------------------- SUB-MODULE: AUDIT LOGS -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditLogsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Audit Trail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(auditLogs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ScienceTeal)
                            Text(sdf.format(Date(log.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("User: ${log.userId} (${log.role}) • Target: ${log.targetId}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

// ------------------- SUB-MODULE: SETTINGS -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Configuration", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(settings) { st ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(st.settingKey, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Current value: ${st.settingValue}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
