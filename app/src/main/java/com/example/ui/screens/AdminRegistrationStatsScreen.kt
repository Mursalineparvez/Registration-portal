package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@Composable
fun AdminRegistrationStatsScreen(viewModel: BdjsoViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header (Screenshot 3)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Registration Stat",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = PrimaryDarkNavy
                    )
                    Text(
                        text = "Sun, August 23, 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ScienceTeal
                    )
                }
            }
        }

        // Summary Badges
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("• Total Registration", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("11,621", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.2f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("• Total Institutes", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("4,740", color = OlympiadGold, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                }
            }
        }

        // Section 1: Donut Charts (Screenshot 3)
        item {
            Text(
                text = "Distribution Ratios",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PrimaryDarkNavy
            )
        }

        // Chart 1: Old - New Student Ratio
        item {
            DonutCard(
                title = "Old - New Student Ratio",
                slices = listOf(
                    DonutSlice("New", 11480f, 99f, BdjsoEmerald),
                    DonutSlice("Old", 141f, 1f, Color(0xFFE11D48))
                ),
                total = "11,621"
            )
        }

        // Chart 2: Registration by Gender
        item {
            DonutCard(
                title = "Registration by Gender",
                slices = listOf(
                    DonutSlice("Male", 7366f, 63.4f, ScienceTeal),
                    DonutSlice("Female", 4255f, 36.6f, Color(0xFFEC4899))
                ),
                total = "11,621"
            )
        }

        // Chart 3: Registration by Category
        item {
            DonutCard(
                title = "Registration by Category",
                slices = listOf(
                    DonutSlice("Junior", 4900f, 42.2f, PrimaryDarkNavy),
                    DonutSlice("Secondary", 4511f, 38.8f, ScienceTeal),
                    DonutSlice("Primary", 2174f, 18.7f, OlympiadGold),
                    DonutSlice("Special", 36f, 0.3f, Color(0xFF94A3B8))
                ),
                total = "11,621"
            )
        }

        // Section 2: Bar Graphs (Screenshot 4)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Regional & Academic Breakdown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PrimaryDarkNavy
            )
        }

        // Bar Chart 1: Registration by Division
        item {
            BarGraphCard(
                title = "Registration by Division",
                bars = listOf(
                    BarItem("Dhaka", 3597, 31, PrimaryDarkNavy),
                    BarItem("Rangpur", 2355, 20, ScienceTeal),
                    BarItem("Rajshahi", 1540, 13, BdjsoEmerald),
                    BarItem("Chattogram", 1538, 13, ElectricCyan),
                    BarItem("Mymensingh", 978, 8, OlympiadGold),
                    BarItem("Khulna", 798, 7, Color(0xFF8B5CF6)),
                    BarItem("Sylhet", 595, 5, Color(0xFFF97316)),
                    BarItem("Barisal", 220, 2, Color(0xFFEC4899))
                ),
                maxCount = 4000
            )
        }

        // Bar Chart 2: Registration by Class
        item {
            BarGraphCard(
                title = "Registration by Class",
                bars = listOf(
                    BarItem("Class 8", 2150, 19, ScienceTeal),
                    BarItem("Class 9", 2055, 18, PrimaryDarkNavy),
                    BarItem("Class 10", 1975, 17, ScienceTeal),
                    BarItem("Class 7", 1517, 13, BdjsoEmerald),
                    BarItem("Class 6", 1213, 10, ElectricCyan),
                    BarItem("Class 5", 1001, 9, OlympiadGold),
                    BarItem("Class 4", 652, 6, Color(0xFF8B5CF6)),
                    BarItem("Class 3", 521, 4, Color(0xFFF97316)),
                    BarItem("Class 11", 510, 4, Color(0xFFEC4899)),
                    BarItem("Class 12", 27, 0, Color.Gray)
                ),
                maxCount = 2500
            )
        }

        // Bar Chart 3: Top Institutes (Screenshot 4)
        item {
            BarGraphCard(
                title = "Registration by Institute (Top 10 of 4,740)",
                bars = listOf(
                    BarItem("Rangdhenu Model School", 504, 100, PrimaryDarkNavy),
                    BarItem("Cantonment Public School & College Saidpur", 170, 34, ScienceTeal),
                    BarItem("Birshreshtha Noor Mohammad Public College", 152, 30, BdjsoEmerald),
                    BarItem("Sirajganj Govt. High School", 148, 29, ElectricCyan),
                    BarItem("Mymensingh Zilla School", 142, 28, OlympiadGold),
                    BarItem("Ideal School and College, Motijheel", 138, 27, Color(0xFF8B5CF6)),
                    BarItem("Dhaka Residential Model College", 130, 26, Color(0xFFF97316)),
                    BarItem("Rajuk Uttara Model College", 125, 25, Color(0xFFEC4899)),
                    BarItem("Viqarunnisa Noon School & College", 118, 23, ScienceTeal),
                    BarItem("Chittagong Collegiate School", 110, 22, BdjsoEmerald)
                ),
                maxCount = 550
            )
        }

        // Bar Chart 4: Top Districts
        item {
            BarGraphCard(
                title = "Registration by District (Total: 64 Districts)",
                bars = listOf(
                    BarItem("Dhaka", 2755, 24, PrimaryDarkNavy),
                    BarItem("Dinajpur", 1142, 10, ScienceTeal),
                    BarItem("Chattogram", 911, 8, BdjsoEmerald),
                    BarItem("Nilphamari", 780, 7, ElectricCyan),
                    BarItem("Sirajganj", 695, 6, OlympiadGold),
                    BarItem("Mymensingh", 620, 5, Color(0xFF8B5CF6)),
                    BarItem("Sylhet", 480, 4, Color(0xFFF97316)),
                    BarItem("Bogura", 450, 4, Color(0xFFEC4899))
                ),
                maxCount = 3000
            )
        }

        // Section 3: Trend & Date Log (Screenshot 5)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registration Trend Timeline",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PrimaryDarkNavy
            )
        }

        // Interactive Trend Curve Canvas (Screenshot 5)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                            text = "Daily Registration Trajectory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryDarkNavy
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ScienceTeal.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Peak: 1,840/day", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ScienceTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Trend Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(PrimaryDarkNavy.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        val points = listOf(
                            12f, 25f, 45f, 80f, 130f, 210f, 340f, 480f,
                            710f, 950f, 1200f, 1550f, 1840f, 1420f, 980f, 650f
                        )
                        val maxP = 2000f
                        val stepX = size.width / (points.size - 1)

                        // Grid lines
                        for (i in 1..3) {
                            val y = size.height * (i / 4f)
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Curve line
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val x1 = i * stepX
                            val y1 = size.height - (p1 / maxP) * size.height
                            val x2 = (i + 1) * stepX
                            val y2 = size.height - (p2 / maxP) * size.height

                            drawLine(
                                color = ScienceTeal,
                                start = Offset(x1, y1),
                                end = Offset(x2, y2),
                                strokeWidth = 4f,
                                cap = StrokeCap.Round
                            )

                            drawCircle(
                                color = OlympiadGold,
                                radius = 4f,
                                center = Offset(x1, y1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jul 16", fontSize = 10.sp, color = Color.Gray)
                        Text("Aug 01", fontSize = 10.sp, color = Color.Gray)
                        Text("Aug 15", fontSize = 10.sp, color = Color.Gray)
                        Text("Aug 23", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Daily Counts Table (Matching Screenshot 5)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Detailed Registration Logs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryDarkNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val trendData = listOf(
                        Pair("Sun, Aug 23, 2026", "239"),
                        Pair("Sat, Aug 22, 2026", "1,274"),
                        Pair("Fri, Aug 21, 2026", "1,840"),
                        Pair("Thu, Aug 20, 2026", "1,452"),
                        Pair("Wed, Aug 19, 2026", "1,118"),
                        Pair("Tue, Aug 18, 2026", "890"),
                        Pair("Mon, Aug 17, 2026", "765"),
                        Pair("Sun, Aug 16, 2026", "620"),
                        Pair("Sat, Aug 15, 2026", "540"),
                        Pair("Fri, Aug 14, 2026", "482"),
                        Pair("Thu, Aug 13, 2026", "410"),
                        Pair("Wed, Aug 12, 2026", "365"),
                        Pair("Tue, Aug 11, 2026", "312"),
                        Pair("Mon, Aug 10, 2026", "285")
                    )

                    trendData.forEach { (date, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ScienceTeal.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = count,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ScienceTeal
                                )
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// REUSABLE STATS COMPONENTS
// -----------------------------------------------------------------------------------------

data class DonutSlice(val label: String, val count: Float, val percentage: Float, val color: Color)

@Composable
fun DonutCard(
    title: String,
    slices: List<DonutSlice>,
    total: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryDarkNavy
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Canvas
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val stroke = 22f
                        var startAngle = -90f
                        val totalCount = slices.sumOf { it.count.toDouble() }.toFloat()

                        slices.forEach { slice ->
                            val sweep = (slice.count / totalCount) * 360f
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Butt),
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweep
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = total,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = PrimaryDarkNavy
                        )
                        Text(
                            text = "total",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legend Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    slices.forEach { slice ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(slice.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slice.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${slice.count.toInt()} (${slice.percentage}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

data class BarItem(val label: String, val count: Int, val percentage: Int, val color: Color)

@Composable
fun BarGraphCard(
    title: String,
    bars: List<BarItem>,
    maxCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryDarkNavy
            )

            bars.forEach { bar ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = bar.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "${bar.count} (${bar.percentage}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScienceTeal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (bar.count.toFloat() / maxCount).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = bar.color,
                        trackColor = Color.LightGray.copy(alpha = 0.25f)
                    )
                }
            }
        }
    }
}
