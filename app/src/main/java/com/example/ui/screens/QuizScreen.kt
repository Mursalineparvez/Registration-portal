package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BDJSOData
import com.example.model.OlympiadCategory
import com.example.model.QuestionType
import com.example.model.QuizQuestion
import com.example.ui.components.QuestionDiagram
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    initialCategory: OlympiadCategory = OlympiadCategory.PRIMARY,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    val questions = remember(selectedCategory) {
        BDJSOData.sampleQuestions.filter { it.category == selectedCategory }.ifEmpty {
            BDJSOData.sampleQuestions
        }
    }

    var currentIndex by remember(selectedCategory) { mutableIntStateOf(0) }
    val selectedAnswers = remember(selectedCategory) { mutableStateMapOf<Int, Int>() }
    val numericAnswers = remember(selectedCategory) { mutableStateMapOf<Int, String>() }
    val isNumericSubmitted = remember(selectedCategory) { mutableStateMapOf<Int, Boolean>() }
    var currentNumericInput by remember(currentIndex, selectedCategory) { mutableStateOf("") }
    var showExplanation by remember(currentIndex, selectedCategory) { mutableStateOf(false) }
    var quizCompleted by remember(selectedCategory) { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val currentQuestion = questions.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BDJSO Question Practice",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTealDark
                        )
                        Text(
                            text = "${selectedCategory.displayName} Category • Pattern from BDJSO Online",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        }
    ) { paddingValues ->
        if (quizCompleted) {
            val score = questions.count { q ->
                if (q.questionType == QuestionType.NUMERIC) {
                    numericAnswers[q.id]?.trim()?.equals(q.correctNumericAnswer.trim(), ignoreCase = true) == true
                } else {
                    selectedAnswers[q.id] == q.correctIndex
                }
            }
            QuizResultDialog(
                total = questions.size,
                score = score,
                onRestart = {
                    selectedAnswers.clear()
                    numericAnswers.clear()
                    isNumericSubmitted.clear()
                    currentIndex = 0
                    quizCompleted = false
                },
                onExit = onBack
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundClean)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .testTag("quiz_screen_content"),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Category Switcher
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollableTabRow(
                        selectedTabIndex = if (selectedCategory == OlympiadCategory.PRIMARY) 0 else 1,
                        edgePadding = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        listOf(OlympiadCategory.PRIMARY, OlympiadCategory.JUNIOR).forEach { cat ->
                            Tab(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = cat
                                    selectedAnswers.clear()
                                    numericAnswers.clear()
                                    isNumericSubmitted.clear()
                                    currentIndex = 0
                                    quizCompleted = false
                                },
                                text = {
                                    Text(
                                        text = "${cat.displayName} (${cat.classes})",
                                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (currentQuestion != null) {
                    // Question Header Card (Pattern matching PDF Pages 6, 7, 14)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = BorderStroke(1.dp, BorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Question Header (Code + Action Indicator like PDF)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = ServiceTealBg,
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, ServiceTealBorder)
                                        ) {
                                            Text(
                                                text = currentQuestion.code.ifEmpty { "Q${currentIndex + 1}" },
                                                color = PrimaryTealDark,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Surface(
                                            color = when (currentQuestion.subject) {
                                                "Physics" -> ServiceBlueBg
                                                "Chemistry" -> ServiceAmberBg
                                                else -> ServiceGreenBg
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = currentQuestion.subject,
                                                color = when (currentQuestion.subject) {
                                                    "Physics" -> ServiceBlue
                                                    "Chemistry" -> Color(0xFFB45309)
                                                    else -> ServiceGreen
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    // Action green badge icon from PDF
                                    Surface(
                                        color = ServiceGreen,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Science,
                                                contentDescription = "Question Action",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Question Bengali Text
                                if (currentQuestion.questionBangla.isNotBlank()) {
                                    Text(
                                        text = currentQuestion.questionBangla,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Question English Text (below Bangla)
                                Text(
                                    text = currentQuestion.questionEnglish.ifEmpty { currentQuestion.question },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )

                                // Diagram if applicable
                                if (currentQuestion.diagramType != "NONE") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    QuestionDiagram(diagramType = currentQuestion.diagramType)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Input / Options section based on QuestionType
                    if (currentQuestion.questionType == QuestionType.NUMERIC) {
                        val isSubmitted = isNumericSubmitted[currentQuestion.id] == true
                        val userAnswer = numericAnswers[currentQuestion.id] ?: ""
                        val isCorrect = userAnswer.trim().equals(currentQuestion.correctNumericAnswer.trim(), ignoreCase = true)

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "গাণিতিক উত্তর দিন (Numeric Input)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryTealDark
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = if (isSubmitted) userAnswer else currentNumericInput,
                                            onValueChange = {
                                                if (!isSubmitted) currentNumericInput = it
                                            },
                                            label = { Text("আপনার উত্তর লিখুন") },
                                            placeholder = { Text("উদাহরণ: ${currentQuestion.correctNumericAnswer}") },
                                            enabled = !isSubmitted,
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    if (currentNumericInput.isNotBlank()) {
                                                        numericAnswers[currentQuestion.id] = currentNumericInput
                                                        isNumericSubmitted[currentQuestion.id] = true
                                                        showExplanation = true
                                                        focusManager.clearFocus()
                                                    }
                                                }
                                            ),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Button(
                                            onClick = {
                                                if (!isSubmitted && currentNumericInput.isNotBlank()) {
                                                    numericAnswers[currentQuestion.id] = currentNumericInput
                                                    isNumericSubmitted[currentQuestion.id] = true
                                                    showExplanation = true
                                                    focusManager.clearFocus()
                                                }
                                            },
                                            enabled = !isSubmitted && currentNumericInput.isNotBlank(),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(if (isSubmitted) "জমা হয়েছে" else "উত্তর যাচাই")
                                        }
                                    }

                                    if (isSubmitted) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            color = if (isCorrect) ServiceGreenBg else ServiceRedBg,
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, if (isCorrect) ServiceGreenBorder else ServiceRedBorder),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                    contentDescription = null,
                                                    tint = if (isCorrect) ServiceGreen else ServiceRed
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (isCorrect) "সঠিক উত্তর! (Correct Answer)" else "ভুল উত্তর! সঠিক উত্তর: ${currentQuestion.answerLabel}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (isCorrect) ServiceGreen else ServiceRed
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // MCQ Options List
                        itemsIndexed(currentQuestion.options) { optIndex, optionText ->
                            val isSelected = selectedAnswers[currentQuestion.id] == optIndex
                            val isAnswered = selectedAnswers.containsKey(currentQuestion.id)
                            val isCorrect = optIndex == currentQuestion.correctIndex

                            val borderColor = when {
                                !isAnswered -> if (isSelected) PrimaryTeal else BorderLight
                                isSelected && isCorrect -> ServiceGreen
                                isSelected && !isCorrect -> ServiceRed
                                isCorrect -> ServiceGreen
                                else -> BorderLight
                            }

                            val bgColor = when {
                                !isAnswered -> if (isSelected) ServiceTealBg else SurfaceWhite
                                isSelected && isCorrect -> ServiceGreenBg
                                isSelected && !isCorrect -> ServiceRedBg
                                isCorrect -> ServiceGreenBg
                                else -> SurfaceWhite
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (!isAnswered) {
                                            selectedAnswers[currentQuestion.id] = optIndex
                                            showExplanation = true
                                        }
                                    }
                                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = bgColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(borderColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val optionLetter = ('A'.code + optIndex).toChar().toString()
                                        Text(
                                            text = optionLetter,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = borderColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = optionText,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isAnswered) {
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct",
                                                tint = ServiceGreen
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = "Wrong",
                                                tint = ServiceRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Answer & Olympiad Solution Box (Pattern matching PDF "Answer: 30", "Answer: 10", etc.)
                    item {
                        val isAnswered = selectedAnswers.containsKey(currentQuestion.id) || isNumericSubmitted[currentQuestion.id] == true

                        AnimatedVisibility(visible = showExplanation || isAnswered) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ServiceTealBg),
                                border = BorderStroke(1.dp, ServiceTealBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Official Answer Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = PrimaryTeal,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = currentQuestion.answerLabel.ifEmpty {
                                                    "Answer: ${if (currentQuestion.questionType == QuestionType.NUMERIC) currentQuestion.correctNumericAnswer else ('A'.code + currentQuestion.correctIndex).toChar()}"
                                                },
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Lightbulb,
                                                contentDescription = "Solution",
                                                tint = PrimaryTeal,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "সমাধান ও ব্যাখ্যা",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = PrimaryTealDark
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = currentQuestion.explanation,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Navigation Buttons
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentIndex > 0) {
                                        currentIndex--
                                    }
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পূর্ববর্তী (Previous)")
                            }

                            if (currentIndex == questions.size - 1) {
                                Button(
                                    onClick = { quizCompleted = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("সম্পন্ন করুন")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (currentIndex < questions.size - 1) {
                                            currentIndex++
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("পরবর্তী (Next)")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultDialog(
    total: Int,
    score: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val percentage = (score.toDouble() / total.coerceAtLeast(1) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundClean)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(if (percentage >= 70) ServiceGreen else ServiceAmberBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (percentage >= 70) Icons.Default.EmojiEvents else Icons.Default.Stars,
                contentDescription = "Result",
                tint = if (percentage >= 70) Color.White else ServiceAmber,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (percentage >= 70) "চমৎকার প্রস্তুতি! অভিনন্দন!" else "ভালো প্রচেষ্টা! আরও অনুশীলন করুন!",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTealDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "আপনি $total টি প্রশ্নের মধ্যে $score টি সঠিক উত্তর দিয়েছেন ($percentage%)",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("পুনরায় পরীক্ষা দিন (Retry Quiz)")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onExit,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text("হোমে ফিরে যান")
        }
    }
}
