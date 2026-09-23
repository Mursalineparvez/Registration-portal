package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ResultEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BdjsoViewModel

/**
 * Filter and sorting criteria for examination results stored in Room.
 */
enum class ResultSortCriteria(val label: String) {
    RANK_ASC("Rank #1 → N"),
    SCORE_DESC("Score: High → Low"),
    PERCENTAGE_DESC("Percentage %"),
    NAME_ASC("Student Name (A–Z)")
}

/**
 * Student Result Dashboard Component.
 * Fetches and displays examination scores in a formatted list, backed by Room database for local storage.
 */
@Composable
fun StudentResultDashboardComponent(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier,
    onStudentClick: ((ResultEntity) -> Unit)? = null
) {
    // Reactive Room database stream via ViewModel StateFlow
    val resultsFromRoom by viewModel.publishedResults.collectAsStateWithLifecycle()
    val allResultsFromRoom by viewModel.allResults.collectAsStateWithLifecycle()
    val effectiveResults = if (resultsFromRoom.isNotEmpty()) resultsFromRoom else allResultsFromRoom

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var selectedSortCriteria by remember { mutableStateOf(ResultSortCriteria.RANK_ASC) }

    var expandedResultId by remember { mutableStateOf<Long?>(null) }
    var scorecardModalResult by remember { mutableStateOf<ResultEntity?>(null) }
    var showAddScoreDialog by remember { mutableStateOf(false) }

    // Filter and sort the Room database results
    val filteredResults = remember(effectiveResults, searchQuery, selectedCategoryFilter, selectedStatusFilter, selectedSortCriteria) {
        effectiveResults.filter { res ->
            val matchesCategory = selectedCategoryFilter == "ALL" || res.categoryId.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesStatus = selectedStatusFilter == "ALL" || res.selectionStatus.equals(selectedStatusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    res.studentName.contains(searchQuery, ignoreCase = true) ||
                    res.studentRegistrationId.contains(searchQuery, ignoreCase = true) ||
                    res.schoolName.contains(searchQuery, ignoreCase = true) ||
                    res.district.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesStatus && matchesSearch
        }.let { list ->
            when (selectedSortCriteria) {
                ResultSortCriteria.RANK_ASC -> list.sortedBy { it.rank }
                ResultSortCriteria.SCORE_DESC -> list.sortedByDescending { it.totalMarks }
                ResultSortCriteria.PERCENTAGE_DESC -> list.sortedByDescending { it.percentage }
                ResultSortCriteria.NAME_ASC -> list.sortedBy { it.studentName }
            }
        }
    }

    // Dashboard Statistics computed directly from Room records
    val totalCount = effectiveResults.size
    val averageScore = if (totalCount > 0) effectiveResults.sumOf { it.totalMarks } / totalCount else 0.0
    val averagePercentage = if (totalCount > 0) effectiveResults.sumOf { it.percentage } / totalCount else 0.0
    val topScoreResult = effectiveResults.maxByOrNull { it.totalMarks }
    val selectedCount = effectiveResults.count { it.selectionStatus == "SELECTED" }
    val selectionRate = if (totalCount > 0) (selectedCount.toDouble() / totalCount * 100.0) else 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_result_dashboard_component"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header & Storage Info Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = PrimaryDarkNavy,
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = OlympiadGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Examination Score Dashboard",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Official BDJSO National & Regional Merit Standings",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Room DB Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BdjsoEmerald)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Room DB (${effectiveResults.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row: Add Score, Seed Sample, Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddScoreDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_exam_score_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record Score", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.seedSampleResultsIfEmpty() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("seed_scores_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF475569))
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Seed Room DB", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Summary Metric Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResultMetricCard(
                title = "Evaluated",
                value = "$totalCount",
                subtext = "Candidates in DB",
                icon = Icons.Default.People,
                accentColor = ScienceTeal,
                modifier = Modifier.weight(1f)
            )

            ResultMetricCard(
                title = "Average Score",
                value = String.format("%.1f", averageScore),
                subtext = String.format("%.1f%% mean", averagePercentage),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                accentColor = ElectricCyan,
                modifier = Modifier.weight(1f)
            )

            ResultMetricCard(
                title = "Top Mark",
                value = if (topScoreResult != null) String.format("%.1f", topScoreResult.totalMarks) else "—",
                subtext = topScoreResult?.studentName?.take(11) ?: "No data",
                icon = Icons.Default.MilitaryTech,
                accentColor = OlympiadGold,
                modifier = Modifier.weight(1f)
            )

            ResultMetricCard(
                title = "Qualified",
                value = String.format("%.0f%%", selectionRate),
                subtext = "$selectedCount selected",
                icon = Icons.Default.CheckCircle,
                accentColor = BdjsoEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, roll ID, school or district...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ScienceTeal) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("result_search_field"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ScienceTeal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Filters: Category and Selection Status Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf(
                    "ALL" to "All Categories",
                    "PRIMARY" to "Primary (3–5)",
                    "JUNIOR" to "Junior (6–8)",
                    "SECONDARY" to "Secondary (9–10)",
                    "SPECIAL" to "Special (11–12)"
                )
                items(categories) { (catId, catLabel) ->
                    FilterChip(
                        selected = selectedCategoryFilter == catId,
                        onClick = { selectedCategoryFilter = catId },
                        label = { Text(catLabel, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ScienceTeal,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Selection Status & Sort Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Filter
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL" to "All", "SELECTED" to "Selected", "WAITING_LIST" to "Waiting").forEach { (statusKey, statusLabel) ->
                        FilterChip(
                            selected = selectedStatusFilter == statusKey,
                            onClick = { selectedStatusFilter = statusKey },
                            label = { Text(statusLabel, fontSize = 11.sp) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                // Sorting Menu Button
                var showSortMenu by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedSortCriteria.label, fontSize = 11.sp)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        ResultSortCriteria.entries.forEach { criteria ->
                            DropdownMenuItem(
                                text = { Text(criteria.label, fontSize = 12.sp) },
                                onClick = {
                                    selectedSortCriteria = criteria
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Formatted List of Examination Scores
        if (filteredResults.isEmpty()) {
            // Empty State Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("empty_results_view"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (effectiveResults.isEmpty()) "No Examination Scores in Room DB" else "No matching results found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (effectiveResults.isEmpty()) "Populate local SQLite Room database with official BDJSO scores." else "Try adjusting your search query or category filters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (effectiveResults.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.seedSampleResultsIfEmpty() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Populate Sample Scores into Room")
                        }
                    }
                }
            }
        } else {
            // Count indicator
            Text(
                text = "Displaying ${filteredResults.size} of $totalCount candidate scores",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Formatted List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredResults.forEach { result ->
                    FormattedScoreListItem(
                        result = result,
                        isExpanded = expandedResultId == result.id,
                        onToggleExpand = {
                            expandedResultId = if (expandedResultId == result.id) null else result.id
                        },
                        onViewScorecard = { scorecardModalResult = result },
                        onDeleteScore = { viewModel.deleteExamResult(result.id) },
                        onItemClick = { onStudentClick?.invoke(result) }
                    )
                }
            }
        }
    }

    // Modal: Detailed Scorecard / Certificate Dialog
    scorecardModalResult?.let { result ->
        OfficialScorecardDialog(
            result = result,
            onDismiss = { scorecardModalResult = null }
        )
    }

    // Modal: Add / Record New Exam Score Dialog into Room
    if (showAddScoreDialog) {
        AddExamScoreDialog(
            onDismiss = { showAddScoreDialog = false },
            onSaveScore = { regId, name, cat, school, dist, div, pMarks, cMarks, bMarks, mMarks ->
                val total = pMarks + cMarks + bMarks + mMarks
                val percentage = (total / 100.0) * 100.0
                val rank = (effectiveResults.count { it.totalMarks > total } + 1)
                val status = if (percentage >= 80.0) "SELECTED" else if (percentage >= 60.0) "WAITING_LIST" else "NOT_SELECTED"

                viewModel.addExamResult(
                    studentRegId = regId,
                    studentName = name,
                    categoryId = cat,
                    schoolName = school,
                    district = dist,
                    division = div,
                    physicsMarks = pMarks,
                    chemistryMarks = cMarks,
                    biologyMarks = bMarks,
                    mathMarks = mMarks,
                    totalMarks = total,
                    percentage = percentage,
                    rank = rank,
                    selectionStatus = status
                )
                showAddScoreDialog = false
            }
        )
    }
}

/**
 * Formatted Score List Item Card.
 * Clean, structured Material 3 card highlighting Rank, Student Details, Category, Total Score, Status & Subject Breakdown.
 */
@Composable
fun FormattedScoreListItem(
    result: ResultEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onViewScorecard: () -> Unit,
    onDeleteScore: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rankBadgeColors = when (result.rank) {
        1 -> Pair(Color(0xFFFEF3C7), OlympiadGold) // Gold
        2 -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B)) // Silver
        3 -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C)) // Bronze
        in 4..10 -> Pair(Color(0xFFE0F2FE), ScienceTeal) // Top 10
        else -> Pair(Color(0xFFF3F4F6), Color(0xFF6B7280)) // General
    }

    val statusBadgeColors = when (result.selectionStatus.uppercase()) {
        "SELECTED" -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "QUALIFIED / SELECTED")
        "WAITING_LIST" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "WAITING LIST")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), "NOT SELECTED")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("score_item_${result.studentRegistrationId}")
            .clickable { onItemClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Row: Rank, Student Info, Score & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(rankBadgeColors.first)
                        .border(1.dp, rankBadgeColors.second.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "#${result.rank}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = rankBadgeColors.second
                        )
                        Text(
                            text = "RANK",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = rankBadgeColors.second.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Student Academic Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = result.studentRegistrationId,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = ScienceTeal
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = result.categoryId,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${result.schoolName}, ${result.district}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Score Display
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", result.totalMarks),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryDarkNavy
                        )
                        Text(
                            text = "/100",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusBadgeColors.first
                    ) {
                        Text(
                            text = statusBadgeColors.third,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusBadgeColors.second,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject Marks Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SubjectScorePill("Physics", result.physicsMarks, 25.0, modifier = Modifier.weight(1f))
                SubjectScorePill("Chemistry", result.chemistryMarks, 25.0, modifier = Modifier.weight(1f))
                SubjectScorePill("Biology", result.biologyMarks, 25.0, modifier = Modifier.weight(1f))
                SubjectScorePill("Math", result.mathMarks, 25.0, modifier = Modifier.weight(1f))
            }

            // Expandable Detail Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleExpand,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isExpanded) "Hide Details" else "View Details & Breakdown", fontSize = 11.sp)
                }

                Button(
                    onClick = onViewScorecard,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scorecard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Animated Expanded Details
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailText("Percentage", String.format("%.2f%%", result.percentage))
                        DetailText("National Rank", "#${result.rank}")
                        DetailText("Division", result.division)
                        DetailText("Status in Room", if (result.isPublished) "Published" else "Draft")
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Record ID: #${result.id} in Room Database",
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        TextButton(
                            onClick = onDeleteScore,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Score", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Metric summary card for top dashboard statistics.
 */
@Composable
private fun ResultMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Compact subject pill rendering obtained marks over maximum subject marks.
 */
@Composable
private fun SubjectScorePill(
    subject: String,
    marks: Double,
    maxMarks: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = subject,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = String.format("%.1f", marks),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkNavy
            )
        }
    }
}

@Composable
private fun DetailText(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

/**
 * Official BDJSO Scorecard Modal Dialog.
 */
@Composable
fun OfficialScorecardDialog(
    result: ResultEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("official_scorecard_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BDJSO 2026 OFFICIAL SCORECARD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryDarkNavy
                        )
                        Text(
                            text = "Bangladesh Junior Science Olympiad",
                            fontSize = 11.sp,
                            color = ScienceTeal
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider()

                // Candidate Details Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ScorecardRow("Student Name:", result.studentName)
                        ScorecardRow("Registration ID:", result.studentRegistrationId)
                        ScorecardRow("Category:", result.categoryId)
                        ScorecardRow("Institution:", result.schoolName)
                        ScorecardRow("District / Div:", "${result.district}, ${result.division}")
                    }
                }

                // Marks Breakdown Table
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subject Component", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                            Text("Max", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Score", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider()
                        ScorecardTableRow("Physics", "25.0", String.format("%.1f", result.physicsMarks))
                        ScorecardTableRow("Chemistry", "25.0", String.format("%.1f", result.chemistryMarks))
                        ScorecardTableRow("Biology", "25.0", String.format("%.1f", result.biologyMarks))
                        ScorecardTableRow("Mathematics", "25.0", String.format("%.1f", result.mathMarks))
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Grand Total", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, modifier = Modifier.weight(2f), color = PrimaryDarkNavy)
                            Text("100.0", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text(
                                String.format("%.1f", result.totalMarks),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f),
                                color = BdjsoEmerald
                            )
                        }
                    }
                }

                // National Standing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("National Rank: #${result.rank}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OlympiadGold)
                        Text("Percentage: ${String.format("%.2f%%", result.percentage)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (result.selectionStatus == "SELECTED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = result.selectionStatus,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (result.selectionStatus == "SELECTED") Color(0xFF15803D) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy)
                ) {
                    Text("Close Scorecard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ScorecardRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}

@Composable
private fun ScorecardTableRow(subject: String, max: String, score: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(subject, fontSize = 11.sp, modifier = Modifier.weight(2f))
        Text(max, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(score, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}

/**
 * Add / Record New Exam Score Dialog to insert into Room Database.
 */
@Composable
fun AddExamScoreDialog(
    onDismiss: () -> Unit,
    onSaveScore: (
        regId: String,
        name: String,
        category: String,
        school: String,
        district: String,
        division: String,
        pMarks: Double,
        cMarks: Double,
        bMarks: Double,
        mMarks: Double
    ) -> Unit
) {
    var regId by remember { mutableStateOf("BDJSO-2026-${(1000..9999).random()}") }
    var studentName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("JUNIOR") }
    var schoolName by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Dhaka") }
    var division by remember { mutableStateOf("Dhaka") }

    var pMarksStr by remember { mutableStateOf("20.0") }
    var cMarksStr by remember { mutableStateOf("20.0") }
    var bMarksStr by remember { mutableStateOf("20.0") }
    var mMarksStr by remember { mutableStateOf("20.0") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_score_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Record Exam Score into Room DB",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkNavy
                )

                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = regId,
                    onValueChange = { regId = it },
                    label = { Text("Registration ID *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("School / Institution *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("PRIMARY", "JUNIOR", "SECONDARY", "SPECIAL").forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.take(4), fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Marks row
                Text("Subject Marks (Max 25 each):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = pMarksStr,
                        onValueChange = { pMarksStr = it },
                        label = { Text("Phy") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cMarksStr,
                        onValueChange = { cMarksStr = it },
                        label = { Text("Chem") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bMarksStr,
                        onValueChange = { bMarksStr = it },
                        label = { Text("Bio") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = mMarksStr,
                        onValueChange = { mMarksStr = it },
                        label = { Text("Math") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (studentName.isBlank()) {
                                errorMessage = "Please enter student name"
                                return@Button
                            }
                            if (schoolName.isBlank()) {
                                errorMessage = "Please enter school name"
                                return@Button
                            }
                            val p = pMarksStr.toDoubleOrNull() ?: 0.0
                            val c = cMarksStr.toDoubleOrNull() ?: 0.0
                            val b = bMarksStr.toDoubleOrNull() ?: 0.0
                            val m = mMarksStr.toDoubleOrNull() ?: 0.0

                            onSaveScore(
                                regId.trim(),
                                studentName.trim(),
                                selectedCategory,
                                schoolName.trim(),
                                district,
                                division,
                                p, c, b, m
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald)
                    ) {
                        Text("Save to Room", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
