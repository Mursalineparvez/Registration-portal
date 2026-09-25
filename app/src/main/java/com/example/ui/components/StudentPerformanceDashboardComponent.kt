package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResultEntity
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.BdjsoRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.theme.StatusSuccess
import kotlin.math.max

/**
 * High-performance, comprehensive dashboard component that uses existing ResultEntity data
 * structures to display student performance results and overall ranking summaries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPerformanceDashboardComponent(
    currentStudentResult: ResultEntity?,
    allPublishedResults: List<ResultEntity>,
    onViewFullLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = My Performance, 1 = Overall Ranking Summary
    var selectedCategoryFilter by remember {
        mutableStateOf(currentStudentResult?.categoryId ?: "ALL")
    }

    // Filtered cohort for ranking summaries
    val cohortResults = remember(allPublishedResults, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") {
            allPublishedResults.sortedBy { it.rank }
        } else {
            allPublishedResults.filter { it.categoryId.equals(selectedCategoryFilter, ignoreCase = true) }
                .sortedBy { it.rank }
        }
    }

    val totalCandidates = cohortResults.size
    val averageScore = if (cohortResults.isNotEmpty()) cohortResults.map { it.totalMarks }.average() else 0.0
    val highestScore = if (cohortResults.isNotEmpty()) cohortResults.maxOf { it.totalMarks } else 0.0
    val qualifyingCutoff = if (cohortResults.isNotEmpty()) {
        val selectedOnly = cohortResults.filter { it.selectionStatus == "SELECTED" }
        if (selectedOnly.isNotEmpty()) selectedOnly.minOf { it.totalMarks } else averageScore
    } else 0.0

    // Percentile rank calculation for current student
    val studentPercentile = remember(currentStudentResult, cohortResults) {
        if (currentStudentResult == null || cohortResults.isEmpty()) 0.0
        else {
            val lowerCount = cohortResults.count { it.totalMarks < currentStudentResult.totalMarks }
            (lowerCount.toDouble() / cohortResults.size.toDouble()) * 100.0
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(PrimaryDarkNavy, ScienceTeal))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "Performance & Ranking",
                        tint = OlympiadGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Performance & Ranking Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "BDJSO National Evaluation Analytics",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Selection Badge
            if (currentStudentResult != null) {
                val isSelected = currentStudentResult.selectionStatus.equals("SELECTED", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) BdjsoEmerald.copy(alpha = 0.15f) else OlympiadGold.copy(alpha = 0.15f),
                    border = borderFromColor(if (isSelected) BdjsoEmerald else OlympiadGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = if (isSelected) BdjsoEmerald else OlympiadGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSelected) "Camp Selected" else "Waitlisted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) BdjsoEmerald else Color(0xFFB45309)
                        )
                    }
                }
            }
        }

        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            contentColor = PrimaryDarkNavy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ScienceTeal,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .testTag("dashboard_tab_row")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Student Scorecard",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Overall Rankings",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        // Tab 0: Student Performance Results
        if (selectedTab == 0) {
            if (currentStudentResult != null) {
                StudentScorecardView(
                    studentResult = currentStudentResult,
                    cohortAverage = averageScore,
                    cohortMax = highestScore,
                    cohortTotal = max(1, totalCandidates),
                    percentile = studentPercentile
                )
            } else {
                EmptyScorecardNotice()
            }
        }

        // Tab 1: Overall Ranking Summaries
        if (selectedTab == 1) {
            OverallRankingSummaryView(
                cohortResults = cohortResults,
                selectedCategory = selectedCategoryFilter,
                onCategoryChange = { selectedCategoryFilter = it },
                averageScore = averageScore,
                highestScore = highestScore,
                cutoffScore = qualifyingCutoff,
                currentStudentResult = currentStudentResult,
                onViewFullLeaderboard = onViewFullLeaderboard
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: INDIVIDUAL STUDENT SCORECARD & BREAKDOWN
// -------------------------------------------------------------------------------------------------

@Composable
private fun StudentScorecardView(
    studentResult: ResultEntity,
    cohortAverage: Double,
    cohortMax: Double,
    cohortTotal: Int,
    percentile: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Hero Highlight Banner
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = PrimaryDarkNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = studentResult.studentName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reg ID: ${studentResult.studentRegistrationId} • ${studentResult.categoryId}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = OlympiadGold
                        )
                        Text(
                            text = "${studentResult.schoolName} (${studentResult.district})",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Rank Trophy Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(OlympiadGold, Color(0xFFD97706))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "MERIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkNavy
                            )
                            Text(
                                text = "#${studentResult.rank}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryDarkNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))

                // Primary Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreMetricPill(
                        label = "Total Score",
                        value = "${studentResult.totalMarks}",
                        subText = "of 100",
                        accentColor = ElectricCyan
                    )
                    ScoreMetricPill(
                        label = "Percentage",
                        value = "${String.format("%.1f", studentResult.percentage)}%",
                        subText = if (studentResult.percentage >= 80.0) "Outstanding" else "Qualified",
                        accentColor = BdjsoEmerald
                    )
                    ScoreMetricPill(
                        label = "National Percentile",
                        value = "${String.format("%.1f", percentile)}th",
                        subText = "Top ${(100.0 - percentile).coerceAtLeast(1.0).toInt()}%",
                        accentColor = OlympiadGold
                    )
                }
            }
        }

        // Subject Breakdown Cards (Physics, Chemistry, Biology, Math)
        Text(
            text = "Subject-Wise Score Breakdown",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        val subjects = listOf(
            SubjectScoreItem("Physics", studentResult.physicsMarks, 25.0, ScienceTeal, Icons.Default.Science),
            SubjectScoreItem("Chemistry", studentResult.chemistryMarks, 25.0, Color(0xFF8B5CF6), Icons.Default.Biotech),
            SubjectScoreItem("Biology", studentResult.biologyMarks, 25.0, BdjsoEmerald, Icons.Default.Psychology),
            SubjectScoreItem("Mathematics & Logic", studentResult.mathMarks, 25.0, OlympiadGold, Icons.Default.Functions)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            subjects.forEach { item ->
                SubjectScoreRow(item = item)
            }
        }

        // Benchmark vs Cohort Comparison
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Cohort Benchmark Comparison",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        CohortComparisonCanvas(
            studentScore = studentResult.totalMarks,
            averageScore = cohortAverage,
            maxScore = cohortMax
        )
    }
}

private data class SubjectScoreItem(
    val name: String,
    val score: Double,
    val total: Double,
    val color: Color,
    val icon: ImageVector
)

@Composable
private fun SubjectScoreRow(item: SubjectScoreItem) {
    val progress = (item.score / item.total).coerceIn(0.0, 1.0).toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "SubjectProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${item.score} / ${item.total.toInt()} (${(progress * 100).toInt()}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = item.color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Visual Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(item.color)
                )
            }
        }
    }
}

@Composable
private fun CohortComparisonCanvas(
    studentScore: Double,
    averageScore: Double,
    maxScore: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = ScienceTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Relative Standing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                val diffFromAvg = studentScore - averageScore
                Text(
                    text = if (diffFromAvg >= 0) "+${String.format("%.1f", diffFromAvg)} above avg" else "${String.format("%.1f", diffFromAvg)} below avg",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (diffFromAvg >= 0) BdjsoEmerald else BdjsoRed
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Range Visualizer Canvas
            val canvasHeight = 44.dp
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
            ) {
                val width = size.width
                val height = size.height
                val trackY = height * 0.45f
                val effectiveMax = max(100.0, maxScore)

                // Background Track
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(0f, trackY),
                    end = Offset(width, trackY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Average Marker
                val avgX = (averageScore / effectiveMax * width).toFloat().coerceIn(0f, width)
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(avgX, trackY - 14f),
                    end = Offset(avgX, trackY + 14f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Student Marker (Highlighted Diamond / Circle)
                val studentX = (studentScore / effectiveMax * width).toFloat().coerceIn(0f, width)
                drawCircle(
                    color = BdjsoEmerald,
                    radius = 8.dp.toPx(),
                    center = Offset(studentX, trackY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.5.dp.toPx(),
                    center = Offset(studentX, trackY)
                )

                // Top Performer Marker
                val maxX = (maxScore / effectiveMax * width).toFloat().coerceIn(0f, width)
                drawCircle(
                    color = OlympiadGold,
                    radius = 5.dp.toPx(),
                    center = Offset(maxX, trackY)
                )
            }

            // Canvas Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendIndicator(color = Color(0xFF64748B), label = "Avg: ${String.format("%.1f", averageScore)}")
                LegendIndicator(color = BdjsoEmerald, label = "You: $studentScore")
                LegendIndicator(color = OlympiadGold, label = "Top: $maxScore")
            }
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ScoreMetricPill(
    label: String,
    value: String,
    subText: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = subText,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun EmptyScorecardNotice() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Assessment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No Student Result Record Attached",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Complete your preliminary exam or select a candidate to inspect the subject scorecard.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: OVERALL RANKING SUMMARIES & COHORT METRICS
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverallRankingSummaryView(
    cohortResults: List<ResultEntity>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    averageScore: Double,
    highestScore: Double,
    cutoffScore: Double,
    currentStudentResult: ResultEntity?,
    onViewFullLeaderboard: () -> Unit
) {
    val categories = listOf("ALL", "PRIMARY", "JUNIOR", "SECONDARY", "SPECIAL")
    var isScoreDistributionExpanded by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = if (selectedCategory == category) {
                        { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ScienceTeal,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }
        }

        // Summary Metric Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryMetricCard(
                title = "Total Rankers",
                value = "${cohortResults.size}",
                sub = "Evaluated",
                icon = Icons.Default.School,
                tint = PrimaryDarkNavy,
                modifier = Modifier.weight(1f)
            )
            SummaryMetricCard(
                title = "National Avg",
                value = String.format("%.1f", averageScore),
                sub = "Marks",
                icon = Icons.Default.Speed,
                tint = ScienceTeal,
                modifier = Modifier.weight(1f)
            )
            SummaryMetricCard(
                title = "Top Score",
                value = "$highestScore",
                sub = "Marks",
                icon = Icons.Default.EmojiEvents,
                tint = OlympiadGold,
                modifier = Modifier.weight(1f)
            )
            SummaryMetricCard(
                title = "Camp Cutoff",
                value = String.format("%.1f", cutoffScore),
                sub = "Selected",
                icon = Icons.Default.WorkspacePremium,
                tint = BdjsoEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        // Score Distribution Histogram Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isScoreDistributionExpanded = !isScoreDistributionExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Score Frequency Distribution",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        if (isScoreDistributionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(visible = isScoreDistributionExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        ScoreHistogramVisualizer(cohortResults = cohortResults)
                    }
                }
            }
        }

        // Top 5 Rankers Summary Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = OlympiadGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Top National Standings (${selectedCategory})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Top 5",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val top5 = cohortResults.take(5)
                if (top5.isEmpty()) {
                    Text(
                        text = "No results available in this category.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    top5.forEachIndexed { index, item ->
                        val isCurrentUser = currentStudentResult?.studentRegistrationId == item.studentRegistrationId
                        RankerRowItem(
                            result = item,
                            isCurrentUser = isCurrentUser,
                            showDivider = index < top5.size - 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onViewFullLeaderboard,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Explore Complete Merit List & Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ScoreHistogramVisualizer(cohortResults: List<ResultEntity>) {
    // 5 score buckets: 0-20, 21-40, 41-60, 61-80, 81-100
    val buckets = remember(cohortResults) {
        val b1 = cohortResults.count { it.totalMarks in 0.0..20.0 }
        val b2 = cohortResults.count { it.totalMarks > 20.0 && it.totalMarks <= 40.0 }
        val b3 = cohortResults.count { it.totalMarks > 40.0 && it.totalMarks <= 60.0 }
        val b4 = cohortResults.count { it.totalMarks > 60.0 && it.totalMarks <= 80.0 }
        val b5 = cohortResults.count { it.totalMarks > 80.0 }
        listOf(
            "0-20" to b1,
            "21-40" to b2,
            "41-60" to b3,
            "61-80" to b4,
            "81-100" to b5
        )
    }

    val maxCount = max(1, buckets.maxOf { it.second })

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val width = size.width
            val height = size.height
            val barWidth = width / (buckets.size * 1.6f)
            val spacing = width / buckets.size

            buckets.forEachIndexed { i, pair ->
                val count = pair.second
                val barHeight = (count.toFloat() / maxCount.toFloat()) * (height - 24f)
                val x = (i * spacing) + (spacing - barWidth) / 2f
                val y = height - barHeight - 10f

                // Color gradient based on performance tier
                val barColor = when (i) {
                    0, 1 -> Color(0xFF94A3B8)
                    2 -> ScienceTeal.copy(alpha = 0.8f)
                    3 -> ScienceTeal
                    else -> BdjsoEmerald
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            buckets.forEach { (label, count) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$count",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RankerRowItem(
    result: ResultEntity,
    isCurrentUser: Boolean,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isCurrentUser) BdjsoEmerald.copy(alpha = 0.1f) else Color.Transparent
                )
                .padding(vertical = 8.dp, horizontal = if (isCurrentUser) 8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when (result.rank) {
                                1 -> OlympiadGold
                                2 -> Color(0xFF94A3B8) // Silver
                                3 -> Color(0xFFB45309) // Bronze
                                else -> PrimaryDarkNavy.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${result.rank}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.rank in 1..3) Color.White else PrimaryDarkNavy
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = result.studentName,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrentUser) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BdjsoEmerald
                            ) {
                                Text(
                                    text = "YOU",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${result.schoolName} • ${result.district}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Score details
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${result.totalMarks} Marks",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScienceTeal
                )
                Text(
                    text = "${String.format("%.1f", result.percentage)}%",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun borderFromColor(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
