package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.*
import com.example.ui.viewmodel.BdjsoViewModel
import kotlin.math.max
import kotlin.math.roundToInt

// Data models for time series data
data class ParticipationTrendPoint(
    val period: String, // e.g., "Jan", "Feb", "2021", etc.
    val registered: Int,
    val appeared: Int,
    val growthRate: Double,
    val note: String = ""
)

data class ExamPerformancePoint(
    val milestone: String, // e.g., "Mock 1", "Regional", "National"
    val date: String,
    val physicsAvg: Double,
    val chemistryAvg: Double,
    val biologyAvg: Double,
    val overallAvg: Double,
    val passRatePercent: Double,
    val highestScore: Double
)

data class CategoryPerformanceBreakdown(
    val category: String,
    val totalStudents: Int,
    val avgScorePercent: Double,
    val selectionRate: Double,
    val color: Color
)

data class ScoreBandDistribution(
    val range: String,
    val studentCount: Int,
    val percentage: Double,
    val isMedianBracket: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataVisualizationDashboardScreen(
    onBack: () -> Unit,
    viewModel: BdjsoViewModel? = null
) {
    var selectedTimeframe by remember { mutableStateOf("2026 Season") }
    var selectedCategoryFilter by remember { mutableStateOf("All Categories") }
    var showWebEngine by remember { mutableStateOf(false) }

    // Subject toggle states for multi-line chart
    var showPhysics by remember { mutableStateOf(true) }
    var showChemistry by remember { mutableStateOf(true) }
    var showBiology by remember { mutableStateOf(true) }
    var showOverall by remember { mutableStateOf(true) }

    // Sample participation trends over time
    val participationTrends2026 = remember {
        listOf(
            ParticipationTrendPoint("Jan '26", 1250, 980, 15.2, "Registration Launch"),
            ParticipationTrendPoint("Feb '26", 2840, 2250, 18.5, "School Outreach Drive"),
            ParticipationTrendPoint("Mar '26", 4650, 3910, 22.1, "Regional Mock Tests"),
            ParticipationTrendPoint("Apr '26", 6400, 5420, 25.4, "Division Olympiad Prep"),
            ParticipationTrendPoint("May '26", 7900, 6800, 21.8, "Mid-Season Camp"),
            ParticipationTrendPoint("Jun '26", 9250, 8100, 27.3, "Regional Round 1"),
            ParticipationTrendPoint("Jul '26", 10400, 9050, 29.6, "Regional Round 2"),
            ParticipationTrendPoint("Aug '26", 11200, 9600, 24.1, "National Qualifiers"),
            ParticipationTrendPoint("Sept '26", 11621, 9840, 24.8, "National Final Roster")
        )
    }

    val participationTrendsAnnual = remember {
        listOf(
            ParticipationTrendPoint("2021", 3850, 3100, 12.0, "9th BDJSO Online"),
            ParticipationTrendPoint("2022", 5400, 4420, 40.2, "10th Anniversary"),
            ParticipationTrendPoint("2023", 7200, 5950, 33.3, "Hybrid Regional"),
            ParticipationTrendPoint("2024", 8950, 7540, 24.3, "64 Districts Covered"),
            ParticipationTrendPoint("2025", 10200, 8710, 14.0, "Record School Join"),
            ParticipationTrendPoint("2026", 11621, 9840, 13.9, "12th BDJSO IJSO Camp")
        )
    }

    val activeTrends = if (selectedTimeframe == "All-Time (Years)") participationTrendsAnnual else participationTrends2026

    // Sample exam performance milestones over time
    val examPerformanceData = remember {
        listOf(
            ExamPerformancePoint("Mock 1", "Feb 10", 22.4, 21.0, 23.5, 22.3, 55.7, 36.0),
            ExamPerformancePoint("Regional 1", "Mar 15", 24.8, 23.2, 25.0, 24.3, 60.8, 37.5),
            ExamPerformancePoint("Mock 2", "Apr 20", 26.1, 24.5, 26.8, 25.8, 64.5, 38.0),
            ExamPerformancePoint("Divisional", "May 25", 25.3, 24.0, 25.8, 25.0, 62.6, 38.5),
            ExamPerformancePoint("Regional 2", "Jun 18", 27.5, 26.2, 28.0, 27.2, 68.1, 39.0),
            ExamPerformancePoint("Qualifiers", "Jul 22", 28.2, 27.0, 29.1, 28.1, 70.3, 39.5),
            ExamPerformancePoint("National", "Aug 20", 29.4, 28.5, 30.2, 29.4, 73.4, 40.0),
            ExamPerformancePoint("Camp Mock", "Sept 12", 30.8, 29.6, 31.5, 30.6, 76.6, 40.0)
        )
    }

    // Category breakdown
    val categoryBreakdowns = remember {
        listOf(
            CategoryPerformanceBreakdown("Junior (6-8)", 4900, 71.4, 14.8, PrimaryTeal),
            CategoryPerformanceBreakdown("Secondary (9-10)", 4511, 74.8, 16.2, ElectricCyan),
            CategoryPerformanceBreakdown("Primary (3-5)", 2174, 58.2, 8.5, OlympiadGold),
            CategoryPerformanceBreakdown("Special (11-12)", 36, 82.5, 25.0, Color(0xFF8B5CF6))
        )
    }

    // Score distribution histogram
    val scoreDistributions = remember {
        listOf(
            ScoreBandDistribution("0 - 10", 820, 7.1),
            ScoreBandDistribution("11 - 20", 2150, 18.5),
            ScoreBandDistribution("21 - 30", 5240, 45.1, isMedianBracket = true),
            ScoreBandDistribution("31 - 36", 2480, 21.3),
            ScoreBandDistribution("37 - 40", 931, 8.0)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Data Visualization Dashboard",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTealDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = PrimaryTeal.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Recharts",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Student Participation & Exam Performance Trends",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Switch between Native Compose Chart Engine and Web Recharts Engine
                    FilterChip(
                        selected = showWebEngine,
                        onClick = { showWebEngine = !showWebEngine },
                        label = {
                            Text(
                                text = if (showWebEngine) "📱 Native" else "🌐 Web Engine",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryTeal,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { paddingValues ->
        if (showWebEngine) {
            // Live Recharts Web Engine inside Android WebView
            RechartsWebEngineView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // Native Jetpack Compose Recharts-fidelity visual dashboard
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundClean)
                    .padding(paddingValues)
                    .testTag("data_visualization_content"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Time Horizon & Filter Controls
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "সময়সীমা নির্বাচন (Time Horizon)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Live Data Synced",
                                fontSize = 11.sp,
                                color = ServiceGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val timeframes = listOf("2026 Season", "All-Time (Years)", "Last 12 Mos", "Regional Stage")
                            items(timeframes) { tf ->
                                FilterChip(
                                    selected = selectedTimeframe == tf,
                                    onClick = { selectedTimeframe = tf },
                                    label = { Text(tf, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryDarkNavy,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categories = listOf("All Categories", "Junior (6-8)", "Secondary (9-10)", "Primary (3-5)", "Special")
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategoryFilter == cat,
                                    onClick = { selectedCategoryFilter = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryTeal,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // 2. High-Level KPI Stat Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiSummaryCard(
                            title = "মোট অংশগ্রহণ",
                            value = "11,621",
                            subtext = "+24.8% YoY বৃদ্ধি",
                            icon = Icons.Default.People,
                            color = PrimaryTeal,
                            modifier = Modifier.weight(1f)
                        )
                        KpiSummaryCard(
                            title = "পরীক্ষায় উপস্থিতি",
                            value = "9,840",
                            subtext = "84.7% উপস্থিতি হার",
                            icon = Icons.Default.AssignmentTurnedIn,
                            color = ElectricCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiSummaryCard(
                            title = "গড় স্কোর (Overall)",
                            value = "68.4%",
                            subtext = "27.4 / 40 নম্বর",
                            icon = Icons.Default.TrendingUp,
                            color = BdjsoEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        KpiSummaryCard(
                            title = "ক্যাম্প সিলেকশন",
                            value = "1,440",
                            subtext = "শীর্ষ 12.4% মেধা",
                            icon = Icons.Default.EmojiEvents,
                            color = OlympiadGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. CHART 1: Student Participation Trends Over Time (Recharts-Style Area & Line Chart)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chart_participation_trends"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📈 শিক্ষার্থী অংশগ্রহণ ট্রেন্ড (Participation Trends)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "নিবন্ধিত বনাম পরীক্ষায় উপস্থিত শিক্ষার্থীর বৃদ্ধি",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = Color(0xFFE0F2FE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "AreaChart",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Legend
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryTeal)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("নিবন্ধন (Registered)", fontSize = 11.sp, color = TextPrimary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(BdjsoEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("উপস্থিতি (Appeared)", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Area Chart Canvas
                            RechartsAreaTrendCanvas(
                                dataPoints = activeTrends,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                            )
                        }
                    }
                }

                // 4. CHART 2: Exam Performance Scores Over Time (Recharts Multi-Line Chart with Interactive Tooltips & Legend)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chart_exam_performance_scores"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "🎯 বিষয়ভিত্তিক পরীক্ষার স্কোর ট্রেন্ড (Scores Over Time)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "পদার্থ, রসায়ন, জীববিজ্ঞান ও সামগ্রিক গড় নম্বর (৪০-এ)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "LineChart",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive Clickable Legend (Toggle lines on/off)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LegendToggleItem(
                                    label = "পদার্থ",
                                    color = Color(0xFF0D9488),
                                    enabled = showPhysics,
                                    onClick = { showPhysics = !showPhysics }
                                )
                                LegendToggleItem(
                                    label = "রসায়ন",
                                    color = Color(0xFFF59E0B),
                                    enabled = showChemistry,
                                    onClick = { showChemistry = !showChemistry }
                                )
                                LegendToggleItem(
                                    label = "জীববিজ্ঞান",
                                    color = Color(0xFF0284C7),
                                    enabled = showBiology,
                                    onClick = { showBiology = !showBiology }
                                )
                                LegendToggleItem(
                                    label = "গড় মোট",
                                    color = Color(0xFF6366F1),
                                    enabled = showOverall,
                                    onClick = { showOverall = !showOverall }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Multi-line Canvas
                            RechartsMultiLineCanvas(
                                dataPoints = examPerformanceData,
                                showPhysics = showPhysics,
                                showChemistry = showChemistry,
                                showBiology = showBiology,
                                showOverall = showOverall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                            )
                        }
                    }
                }

                // 5. CHART 3: Category Breakdown & Selection Rates (Composed Bar & Progress)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📊 ক্যাটাগরিভিত্তিক অংশগ্রহণ ও মেধা হার (Category Breakdown)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "প্রাইমারি, জুনিয়র ও সেকেন্ডারির তুলনামূলক ফলাফল",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            categoryBreakdowns.forEach { cb ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cb.category,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${cb.totalStudents} জন • গড়: ${cb.avgScorePercent}%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = cb.color
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    LinearProgressIndicator(
                                        progress = { (cb.avgScorePercent / 100.0).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = cb.color,
                                        trackColor = Color(0xFFE2E8F0)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "ক্যাম্প বাছাই হার: ${cb.selectionRate}%",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "সাফল্য মান: উচ্চ",
                                            fontSize = 10.sp,
                                            color = BdjsoEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. CHART 4: Score Distribution Histogram (Recharts BarChart)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, BorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📶 স্কোর ডিস্ট্রিবিউশন হিস্টোগ্রাম (Score Distribution)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "নম্বর ব্যান্ডের ভিত্তিতে শিক্ষার্থীর বন্টন (৪০-এ)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF3E8FF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "BarChart",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E22CE),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            RechartsScoreDistributionCanvas(
                                distributions = scoreDistributions,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MetricPill("২৫তম পারসেন্টাইল", "১৮.৫")
                                MetricPill("মিডিয়ান স্কোর", "২৭.২", highlight = true)
                                MetricPill("৭৫তম পারসেন্টাইল", "৩৪.০")
                                MetricPill("৯০তম পারসেন্টাইল", "৩৭.৫")
                            }
                        }
                    }
                }

                // 7. Key Findings & Insights
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Insights,
                                    contentDescription = null,
                                    tint = OlympiadGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "বিজ্ঞান অলিম্পিয়াড ডাটা ইনসাইট (Key Analytics)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "• অংশগ্রহণ বৃদ্ধি: ২০২৫ সালের তুলনায় ২০২৬ সালে দেশব্যাপী অংশগ্রহণ ২৪.৮% বৃদ্ধি পেয়েছে।",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• সবচেয়ে শক্তিশালী বিষয়: জীববিজ্ঞানে সার্বিক গড় স্কোর সর্বোচ্চ (৩০.৬/৪০), যেখানে রসায়নে ক্রমাগত অগ্রগতি লক্ষ্যণীয়।",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• আঞ্চলিক মেধা বন্টন: ঢাকা ও রংপুর বিভাগের শিক্ষার্থীদের উপস্থিতির হার ও পারফরম্যান্স ধারাবাহিক শীর্ষে।",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: KPI Summary Card
// ==========================================
@Composable
private fun KpiSummaryCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtext,
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==========================================
// COMPONENT: Legend Toggle Item
// ==========================================
@Composable
private fun LegendToggleItem(
    label: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) color.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, if (enabled) color.copy(alpha = 0.4f) else BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (enabled) color else Color.Gray)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (enabled) FontWeight.Bold else FontWeight.Normal,
                color = if (enabled) color else TextSecondary
            )
        }
    }
}

// ==========================================
// CANVAS: Recharts-Style Area & Trend Canvas
// ==========================================
@Composable
private fun RechartsAreaTrendCanvas(
    dataPoints: List<ParticipationTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    var touchIndex by remember { mutableStateOf<Int?>(null) }
    val maxVal = remember(dataPoints) {
        max(1000, dataPoints.maxOf { it.registered }) * 1.15f
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectTapGestures(
                        onPress = { offset ->
                            val itemWidth = size.width / (dataPoints.size - 1)
                            val idx = (offset.x / itemWidth).roundToInt().coerceIn(0, dataPoints.size - 1)
                            touchIndex = idx
                        }
                    )
                }
                .pointerInput(dataPoints) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val itemWidth = size.width / (dataPoints.size - 1)
                            val idx = (change.position.x / itemWidth).roundToInt().coerceIn(0, dataPoints.size - 1)
                            touchIndex = idx
                        },
                        onDragEnd = { touchIndex = null }
                    )
                }
        ) {
            val width = size.width
            val height = size.height - 30.dp.toPx() // leave room for X axis labels
            val stepX = width / (dataPoints.size - 1)

            // 1. Cartesian Grid Lines (Recharts style)
            val gridLines = 4
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = dashEffect
                )
            }

            // 2. Build Paths for Registered (Area + Line) and Appeared (Line)
            val regPath = Path()
            val regAreaPath = Path()
            val appPath = Path()

            val regPoints = mutableListOf<Offset>()
            val appPoints = mutableListOf<Offset>()

            dataPoints.forEachIndexed { i, dp ->
                val x = i * stepX
                val yReg = height - (dp.registered / maxVal) * height
                val yApp = height - (dp.appeared / maxVal) * height

                regPoints.add(Offset(x, yReg))
                appPoints.add(Offset(x, yApp))
            }

            // Cubic Bezier curve construction (matching Recharts spline)
            fun buildSmoothPath(points: List<Offset>, path: Path) {
                path.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx1 = p0.x + (p1.x - p0.x) / 2
                    val cy1 = p0.y
                    val cx2 = p0.x + (p1.x - p0.x) / 2
                    val cy2 = p1.y
                    path.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                }
            }

            buildSmoothPath(regPoints, regPath)
            buildSmoothPath(appPoints, appPath)

            // Area path starts with curve, then goes down to bottom
            regAreaPath.addPath(regPath)
            regAreaPath.lineTo(width, height)
            regAreaPath.lineTo(0f, height)
            regAreaPath.close()

            // Draw Area Fill (Cyan/Teal Gradient)
            drawPath(
                path = regAreaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryTeal.copy(alpha = 0.35f),
                        PrimaryTeal.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Registered Line
            drawPath(
                path = regPath,
                color = PrimaryTeal,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Appeared Line
            drawPath(
                path = appPath,
                color = BdjsoEmerald,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Point Markers
            regPoints.forEach { pt ->
                drawCircle(color = SurfaceWhite, radius = 5.dp.toPx(), center = pt)
                drawCircle(color = PrimaryTeal, radius = 3.dp.toPx(), center = pt)
            }

            appPoints.forEach { pt ->
                drawCircle(color = SurfaceWhite, radius = 4.dp.toPx(), center = pt)
                drawCircle(color = BdjsoEmerald, radius = 2.5.dp.toPx(), center = pt)
            }

            // Draw Active Touch Vertical Cursor line
            touchIndex?.let { idx ->
                val x = idx * stepX
                drawLine(
                    color = PrimaryDarkNavy.copy(alpha = 0.6f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                val ptReg = regPoints[idx]
                val ptApp = appPoints[idx]
                drawCircle(color = PrimaryTealDark, radius = 7.dp.toPx(), center = ptReg)
                drawCircle(color = SurfaceWhite, radius = 3.dp.toPx(), center = ptReg)

                drawCircle(color = BdjsoEmerald, radius = 6.dp.toPx(), center = ptApp)
                drawCircle(color = SurfaceWhite, radius = 2.5.dp.toPx(), center = ptApp)
            }
        }

        // Floating Tooltip Popover (Recharts Tooltip)
        touchIndex?.let { idx ->
            val point = dataPoints[idx]
            val isRightHalf = idx > dataPoints.size / 2

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(if (isRightHalf) Alignment.TopStart else Alignment.TopEnd)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryDarkNavy.copy(alpha = 0.95f),
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "${point.period}: ${point.note}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OlympiadGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "নিবন্ধিত: ${point.registered} জন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "অংশগ্রহণ: ${point.appeared} জন (${((point.appeared.toDouble() / point.registered) * 100).roundToInt()}%)",
                            fontSize = 10.sp,
                            color = Color(0xFF6EE7B7)
                        )
                        Text(
                            text = "বৃদ্ধি: +${point.growthRate}% YoY",
                            fontSize = 10.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }
            }
        }

        // X-Axis Labels at Bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEachIndexed { i, dp ->
                if (i % 2 == 0 || i == dataPoints.size - 1) {
                    Text(
                        text = dp.period,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = if (touchIndex == i) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ==========================================
// CANVAS: Recharts-Style Multi-Line Canvas
// ==========================================
@Composable
private fun RechartsMultiLineCanvas(
    dataPoints: List<ExamPerformancePoint>,
    showPhysics: Boolean,
    showChemistry: Boolean,
    showBiology: Boolean,
    showOverall: Boolean,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    var touchIndex by remember { mutableStateOf<Int?>(null) }
    val maxScore = 40.0 // Full score in BDJSO is 40

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectTapGestures(
                        onPress = { offset ->
                            val itemWidth = size.width / (dataPoints.size - 1)
                            val idx = (offset.x / itemWidth).roundToInt().coerceIn(0, dataPoints.size - 1)
                            touchIndex = idx
                        }
                    )
                }
                .pointerInput(dataPoints) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val itemWidth = size.width / (dataPoints.size - 1)
                            val idx = (change.position.x / itemWidth).roundToInt().coerceIn(0, dataPoints.size - 1)
                            touchIndex = idx
                        },
                        onDragEnd = { touchIndex = null }
                    )
                }
        ) {
            val width = size.width
            val height = size.height - 30.dp.toPx()
            val stepX = width / (dataPoints.size - 1)

            // Grid Lines
            val gridLines = 4
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = dashEffect
                )
            }

            fun drawSeries(
                color: Color,
                strokeWidthDp: Float,
                extractor: (ExamPerformancePoint) -> Double
            ) {
                val points = dataPoints.mapIndexed { i, dp ->
                    val x = i * stepX
                    val y = height - (extractor(dp) / maxScore).toFloat() * height
                    Offset(x, y)
                }

                val path = Path()
                path.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx1 = p0.x + (p1.x - p0.x) / 2
                    val cy1 = p0.y
                    val cx2 = p0.x + (p1.x - p0.x) / 2
                    val cy2 = p1.y
                    path.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidthDp.dp.toPx(), cap = StrokeCap.Round)
                )

                points.forEach { pt ->
                    drawCircle(color = SurfaceWhite, radius = 4.5.dp.toPx(), center = pt)
                    drawCircle(color = color, radius = 2.5.dp.toPx(), center = pt)
                }
            }

            if (showPhysics) drawSeries(Color(0xFF0D9488), 2.5f) { it.physicsAvg }
            if (showChemistry) drawSeries(Color(0xFFF59E0B), 2.5f) { it.chemistryAvg }
            if (showBiology) drawSeries(Color(0xFF0284C7), 2.5f) { it.biologyAvg }
            if (showOverall) drawSeries(Color(0xFF6366F1), 3.5f) { it.overallAvg }

            // Touch line
            touchIndex?.let { idx ->
                val x = idx * stepX
                drawLine(
                    color = PrimaryDarkNavy.copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
        }

        // Floating Tooltip
        touchIndex?.let { idx ->
            val pt = dataPoints[idx]
            val isRightHalf = idx > dataPoints.size / 2

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(if (isRightHalf) Alignment.TopStart else Alignment.TopEnd)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryDarkNavy.copy(alpha = 0.95f),
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "${pt.milestone} (${pt.date})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OlympiadGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "মোট গড় স্কোর: ${pt.overallAvg} / 40 (${pt.passRatePercent}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("• পদার্থ: ${pt.physicsAvg} / 40", fontSize = 10.sp, color = Color(0xFF5EEAD4))
                        Text("• রসায়ন: ${pt.chemistryAvg} / 40", fontSize = 10.sp, color = Color(0xFFFCD34D))
                        Text("• জীববিজ্ঞান: ${pt.biologyAvg} / 40", fontSize = 10.sp, color = Color(0xFF7DD3FC))
                        Text("• সর্বোচ্চ প্রাপ্ত নম্বর: ${pt.highestScore}", fontSize = 10.sp, color = Color(0xFFA7F3D0))
                    }
                }
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEachIndexed { i, dp ->
                Text(
                    text = dp.milestone,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    fontWeight = if (touchIndex == i) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ==========================================
// CANVAS: Score Distribution Histogram
// ==========================================
@Composable
private fun RechartsScoreDistributionCanvas(
    distributions: List<ScoreBandDistribution>,
    modifier: Modifier = Modifier
) {
    if (distributions.isEmpty()) return

    val maxCount = remember(distributions) {
        max(100, distributions.maxOf { it.studentCount }) * 1.15f
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height - 24.dp.toPx()
            val barWidth = width / (distributions.size * 1.6f)
            val gap = (width - (barWidth * distributions.size)) / (distributions.size + 1)

            // Grid lines
            for (i in 0..3) {
                val y = height * (i.toFloat() / 3)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }

            distributions.forEachIndexed { i, dist ->
                val x = gap + i * (barWidth + gap)
                val barHeight = (dist.studentCount / maxCount) * height
                val y = height - barHeight

                val barColor = if (dist.isMedianBracket) OlympiadGold else PrimaryTeal

                // Draw Rounded Bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor, barColor.copy(alpha = 0.7f)),
                        startY = y,
                        endY = height
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        // Labels underneath each bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            distributions.forEach { dist ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dist.range,
                        fontSize = 10.sp,
                        fontWeight = if (dist.isMedianBracket) FontWeight.Bold else FontWeight.Normal,
                        color = if (dist.isMedianBracket) Color(0xFFB45309) else TextPrimary
                    )
                    Text(
                        text = "${dist.studentCount} জন",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPONENT: Metric Pill Helper
// ==========================================
@Composable
private fun MetricPill(label: String, value: String, highlight: Boolean = false) {
    Surface(
        color = if (highlight) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (highlight) Color(0xFFFDE68A) else BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = TextSecondary)
            Text(
                value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) Color(0xFFB45309) else TextPrimary
            )
        }
    }
}

// ==========================================
// COMPONENT: Embedded Recharts Web Engine View
// ==========================================
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsWebEngineView(modifier: Modifier = Modifier) {
    val rechartsHtml = remember {
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <title>BDJSO Recharts Dashboard</title>
            <!-- React & ReactDOM -->
            <script src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
            <script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
            <!-- Recharts Library -->
            <script src="https://unpkg.com/recharts@2.12.7/umd/Recharts.min.js"></script>
            <style>
                * { box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                body { margin: 0; padding: 12px; background: #F8FAFC; color: #0F172A; }
                .card { background: #FFFFFF; border-radius: 12px; padding: 14px; margin-bottom: 14px; border: 1px solid #E2E8F0; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
                .title { font-size: 14px; font-weight: 700; color: #0F172A; margin: 0 0 4px 0; }
                .subtitle { font-size: 11px; color: #64748B; margin: 0 0 12px 0; }
                .stat-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 14px; }
                .stat-box { background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 10px; }
                .stat-label { font-size: 10px; color: #64748B; font-weight: 500; }
                .stat-val { font-size: 18px; font-weight: 800; color: #008080; margin: 2px 0; }
                .stat-badge { font-size: 9px; color: #10B981; font-weight: 600; }
                .chart-container { width: 100%; height: 230px; position: relative; }
            </style>
        </head>
        <body>
            <div class="stat-grid">
                <div class="stat-box">
                    <div class="stat-label">Total Participation</div>
                    <div class="stat-val">11,621</div>
                    <div class="stat-badge">+24.8% YoY Growth</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Avg Exam Score</div>
                    <div class="stat-val">68.4%</div>
                    <div class="stat-badge">27.4 / 40 Marks</div>
                </div>
            </div>

            <div class="card">
                <div class="title">📈 Student Participation Trend (Recharts AreaChart)</div>
                <div class="subtitle">Monthly registered and attendee student progression</div>
                <div id="area-chart" class="chart-container"></div>
            </div>

            <div class="card">
                <div class="title">🎯 Subject Exam Scores Over Time (Recharts LineChart)</div>
                <div class="subtitle">Physics, Chemistry, Biology & Overall scores across milestones</div>
                <div id="line-chart" class="chart-container"></div>
            </div>

            <div class="card">
                <div class="title">📊 Category Distribution (Recharts BarChart)</div>
                <div class="subtitle">Participants and qualifying rates across Junior, Secondary & Primary</div>
                <div id="bar-chart" class="chart-container"></div>
            </div>

            <script>
                const { ResponsiveContainer, AreaChart, Area, LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } = window.Recharts || {};

                // 1. Participation Area Chart
                const participationData = [
                    { name: 'Jan', Registered: 1250, Appeared: 980 },
                    { name: 'Feb', Registered: 2840, Appeared: 2250 },
                    { name: 'Mar', Registered: 4650, Appeared: 3910 },
                    { name: 'Apr', Registered: 6400, Appeared: 5420 },
                    { name: 'May', Registered: 7900, Appeared: 6800 },
                    { name: 'Jun', Registered: 9250, Appeared: 8100 },
                    { name: 'Jul', Registered: 10400, Appeared: 9050 },
                    { name: 'Aug', Registered: 11200, Appeared: 9600 },
                    { name: 'Sep', Registered: 11621, Appeared: 9840 }
                ];

                const AreaApp = () => {
                    if (!AreaChart) {
                        return React.createElement('div', {style: {padding: 20, textAlign: 'center', color: '#64748B'}}, 'Rendering SVG charts...');
                    }
                    return React.createElement(ResponsiveContainer, { width: '100%', height: 220 },
                        React.createElement(AreaChart, { data: participationData, margin: { top: 10, right: 10, left: -20, bottom: 0 } },
                            React.createElement(CartesianGrid, { strokeDasharray: '3 3', stroke: '#E2E8F0' }),
                            React.createElement(XAxis, { dataKey: 'name', tick: { fontSize: 10 } }),
                            React.createElement(YAxis, { tick: { fontSize: 10 } }),
                            React.createElement(Tooltip, null),
                            React.createElement(Area, { type: 'monotone', dataKey: 'Registered', stroke: '#008080', fill: '#CCECEC', strokeWidth: 2 }),
                            React.createElement(Area, { type: 'monotone', dataKey: 'Appeared', stroke: '#10B981', fill: '#D1FAE5', strokeWidth: 2 })
                        )
                    );
                };

                // 2. Exam Scores Multi-Line Chart
                const scoreData = [
                    { milestone: 'Mock 1', Physics: 22.4, Chemistry: 21.0, Biology: 23.5, Overall: 22.3 },
                    { milestone: 'Reg 1', Physics: 24.8, Chemistry: 23.2, Biology: 25.0, Overall: 24.3 },
                    { milestone: 'Mock 2', Physics: 26.1, Chemistry: 24.5, Biology: 26.8, Overall: 25.8 },
                    { milestone: 'Divisional', Physics: 25.3, Chemistry: 24.0, Biology: 25.8, Overall: 25.0 },
                    { milestone: 'Reg 2', Physics: 27.5, Chemistry: 26.2, Biology: 28.0, Overall: 27.2 },
                    { milestone: 'Qualifiers', Physics: 28.2, Chemistry: 27.0, Biology: 29.1, Overall: 28.1 },
                    { milestone: 'National', Physics: 29.4, Chemistry: 28.5, Biology: 30.2, Overall: 29.4 }
                ];

                const LineApp = () => {
                    if (!LineChart) return null;
                    return React.createElement(ResponsiveContainer, { width: '100%', height: 220 },
                        React.createElement(LineChart, { data: scoreData, margin: { top: 10, right: 10, left: -20, bottom: 0 } },
                            React.createElement(CartesianGrid, { strokeDasharray: '3 3', stroke: '#E2E8F0' }),
                            React.createElement(XAxis, { dataKey: 'milestone', tick: { fontSize: 9 } }),
                            React.createElement(YAxis, { domain: [15, 35], tick: { fontSize: 10 } }),
                            React.createElement(Tooltip, null),
                            React.createElement(Legend, { wrapperStyle: { fontSize: 10 } }),
                            React.createElement(Line, { type: 'monotone', dataKey: 'Physics', stroke: '#0D9488', strokeWidth: 2 }),
                            React.createElement(Line, { type: 'monotone', dataKey: 'Chemistry', stroke: '#F59E0B', strokeWidth: 2 }),
                            React.createElement(Line, { type: 'monotone', dataKey: 'Biology', stroke: '#0284C7', strokeWidth: 2 }),
                            React.createElement(Line, { type: 'monotone', dataKey: 'Overall', stroke: '#6366F1', strokeWidth: 3 })
                        )
                    );
                };

                // 3. Category Bar Chart
                const categoryData = [
                    { category: 'Primary', Students: 2174, SelectionRate: 8.5 },
                    { category: 'Junior', Students: 4900, SelectionRate: 14.8 },
                    { category: 'Secondary', Students: 4511, SelectionRate: 16.2 },
                    { category: 'Special', Students: 36, SelectionRate: 25.0 }
                ];

                const BarApp = () => {
                    if (!BarChart) return null;
                    return React.createElement(ResponsiveContainer, { width: '100%', height: 200 },
                        React.createElement(BarChart, { data: categoryData, margin: { top: 10, right: 10, left: -20, bottom: 0 } },
                            React.createElement(CartesianGrid, { strokeDasharray: '3 3', stroke: '#E2E8F0' }),
                            React.createElement(XAxis, { dataKey: 'category', tick: { fontSize: 10 } }),
                            React.createElement(YAxis, { tick: { fontSize: 10 } }),
                            React.createElement(Tooltip, null),
                            React.createElement(Bar, { dataKey: 'Students', fill: '#008080', radius: [6, 6, 0, 0] })
                        )
                    );
                };

                function renderCharts() {
                    try {
                        const areaEl = document.getElementById('area-chart');
                        if (areaEl) ReactDOM.render(React.createElement(AreaApp), areaEl);

                        const lineEl = document.getElementById('line-chart');
                        if (lineEl) ReactDOM.render(React.createElement(LineApp), lineEl);

                        const barEl = document.getElementById('bar-chart');
                        if (barEl) ReactDOM.render(React.createElement(BarApp), barEl);
                    } catch (e) {
                        console.error('Recharts render error', e);
                    }
                }

                window.addEventListener('load', renderCharts);
                setTimeout(renderCharts, 500);
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://bdjso.org", rechartsHtml, "text/html", "UTF-8", null)
            }
        }
    )
}
