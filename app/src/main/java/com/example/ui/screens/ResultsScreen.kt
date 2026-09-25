package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BDJSOData
import com.example.data.model.ResultEntity
import com.example.model.OlympiadCategory
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.viewmodel.BdjsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: BdjsoViewModel? = null,
    onNavigateToTrends: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<OlympiadCategory?>(null) }

    val roomResults by viewModel?.allResults?.collectAsStateWithLifecycle(emptyList())
        ?: remember { mutableStateOf(emptyList()) }

    val filteredRoomResults = remember(roomResults, searchQuery, selectedCategory) {
        roomResults.filter { res ->
            val matchesCategory = when (selectedCategory) {
                null -> true
                OlympiadCategory.PRIMARY -> res.categoryId.equals("PRIMARY", ignoreCase = true)
                OlympiadCategory.JUNIOR -> res.categoryId.equals("JUNIOR", ignoreCase = true)
                OlympiadCategory.SECONDARY -> res.categoryId.equals("SECONDARY", ignoreCase = true)
                OlympiadCategory.SPECIAL -> res.categoryId.equals("SPECIAL", ignoreCase = true)
            }
            val matchesQuery = searchQuery.isBlank() ||
                    res.studentRegistrationId.contains(searchQuery, ignoreCase = true) ||
                    res.studentName.contains(searchQuery, ignoreCase = true) ||
                    res.schoolName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val sampleResults = remember(searchQuery, selectedCategory) {
        BDJSOData.sampleResults.filter { res ->
            (selectedCategory == null || res.category == selectedCategory) &&
            (searchQuery.isBlank() || res.rollNumber.contains(searchQuery, ignoreCase = true) || res.name.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BDJSO Merit List & Results",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Room Persistence • Local SQLite Database",
                            fontSize = 11.sp,
                            color = GreenDark
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onNavigateToTrends,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ট্রেন্ডস", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .testTag("results_content"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Room SQLite Persistence Banner
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Storage,
                                    contentDescription = "Room DB",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Local Room Persistence Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                            Text(
                                text = if (roomResults.isNotEmpty())
                                    "${roomResults.size} official exam results stored locally on device"
                                else
                                    "Connected to Room database (Results sync automatically)",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SQLite",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by Roll (e.g. BDJSO-2026-000101) or Name") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Category Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All") }
                    )
                    OlympiadCategory.entries.filter { it != OlympiadCategory.SPECIAL }.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // If Room database has records, display Room persisted results
            if (filteredRoomResults.isNotEmpty()) {
                item {
                    Text(
                        text = "Room Persistence Results (${filteredRoomResults.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(filteredRoomResults) { res ->
                    RoomResultCard(result = res)
                }
            } else if (roomResults.isEmpty()) {
                // Fallback to sample results when Room has not completed initial load
                items(sampleResults) { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (res.medal) {
                                                    "Gold Medal" -> GoldAccent
                                                    "Silver Medal" -> Color(0xFFBDC3C7)
                                                    else -> Color(0xFFCD7F32)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = "Medal",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = res.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = res.rollNumber,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rank #${res.nationalRank}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GreenPrimary
                                    )
                                    Text(
                                        text = "${res.marks} / 100",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${res.school}, ${res.district}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (res.medal == "Gold Medal") Color(0xFFFEF9E7) else Color(0xFFF4F6F6),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = res.medal,
                                        color = if (res.medal == "Gold Medal") Color(0xFFB7950B) else Color(0xFF5D6D7E),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                if (res.selectedForCamp) {
                                    Surface(
                                        color = Color(0xFFE8F8F5),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.MilitaryTech,
                                                contentDescription = null,
                                                tint = GreenPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Selected for IJSO Camp",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GreenDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredRoomResults.isEmpty() && sampleResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No participant found with this roll or name.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun RoomResultCard(result: ResultEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (result.rank) {
                                    1 -> GoldAccent
                                    2 -> Color(0xFFBDC3C7)
                                    3 -> Color(0xFFCD7F32)
                                    else -> Color(0xFF0284C7)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Medal",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = result.studentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = result.studentRegistrationId,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = result.categoryId,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rank #${result.rank}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GreenPrimary
                    )
                    Text(
                        text = "${result.totalMarks} Marks (${result.percentage}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${result.schoolName}, ${result.district}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Marks Breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Physics: ${result.physicsMarks}",
                    fontSize = 11.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Chemistry: ${result.chemistryMarks}",
                    fontSize = 11.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Biology: ${result.biologyMarks}",
                    fontSize = 11.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (result.selectionStatus) {
                        "SELECTED" -> Color(0xFFDCFCE7)
                        "WAITING_LIST" -> Color(0xFFFEF9C3)
                        else -> Color(0xFFF1F5F9)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (result.selectionStatus == "SELECTED") Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (result.selectionStatus == "SELECTED") GreenPrimary else Color(0xFF854D0E),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (result.selectionStatus == "SELECTED") "Selected for IJSO Camp" else "Status: ${result.selectionStatus}",
                            color = if (result.selectionStatus == "SELECTED") GreenDark else Color(0xFF854D0E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF7E22CE),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Room SQLite",
                            color = Color(0xFF7E22CE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
