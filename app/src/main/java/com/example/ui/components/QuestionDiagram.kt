package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun QuestionDiagram(diagramType: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (diagramType) {
                "SPEED_TIME_GRAPH" -> SpeedTimeGraphDiagram()
                "LEVER_BALANCE" -> LeverBalanceDiagram()
                "FOOD_CHAIN" -> FoodChainDiagram()
                "FORCE_COORDINATE_GRAPH" -> ForceCoordinateGraphDiagram()
                "HYDRAULIC_SYRINGE" -> HydraulicSyringeDiagram()
                "TOROIDAL_PLANET" -> ToroidalPlanetDiagram()
                else -> {}
            }
        }
    }
}

@Composable
fun SpeedTimeGraphDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Distance (km) vs Time (hours)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val originX = 40f
                val originY = h - 25f
                val maxX = w - 20f
                val maxY = 20f

                // Grid lines & Axis
                drawLine(Color(0xFFE2E8F0), Offset(originX, originY), Offset(maxX, originY), strokeWidth = 2f)
                drawLine(Color(0xFFE2E8F0), Offset(originX, originY), Offset(originX, maxY), strokeWidth = 2f)

                // Intermediate grid
                for (i in 1..3) {
                    val y = originY - (originY - maxY) * (i / 4f)
                    drawLine(Color(0xFFF1F5F9), Offset(originX, y), Offset(maxX, y), strokeWidth = 1f)
                }

                // Straight Line from (0,0) to (2, 60)
                drawLine(
                    color = Color(0xFF2563EB),
                    start = Offset(originX, originY),
                    end = Offset(maxX - 20f, maxY + 10f),
                    strokeWidth = 4f
                )

                // Coordinate Points
                drawCircle(Color(0xFF1D4ED8), radius = 5f, center = Offset(originX, originY))
                drawCircle(Color(0xFF1D4ED8), radius = 6f, center = Offset(maxX - 20f, maxY + 10f))
            }

            // Labels overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", fontSize = 10.sp, color = TextMuted)
                Text("0.5", fontSize = 10.sp, color = TextMuted)
                Text("1.0", fontSize = 10.sp, color = TextMuted)
                Text("1.5", fontSize = 10.sp, color = TextMuted)
                Text("2.0 hr", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
        Text("Time (hours) ➔", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun LeverBalanceDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Class 1 Lever in Equilibrium", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val beamY = h * 0.5f
                val fulcrumX = w * 0.7f

                // Lever beam
                drawLine(Color(0xFF334155), Offset(40f, beamY), Offset(w - 40f, beamY), strokeWidth = 8f)

                // Fulcrum Triangle (Pivot)
                val fulcrumPath = Path().apply {
                    moveTo(fulcrumX, beamY)
                    lineTo(fulcrumX - 16f, beamY + 30f)
                    lineTo(fulcrumX + 16f, beamY + 30f)
                    close()
                }
                drawPath(fulcrumPath, color = Color(0xFF0F172A))

                // Effort Arrow (Downwards at left end)
                drawLine(Color(0xFF2563EB), Offset(50f, beamY - 45f), Offset(50f, beamY), strokeWidth = 5f)
                val effortArrow = Path().apply {
                    moveTo(50f, beamY)
                    lineTo(43f, beamY - 12f)
                    lineTo(57f, beamY - 12f)
                    close()
                }
                drawPath(effortArrow, color = Color(0xFF2563EB))

                // Load Arrow (Downwards at right end)
                drawLine(Color(0xFFDC2626), Offset(w - 50f, beamY - 45f), Offset(w - 50f, beamY), strokeWidth = 5f)
                val loadArrow = Path().apply {
                    moveTo(w - 50f, beamY)
                    lineTo(w - 57f, beamY - 12f)
                    lineTo(w - 43f, beamY - 12f)
                    close()
                }
                drawPath(loadArrow, color = Color(0xFFDC2626))
            }

            // Annotation Texts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Effort (E) = ?\nArm = 6 m", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                Text("Fulcrum", fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(start = 50.dp))
                Text("Load (L) = 30 N\nArm = 2 m", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
        }
    }
}

@Composable
fun FoodChainDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("খাদ্যশৃঙ্খলে শক্তি স্থানান্তর (১০% শক্তি প্রবাহ)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tier 1: Grass
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = ServiceGreenBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServiceGreenBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("১ম স্তর", fontSize = 10.sp, color = TextSecondary)
                        Text("ঘাস (Grass)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
                        Text("৫০০০ ক্যালরি", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("১০% শক্তি", fontSize = 9.sp, color = ServiceGreen, fontWeight = FontWeight.Bold)
                Text("➔", fontSize = 16.sp, color = ServiceGreen)
            }

            // Tier 2: Grasshopper
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = ServiceAmberBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServiceAmberBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("২য় স্তর", fontSize = 10.sp, color = TextSecondary)
                        Text("ফড়িং (Insect)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        Text("৫০০ ক্যালরি", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("১০% শক্তি", fontSize = 9.sp, color = ServiceRed, fontWeight = FontWeight.Bold)
                Text("➔", fontSize = 16.sp, color = ServiceRed)
            }

            // Tier 3: Eagle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = ServiceRedBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServiceRedBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("৩য় স্তর", fontSize = 10.sp, color = TextSecondary)
                        Text("বাজপাখি (Eagle)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text("? ক্যালরি", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ServiceRed)
                    }
                }
            }
        }
    }
}

@Composable
fun ForceCoordinateGraphDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Force Field F = 10 N (along x-axis) — Paths to B(6,4)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val originX = 35f
                val originY = h - 25f
                val scaleX = (w - 70f) / 6f
                val scaleY = (originY - 20f) / 4f

                fun pt(x: Float, y: Float) = Offset(originX + x * scaleX, originY - y * scaleY)

                // Grid lines
                drawLine(Color(0xFFCBD5E1), pt(0f, 0f), pt(6f, 0f), strokeWidth = 2f)
                drawLine(Color(0xFFCBD5E1), pt(0f, 0f), pt(0f, 4f), strokeWidth = 2f)

                // Path 1 (Red line from A(0,0) to B(6,4))
                drawLine(Color(0xFFEF4444), pt(0f, 0f), pt(6f, 4f), strokeWidth = 3f)

                // Path 2 (Blue via (4,3))
                val p2 = Path().apply {
                    moveTo(pt(0f, 0f).x, pt(0f, 0f).y)
                    lineTo(pt(4f, 3f).x, pt(4f, 3f).y)
                    lineTo(pt(6f, 4f).x, pt(6f, 4f).y)
                }
                drawPath(p2, Color(0xFF3B82F6), style = Stroke(width = 3f))

                // Path 3 (Green via (2,1) and (4,1))
                val p3 = Path().apply {
                    moveTo(pt(0f, 0f).x, pt(0f, 0f).y)
                    lineTo(pt(2f, 1f).x, pt(2f, 1f).y)
                    lineTo(pt(4f, 1f).x, pt(4f, 1f).y)
                    lineTo(pt(6f, 4f).x, pt(6f, 4f).y)
                }
                drawPath(p3, Color(0xFF10B981), style = Stroke(width = 3f))

                // Vertices
                drawCircle(Color(0xFF0F172A), radius = 5f, center = pt(0f, 0f))
                drawCircle(Color(0xFF0F172A), radius = 6f, center = pt(6f, 4f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("A (0,0)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Path 1 (Red) • Path 2 (Blue) • Path 3 (Green)", fontSize = 9.sp, color = TextSecondary)
                Text("B (6,4)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun HydraulicSyringeDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pascal's Principle: Connected Syringes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ServiceBlueBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServiceBlueBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Syringe A", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ServiceBlue)
                    Text("Area = 1 cm²", fontSize = 10.sp, color = TextSecondary)
                    Text("Weight = 2 kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            Text("═══ Water Tube ═══", fontSize = 10.sp, color = ServiceBlue, fontWeight = FontWeight.Bold)

            Surface(
                color = ServiceGreenBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServiceGreenBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Syringe B", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Text("Area = 5 cm²", fontSize = 10.sp, color = TextSecondary)
                    Text("Weight = ? kg", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ServiceRed)
                }
            }
        }
    }
}

@Composable
fun ToroidalPlanetDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Doughnut-shaped (Toroidal) Planet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(110.dp)) {
                // Outer ring
                drawCircle(color = Color(0xFF94A3B8), radius = size.minDimension / 2f, style = Stroke(width = 24f))
                // Inner center point
                drawCircle(color = Color(0xFFDC2626), radius = 6f)
            }
            Text("Craft at Center: g = ?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
        }
    }
}
