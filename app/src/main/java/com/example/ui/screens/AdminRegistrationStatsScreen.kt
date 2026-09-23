package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

// Official BDJSO Web Portal Colors from Screenshot
val ChartBlue = Color(0xFF5DADE2)
val ChartYellow = Color(0xFFF9C851)
val ChartPurple = Color(0xFFA78BFA)
val ChartPink = Color(0xFFF87171)
val ChartRose = Color(0xFFF472B6)
val ChartTeal = Color(0xFF5EEAD4)
val ChartGreen = Color(0xFF4ADE80)
val ChartSkyBlue = Color(0xFF38BDF8)
val ChartAmber = Color(0xFFFDE047)
val PortalBorderColor = Color(0xFFE2E8F0)
val PortalBgColor = Color(0xFFF8FAFC)
val PortalDarkText = Color(0xFF1E293B)
val PortalMutedText = Color(0xFF64748B)

// Institute Item Data
data class InstituteStat(
    val rank: Int,
    val name: String,
    val count: Int,
    val percentage: String,
    val district: String,
    val color: Color
)

@Composable
fun AdminRegistrationStatsScreen(viewModel: BdjsoViewModel) {
    AdminRegistrationStatsView(
        onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRegistrationStatsView(
    onBack: (() -> Unit)? = null
) {
    var showTotalInstitutesDialog by remember { mutableStateOf(false) }
    var showTotalDistrictsDialog by remember { mutableStateOf(false) }
    var showTotalUpazilasDialog by remember { mutableStateOf(false) }

    // Institute List matching Screenshot 2026-08-23 090446.png
    val top15Institutes = remember {
        listOf(
            InstituteStat(1, "Rangdhanu Model School", 504, "4%", "Dinajpur", Color(0xFF6B21A8)),
            InstituteStat(2, "Cantonment Public School and College, Saidpur", 170, "1%", "Nilphamari", Color(0xFF2DD4BF)),
            InstituteStat(3, "Cantonment Public School and College Saidpur", 152, "1%", "Nilphamari", Color(0xFF14B8A6)),
            InstituteStat(4, "Birol Residential Public School", 145, "1%", "Dinajpur", Color(0xFF0F766E)),
            InstituteStat(5, "Savar Cantonment public school and College", 138, "1%", "Dhaka", Color(0xFF115E59)),
            InstituteStat(6, "Rajuk Uttara Model College", 109, "1%", "Dhaka", Color(0xFFA3E635)),
            InstituteStat(7, "Birol city school and college", 104, "1%", "Dinajpur", Color(0xFF99F6E4)),
            InstituteStat(8, "Birol Green School", 91, "1%", "Dinajpur", Color(0xFFFCD34D)),
            InstituteStat(9, "FARAKKABAD NUROL ISLAM SCHOOL AND COLLEGE", 90, "1%", "Dinajpur", Color(0xFFDC2626)),
            InstituteStat(10, "BUNIADPUR IDEAL CADET SCHOOL", 88, "1%", "Dinajpur", Color(0xFF86EFAC)),
            InstituteStat(11, "St. Joseph Higher Secondary School", 78, "1%", "Dhaka", Color(0xFF9333EA)),
            InstituteStat(12, "Cantonment public school and college", 75, "1%", "Bogura", Color(0xFFEA580C)),
            InstituteStat(13, "champion Academy", 72, "1%", "Dhaka", Color(0xFF06B6D4)),
            InstituteStat(14, "Dhaka Residential Model College", 71, "1%", "Dhaka", Color(0xFF334155)),
            InstituteStat(15, "Mymensingh Zilla school", 68, "1%", "Mymensingh", Color(0xFF701A75))
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PortalBgColor)
            .padding(horizontal = 14.dp)
            .testTag("admin_registration_stats_content"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Web Breadcrumb & System Timestamp Header matching online.bdjso.org/admin/stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PortalDarkText
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = "Dashboard",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PortalMutedText
                    )
                }
                Text(
                    text = "Sunday, August 23, 2026 9:04:43 am",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = PortalMutedText
                )
            }
        }

        // Main Title Header with BDJSO Web Portal Stats Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, PortalBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "Stats",
                            tint = PortalDarkText,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registration Stat",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PortalDarkText
                        )
                    }
                    Text(
                        text = "Sun, August 23, 2026",
                        fontSize = 12.sp,
                        color = PortalMutedText,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = PortalBorderColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "• ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PortalDarkText
                            )
                            Text(
                                text = "Total Registration: ",
                                fontSize = 13.sp,
                                color = PortalDarkText
                            )
                            Text(
                                text = "11,621",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PortalDarkText
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "• ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PortalDarkText
                            )
                            Text(
                                text = "Total Institutes: ",
                                fontSize = 13.sp,
                                color = PortalDarkText
                            )
                            Text(
                                text = "4,740",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PortalDarkText
                            )
                        }
                    }
                }
            }
        }

        // CHART 1: Old - New Student Ratio
        item {
            WebDonutCard(
                title = "Old - New Student Ratio",
                slices = listOf(
                    WebDonutSlice("Old", 141f, "1%", ChartBlue),
                    WebDonutSlice("New", 11480f, "99%", ChartYellow)
                ),
                totalLabel = "Total Registration: 11,621"
            )
        }

        // CHART 2: Registration by Gender
        item {
            WebDonutCard(
                title = "Registration by Gender",
                slices = listOf(
                    WebDonutSlice("Male", 7366f, "63%", ChartBlue),
                    WebDonutSlice("Female", 4255f, "37%", ChartYellow)
                ),
                totalLabel = "Total Registration: 11,621"
            )
        }

        // CHART 3: Registration by Category
        item {
            WebDonutCard(
                title = "Registration by Category",
                slices = listOf(
                    WebDonutSlice("Junior", 4900f, "42%", ChartBlue),
                    WebDonutSlice("Secondary", 4511f, "39%", ChartYellow),
                    WebDonutSlice("Primary", 2174f, "19%", ChartPurple),
                    WebDonutSlice("Special", 36f, "0%", ChartPink)
                ),
                totalLabel = "Total Registration: 11,621"
            )
        }

        // CHART 4: Registration by Division (Pie/Donut with 8 Divisions)
        item {
            WebDonutCard(
                title = "Registration by Division",
                slices = listOf(
                    WebDonutSlice("Dhaka", 3597f, "31%", ChartBlue),
                    WebDonutSlice("Rangpur", 2355f, "20%", ChartYellow),
                    WebDonutSlice("Rajshahi", 1540f, "13%", Color(0xFFC084FC)),
                    WebDonutSlice("Chattagram", 1538f, "13%", ChartRose),
                    WebDonutSlice("Mymensingh", 970f, "8%", ChartTeal),
                    WebDonutSlice("Khulna", 798f, "7%", ChartGreen),
                    WebDonutSlice("Sylhet", 595f, "5%", ChartSkyBlue),
                    WebDonutSlice("Barisal", 228f, "2%", ChartAmber)
                ),
                totalLabel = "Total Registration: 11,621"
            )
        }

        // CHART 5: Registration by Class (Horizontal Bar Chart with grid lines 0-2500)
        item {
            WebClassBarChartCard()
        }

        // CHART 6: Registration by Institute (Top: 15 of 4,740) // View Total List
        item {
            WebInstituteBarChartCard(
                institutes = top15Institutes,
                onViewTotalList = { showTotalInstitutesDialog = true }
            )
        }

        // CHART 7: Registration by District (Total: 64) // View Details
        item {
            WebDistrictBarChartCard(
                onViewDetails = { showTotalDistrictsDialog = true }
            )
        }

        // CHART 8: Registration by Upazila (Total: 398) // View Details
        item {
            WebUpazilaBarChartCard(
                onViewDetails = { showTotalUpazilasDialog = true }
            )
        }

        // CHART 9: Registration Trend (Line Graph + Data Table)
        item {
            WebRegistrationTrendCard()
        }

        // Web Footer
        item {
            WebPortalFooter()
        }
    }

    // Modal: View Total List of Institutes
    if (showTotalInstitutesDialog) {
        TotalInstitutesDialog(
            topInstitutes = top15Institutes,
            onDismiss = { showTotalInstitutesDialog = false }
        )
    }

    // Modal: View Total List of Districts (All 64)
    if (showTotalDistrictsDialog) {
        TotalDistrictsDialog(
            onDismiss = { showTotalDistrictsDialog = false }
        )
    }

    // Modal: View Total List of Upazilas (All 398)
    if (showTotalUpazilasDialog) {
        TotalUpazilasDialog(
            onDismiss = { showTotalUpazilasDialog = false }
        )
    }
}

// -----------------------------------------------------------------------------------------
// DONUT CHART COMPONENT MATCHING SCREENSHOT EXACTLY
// -----------------------------------------------------------------------------------------

data class WebDonutSlice(
    val label: String,
    val count: Float,
    val percentage: String,
    val color: Color
)

@Composable
fun WebDonutCard(
    title: String,
    slices: List<WebDonutSlice>,
    totalLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Title
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PortalDarkText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Top Legend with colored chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                slices.chunked(4).forEach { rowSlices ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowSlices.forEach { s ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp, 8.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(s.color)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = s.label,
                                    fontSize = 11.sp,
                                    color = PortalMutedText
                                )
                            }
                        }
                    }
                }
            }

            // Donut Chart Canvas
            val totalSum = slices.sumOf { it.count.toDouble() }.toFloat()
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 38f
                    var startAngle = -90f

                    slices.forEach { slice ->
                        val sweep = (slice.count / totalSum) * 360f
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                            size = Size(size.width, size.height)
                        )
                        startAngle += sweep
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Registration Label under Donut
            Text(
                text = totalLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PortalDarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bullets List matching exact format in screenshot:
            // • Male: 7366 (63%)
            // • Female: 4255 (37%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                slices.forEach { slice ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "• ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PortalDarkText
                        )
                        Text(
                            text = "${slice.label}: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = PortalDarkText
                        )
                        Text(
                            text = "${slice.count.toInt()} (${slice.percentage})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PortalDarkText
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// HORIZONTAL BAR CHART: REGISTRATION BY CLASS
// -----------------------------------------------------------------------------------------

data class ClassStatItem(
    val label: String,
    val count: Int,
    val percentage: String,
    val color: Color
)

@Composable
fun WebClassBarChartCard() {
    val classStats = remember {
        listOf(
            ClassStatItem("Class 8", 2150, "19%", Color(0xFF4A90E2)),
            ClassStatItem("Class 9", 2055, "18%", Color(0xFF82D87B)),
            ClassStatItem("Class 10", 1975, "17%", Color(0xFF7BD8A5)),
            ClassStatItem("Class 7", 1537, "13%", Color(0xFF5BB1E4)),
            ClassStatItem("Class 6", 1213, "10%", Color(0xFF76D7C4)),
            ClassStatItem("Class 5", 1001, "9%", Color(0xFF168B8D)),
            ClassStatItem("Class 4", 652, "6%", Color(0xFF8E44AD)),
            ClassStatItem("Class 3", 521, "4%", Color(0xFF5DADE2)),
            ClassStatItem("Class 11", 510, "4%", Color(0xFFE67E22)),
            ClassStatItem("Class 12", 7, "0%", Color(0xFFBDC3C7))
        )
    }

    var selectedClass by remember { mutableStateOf<ClassStatItem?>(classStats[1]) } // Class 9 selected by default to match screenshot tooltip

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = "Registration by Class",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PortalDarkText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Legend: [blue] Class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp, 8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF4A90E2))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Class", fontSize = 11.sp, color = PortalMutedText)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tooltip if class selected
            selectedClass?.let { sel ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "${sel.label}: ${sel.count} (${sel.percentage})",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Chart area with Bars and vertical grid lines
            val maxScale = 2500f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                classStats.forEach { item ->
                    val isSelected = selectedClass?.label == item.label

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedClass = item },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Y-Axis label
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = PortalDarkText,
                            modifier = Modifier.width(54.dp),
                            textAlign = TextAlign.Start
                        )

                        // Bar Container with Grid background
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                        ) {
                            // Proportional bar
                            val barFraction = (item.count / maxScale).coerceIn(0.005f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(barFraction)
                                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                                    .background(item.color)
                            )
                        }
                    }
                }

                // X-Axis grid line & markers (0, 500, 1000, 1500, 2000, 2500)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 54.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0", "500", "1,000", "1,500", "2,000", "2,500").forEach { mark ->
                        Text(
                            text = mark,
                            fontSize = 9.sp,
                            color = PortalMutedText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = PortalBorderColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Exact bottom summary line from screenshot:
            Text(
                text = "Total Registration: 11,621 | Total: 10 | * Class 8: 2150 (19%) * Class 9: 2055 (18%) * Class 10: 1975 (17%) * Class 7: 1537 (13%) * Class 6: 1213 (10%) * Class 5: 1001 (9%) * Class 4: 652 (6%) * Class 3: 521 (4%) * Class 11: 510 (4%) * Class 12: 7 (0%)",
                fontSize = 10.sp,
                color = PortalMutedText,
                lineHeight = 15.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// HORIZONTAL BAR CHART: REGISTRATION BY INSTITUTE (TOP: 15 of 4,740)
// -----------------------------------------------------------------------------------------

@Composable
fun WebInstituteBarChartCard(
    institutes: List<InstituteStat>,
    onViewTotalList: () -> Unit
) {
    var selectedInstitute by remember { mutableStateOf<InstituteStat?>(institutes.firstOrNull()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Title + "View Total List" link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registration by Institute (Top: 15 of 4,740) // ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PortalDarkText
                )
                Text(
                    text = "View Total List",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.clickable { onViewTotalList() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend: [purple box] District
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp, 8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF6B21A8))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("District", fontSize = 11.sp, color = PortalMutedText)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tooltip preview
            selectedInstitute?.let { inst ->
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(4.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = inst.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${inst.count} (${inst.percentage}) • ${inst.district}",
                            fontSize = 11.sp,
                            color = ChartYellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Horizontal Bars for 15 Institutes
            val maxInstScale = 600f
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                institutes.forEach { inst ->
                    val isSelected = selectedInstitute?.name == inst.name

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedInstitute = inst },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // School Name on Y axis
                        Text(
                            text = inst.name,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = PortalDarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(135.dp),
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Bar container
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(14.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                        ) {
                            val fraction = (inst.count / maxInstScale).coerceIn(0.01f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                                    .background(inst.color)
                            )
                        }
                    }
                }

                // X-Axis markings: 0, 100, 200, 300, 400, 500, 600
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 141.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0", "100", "200", "300", "400", "500", "600").forEach { mark ->
                        Text(
                            text = mark,
                            fontSize = 8.sp,
                            color = PortalMutedText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = PortalBorderColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Exact bottom summary text matching screenshot:
            Text(
                text = "Total Registration: 11,621 // Top Institutes: 15 of 4,740\n* Rangdhanu Model School: 504 (4%) * Cantonment Public School and College, Saidpur: 170 (1%) * Cantonment Public School and College Saidpur: 152 (1%) * Birol Residential Public School: 145 (1%) * Savar Cantonment public school and College: 138 (1%) * Rajuk Uttara Model College: 109 (1%) * Birol city school and college: 104 (1%) * Birol Green School: 91 (1%) * FARAKKABAD NUROL ISLAM SCHOOL AND COLLEGE: 90 (1%) * BUNIADPUR IDEAL CADET SCHOOL: 88 (1%) * St. Joseph Higher Secondary School: 78 (1%) * Cantonment public school and college: 75 (1%) * champion Academy: 72 (1%) * Dhaka Residential Model College: 71 (1%) * Mymensingh Zilla school: 68 (1%)",
                fontSize = 10.sp,
                color = PortalMutedText,
                lineHeight = 15.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// DIALOG FOR "VIEW TOTAL LIST" (4,740 INSTITUTES SEARCHABLE)
// -----------------------------------------------------------------------------------------

@Composable
fun TotalInstitutesDialog(
    topInstitutes: List<InstituteStat>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Expanded mock list for full institute search
    val extendedInstitutes = remember {
        topInstitutes + listOf(
            InstituteStat(16, "Viqarunnisa Noon School & College", 65, "1%", "Dhaka", Color(0xFF6B21A8)),
            InstituteStat(17, "Chittagong Collegiate School", 62, "1%", "Chattagram", Color(0xFF2DD4BF)),
            InstituteStat(18, "Barishal Zilla School", 59, "1%", "Barisal", Color(0xFF14B8A6)),
            InstituteStat(19, "Sylhet Govt. Pilot High School", 56, "1%", "Sylhet", Color(0xFF0F766E)),
            InstituteStat(20, "Sirajganj Govt. High School", 54, "1%", "Sirajganj", Color(0xFF115E59)),
            InstituteStat(21, "Ideal School and College, Motijheel", 51, "1%", "Dhaka", Color(0xFFA3E635)),
            InstituteStat(22, "Birshreshtha Noor Mohammad Public College", 48, "0.4%", "Dhaka", Color(0xFF99F6E4)),
            InstituteStat(23, "Rangpur Zilla School", 45, "0.4%", "Rangpur", Color(0xFFFCD34D)),
            InstituteStat(24, "Khulna Zilla School", 42, "0.4%", "Khulna", Color(0xFFDC2626)),
            InstituteStat(25, "Comilla Zilla School", 40, "0.3%", "Cumilla", Color(0xFF86EFAC))
        )
    }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) extendedInstitutes
        else extendedInstitutes.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.district.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, PortalBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Institutes Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PortalDarkText
                        )
                        Text(
                            text = "Total Institutes: 4,740 across Bangladesh",
                            fontSize = 11.sp,
                            color = PortalMutedText
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PortalDarkText)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by School Name or District...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { inst ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = PortalBgColor),
                            border = BorderStroke(1.dp, PortalBorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        color = PrimaryTeal,
                                        shape = CircleShape,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${inst.rank}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = inst.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PortalDarkText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "District: ${inst.district}",
                                            fontSize = 10.sp,
                                            color = PortalMutedText
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, PortalBorderColor)
                                ) {
                                    Text(
                                        text = "${inst.count} (${inst.percentage})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PortalDarkText,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkNavy)
                ) {
                    Text("Close Directory", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
