package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StudentPerformanceDashboardComponent
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@Composable
fun ExamResultScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val lastAttempt by viewModel.lastAttemptResult.collectAsStateWithLifecycle()
    val currentRegId by viewModel.currentStudentRegId.collectAsStateWithLifecycle()
    val results by viewModel.publishedResults.collectAsStateWithLifecycle()

    val studentResult = results.find { it.studentRegistrationId == currentRegId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Celebratory Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(PrimaryDarkNavy, Color(0xFF1E3A5F))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F2B48)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = OlympiadGold,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Examination Completed!",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Automatic evaluation and scoring completed",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Score Circle / Chip
                        val score = lastAttempt?.score ?: studentResult?.totalMarks ?: 24.0
                        Text(
                            text = String.format("%.1f", score),
                            color = ElectricCyan,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Total Marks Scored",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Selection Tag
                        val selectionStatus = studentResult?.selectionStatus ?: if (score >= 20.0) "SELECTED" else "WAITING_LIST"
                        val tagColor = if (selectionStatus == "SELECTED") BdjsoEmerald else OlympiadGold

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(tagColor)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (selectionStatus == "SELECTED") "★ SELECTED FOR REGIONAL ROUND" else "PROVISIONAL WAITING LIST",
                                color = if (selectionStatus == "SELECTED") Color.White else PrimaryDarkNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Breakdown Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Response Breakdown",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBadge(
                            label = "Correct",
                            value = "${lastAttempt?.correctCount ?: 6}",
                            color = BdjsoEmerald
                        )
                        StatBadge(
                            label = "Wrong",
                            value = "${lastAttempt?.wrongCount ?: 1}",
                            color = StatusDanger
                        )
                        StatBadge(
                            label = "Unattempted",
                            value = "${lastAttempt?.unattemptedCount ?: 0}",
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Subject Performance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SubjectBar("Physics", studentResult?.physicsMarks ?: 8.0, 8.0, ScienceTeal)
                    SubjectBar("Chemistry", studentResult?.chemistryMarks ?: 7.0, 8.0, BdjsoEmerald)
                    SubjectBar("Biology", studentResult?.biologyMarks ?: 8.0, 8.0, OlympiadGold)
                    SubjectBar("Mathematics", studentResult?.mathMarks ?: 4.0, 4.0, ElectricCyan)
                }
            }
        }

        // Comprehensive Performance Analytics & Overall Ranking Summaries Component
        item {
            StudentPerformanceDashboardComponent(
                currentStudentResult = studentResult,
                allPublishedResults = results,
                onViewFullLeaderboard = { viewModel.navigateTo(AppScreen.RESULTS_LOOKUP) }
            )
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.RESULTS_LOOKUP) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View National Merit List & Leaderboard", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.STUDENT_DASHBOARD) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Return to Student Dashboard")
                }
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SubjectBar(subject: String, scored: Double, max: Double, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = subject, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "${scored.toInt()} / ${max.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val fraction = (scored / max).toFloat().coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
