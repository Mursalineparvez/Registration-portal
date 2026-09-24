package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudentEntity
import com.example.ui.components.StudentPerformanceDashboardComponent
import com.example.ui.components.StudentProfileComponent
import com.example.ui.components.StudentResultDashboardComponent
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.BdjsoRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@Composable
fun StudentDashboardScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val currentRegId by viewModel.currentStudentRegId.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val liveExam by viewModel.liveExam.collectAsStateWithLifecycle()
    val results by viewModel.publishedResults.collectAsStateWithLifecycle()

    val student = students.find { it.registrationId == currentRegId } ?: students.firstOrNull()
    val studentResult = results.find { it.studentRegistrationId == currentRegId }

    var showAdmitCardDialog by remember { mutableStateOf(false) }
    var resultsTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(PrimaryDarkNavy, Color(0xFF1E3A5F))
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BdjsoEmerald)
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
                                Text(
                                    text = student?.categoryId ?: "PRIMARY",
                                    color = ElectricCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = student?.fullName ?: "Participant",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!student?.banglaName.isNullOrBlank()) {
                                Text(
                                    text = student?.banglaName ?: "",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Reg ID: ${student?.registrationId ?: currentRegId}",
                                color = OlympiadGold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF233A5E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Exam Call-out
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (liveExam != null) Color(0xFFDC2626) else StatusSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (liveExam != null) "LIVE ONLINE EXAMINATION" else "SCHEDULED EXAM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (liveExam != null) Color(0xFFDC2626) else StatusSuccess
                            )
                        }
                        Text(
                            text = "Round 1: Preliminary",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = liveExam?.title ?: "BDJSO Preliminary National Round 2026",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Duration: ${liveExam?.durationMinutes ?: 45} mins • Negative Marking: 1.0 mark per wrong answer",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            liveExam?.let { exam ->
                                viewModel.startExam(exam, student?.categoryId ?: "PRIMARY")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (liveExam != null) BdjsoEmerald else ScienceTeal,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (liveExam != null) "Enter Online Exam Room" else "Start Practice Session",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Admit Card / Registration Slip Preview Card
        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Official Registration Slip",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = { showAdmitCardDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Admit Card", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Details grid
                    student?.let { s ->
                        DetailRow("Institution", s.schoolName)
                        DetailRow("Class & Roll", "${s.className} (Roll: ${s.studentRoll})")
                        DetailRow("Division & District", "${s.division} • ${s.district}")
                        DetailRow("Date of Birth", s.dob)
                        DetailRow("Guardian", "${s.guardianName} (${s.guardianRelation})")
                        DetailRow("Mobile", s.mobile)
                    }
                }
            }
        }

        // Results & Performance Section
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = resultsTab == 0,
                    onClick = { resultsTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("My Standing", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
                SegmentedButton(
                    selected = resultsTab == 1,
                    onClick = { resultsTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Profile & History", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
                SegmentedButton(
                    selected = resultsTab == 2,
                    onClick = { resultsTab = 2 },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Leaderboard", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }

        when (resultsTab) {
            0 -> {
                // Performance Results & Overall Ranking Dashboard Component
                item {
                    StudentPerformanceDashboardComponent(
                        currentStudentResult = studentResult,
                        allPublishedResults = results,
                        onViewFullLeaderboard = { resultsTab = 2 }
                    )
                }
            }
            1 -> {
                // Full Student Profile & Participation History Component
                item {
                    StudentProfileComponent(
                        viewModel = viewModel,
                        initialStudentRegId = currentRegId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 550.dp, max = 850.dp)
                    )
                }
            }
            else -> {
                // Formatted Exam Scores List backed by Room Database
                item {
                    StudentResultDashboardComponent(viewModel = viewModel)
                }
            }
        }

        // Quick Navigation & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Portal Home", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.RULES_AND_INFO) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rules & FAQ", fontSize = 12.sp)
                }
            }
        }
    }

    // Official Printable Admit Card Dialog
    if (showAdmitCardDialog && student != null) {
        AlertDialog(
            onDismissRequest = { showAdmitCardDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showSnackbar("Admit Card downloaded successfully (PDF)")
                        showAdmitCardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download PDF Slip")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdmitCardDialog = false }) {
                    Text("Close")
                }
            },
            title = {
                Text(
                    text = "Official BDJSO 2026 Admit Card",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BDJSO 2026",
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryDarkNavy,
                            fontSize = 16.sp
                        )
                        Text(
                            text = student.registrationId,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ScienceTeal,
                            fontSize = 12.sp
                        )
                    }

                    Divider(color = Color(0xFFE2E8F0))

                    Text("Candidate: ${student.fullName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Bangla: ${student.banglaName}", fontSize = 12.sp)
                    Text("Category: ${student.categoryId} (${student.className})", fontSize = 12.sp, color = BdjsoEmerald, fontWeight = FontWeight.SemiBold)
                    Text("School: ${student.schoolName}", fontSize = 12.sp)
                    Text("Roll: ${student.studentRoll} • DOB: ${student.dob}", fontSize = 12.sp)
                    Text("Division/District: ${student.division}, ${student.district}", fontSize = 12.sp)

                    Divider(color = Color(0xFFE2E8F0))

                    Text(
                        text = "Status: OFFICIAL PARTICIPANT (VERIFIED)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BdjsoEmerald
                    )
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ScoreItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF166534)
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color(0xFF15803D)
        )
    }
}
