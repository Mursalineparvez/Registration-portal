package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

/**
 * Exam Timer state level denoting urgency based on remaining time.
 */
enum class ExamTimerLevel {
    NORMAL,      // > 10 mins remaining
    CAUTION,     // 5 to 10 mins remaining
    WARNING,     // 1 to 5 mins remaining
    CRITICAL,    // < 1 min remaining
    EXPIRED      // 0 sec remaining
}

/**
 * Helper to determine current urgency level from remaining seconds.
 */
fun getExamTimerLevel(remainingSeconds: Int): ExamTimerLevel {
    return when {
        remainingSeconds <= 0 -> ExamTimerLevel.EXPIRED
        remainingSeconds < 60 -> ExamTimerLevel.CRITICAL
        remainingSeconds < 300 -> ExamTimerLevel.WARNING
        remainingSeconds < 600 -> ExamTimerLevel.CAUTION
        else -> ExamTimerLevel.NORMAL
    }
}

/**
 * Formats seconds into human-readable digital clock time (MM:SS or HH:MM:SS).
 */
fun formatExamTime(seconds: Int): String {
    if (seconds <= 0) return "00:00"
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}

/**
 * Comprehensive Exam Timer Component.
 * Tracks remaining time, renders digital clock, progress bar, and automatic countdown notifications.
 */
@Composable
fun ExamTimerComponent(
    totalSeconds: Int,
    remainingSeconds: Int,
    modifier: Modifier = Modifier,
    totalQuestions: Int = 0,
    answeredQuestions: Int = 0,
    onTimeExpired: () -> Unit = {},
    showNotificationBanner: Boolean = true
) {
    val level = getExamTimerLevel(remainingSeconds)
    val formattedTime = formatExamTime(remainingSeconds)
    val progress = if (totalSeconds > 0) {
        (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    // Pulsing animation for urgent states (< 5 mins)
    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulseScale by if (level == ExamTimerLevel.CRITICAL || level == ExamTimerLevel.WARNING) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    // Color definitions based on urgency level
    val (cardBg, borderColor, contentColor, statusLabel) = when (level) {
        ExamTimerLevel.NORMAL -> Quadruple(
            Color(0xFFF0FDF4),
            Color(0xFF86EFAC),
            PrimaryTealDark,
            "On Schedule"
        )
        ExamTimerLevel.CAUTION -> Quadruple(
            Color(0xFFFFFBEB),
            Color(0xFFFDE68A),
            Color(0xFFB45309),
            "10 Min Alert"
        )
        ExamTimerLevel.WARNING -> Quadruple(
            Color(0xFFFEF2F2),
            Color(0xFFFECACA),
            Color(0xFFDC2626),
            "5 Min Warning"
        )
        ExamTimerLevel.CRITICAL -> Quadruple(
            Color(0xFFFFF1F2),
            Color(0xFFFDA4AF),
            Color(0xFFE11D48),
            "Final Minutes!"
        )
        ExamTimerLevel.EXPIRED -> Quadruple(
            Color(0xFFF3F4F6),
            Color(0xFFD1D5DB),
            Color(0xFF374151),
            "Time's Up"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exam_timer_component"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Timer Header & Digital Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(contentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (level) {
                                ExamTimerLevel.CRITICAL -> Icons.Default.HourglassBottom
                                ExamTimerLevel.WARNING -> Icons.Default.Warning
                                ExamTimerLevel.CAUTION -> Icons.Default.AccessTime
                                ExamTimerLevel.EXPIRED -> Icons.Default.TimerOff
                                else -> Icons.Default.Timer
                            },
                            contentDescription = "Timer Status",
                            tint = contentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Remaining Exam Time",
                            fontSize = 11.sp,
                            color = contentColor.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = statusLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }

                // Digital Clock Display
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = contentColor,
                    modifier = Modifier.testTag("exam_countdown_clock")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Linear Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = contentColor,
                    trackColor = borderColor.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% Time Remaining",
                        fontSize = 10.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )

                    if (totalQuestions > 0) {
                        val unAnswered = totalQuestions - answeredQuestions
                        val estPerQuestion = if (unAnswered > 0) remainingSeconds / unAnswered else 0
                        Text(
                            text = if (unAnswered > 0) "~${estPerQuestion}s / remaining question" else "All Answered",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Notification Banner / Urgent Warning Prompt
            if (showNotificationBanner && level != ExamTimerLevel.NORMAL && level != ExamTimerLevel.EXPIRED) {
                Surface(
                    color = contentColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Notice",
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (level) {
                                ExamTimerLevel.CRITICAL -> "🚨 Critical Alert: Less than 1 minute remaining! System will auto-submit soon."
                                ExamTimerLevel.WARNING -> "⚠️ 5-Minute Warning: Time is running short. Review your answers now."
                                ExamTimerLevel.CAUTION -> "ℹ️ 10-Minute Notice: Please ensure all mandatory questions are answered."
                                else -> ""
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Pill Badge for TopAppBar actions with live countdown and warning colors.
 */
@Composable
fun ExamTimerBadge(
    remainingSeconds: Int,
    totalSeconds: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val level = getExamTimerLevel(remainingSeconds)
    val formattedTime = formatExamTime(remainingSeconds)

    val (bg, textColor, iconColor) = when (level) {
        ExamTimerLevel.NORMAL -> Triple(Color(0xFFE0F2FE), PrimaryDarkNavy, ScienceTeal)
        ExamTimerLevel.CAUTION -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Color(0xFFD97706))
        ExamTimerLevel.WARNING -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), Color(0xFFDC2626))
        ExamTimerLevel.CRITICAL -> Triple(Color(0xFFFFE4E6), Color(0xFFBE123C), Color(0xFFE11D48))
        ExamTimerLevel.EXPIRED -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), Color(0xFF6B7280))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("exam_timer_badge"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (level == ExamTimerLevel.CRITICAL || level == ExamTimerLevel.WARNING) {
                Icons.Default.NotificationsActive
            } else {
                Icons.Default.Timer
            },
            contentDescription = "Timer",
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = formattedTime,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

/**
 * Interactive Countdown Notification Banner shown to the student when time milestones are reached.
 */
@Composable
fun ExamCountdownNotificationBanner(
    remainingSeconds: Int,
    totalSeconds: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = getExamTimerLevel(remainingSeconds)
    if (level == ExamTimerLevel.NORMAL || level == ExamTimerLevel.EXPIRED) return

    val formattedTime = formatExamTime(remainingSeconds)

    val (bannerBg, textClr, borderClr, icon, title, message) = when (level) {
        ExamTimerLevel.CRITICAL -> Hexuple(
            Color(0xFFFFE4E6),
            Color(0xFF9F1239),
            Color(0xFFFDA4AF),
            Icons.Default.Alarm,
            "FINAL MINUTE COUNTDOWN!",
            "Only $formattedTime remaining. Your answers are auto-saved and will be submitted automatically when the timer reaches 00:00."
        )
        ExamTimerLevel.WARNING -> Hexuple(
            Color(0xFFFEF2F2),
            Color(0xFFB91C1C),
            Color(0xFFFECACA),
            Icons.Default.HourglassBottom,
            "5-MINUTE COUNTDOWN ALERT",
            "You have $formattedTime remaining. Double check marked questions and complete your responses."
        )
        ExamTimerLevel.CAUTION -> Hexuple(
            Color(0xFFFFFBEB),
            Color(0xFF92400E),
            Color(0xFFFDE68A),
            Icons.Default.Timer,
            "10-MINUTE COUNTDOWN NOTICE",
            "10 minutes remaining ($formattedTime). Pace your remaining questions accordingly."
        )
        else -> Hexuple(
            Color.White, Color.Black, Color.Gray, Icons.Default.Info, "", ""
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exam_countdown_notification_banner"),
        color = bannerBg,
        border = BorderStroke(1.dp, borderClr),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(textClr.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = textClr, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textClr
                    )
                    Surface(
                        color = textClr,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = formattedTime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = message,
                    fontSize = 10.5.sp,
                    color = textClr.copy(alpha = 0.9f),
                    lineHeight = 14.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = textClr, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Modal dialog presented when exam time has fully expired.
 */
@Composable
fun ExamTimeExpiredDialog(
    onAutoSubmit: () -> Unit
) {
    Dialog(onDismissRequest = onAutoSubmit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exam_time_expired_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(StatusDanger.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.TimerOff,
                        contentDescription = "Expired",
                        tint = StatusDanger,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Exam Time Has Expired!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "The allocated time for this BDJSO examination has ended. All your saved responses are being compiled and submitted now.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onAutoSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Submit Answers Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Simple tuple classes for clean helper returns
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
private data class Hexuple<A, B, C, D, E, F>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F
)
