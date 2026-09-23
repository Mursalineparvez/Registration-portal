package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BDJSOData
import com.example.model.OlympiadCategory
import com.example.model.PastPaper
import com.example.model.QuestionType
import com.example.model.QuizQuestion
import com.example.ui.components.QuestionDiagram
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    onSelectPaper: (PastPaper) -> Unit
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Official PDF Questions, 1: Past Papers
    var selectedCategoryFilter by remember { mutableStateOf<OlympiadCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedAnswers by remember { mutableStateOf(setOf<Int>()) }
    var downloadedPaperId by remember { mutableStateOf<String?>(null) }

    val filteredQuestions = remember(selectedCategoryFilter, searchQuery) {
        BDJSOData.sampleQuestions.filter { q ->
            (selectedCategoryFilter == null || q.category == selectedCategoryFilter) &&
            (searchQuery.isBlank() ||
             q.code.contains(searchQuery, ignoreCase = true) ||
             q.questionBangla.contains(searchQuery, ignoreCase = true) ||
             q.questionEnglish.contains(searchQuery, ignoreCase = true) ||
             q.subject.contains(searchQuery, ignoreCase = true))
        }
    }

    val filteredPapers = remember(selectedCategoryFilter, searchQuery) {
        BDJSOData.pastPapers.filter { paper ->
            (selectedCategoryFilter == null || paper.category == selectedCategoryFilter) &&
            (searchQuery.isBlank() || paper.stage.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BDJSO Questions & Past Papers",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = PrimaryTealDark
                        )
                        Text(
                            text = "অফিসিয়াল প্রশ্নপত্র ও বিগত বছরের প্রশ্নমালা",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
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
                .padding(horizontal = 16.dp)
                .testTag("question_bank_content"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Main Mode Tabs (PDF Questions List vs Downloadable Past Papers)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryTeal,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = {
                            Text(
                                text = "PDF প্রশ্নমালা (${filteredQuestions.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = {
                            Text(
                                text = "বিগত প্রশ্নপত্র (${filteredPapers.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Search Bar
            item {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (selectedMainTab == 0) "অনুসন্ধান (যেমন: Primary-Q1, লিভার, বেগ, ফিউজ)"
                            else "Search papers (e.g. National, Regional)"
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryTeal) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
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

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("সকল ক্যাটাগরি (All)") }
                        )
                    }
                    items(listOf(OlympiadCategory.PRIMARY, OlympiadCategory.JUNIOR, OlympiadCategory.SECONDARY)) { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                            },
                            label = { Text("${cat.displayName} (${cat.classes})") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedMainTab == 0) {
                // === PDF QUESTIONS LIST (Matching PDF Pages 6, 7, 14) ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BDJSO Questions List",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryTealDark
                        )
                        Surface(
                            color = ServiceGreenBg,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ServiceGreenBorder)
                        ) {
                            Text(
                                text = "Pattern from online.bdjso.org",
                                color = ServiceGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(filteredQuestions) { q ->
                    val isExpanded = expandedAnswers.contains(q.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row: Code & Green Action Button (as in PDF)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = ServiceTealBg,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, ServiceTealBorder)
                                    ) {
                                        Text(
                                            text = q.code.ifEmpty { "Q${q.id}" },
                                            color = PrimaryTealDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        color = when (q.subject) {
                                            "Physics" -> ServiceBlueBg
                                            "Chemistry" -> ServiceAmberBg
                                            else -> ServiceGreenBg
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = q.subject,
                                            color = when (q.subject) {
                                                "Physics" -> ServiceBlue
                                                "Chemistry" -> Color(0xFFB45309)
                                                else -> ServiceGreen
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Green Action Box (from PDF)
                                Surface(
                                    color = ServiceGreen,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.RemoveRedEye,
                                            contentDescription = "Action",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bangla Question Text
                            if (q.questionBangla.isNotBlank()) {
                                Text(
                                    text = q.questionBangla,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    lineHeight = 21.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            // English Question Text
                            Text(
                                text = q.questionEnglish.ifEmpty { q.question },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary,
                                lineHeight = 19.sp
                            )

                            // Diagram if available
                            if (q.diagramType != "NONE") {
                                Spacer(modifier = Modifier.height(8.dp))
                                QuestionDiagram(diagramType = q.diagramType)
                            }

                            // MCQ Options if applicable
                            if (q.questionType == QuestionType.MCQ && q.options.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    q.options.forEachIndexed { idx, opt ->
                                        val optLetter = ('A'.code + idx).toChar()
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "•  $optLetter. ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = PrimaryTealDark
                                            )
                                            Text(
                                                text = opt,
                                                fontSize = 13.sp,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Toggle Answer / Solution button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (q.questionType == QuestionType.NUMERIC) "টাইপ: গাণিতিক মান" else "টাইপ: বহু নির্বাচনী (MCQ)",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandedAnswers = if (isExpanded) {
                                            expandedAnswers - q.id
                                        } else {
                                            expandedAnswers + q.id
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = PrimaryTeal
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isExpanded) "উত্তর লুকান" else "উত্তর ও ব্যাখ্যা দেখুন",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = PrimaryTeal
                                    )
                                }
                            }

                            // Expanded Answer & Solution Box (Matching PDF "Answer: 30" layout)
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .background(ServiceTealBg, RoundedCornerShape(10.dp))
                                        .border(1.dp, ServiceTealBorder, RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Surface(
                                        color = PrimaryTeal,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = q.answerLabel.ifEmpty { "Answer: ${q.correctNumericAnswer}" },
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = q.explanation,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // === DOWNLOADABLE PAST PAPERS TAB ===
                items(filteredPapers) { paper ->
                    val isDownloaded = downloadedPaperId == paper.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ServiceGreenBg,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, ServiceGreenBorder)
                                ) {
                                    Text(
                                        text = "${paper.year} • ${paper.stage}",
                                        color = ServiceGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = ServiceBlueBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = paper.category.displayName,
                                        color = ServiceBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "BDJSO ${paper.stage} Question Paper (${paper.year})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${paper.timeMinutes} Mins", fontSize = 12.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.FormatListNumbered,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${paper.questionCount} Questions", fontSize = 12.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Grade,
                                        contentDescription = null,
                                        tint = ServiceAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${paper.totalMarks} Marks", fontSize = 12.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { downloadedPaperId = paper.id },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        if (isDownloaded) Icons.Default.Check else Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isDownloaded) "Downloaded" else "Save PDF")
                                }

                                Button(
                                    onClick = { onSelectPaper(paper) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Start Mock Test")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
