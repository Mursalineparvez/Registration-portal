package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BDJSOData
import com.example.model.ScienceTopic
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusScreen() {
    var selectedSubject by remember { mutableStateOf("All") }
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    val subjects = listOf("All", "Physics", "Chemistry", "Biology")
    val filteredTopics = remember(selectedSubject) {
        if (selectedSubject == "All") BDJSOData.scienceTopics
        else BDJSOData.scienceTopics.filter { it.subject == selectedSubject }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BDJSO Syllabus & Science Lab",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
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
                .testTag("syllabus_content"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Subject filter tabs
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjects.forEach { subj ->
                        val isSelected = selectedSubject == subj
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubject = subj },
                            label = { Text(subj, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Quick Formulas Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9E7))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color(0xFFB7950B),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Formula & Constant Sheet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF7D6608)
                            )
                            Text(
                                text = "g = 9.8 m/s² • c = 3×10⁸ m/s • R = 8.314 J/(mol·K) • NA = 6.022×10²³",
                                fontSize = 11.sp,
                                color = Color(0xFF7D6608),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Science Topic Cards
            items(filteredTopics) { topic ->
                val isExpanded = expandedTopicId == topic.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            expandedTopicId = if (isExpanded) null else topic.id
                        },
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
                            Surface(
                                color = when (topic.subject) {
                                    "Physics" -> Color(0xFFE8F4FD)
                                    "Chemistry" -> Color(0xFFFEF9E7)
                                    else -> Color(0xFFE8F8F5)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = topic.subject,
                                    color = when (topic.subject) {
                                        "Physics" -> Color(0xFF1B4F72)
                                        "Chemistry" -> Color(0xFF7D6608)
                                        else -> Color(0xFF117864)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = topic.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = topic.summary,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                if (topic.formulas.isNotEmpty()) {
                                    Text(
                                        text = "Important Formulas:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = GreenPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    topic.formulas.forEach { formula ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            color = Color(0xFFF4F6F6),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "• $formula",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF2C3E50),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                if (topic.tips.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Olympiad Pro Tips:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFD35400)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    topic.tips.forEach { tip ->
                                        Text(
                                            text = "★ $tip",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
