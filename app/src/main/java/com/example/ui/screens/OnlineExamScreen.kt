package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.ui.components.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.BdjsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineExamScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    val activeExam by viewModel.activeExam.collectAsStateWithLifecycle()
    val questions by viewModel.examQuestions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
    val markedForReview by viewModel.markedForReview.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val lastAutosave by viewModel.lastAutosaveTime.collectAsStateWithLifecycle()

    var showSubmitDialog by remember { mutableStateOf(false) }
    var showTimerDetailsDialog by remember { mutableStateOf(false) }
    var isNotificationBannerDismissed by remember { mutableStateOf(false) }

    val totalSeconds = remember(activeExam) {
        ((activeExam?.durationMinutes ?: 60) * 60).coerceAtLeast(60)
    }

    LaunchedEffect(remainingSeconds) {
        if (remainingSeconds == 900 || remainingSeconds == 600 || remainingSeconds == 300 || remainingSeconds == 120 || remainingSeconds == 60 || remainingSeconds == 30) {
            isNotificationBannerDismissed = false
        }
    }

    val currentQuestion = questions.getOrNull(currentIndex)
    val totalQuestions = questions.size

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val isTimeWarning = remainingSeconds < 300 // < 5 mins

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeExam?.title ?: "BDJSO Online Examination",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = BdjsoEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = lastAutosave,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Enhanced Exam Timer Badge with live urgency state and clickable detail view
                    ExamTimerBadge(
                        remainingSeconds = remainingSeconds,
                        totalSeconds = totalSeconds,
                        onClick = { showTimerDetailsDialog = true },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Prominent Countdown Notification Banner for Milestone Warnings (<= 10 mins)
            if (!isNotificationBannerDismissed && remainingSeconds in 1..600) {
                ExamCountdownNotificationBanner(
                    remainingSeconds = remainingSeconds,
                    totalSeconds = totalSeconds,
                    onDismiss = { isNotificationBannerDismissed = true },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
            // Horizontal Question Palette / Navigation Strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of $totalQuestions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LegendChip("Answered", BdjsoEmerald)
                        LegendChip("Review", OlympiadGold)
                        LegendChip("Left", Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(questions) { index, q ->
                        val isCurrent = index == currentIndex
                        val isAnswered = userAnswers.containsKey(q.id)
                        val isReview = markedForReview.contains(q.id)

                        val bgColor = when {
                            isReview -> OlympiadGold
                            isAnswered -> BdjsoEmerald
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val textColor = when {
                            isReview -> PrimaryDarkNavy
                            isAnswered -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isCurrent) 2.5.dp else 0.dp,
                                    color = if (isCurrent) ScienceTeal else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.goToQuestion(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                color = textColor,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Question Content Area
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Question Metadata Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ScienceTeal)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = currentQuestion.subject,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = currentQuestion.topic,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "+4.0 Marks • -1.0 Neg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = currentQuestion.questionText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (currentQuestion.diagramType.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                QuestionDiagramViewer(diagramType = currentQuestion.diagramType)
                            }
                        }
                    }

                    // Options List
                    val selectedAnswer = userAnswers[currentQuestion.id]

                    val options = listOf(
                        "A" to currentQuestion.optionA,
                        "B" to currentQuestion.optionB,
                        "C" to currentQuestion.optionC,
                        "D" to currentQuestion.optionD
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        options.forEach { (letter, optionText) ->
                            val isSelected = selectedAnswer == letter

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectAnswer(currentQuestion.id, letter)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFE0F2FE) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ScienceTeal) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectAnswer(currentQuestion.id, letter) },
                                        colors = RadioButtonDefaults.colors(selectedColor = ScienceTeal)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$letter. $optionText",
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PrimaryDarkNavy else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Question Action Toolbar (Clear Answer, Mark for Review)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isMarked = markedForReview.contains(currentQuestion.id)
                        OutlinedButton(
                            onClick = { viewModel.toggleMarkForReview(currentQuestion.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isMarked) OlympiadGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isMarked) OlympiadGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isMarked) "Flagged for Review" else "Mark for Review", fontSize = 12.sp)
                        }

                        if (selectedAnswer != null) {
                            TextButton(
                                onClick = { viewModel.clearAnswer(currentQuestion.id) }
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusDanger)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear Answer", fontSize = 12.sp, color = StatusDanger)
                            }
                        }
                    }
                }
            }

            // Bottom Navigation & Submit Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevQuestion() },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.NavigateBefore, contentDescription = null)
                        Text("Prev", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { showSubmitDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish & Submit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        enabled = currentIndex < totalQuestions - 1,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal)
                    ) {
                        Text("Next", fontSize = 13.sp)
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
    }

    // Confirmation modal before submission
    if (showSubmitDialog) {
        val answeredCount = userAnswers.size
        val flaggedCount = markedForReview.size
        val leftCount = totalQuestions - answeredCount

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitActiveExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                ) {
                    Text("Confirm Final Submission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Resume Exam")
                }
            },
            title = {
                Text("Confirm Exam Submission", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you sure you want to submit your examination paper now?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Total Questions: $totalQuestions", fontSize = 13.sp)
                    Text("• Answered: $answeredCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BdjsoEmerald)
                    Text("• Marked for Review: $flaggedCount", fontSize = 13.sp, color = OlympiadGold)
                    Text("• Unanswered: $leftCount", fontSize = 13.sp, color = if (leftCount > 0) StatusWarning else Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Once submitted, answers cannot be modified. Your score and performance breakdown will be automatically evaluated.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    // Modal: Detailed Exam Timer & Pace Breakdown
    if (showTimerDetailsDialog) {
        Dialog(onDismissRequest = { showTimerDetailsDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                ExamTimerComponent(
                    totalSeconds = totalSeconds,
                    remainingSeconds = remainingSeconds,
                    totalQuestions = totalQuestions,
                    answeredQuestions = userAnswers.size,
                    showNotificationBanner = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showTimerDetailsDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy)
                ) {
                    Text("Return to Exam", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal: Automatic Notification when Time Expires
    if (remainingSeconds <= 0) {
        ExamTimeExpiredDialog(
            onAutoSubmit = { viewModel.submitActiveExam() }
        )
    }
}

@Composable
fun LegendChip(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
