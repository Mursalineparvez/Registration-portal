package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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

@Composable
fun QuestionDiagramViewer(diagramType: String) {
    if (diagramType.isBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (diagramType) {
                "DISTANCE_TIME" -> DistanceTimeDiagram()
                "LEVER" -> LeverDiagram()
                "LIFT_WORK" -> LiftDiagram()
                "MANGO_DROP" -> MangoDropDiagram()
                "ENERGY_PYRAMID" -> EnergyPyramidDiagram()
                "MAGNET" -> MagnetDiagram()
                "SYRINGES" -> SyringesDiagram()
                "FUSE_CIRCUIT" -> FuseCircuitDiagram()
                "GRID_WORK" -> GridWorkDiagram()
                "TOROIDAL" -> ToroidalDiagram()
                else -> {}
            }
        }
    }
}

// 1. Distance-Time Graph Diagram
@Composable
fun DistanceTimeDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("দূরত্ব-সময় লেখচিত্র / Distance-Time Graph", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val origin = Offset(40f, size.height - 20f)
            val endX = Offset(size.width - 20f, size.height - 20f)
            val endY = Offset(40f, 15f)

            // Axes
            drawLine(Color.DarkGray, origin, endX, strokeWidth = 3f)
            drawLine(Color.DarkGray, origin, endY, strokeWidth = 3f)

            // Grid markers
            val w = (endX.x - origin.x) / 4f
            val h = (origin.y - endY.y) / 4f

            for (i in 1..4) {
                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(origin.x + i * w, origin.y), Offset(origin.x + i * w, endY.y), strokeWidth = 1f)
                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(origin.x, origin.y - i * h), Offset(endX.x, origin.y - i * h), strokeWidth = 1f)
            }

            // Line graph: (0,0) -> (1h, 30km) -> (2h, 60km) -> (3h, 90km)
            val p0 = origin
            val p2 = Offset(origin.x + 2 * w, origin.y - 2.4f * h)
            val p3 = Offset(origin.x + 3 * w, origin.y - 3.6f * h)

            drawLine(ScienceTeal, p0, p3, strokeWidth = 4f, cap = StrokeCap.Round)
            drawCircle(OlympiadGold, radius = 5f, center = p2)

            // Highlight point t=2, d=60
            drawLine(Color.Red, Offset(p2.x, origin.y), p2, strokeWidth = 2f)
            drawLine(Color.Red, Offset(origin.x, p2.y), p2, strokeWidth = 2f)
        }
        Text("t = 2 Hours -> Distance = 60 Kilometers", fontSize = 10.sp, color = ScienceTeal, fontWeight = FontWeight.Bold)
    }
}

// 2. Lever Diagram
@Composable
fun LeverDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("লিভার সিস্টেম / Lever System Diagram", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val centerY = size.height * 0.5f
            val fulcrumX = size.width * 0.65f // load arm is 2m, effort arm is 6m (effort is 3x longer)

            // Lever Beam
            drawLine(PrimaryDarkNavy, Offset(20f, centerY), Offset(size.width - 20f, centerY), strokeWidth = 6f, cap = StrokeCap.Round)

            // Fulcrum triangle
            val trianglePath = Path().apply {
                moveTo(fulcrumX, centerY + 3f)
                lineTo(fulcrumX - 16f, centerY + 30f)
                lineTo(fulcrumX + 16f, centerY + 30f)
                close()
            }
            drawPath(trianglePath, OlympiadGold)

            // Effort Force Arrow (Left end, downwards)
            drawLine(Color(0xFFDC2626), Offset(30f, centerY - 25f), Offset(30f, centerY), strokeWidth = 3f)
            drawCircle(Color(0xFFDC2626), radius = 4f, center = Offset(30f, centerY))

            // Load Weight Box (Right end)
            drawRect(ScienceTeal, topLeft = Offset(size.width - 45f, centerY - 24f), size = Size(24f, 24f))
        }
        Text("Effort Arm = 6 m | Fulcrum | Load Arm = 2 m (Load = 30 N)", fontSize = 10.sp, color = PrimaryDarkNavy, fontWeight = FontWeight.SemiBold)
    }
}

// 3. Lift Work Diagram
@Composable
fun LiftDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("লিফট ও কাজ / Elevator & Work (50 kg to 5th Floor)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val shaftX = size.width * 0.5f

            // Building Floors
            for (i in 0..5) {
                val y = size.height - 15f - (i * 18f)
                drawLine(Color.LightGray, Offset(shaftX - 60f, y), Offset(shaftX + 60f, y), strokeWidth = 1.5f)
            }

            // Elevator Cab
            val cabY = size.height - 15f - (5 * 18f)
            drawRect(ScienceTeal, topLeft = Offset(shaftX - 18f, cabY), size = Size(36f, 16f))
            drawCircle(Color.White, radius = 3f, center = Offset(shaftX, cabY + 8f))

            // Height dimension arrow
            drawLine(OlympiadGold, Offset(shaftX + 80f, size.height - 15f), Offset(shaftX + 80f, cabY), strokeWidth = 2f)
        }
        Text("5 Floors x 3 m = 15 m height | Work = mgh = 50 * 10 * 15 = 7500 J", fontSize = 10.sp, color = ScienceTeal, fontWeight = FontWeight.Bold)
    }
}

// 4. Mango Drop Diagram
@Composable
fun MangoDropDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("পড়ন্ত ও প্রক্ষিপ্ত আম / Dropped vs Projected Mango", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val startX = 60f
            val startY = 20f
            val groundY = size.height - 15f

            // Branch
            drawLine(Color(0xFF854D0E), Offset(20f, startY), Offset(120f, startY), strokeWidth = 5f, cap = StrokeCap.Round)

            // Mango 1: Straight Down
            drawCircle(BdjsoEmerald, radius = 6f, center = Offset(startX, startY + 8f))
            drawLine(Color(0xFFDC2626), Offset(startX, startY + 14f), Offset(startX, groundY), strokeWidth = 2f)

            // Mango 2: Parabola
            drawCircle(OlympiadGold, radius = 6f, center = Offset(startX + 30f, startY + 8f))
            val path = Path().apply {
                moveTo(startX + 30f, startY + 8f)
                quadraticBezierTo(size.width * 0.5f, startY + 10f, size.width - 30f, groundY)
            }
            drawPath(path, ScienceTeal, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

            // Ground
            drawLine(Color.DarkGray, Offset(20f, groundY), Offset(size.width - 20f, groundY), strokeWidth = 2f)
        }
        Text("Vertical acceleration g is identical -> Both land in exactly 2 seconds", fontSize = 10.sp, color = PrimaryDarkNavy, fontWeight = FontWeight.Medium)
    }
}

// 5. Energy Pyramid Diagram
@Composable
fun EnergyPyramidDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("খাদ্য পিরামিড (১০% নিয়ম) / Trophic Energy Pyramid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val centerX = size.width * 0.5f
            val topY = 15f
            val bottomY = size.height - 15f

            // Level 3 (Hawk, top)
            val pTop = Path().apply {
                moveTo(centerX, topY)
                lineTo(centerX - 35f, topY + 28f)
                lineTo(centerX + 35f, topY + 28f)
                close()
            }
            drawPath(pTop, OlympiadGold)

            // Level 2 (Grasshopper, middle)
            val pMid = Path().apply {
                moveTo(centerX - 35f, topY + 30f)
                lineTo(centerX + 35f, topY + 30f)
                lineTo(centerX + 70f, topY + 58f)
                lineTo(centerX - 70f, topY + 58f)
                close()
            }
            drawPath(pMid, ScienceTeal)

            // Level 1 (Grass, base)
            val pBase = Path().apply {
                moveTo(centerX - 70f, topY + 60f)
                lineTo(centerX + 70f, topY + 60f)
                lineTo(centerX + 110f, bottomY)
                lineTo(centerX - 110f, bottomY)
                close()
            }
            drawPath(pBase, BdjsoEmerald)
        }
        Text("Grass (5000 cal) -> Grasshopper (500 cal) -> Hawk (50 cal)", fontSize = 10.sp, color = BdjsoEmerald, fontWeight = FontWeight.Bold)
    }
}

// 6. Snapped Magnet Diagram
@Composable
fun MagnetDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ভাঙা দণ্ডচুম্বক / Snapped Bar Magnet Polarity", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val centerY = size.height * 0.5f
            val centerX = size.width * 0.5f

            // Left piece [N (red) | S (blue)]
            drawRect(Color(0xFFDC2626), topLeft = Offset(centerX - 80f, centerY - 14f), size = Size(35f, 28f))
            drawRect(ScienceTeal, topLeft = Offset(centerX - 45f, centerY - 14f), size = Size(35f, 28f))

            // Right piece [N (red) | S (blue)]
            drawRect(Color(0xFFDC2626), topLeft = Offset(centerX + 10f, centerY - 14f), size = Size(35f, 28f))
            drawRect(ScienceTeal, topLeft = Offset(centerX + 45f, centerY - 14f), size = Size(35f, 28f))

            // Facing broken edges: Left is S, Right is N -> Attraction arrows
            drawLine(OlympiadGold, Offset(centerX - 8f, centerY), Offset(centerX + 6f, centerY), strokeWidth = 3f)
        }
        Text("Facing faces are S and N -> They attract each other!", fontSize = 10.sp, color = ScienceTeal, fontWeight = FontWeight.Bold)
    }
}

// 7. Pascal's Law Syringes Diagram
@Composable
fun SyringesDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("হাইড্রলিক সিরিঞ্জ / Hydraulic Syringes (Pascal's Law)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val pipeY = size.height - 20f

            // Small Syringe A (1 cm²)
            drawRect(ScienceTeal.copy(alpha = 0.3f), topLeft = Offset(60f, 30f), size = Size(20f, pipeY - 30f))
            // Piston A with 2kg
            drawRect(PrimaryDarkNavy, topLeft = Offset(58f, 30f), size = Size(24f, 8f))
            drawRect(OlympiadGold, topLeft = Offset(62f, 15f), size = Size(16f, 15f))

            // Connecting pipe
            drawLine(ScienceTeal, Offset(60f, pipeY), Offset(size.width - 60f, pipeY), strokeWidth = 12f)

            // Large Syringe B (5 cm²)
            drawRect(ScienceTeal.copy(alpha = 0.3f), topLeft = Offset(size.width - 110f, 30f), size = Size(50f, pipeY - 30f))
            // Piston B with 10kg
            drawRect(PrimaryDarkNavy, topLeft = Offset(size.width - 112f, 30f), size = Size(54f, 8f))
            drawRect(OlympiadGold, topLeft = Offset(size.width - 98f, 10f), size = Size(26f, 20f))
        }
        Text("A1 = 1 cm² (2 kg) | A2 = 5 cm² (10 kg) -> F1/A1 = F2/A2", fontSize = 10.sp, color = PrimaryDarkNavy, fontWeight = FontWeight.SemiBold)
    }
}

// 8. Fuse Circuit Diagram
@Composable
fun FuseCircuitDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("বৈদ্যুতিক বর্তনী ও ফিউজ / 220V Electric Circuit with Fuse", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val topY = 25f
            val botY = size.height - 25f

            // Circuit loops
            drawLine(PrimaryDarkNavy, Offset(30f, topY), Offset(size.width - 30f, topY), strokeWidth = 2f)
            drawLine(PrimaryDarkNavy, Offset(30f, botY), Offset(size.width - 30f, botY), strokeWidth = 2f)

            // Fuse box on top rail
            drawRect(Color(0xFFDC2626), topLeft = Offset(70f, topY - 6f), size = Size(24f, 12f))

            // 3 Parallel load branches
            val step = (size.width - 150f) / 3f
            for (i in 1..3) {
                val bx = 110f + i * step
                drawLine(PrimaryDarkNavy, Offset(bx, topY), Offset(bx, botY), strokeWidth = 2f)
                drawCircle(ScienceTeal, radius = 8f, center = Offset(bx, (topY + botY) / 2f))
            }
        }
        Text("Total Power = 1342 W -> Current = 6.1 A -> 10 A Fuse is optimal", fontSize = 10.sp, color = ScienceTeal, fontWeight = FontWeight.Bold)
    }
}

// 9. Grid Work Diagram
@Composable
fun GridWorkDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("গ্রিডে কৃতকাজ / Work Done Along 3 Paths (F = 10 N along x)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val ax = 40f
            val ay = size.height - 20f
            val bx = size.width - 40f
            val by = 20f

            // Grid lines
            drawLine(Color.LightGray, Offset(ax, ay), Offset(bx, ay), strokeWidth = 1f)
            drawLine(Color.LightGray, Offset(ax, ay), Offset(ax, by), strokeWidth = 1f)

            // Path 1 (Diagonal)
            drawLine(ScienceTeal, Offset(ax, ay), Offset(bx, by), strokeWidth = 2.5f)

            // Path 2 (Right angle)
            val p2 = Path().apply {
                moveTo(ax, ay)
                lineTo(bx, ay)
                lineTo(bx, by)
            }
            drawPath(p2, OlympiadGold, style = Stroke(width = 2.5f))

            // Path 3 (Curve)
            val p3 = Path().apply {
                moveTo(ax, ay)
                quadraticBezierTo(ax, by, bx, by)
            }
            drawPath(p3, Color(0xFFDC2626), style = Stroke(width = 2.5f))

            drawCircle(PrimaryDarkNavy, radius = 5f, center = Offset(ax, ay))
            drawCircle(PrimaryDarkNavy, radius = 5f, center = Offset(bx, by))
        }
        Text("W1 = W2 = W3 = F * delta_x = 10 * 6 = 60 J -> Sum = 180 J", fontSize = 10.sp, color = PrimaryDarkNavy, fontWeight = FontWeight.Bold)
    }
}

// 10. Toroidal Planet Diagram
@Composable
fun ToroidalDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("টোরয়েডাল গ্রহ / Toroidal (Donut) Planet Gravity at Center", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            val center = Offset(size.width * 0.5f, size.height * 0.5f)

            // Torus outer circle
            drawCircle(PrimaryDarkNavy.copy(alpha = 0.15f), radius = 45f, center = center)
            drawCircle(PrimaryDarkNavy, radius = 45f, center = center, style = Stroke(width = 8f))

            // Torus inner hole
            drawCircle(Color.White, radius = 22f, center = center)
            drawCircle(PrimaryDarkNavy, radius = 22f, center = center, style = Stroke(width = 4f))

            // Center test particle
            drawCircle(Color(0xFFDC2626), radius = 4f, center = center)

            // Symmetrical cancelling arrows
            val r = 16f
            for (angle in listOf(0.0, 90.0, 180.0, 270.0)) {
                val rad = Math.toRadians(angle)
                val ex = center.x + (r * Math.cos(rad)).toFloat()
                val ey = center.y + (r * Math.sin(rad)).toFloat()
                drawLine(OlympiadGold, center, Offset(ex, ey), strokeWidth = 2f)
            }
        }
        Text("Radial symmetry causes opposing gravities to cancel -> g = 0 m/s²", fontSize = 10.sp, color = ScienceTeal, fontWeight = FontWeight.Bold)
    }
}
