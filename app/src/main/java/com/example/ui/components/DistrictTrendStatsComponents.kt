package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.example.ui.screens.PortalBgColor
import com.example.ui.screens.PortalBorderColor
import com.example.ui.screens.PortalDarkText
import com.example.ui.screens.PortalMutedText
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.PrimaryTeal

// -----------------------------------------------------------------------------------------
// DATA MODELS FOR DISTRICTS, UPAZILAS, AND TRENDS
// -----------------------------------------------------------------------------------------

data class DistrictStatItem(
    val name: String,
    val count: Int,
    val percentage: String,
    val color: Color
)

data class UpazilaStatItem(
    val name: String,
    val count: Int,
    val percentage: String
)

data class TrendDayStat(
    val dateCode: String,
    val dateLabel: String,
    val count: Int?,
    val displayValue: String
)

// Complete 64 districts in exact order from Screenshot 2026-08-23 090457.png
val All64DistrictsList = listOf(
    DistrictStatItem("Dhaka", 2798, "24%", Color(0xFF2563EB)),
    DistrictStatItem("Dinajpur", 1142, "10%", Color(0xFF0D9488)),
    DistrictStatItem("Chattagram", 911, "8%", Color(0xFF16A34A)),
    DistrictStatItem("Mymensingh", 717, "6%", Color(0xFF9333EA)),
    DistrictStatItem("Nilphamari", 689, "6%", Color(0xFF06B6D4)),
    DistrictStatItem("Sirajganj", 657, "6%", Color(0xFFD97706)),
    DistrictStatItem("Rajshahi", 288, "2%", Color(0xFF10B981)),
    DistrictStatItem("Sylhet", 282, "2%", Color(0xFF6366F1)),
    DistrictStatItem("Rangpur", 281, "2%", Color(0xFFEC4899)),
    DistrictStatItem("Khulna", 249, "2%", Color(0xFF14B8A6)),
    DistrictStatItem("Bogura", 234, "2%", Color(0xFFF59E0B)),
    DistrictStatItem("Kushtia", 205, "2%", Color(0xFF8B5CF6)),
    DistrictStatItem("Narayanganj", 192, "2%", Color(0xFF0284C7)),
    DistrictStatItem("Netrokona", 187, "2%", Color(0xFF059669)),
    DistrictStatItem("Moulvibazar", 182, "2%", Color(0xFF7C3AED)),
    DistrictStatItem("Comilla", 182, "2%", Color(0xFFEA580C)),
    DistrictStatItem("Gazipur", 178, "2%", Color(0xFF2563EB)),
    DistrictStatItem("Narsingdi", 163, "1%", Color(0xFF0D9488)),
    DistrictStatItem("Chandpur", 142, "1%", Color(0xFFD97706)),
    DistrictStatItem("Jashore", 134, "1%", Color(0xFF16A34A)),
    DistrictStatItem("Pabna", 122, "1%", Color(0xFF9333EA)),
    DistrictStatItem("Barisal", 120, "1%", Color(0xFF06B6D4)),
    DistrictStatItem("Chapainawabganj", 92, "1%", Color(0xFF10B981)),
    DistrictStatItem("Habiganj", 88, "1%", Color(0xFF6366F1)),
    DistrictStatItem("Tangail", 87, "1%", Color(0xFFEC4899)),
    DistrictStatItem("Bagerhat", 86, "1%", Color(0xFF14B8A6)),
    DistrictStatItem("Noakhali", 82, "1%", Color(0xFFF59E0B)),
    DistrictStatItem("Kurigram", 76, "1%", Color(0xFF8B5CF6)),
    DistrictStatItem("Naogaon", 64, "1%", Color(0xFF0284C7)),
    DistrictStatItem("Gaibandha", 63, "1%", Color(0xFF059669)),
    DistrictStatItem("Coxsbazar", 60, "1%", Color(0xFF7C3AED)),
    DistrictStatItem("Brahmanbaria", 51, "0%", Color(0xFFEA580C)),
    DistrictStatItem("Jamalpur", 50, "0%", Color(0xFF2563EB)),
    DistrictStatItem("Kishoreganj", 47, "0%", Color(0xFF0D9488)),
    DistrictStatItem("Jhenaidah", 47, "0%", Color(0xFFD97706)),
    DistrictStatItem("Natore", 45, "0%", Color(0xFF16A34A)),
    DistrictStatItem("Sunamganj", 43, "0%", Color(0xFF9333EA)),
    DistrictStatItem("Patuakhali", 43, "0%", Color(0xFF06B6D4)),
    DistrictStatItem("Thakurgaon", 41, "0%", Color(0xFF10B981)),
    DistrictStatItem("Lakshmipur", 41, "0%", Color(0xFF6366F1)),
    DistrictStatItem("Lalmonirhat", 38, "0%", Color(0xFFEC4899)),
    DistrictStatItem("Joypurhat", 38, "0%", Color(0xFF14B8A6)),
    DistrictStatItem("Faridpur", 37, "0%", Color(0xFFF59E0B)),
    DistrictStatItem("Feni", 37, "0%", Color(0xFF8B5CF6)),
    DistrictStatItem("Pirojpur", 30, "0%", Color(0xFF0284C7)),
    DistrictStatItem("Munshiganj", 30, "0%", Color(0xFF059669)),
    DistrictStatItem("Panchagarh", 25, "0%", Color(0xFF7C3AED)),
    DistrictStatItem("Bhola", 24, "0%", Color(0xFFEA580C)),
    DistrictStatItem("Satkhira", 22, "0%", Color(0xFF2563EB)),
    DistrictStatItem("Rajbari", 21, "0%", Color(0xFF0D9488)),
    DistrictStatItem("Magura", 20, "0%", Color(0xFFD97706)),
    DistrictStatItem("Chuadanga", 18, "0%", Color(0xFF16A34A)),
    DistrictStatItem("Rangamati", 18, "0%", Color(0xFF9333EA)),
    DistrictStatItem("Gopalganj", 17, "0%", Color(0xFF06B6D4)),
    DistrictStatItem("Sherpur", 16, "0%", Color(0xFF10B981)),
    DistrictStatItem("Manikganj", 12, "0%", Color(0xFF6366F1)),
    DistrictStatItem("Bandarban", 12, "0%", Color(0xFFEC4899)),
    DistrictStatItem("Narail", 10, "0%", Color(0xFF14B8A6)),
    DistrictStatItem("Barguna", 8, "0%", Color(0xFFF59E0B)),
    DistrictStatItem("Madaripur", 8, "0%", Color(0xFF8B5CF6)),
    DistrictStatItem("Meherpur", 7, "0%", Color(0xFF0284C7)),
    DistrictStatItem("Shariatpur", 7, "0%", Color(0xFF059669)),
    DistrictStatItem("Jhalakathi", 3, "0%", Color(0xFF7C3AED)),
    DistrictStatItem("Khagrachhari", 2, "0%", Color(0xFFEA580C))
)

// Selected 22 districts represented on the horizontal chart from screenshot
val ChartDistricts22 = listOf(
    DistrictStatItem("Dhaka", 2798, "24%", Color(0xFF2563EB)),
    DistrictStatItem("Mymensingh", 717, "6%", Color(0xFFEC4899)),
    DistrictStatItem("Rajshahi", 288, "2%", Color(0xFF10B981)),
    DistrictStatItem("Khulna", 249, "2%", Color(0xFF0D9488)),
    DistrictStatItem("Narayanganj", 192, "2%", Color(0xFF3B82F6)),
    DistrictStatItem("Comilla", 182, "2%", Color(0xFFEF4444)),
    DistrictStatItem("Chandpur", 142, "1%", Color(0xFFF59E0B)),
    DistrictStatItem("Barisal", 120, "1%", Color(0xFF06B6D4)),
    DistrictStatItem("Tangail", 87, "1%", Color(0xFF84CC16)),
    DistrictStatItem("Kurigram", 76, "1%", Color(0xFF8B5CF6)),
    DistrictStatItem("CoxsBazar", 60, "1%", Color(0xFFE11D48)),
    DistrictStatItem("Kishoreganj", 47, "0%", Color(0xFF14B8A6)),
    DistrictStatItem("Sunamganj", 43, "0%", Color(0xFF6366F1)),
    DistrictStatItem("Lakshmipur", 41, "0%", Color(0xFF9333EA)),
    DistrictStatItem("Faridpur", 37, "0%", Color(0xFF10B981)),
    DistrictStatItem("Munshiganj", 30, "0%", Color(0xFFF97316)),
    DistrictStatItem("Satkhira", 22, "0%", Color(0xFF06B6D4)),
    DistrictStatItem("Chuadanga", 18, "0%", Color(0xFF22C55E)),
    DistrictStatItem("Sherpur", 16, "0%", Color(0xFFA855F7)),
    DistrictStatItem("Narail", 10, "0%", Color(0xFF0284C7)),
    DistrictStatItem("Meherpur", 7, "0%", Color(0xFFEAB308)),
    DistrictStatItem("Khagrachhari", 2, "0%", Color(0xFFEC4899))
)

// Upazilas List from Screenshot 1 & 2
val AllUpazilasList = listOf(
    UpazilaStatItem("Dhaka Metropolitan", 2415, "21%"),
    UpazilaStatItem("Birol", 896, "8%"),
    UpazilaStatItem("Chattagram Metropolitan", 681, "6%"),
    UpazilaStatItem("Syedpur", 618, "5%"),
    UpazilaStatItem("Shahjadpur", 556, "5%"),
    UpazilaStatItem("Mymensingh Sadar", 483, "4%"),
    UpazilaStatItem("Savar", 317, "3%"),
    UpazilaStatItem("Rajshahi Metropolitan", 232, "2%"),
    UpazilaStatItem("Sylhet Sadar", 213, "2%"),
    UpazilaStatItem("Rangpur Sadar", 198, "2%"),
    UpazilaStatItem("Gafargaon", 167, "1%"),
    UpazilaStatItem("Khulna Metropolitan", 164, "1%"),
    UpazilaStatItem("Kushtia Sadar", 157, "1%"),
    UpazilaStatItem("Bogra Sadar", 153, "1%"),
    UpazilaStatItem("Dinajpur Sadar", 153, "1%"),
    UpazilaStatItem("Narsingdi Sadar", 134, "1%"),
    UpazilaStatItem("Netrokona Sadar", 130, "1%"),
    UpazilaStatItem("Jessore Sadar", 116, "1%"),
    UpazilaStatItem("Chandpur Sadar", 115, "1%"),
    UpazilaStatItem("Sreemangal", 108, "1%"),
    UpazilaStatItem("Narayanganj Sadar", 107, "1%"),
    UpazilaStatItem("Gazipur Sadar", 106, "1%"),
    UpazilaStatItem("Pabna Sadar", 91, "1%"),
    UpazilaStatItem("Comilla Sadar", 90, "1%"),
    UpazilaStatItem("Barisal Sadar", 85, "1%"),
    UpazilaStatItem("Sirajganj Sadar", 68, "1%"),
    UpazilaStatItem("Hathazari", 65, "1%"),
    UpazilaStatItem("Kurigram Sadar", 57, "0%"),
    UpazilaStatItem("Tangail Sadar", 57, "0%"),
    UpazilaStatItem("Nilphamari Sadar", 53, "0%"),
    UpazilaStatItem("Habiganj Sadar", 53, "0%"),
    UpazilaStatItem("Shibganj", 50, "0%"),
    UpazilaStatItem("Noakhali Sadar", 49, "0%"),
    UpazilaStatItem("Bagerhat Sadar", 48, "0%"),
    UpazilaStatItem("Sitakunda", 41, "0%"),
    UpazilaStatItem("Naogaon Sadar", 40, "0%"),
    UpazilaStatItem("Keraniganj", 39, "0%"),
    UpazilaStatItem("Fultola", 39, "0%"),
    UpazilaStatItem("Sylhet Metropolitan", 39, "0%"),
    UpazilaStatItem("Sreepur", 38, "0%"),
    UpazilaStatItem("Rupganj", 38, "0%"),
    UpazilaStatItem("Kulaura", 37, "0%"),
    UpazilaStatItem("Brahmanbaria Sadar", 36, "0%"),
    UpazilaStatItem("Jamalpur Sadar", 35, "0%"),
    UpazilaStatItem("Rangpur Metropolitan", 34, "0%"),
    UpazilaStatItem("Sonargaon", 34, "0%"),
    UpazilaStatItem("Feni Sadar", 30, "0%"),
    UpazilaStatItem("Parbatipur", 29, "0%"),
    UpazilaStatItem("Sherpur", 28, "0%"),
    UpazilaStatItem("Moulvibazar Sadar", 28, "0%"),
    UpazilaStatItem("Kachua", 28, "0%"),
    UpazilaStatItem("Joypurhat Sadar", 27, "0%"),
    UpazilaStatItem("Gaibandha Sadar", 27, "0%"),
    UpazilaStatItem("Jhenaidah Sadar", 27, "0%"),
    UpazilaStatItem("Lalmonirhat Sadar", 26, "0%"),
    UpazilaStatItem("Thakurgaon Sadar", 25, "0%"),
    UpazilaStatItem("Shajahahanpur", 25, "0%"),
    UpazilaStatItem("Coxsbazar Sadar", 24, "0%"),
    UpazilaStatItem("Natore Sadar", 24, "0%"),
    UpazilaStatItem("Paba", 24, "0%"),
    UpazilaStatItem("Purbadhala", 23, "0%"),
    UpazilaStatItem("Nachol", 23, "0%"),
    UpazilaStatItem("Barisal Metropolitan", 22, "0%"),
    UpazilaStatItem("Dhamrai", 22, "0%"),
    UpazilaStatItem("Gazipur Metropolitan", 22, "0%"),
    UpazilaStatItem("Nesarabad", 21, "0%"),
    UpazilaStatItem("Chunarughat", 21, "0%"),
    UpazilaStatItem("Lakshmipur Sadar", 21, "0%"),
    UpazilaStatItem("Faridpur Sadar", 21, "0%"),
    UpazilaStatItem("Sunamganj Sadar", 21, "0%"),
    UpazilaStatItem("Daulatpur", 20, "0%"),
    UpazilaStatItem("Pakundia", 20, "0%"),
    UpazilaStatItem("Chapainawabganj Sadar", 19, "0%"),
    UpazilaStatItem("Patiya", 18, "0%"),
    UpazilaStatItem("Rupsha", 17, "0%"),
    UpazilaStatItem("Patuakhali Sadar", 17, "0%"),
    UpazilaStatItem("Palashbari", 17, "0%"),
    UpazilaStatItem("Anwara", 17, "0%"),
    UpazilaStatItem("Daudkandi", 16, "0%"),
    UpazilaStatItem("Homna", 16, "0%"),
    UpazilaStatItem("Chirirbandar", 16, "0%"),
    UpazilaStatItem("Chuadanga Sadar", 12, "0%"),
    UpazilaStatItem("Pirgonj", 12, "0%"),
    UpazilaStatItem("Ullapara", 12, "0%"),
    UpazilaStatItem("Bandar", 12, "0%"),
    UpazilaStatItem("Sundarganj", 11, "0%"),
    UpazilaStatItem("Kishoreganj Sadar", 11, "0%"),
    UpazilaStatItem("Ishurdi", 11, "0%"),
    UpazilaStatItem("Charfesson", 11, "0%"),
    UpazilaStatItem("Munshiganj Sadar", 11, "0%"),
    UpazilaStatItem("Digholia", 11, "0%"),
    UpazilaStatItem("Sherpur Sadar", 11, "0%"),
    UpazilaStatItem("Birganj", 11, "0%"),
    UpazilaStatItem("Kumarkhali", 11, "0%"),
    UpazilaStatItem("Kotchandpur", 10, "0%"),
    UpazilaStatItem("Karnafull", 10, "0%"),
    UpazilaStatItem("Dhunot", 10, "0%"),
    UpazilaStatItem("Barhatta", 10, "0%"),
    UpazilaStatItem("Raipura", 10, "0%"),
    UpazilaStatItem("Bandarban Sadar", 10, "0%"),
    UpazilaStatItem("Jaldhaka", 9, "0%"),
    UpazilaStatItem("Bhaluka", 9, "0%"),
    UpazilaStatItem("Taragonj", 9, "0%"),
    UpazilaStatItem("Golapganj", 9, "0%"),
    UpazilaStatItem("Fulbaria", 9, "0%"),
    UpazilaStatItem("Banshkhali", 9, "0%"),
    UpazilaStatItem("Ramganj", 9, "0%"),
    UpazilaStatItem("Dumuria", 9, "0%"),
    UpazilaStatItem("Narail Sadar", 9, "0%"),
    UpazilaStatItem("Bheramara", 9, "0%"),
    UpazilaStatItem("Kaliakair", 9, "0%"),
    UpazilaStatItem("Palash", 9, "0%"),
    UpazilaStatItem("Bhupur", 9, "0%"),
    UpazilaStatItem("Gouripur", 9, "0%"),
    UpazilaStatItem("Phulpur", 8, "0%"),
    UpazilaStatItem("Gopalganj Sadar", 8, "0%"),
    UpazilaStatItem("Abhaynagar", 8, "0%"),
    UpazilaStatItem("Satkania", 8, "0%"),
    UpazilaStatItem("Hajiganj", 8, "0%"),
    UpazilaStatItem("South Sunamganj", 8, "0%"),
    UpazilaStatItem("Laksam", 8, "0%"),
    UpazilaStatItem("Ukhiya", 8, "0%"),
    UpazilaStatItem("Lohagara", 7, "0%"),
    UpazilaStatItem("Tarash", 7, "0%"),
    UpazilaStatItem("Mongla", 7, "0%"),
    UpazilaStatItem("Chandanaish", 7, "0%"),
    UpazilaStatItem("Botiaghata", 7, "0%"),
    UpazilaStatItem("Gobindaganj", 7, "0%"),
    UpazilaStatItem("Pirganj", 7, "0%"),
    UpazilaStatItem("Rangunia", 7, "0%"),
    UpazilaStatItem("Patnitala", 7, "0%"),
    UpazilaStatItem("Melandah", 7, "0%"),
    UpazilaStatItem("Bochaganj", 7, "0%"),
    UpazilaStatItem("Bagha", 7, "0%"),
    UpazilaStatItem("Mirsharai", 7, "0%"),
    UpazilaStatItem("Ashuganj", 6, "0%"),
    UpazilaStatItem("Mohonganj", 6, "0%"),
    UpazilaStatItem("Belkuchi", 6, "0%"),
    UpazilaStatItem("Bagmara", 6, "0%"),
    UpazilaStatItem("Ghatail", 6, "0%"),
    UpazilaStatItem("Derai", 6, "0%"),
    UpazilaStatItem("Monohardi", 6, "0%"),
    UpazilaStatItem("Bhola Sadar", 6, "0%"),
    UpazilaStatItem("Faridpur", 6, "0%"),
    UpazilaStatItem("Sonaimori", 6, "0%"),
    UpazilaStatItem("Kalai", 6, "0%"),
    UpazilaStatItem("Rampal", 6, "0%"),
    UpazilaStatItem("Companiganj", 5, "0%"),
    UpazilaStatItem("Kaunia", 5, "0%"),
    UpazilaStatItem("Nageshwari", 5, "0%"),
    UpazilaStatItem("Ulipur", 5, "0%"),
    UpazilaStatItem("Phulbari", 5, "0%"),
    UpazilaStatItem("Manikganj Sadar", 5, "0%"),
    UpazilaStatItem("Alfadanga", 5, "0%"),
    UpazilaStatItem("Faridgonj", 5, "0%"),
    UpazilaStatItem("Debiganj", 5, "0%"),
    UpazilaStatItem("Begumganj", 5, "0%"),
    UpazilaStatItem("Chatkhil", 5, "0%"),
    UpazilaStatItem("Balaganj", 5, "0%"),
    UpazilaStatItem("Chandina", 5, "0%"),
    UpazilaStatItem("Maksudpur", 5, "0%"),
    UpazilaStatItem("Mirpur", 5, "0%")
)

// Complete Registration Trend Data matching Screenshot 2 verbatim
val TrendDaysData = listOf(
    TrendDayStat("2026-07-16", "Jul 16, 2026", null, "--"),
    TrendDayStat("2026-07-17", "Jul 17, 2026", null, "--"),
    TrendDayStat("2026-07-18", "Jul 18, 2026", null, "--"),
    TrendDayStat("2026-07-19", "Jul 19, 2026", null, "--"),
    TrendDayStat("2026-07-20", "Jul 20, 2026", null, "--"),
    TrendDayStat("2026-07-21", "Jul 21, 2026", null, "--"),
    TrendDayStat("2026-07-22", "Jul 22, 2026", null, "--"),
    TrendDayStat("2026-07-23", "Jul 23, 2026", null, "--"),
    TrendDayStat("2026-07-24", "Jul 24, 2026", null, "--"),
    TrendDayStat("2026-07-25", "Jul 25, 2026", null, "--"),
    TrendDayStat("2026-07-26", "Jul 26, 2026", null, "--"),
    TrendDayStat("2026-07-27", "Jul 27, 2026", null, "--"),
    TrendDayStat("2026-07-28", "Jul 28, 2026", null, "--"),
    TrendDayStat("2026-07-29", "Jul 29, 2026", null, "--"),
    TrendDayStat("2026-07-30", "Jul 30, 2026", null, "--"),
    TrendDayStat("2026-07-31", "Jul 31, 2026", null, "--"),
    TrendDayStat("2026-08-01", "Aug 1, 2026", null, "--"),
    TrendDayStat("2026-08-02", "Aug 2, 2026", null, "--"),
    TrendDayStat("2026-08-03", "Aug 3, 2026", null, "--"),
    TrendDayStat("2026-08-04", "Aug 4, 2026", null, "--"),
    TrendDayStat("2026-08-05", "Aug 5, 2026", 694, "694"),
    TrendDayStat("2026-08-06", "Aug 6, 2026", 606, "606"),
    TrendDayStat("2026-08-07", "Aug 7, 2026", 831, "831"),
    TrendDayStat("2026-08-08", "Aug 8, 2026", 998, "998"),
    TrendDayStat("2026-08-09", "Aug 9, 2026", 594, "594"),
    TrendDayStat("2026-08-10", "Aug 10, 2026", 358, "358"),
    TrendDayStat("2026-08-11", "Aug 11, 2026", 369, "369"),
    TrendDayStat("2026-08-12", "Aug 12, 2026", 375, "375"),
    TrendDayStat("2026-08-13", "Aug 13, 2026", 344, "344"),
    TrendDayStat("2026-08-14", "Aug 14, 2026", 575, "575"),
    TrendDayStat("2026-08-15", "Aug 15, 2026", 390, "390"),
    TrendDayStat("2026-08-16", "Aug 16, 2026", 432, "432"),
    TrendDayStat("2026-08-17", "Aug 17, 2026", 988, "988"),
    TrendDayStat("2026-08-18", "Aug 18, 2026", 1209, "1,209"),
    TrendDayStat("2026-08-19", "Aug 19, 2026", 1216, "1,216"),
    TrendDayStat("2026-08-20", "Aug 20, 2026", 1516, "1,516"),
    TrendDayStat("2026-08-21", "Aug 21, 2026", 126, "126"),
    TrendDayStat("2026-08-22", "Aug 22, 2026", null, "--"),
    TrendDayStat("2026-08-23", "Aug 23, 2026", null, "--")
)

// -----------------------------------------------------------------------------------------
// COMPONENT 1: REGISTRATION BY DISTRICT (HORIZONTAL BAR CHART + VIEW DETAILS)
// -----------------------------------------------------------------------------------------

@Composable
fun WebDistrictBarChartCard(
    onViewDetails: () -> Unit
) {
    var selectedDistrict by remember { mutableStateOf<DistrictStatItem?>(ChartDistricts22.firstOrNull()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("registration_by_district_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Registration by District (Total: 64) // View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registration by District (Total: 64) // ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PortalDarkText
                )
                Text(
                    text = "View Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .clickable { onViewDetails() }
                        .testTag("view_district_details_btn")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend: [Blue box] District
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp, 9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF2563EB))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("District", fontSize = 11.sp, color = PortalMutedText)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tooltip preview
            selectedDistrict?.let { dist ->
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
                            text = dist.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${dist.count} (${dist.percentage}) of 11,621",
                            fontSize = 11.sp,
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Horizontal Bars with 0 to 3,000 scale
            val maxDistrictScale = 3000f
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChartDistricts22.forEach { dist ->
                    val isSelected = selectedDistrict?.name == dist.name

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDistrict = dist },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // District Name on Y axis
                        Text(
                            text = dist.name,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = PortalDarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(95.dp),
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Bar Container with grid background
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(2.dp))
                        ) {
                            val fraction = (dist.count / maxDistrictScale).coerceIn(0.005f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                                    .background(dist.color)
                            )
                        }
                    }
                }

                // X-Axis markings: 0, 500, 1,000, 1,500, 2,000, 2,500, 3,000
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 101.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0", "500", "1,000", "1,500", "2,000", "2,500", "3,000").forEach { mark ->
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

            // Exact bottom summary text matching screenshot 1
            Text(
                text = "Total Registration: 11,621 | Districts: 64",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PortalDarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = All64DistrictsList.joinToString(" * ") { "${it.name}: ${it.count} (${it.percentage})" }.let { "* $it" },
                fontSize = 9.sp,
                color = PortalMutedText,
                lineHeight = 14.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 2: REGISTRATION BY UPAZILA (VERTICAL BARS + VIEW DETAILS)
// -----------------------------------------------------------------------------------------

@Composable
fun WebUpazilaBarChartCard(
    onViewDetails: () -> Unit
) {
    // Show top 35 upazilas with horizontal scrolling bar chart
    val topChartUpazilas = remember { AllUpazilasList.take(35) }
    var selectedUpazila by remember { mutableStateOf<UpazilaStatItem?>(topChartUpazilas.firstOrNull()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("registration_by_upazila_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Registration by Upazila (Total: 398) // View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registration by Upazila (Total: 398) // ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PortalDarkText
                )
                Text(
                    text = "View Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .clickable { onViewDetails() }
                        .testTag("view_upazila_details_btn")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend: [Green box] Upazila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp, 9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF84CC16))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upazila", fontSize = 11.sp, color = PortalMutedText)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Upazila Preview
            selectedUpazila?.let { up ->
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
                            text = up.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${up.count} (${up.percentage}) of 11,621",
                            fontSize = 11.sp,
                            color = Color(0xFFA3E635),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Vertical Bar Chart with scale 0 to 2,500
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Y-Axis Labels: 2,500, 2,000, 1,500, 1,000, 500, 0
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    listOf("2,500", "2,000", "1,500", "1,000", "500", "0").forEach { mark ->
                        Text(
                            text = mark,
                            fontSize = 8.sp,
                            color = PortalMutedText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Scrollable bars container
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Grid background lines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val steps = 5
                        for (i in 0..steps) {
                            val y = size.height * (i.toFloat() / steps)
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // Bars
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        topChartUpazilas.forEach { up ->
                            val heightFraction = (up.count / 2500f).coerceIn(0.01f, 1f)
                            val isSelected = selectedUpazila?.name == up.name

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .clickable { selectedUpazila = up }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                        .background(if (isSelected) Color(0xFF65A30D) else Color(0xFF84CC16))
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = up.name.take(6),
                                    fontSize = 7.sp,
                                    color = PortalDarkText,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = PortalBorderColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Exact bottom summary text matching screenshot 1 & 2
            Text(
                text = "Total Registration: 11,621 | Upazilas: 398",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PortalDarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AllUpazilasList.joinToString(" * ") { "${it.name}: ${it.count} (${it.percentage})" }.let { "* $it" },
                fontSize = 9.sp,
                color = PortalMutedText,
                lineHeight = 14.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 3: REGISTRATION TREND (LINE GRAPH + DATA TABLE)
// -----------------------------------------------------------------------------------------

@Composable
fun WebRegistrationTrendCard() {
    var selectedTrendPoint by remember { mutableStateOf<TrendDayStat?>(TrendDaysData.find { it.dateCode == "2026-08-20" }) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("registration_trend_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PortalBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = "Registration Trend",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PortalDarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Legend: [blue box] Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp, 9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF2563EB))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Total", fontSize = 11.sp, color = PortalMutedText)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Point Inspector Tooltip
            selectedTrendPoint?.let { pt ->
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
                            text = "${pt.dateLabel} (${pt.dateCode})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Registered: ${pt.displayValue}",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Line Chart Canvas with Y-Axis (0 - 1,600)
            val maxYScale = 1600f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Y-Axis scale: 1,600, 1,400, 1,200, 1,000, 800, 600, 400, 200, 0
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    listOf("1,600", "1,400", "1,200", "1,000", "800", "600", "400", "200", "0").forEach { mark ->
                        Text(
                            text = mark,
                            fontSize = 8.sp,
                            color = PortalMutedText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Line Plot Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Draw 8 horizontal grid lines
                        val gridSteps = 8
                        for (i in 0..gridSteps) {
                            val y = height * (i.toFloat() / gridSteps)
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Calculate points
                        val totalPoints = TrendDaysData.size
                        val stepX = width / (totalPoints - 1)

                        val path = Path()
                        val points = mutableListOf<Offset>()

                        TrendDaysData.forEachIndexed { index, item ->
                            val countVal = item.count ?: 0
                            val x = index * stepX
                            val y = height - ((countVal / maxYScale) * height)
                            val pt = Offset(x, y)
                            points.add(pt)

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        // Draw Trend Line
                        drawPath(
                            path = path,
                            color = Color(0xFF2563EB),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw dots for each day
                        points.forEachIndexed { index, pt ->
                            val item = TrendDaysData[index]
                            val isSelected = selectedTrendPoint?.dateCode == item.dateCode
                            val hasData = item.count != null

                            drawCircle(
                                color = if (isSelected) Color(0xFFF97316) else if (hasData) Color(0xFF2563EB) else Color(0xFF94A3B8),
                                radius = if (isSelected) 5.dp.toPx() else if (hasData) 3.5.dp.toPx() else 2.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = if (isSelected) 2.5.dp.toPx() else 1.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // X-Axis quick dates summary
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("2026-07-16", "2026-08-01", "2026-08-10", "2026-08-20", "2026-08-23").forEach { dt ->
                    Text(
                        text = dt,
                        fontSize = 8.sp,
                        color = PortalMutedText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data Table of Date and Count from Screenshot 2
            Text(
                text = "Registration Count by Date",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PortalDarkText
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Table Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, PortalBorderColor),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PortalDarkText)
                        Text("Count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PortalDarkText)
                    }

                    HorizontalDivider(color = PortalBorderColor)

                    // Scrollable table of rows
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        TrendDaysData.forEachIndexed { index, rowItem ->
                            val isSelected = selectedTrendPoint?.dateCode == rowItem.dateCode
                            val isEven = index % 2 == 0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) Color(0xFFE0F2FE)
                                        else if (isEven) Color.White
                                        else Color(0xFFF8FAFC)
                                    )
                                    .clickable { selectedTrendPoint = rowItem }
                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = rowItem.dateLabel,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFF0369A1) else PortalDarkText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = rowItem.displayValue,
                                    fontSize = 10.sp,
                                    color = if (rowItem.count != null) Color(0xFF0F172A) else PortalMutedText,
                                    fontWeight = if (rowItem.count != null) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 4: FOOTER (Copyright & Built with Love from Bangladesh)
// -----------------------------------------------------------------------------------------

@Composable
fun WebPortalFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Copyright © 2026 BdJSO Online",
                fontSize = 10.sp,
                color = PortalMutedText
            )
            Text(
                text = "Built with ♥ From Bangladesh",
                fontSize = 10.sp,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// DIALOG: ALL 64 DISTRICTS DIRECTORY
// -----------------------------------------------------------------------------------------

@Composable
fun TotalDistrictsDialog(
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) All64DistrictsList
        else All64DistrictsList.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                            text = "Districts Directory (All 64)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PortalDarkText
                        )
                        Text(
                            text = "Total Registration: 11,621 across 64 Districts",
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
                    placeholder = { Text("Search District...", fontSize = 12.sp) },
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
                    items(filtered) { dist ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(dist.color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = dist.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PortalDarkText
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, PortalBorderColor)
                                ) {
                                    Text(
                                        text = "${dist.count} (${dist.percentage})",
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
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// DIALOG: ALL 398 UPAZILAS DIRECTORY
// -----------------------------------------------------------------------------------------

@Composable
fun TotalUpazilasDialog(
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) AllUpazilasList
        else AllUpazilasList.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                            text = "Upazilas Directory (Total: 398)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PortalDarkText
                        )
                        Text(
                            text = "Total Registration: 11,621 across 398 Upazilas",
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
                    placeholder = { Text("Search Upazila...", fontSize = 12.sp) },
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
                    items(filtered) { up ->
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
                                Text(
                                    text = up.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PortalDarkText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, PortalBorderColor)
                                ) {
                                    Text(
                                        text = "${up.count} (${up.percentage})",
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
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
