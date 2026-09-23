package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesAndInfoScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rules, Syllabus & Eligibility", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // International Age Eligibility Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = OlympiadGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IJSO Eligibility Requirement",
                                color = OlympiadGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To represent Bangladesh at the International Junior Science Olympiad (IJSO 2026), candidates must be born on or after January 1, 2011. Students must be citizens of Bangladesh and enrolled in an educational institution in Bangladesh.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Categories Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Rule, contentDescription = null, tint = BdjsoEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Official Competition Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        CategoryRow("Primary", "Class 3, 4, 5", "Basic scientific inquiry, nature observation, arithmetic logic", ScienceTeal)
                        CategoryRow("Junior", "Class 6, 7, 8", "Integrated Physics, Chemistry, Biology fundamentals", BdjsoEmerald)
                        CategoryRow("Secondary", "Class 9, 10", "Advanced IJSO syllabus, analytical problem solving, experimental design", OlympiadGold)
                        CategoryRow("Special", "Class 11, 12", "For high schoolers meeting the IJSO age cut-off requirement", ElectricCyan)
                    }
                }
            }

            // Examination Marking Scheme
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = ScienceTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exam Format & Negative Marking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Text("• Question Type: Multiple Choice Questions (MCQ)", fontSize = 13.sp)
                        Text("• Question Allocation: Physics (25%), Chemistry (25%), Biology (25%), Mathematics (25%)", fontSize = 13.sp)
                        Text("• Correct Answer: +4.0 Marks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BdjsoEmerald)
                        Text("• Incorrect Answer: -1.0 Negative Mark", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Text("• Unattempted Question: 0.0 Marks", fontSize = 13.sp)
                        Text("• Exam Timer: Server-synchronized strict countdown. Autosaves continuously.", fontSize = 13.sp)
                    }
                }
            }

            // Frequently Asked Questions
            item {
                Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            val faqs = listOf(
                "Can English Medium and Madrasah students participate?" to "Yes! All students in Bangladesh following National Curriculum, English Medium (Cambridge/Edexcel), or Madrasah Board can participate.",
                "Is there any registration fee?" to "Registration for the preliminary round is completely free of charge, supported by Bangladesh Freedom Foundation and Prothom Alo.",
                "How will I receive my admit card?" to "Upon online registration, your unique BDJSO Registration ID and digital admit card will be instantly generated on your Student Dashboard.",
                "What devices can I use for the online preliminary exam?" to "You can take the exam on any smartphone, tablet, laptop, or desktop computer with a modern web browser."
            )

            items(faqs) { (q, a) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = q, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = a, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    }
                }
            }

            // Helpline Support
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("BDJSO Helpdesk", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Email: info@bdjso.org • Mobile: 01700-BDJSO1 (10 AM - 6 PM)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(name: String, classes: String, desc: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(text = classes, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
