package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ResultEntity
import com.example.data.model.StudentEntity
import com.example.ui.theme.*
import com.example.util.CsvExportUtil

enum class CsvExportType(val label: String, val defaultFilename: String) {
    STUDENTS("Student Registrations", "bdjso_student_registrations_2026.csv"),
    RESULTS("Examination Results", "bdjso_examination_results_2026.csv"),
    COMBINED("Combined Merit Report", "bdjso_combined_merit_report_2026.csv"),
    SUBMISSIONS("Pending Submissions", "bdjso_pending_submissions_2026.csv")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvExportDialog(
    students: List<StudentEntity>,
    results: List<ResultEntity>,
    attempts: List<ExamAttemptEntity>,
    initialType: CsvExportType = CsvExportType.STUDENTS,
    onDismissRequest: () -> Unit,
    onShowSnackbar: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(initialType) }
    var categoryFilter by remember { mutableStateOf("ALL") }
    var statusFilter by remember { mutableStateOf("ALL") }
    var showRawPreview by remember { mutableStateOf(false) }

    // Compute generated CSV string based on selected report type and filters
    val csvContent = remember(selectedType, students, results, attempts, categoryFilter, statusFilter) {
        when (selectedType) {
            CsvExportType.STUDENTS -> CsvExportUtil.generateStudentsCsv(students, categoryFilter, statusFilter)
            CsvExportType.RESULTS -> CsvExportUtil.generateResultsCsv(results, categoryFilter, statusFilter)
            CsvExportType.COMBINED -> CsvExportUtil.generateCombinedReportCsv(students, results)
            CsvExportType.SUBMISSIONS -> CsvExportUtil.generateSubmissionsCsv(attempts, students)
        }
    }

    val lines = remember(csvContent) { csvContent.lines().filter { it.isNotBlank() } }
    val rowCount = remember(lines) { (lines.size - 1).coerceAtLeast(0) }

    // Android Document Picker Launcher to save CSV to user-selected destination
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            val success = CsvExportUtil.writeCsvToDocument(context, uri, csvContent)
            if (success) {
                Toast.makeText(context, "CSV saved successfully!", Toast.LENGTH_SHORT).show()
                onShowSnackbar("CSV saved to device successfully (${selectedType.defaultFilename})")
                onDismissRequest()
            } else {
                Toast.makeText(context, "Failed to write CSV file.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("csv_export_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BdjsoEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = BdjsoEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Export Olympiad Reports",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Generate RFC 4180 CSV for administrative reporting",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Report Type Selector Segmented Controls
                    Text("Select Report Dataset:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportTypeOptionCard(
                                title = "Student Registrations",
                                subtitle = "${students.size} Candidates",
                                icon = Icons.Default.People,
                                isSelected = selectedType == CsvExportType.STUDENTS,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedType = CsvExportType.STUDENTS }
                            )

                            ReportTypeOptionCard(
                                title = "Examination Results",
                                subtitle = "${results.size} Merit Scores",
                                icon = Icons.Default.EmojiEvents,
                                isSelected = selectedType == CsvExportType.RESULTS,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedType = CsvExportType.RESULTS }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportTypeOptionCard(
                                title = "Combined Report",
                                subtitle = "Full Merit & Profile",
                                icon = Icons.Default.Assessment,
                                isSelected = selectedType == CsvExportType.COMBINED,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedType = CsvExportType.COMBINED }
                            )

                            ReportTypeOptionCard(
                                title = "Pending Submissions",
                                subtitle = "${attempts.size} Moderation Logs",
                                icon = Icons.Default.PendingActions,
                                isSelected = selectedType == CsvExportType.SUBMISSIONS,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedType = CsvExportType.SUBMISSIONS }
                            )
                        }
                    }

                    // Optional Category Filter (for Students and Results)
                    if (selectedType == CsvExportType.STUDENTS || selectedType == CsvExportType.RESULTS) {
                        Text("Filter by Category:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                        val categories = listOf("ALL", "PRIMARY", "JUNIOR", "SECONDARY", "SPECIAL")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = categoryFilter.equals(cat, ignoreCase = true),
                                    onClick = { categoryFilter = cat },
                                    label = { Text(cat, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ScienceTeal.copy(alpha = 0.15f),
                                        selectedLabelColor = ScienceTeal
                                    )
                                )
                            }
                        }
                    }

                    // Summary Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BackgroundClean),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$rowCount Records Ready for Export",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Filename: ${selectedType.defaultFilename}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            TextButton(
                                onClick = { showRawPreview = !showRawPreview },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (showRawPreview) "Hide Preview" else "Show Preview",
                                    fontSize = 11.sp,
                                    color = ScienceTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Collapsible CSV Preview Box
                    AnimatedVisibility(visible = showRawPreview) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "CSV Preview (First 5 lines):",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "UTF-8",
                                        color = OlympiadGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                val sampleLines = lines.take(5).joinToString("\n")
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = sampleLines,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF86EFAC),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Actions: Save to Device, Share, Copy
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Primary Action: Save to Device Storage via SAF
                        Button(
                            onClick = {
                                createDocLauncher.launch(selectedType.defaultFilename)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("btn_save_csv_storage")
                        ) {
                            Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Device", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Secondary Action: Share via Email / Sheet apps
                        Button(
                            onClick = {
                                CsvExportUtil.shareCsv(context, selectedType.defaultFilename, csvContent)
                                onShowSnackbar("Opening share sheet for ${selectedType.defaultFilename}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_share_csv")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share File", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("BDJSO CSV Export", csvContent)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "CSV copied to clipboard (${rowCount} rows)", Toast.LENGTH_SHORT).show()
                                onShowSnackbar("Copied ${rowCount} records to clipboard")
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy CSV", fontSize = 11.sp)
                        }

                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportTypeOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ScienceTeal.copy(alpha = 0.08f) else BackgroundClean
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) ScienceTeal else BorderLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ScienceTeal else TextMuted.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = if (isSelected) ScienceTeal else TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
