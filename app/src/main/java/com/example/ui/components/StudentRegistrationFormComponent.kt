package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

/**
 * Data Model for Student Registration.
 * Captures name, age, grade level, and school with mandatory validations.
 */
data class StudentRegistrationFormData(
    val name: String,
    val age: Int,
    val gradeLevel: String,
    val school: String,
    val category: String = getOlympiadCategoryForGrade(gradeLevel),
    val registrationId: String = generateRegistrationId()
) {
    companion object {
        fun getOlympiadCategoryForGrade(grade: String): String {
            return when {
                grade.contains("3") || grade.contains("4") || grade.contains("5") -> "Primary (Class 3–5)"
                grade.contains("6") || grade.contains("7") || grade.contains("8") -> "Junior (Class 6–8)"
                grade.contains("9") || grade.contains("10") -> "Secondary (Class 9–10)"
                grade.contains("11") || grade.contains("12") -> "Special / Higher Secondary (Class 11–12)"
                else -> "General Category"
            }
        }

        private fun generateRegistrationId(): String {
            val randomNum = (10000..99999).random()
            return "BDJSO-2026-$randomNum"
        }
    }
}

/**
 * Reusable Student Registration Form Component with mandatory field validations.
 *
 * Mandatory Fields:
 * 1. Name: Required, min 2 characters.
 * 2. Age: Required, numeric value between 6 and 19 years.
 * 3. Grade Level: Required, selection of school grade (Grade 3 - Grade 12).
 * 4. School: Required, min 3 characters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationFormComponent(
    modifier: Modifier = Modifier,
    initialData: StudentRegistrationFormData? = null,
    onStudentRegistered: (StudentRegistrationFormData) -> Unit = {},
    onCancel: (() -> Unit)? = null,
    showSuccessDialogOnSubmit: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    // Form Field States
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var ageText by remember { mutableStateOf(initialData?.age?.toString() ?: "") }
    var gradeLevel by remember { mutableStateOf(initialData?.gradeLevel ?: "") }
    var school by remember { mutableStateOf(initialData?.school ?: "") }

    // Error States
    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var gradeError by remember { mutableStateOf<String?>(null) }
    var schoolError by remember { mutableStateOf<String?>(null) }
    var formSubmittedAttempt by remember { mutableStateOf(false) }

    // Dropdown state for Grade Level
    var gradeDropdownExpanded by remember { mutableStateOf(false) }

    // Success Dialog State
    var submittedStudent by remember { mutableStateOf<StudentRegistrationFormData?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Standard list of grade levels
    val standardGradeLevels = remember {
        listOf(
            "Grade 3", "Grade 4", "Grade 5",
            "Grade 6", "Grade 7", "Grade 8",
            "Grade 9", "Grade 10",
            "Grade 11", "Grade 12"
        )
    }

    // Top Popular Schools in Bangladesh for quick selection chips
    val popularSchools = remember {
        listOf(
            "Rajuk Uttara Model College",
            "Viqarunnisa Noon School & College",
            "Dhaka Residential Model College",
            "St. Joseph Higher Secondary School",
            "Cantonment Public School and College",
            "Rangdhanu Model School",
            "Ideal School and College",
            "Mymensingh Zilla School",
            "Chittagong Collegiate School"
        )
    }

    // Validation Functions
    fun validateName(value: String): String? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> "Student name is mandatory and cannot be empty."
            trimmed.length < 2 -> "Student name must be at least 2 characters long."
            !trimmed.any { it.isLetter() } -> "Student name must contain letters."
            else -> null
        }
    }

    fun validateAge(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) {
            return "Age is mandatory and cannot be empty."
        }
        val ageInt = trimmed.toIntOrNull()
        return when {
            ageInt == null -> "Please enter a valid numeric age (e.g. 14)."
            ageInt < 6 -> "Age must be at least 6 years old."
            ageInt > 19 -> "Age must be at most 19 years old for Olympiad participation."
            else -> null
        }
    }

    fun validateGrade(value: String): String? {
        return if (value.isBlank()) {
            "Grade level is mandatory. Please choose a grade."
        } else {
            null
        }
    }

    fun validateSchool(value: String): String? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> "School name is mandatory and cannot be empty."
            trimmed.length < 3 -> "School name must be at least 3 characters long."
            else -> null
        }
    }

    // Validation Progress Calculation
    val isNameValid = validateName(name) == null
    val isAgeValid = validateAge(ageText) == null
    val isGradeValid = validateGrade(gradeLevel) == null
    val isSchoolValid = validateSchool(school) == null

    val completedFields = listOf(isNameValid, isAgeValid, isGradeValid, isSchoolValid).count { it }
    val progressFraction = completedFields / 4f

    // Form Submission Handler
    fun submitForm() {
        formSubmittedAttempt = true
        nameError = validateName(name)
        ageError = validateAge(ageText)
        gradeError = validateGrade(gradeLevel)
        schoolError = validateSchool(school)

        if (nameError == null && ageError == null && gradeError == null && schoolError == null) {
            focusManager.clearFocus()
            val finalAge = ageText.trim().toInt()
            val registration = StudentRegistrationFormData(
                name = name.trim(),
                age = finalAge,
                gradeLevel = gradeLevel.trim(),
                school = school.trim()
            )
            submittedStudent = registration
            onStudentRegistered(registration)
            if (showSuccessDialogOnSubmit) {
                showSuccessDialog = true
            }
        }
    }

    fun resetForm() {
        name = ""
        ageText = ""
        gradeLevel = ""
        school = ""
        nameError = null
        ageError = null
        gradeError = null
        schoolError = null
        formSubmittedAttempt = false
        submittedStudent = null
        focusManager.clearFocus()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_registration_form_component"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryTeal.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AppRegistration,
                            contentDescription = "Registration",
                            tint = PrimaryTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Student Registration",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fill all 4 mandatory fields (*)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progress Badge
                Surface(
                    color = if (completedFields == 4) PrimaryTealLight else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (completedFields == 4) PrimaryTeal else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "$completedFields / 4 Completed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completedFields == 4) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Progress Linear Indicator
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryTeal,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // -------------------------------------------------------------
            // FIELD 1: STUDENT NAME (MANDATORY)
            // -------------------------------------------------------------
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (formSubmittedAttempt) {
                            nameError = validateName(it)
                        }
                    },
                    label = { Text("Student Name *") },
                    placeholder = { Text("e.g. Ayesha Rahman") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Name Icon",
                            tint = if (nameError != null) MaterialTheme.colorScheme.error else PrimaryTeal
                        )
                    },
                    trailingIcon = {
                        if (name.isNotBlank()) {
                            if (isNameValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = BdjsoEmerald)
                            } else {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    isError = nameError != null,
                    supportingText = {
                        if (nameError != null) {
                            Text(
                                text = nameError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Full legal name as shown on school records",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // -------------------------------------------------------------
            // FIELD 2: AGE (MANDATORY)
            // -------------------------------------------------------------
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        // Allow only numeric input up to 2 digits
                        val filtered = it.filter { char -> char.isDigit() }.take(2)
                        ageText = filtered
                        if (formSubmittedAttempt) {
                            ageError = validateAge(filtered)
                        }
                    },
                    label = { Text("Age *") },
                    placeholder = { Text("e.g. 14 (Range: 6–19)") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Cake,
                            contentDescription = "Age Icon",
                            tint = if (ageError != null) MaterialTheme.colorScheme.error else PrimaryTeal
                        )
                    },
                    trailingIcon = {
                        if (ageText.isNotBlank()) {
                            if (isAgeValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = BdjsoEmerald)
                            } else {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    isError = ageError != null,
                    supportingText = {
                        if (ageError != null) {
                            Text(
                                text = ageError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Age in years (eligible participants: 6–19)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_age_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Age Suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Select:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(11, 12, 13, 14, 15, 16).forEach { sampleAge ->
                        val isSelected = ageText == sampleAge.toString()
                        Surface(
                            modifier = Modifier.clickable {
                                ageText = sampleAge.toString()
                                if (formSubmittedAttempt) ageError = validateAge(ageText)
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = "$sampleAge",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // FIELD 3: GRADE LEVEL (MANDATORY)
            // -------------------------------------------------------------
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gradeLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grade Level *") },
                        placeholder = { Text("Select grade (e.g. Grade 8)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Bookmarks,
                                contentDescription = "Grade Icon",
                                tint = if (gradeError != null) MaterialTheme.colorScheme.error else PrimaryTeal
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { gradeDropdownExpanded = !gradeDropdownExpanded }) {
                                Icon(
                                    if (gradeDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        },
                        isError = gradeError != null,
                        supportingText = {
                            if (gradeError != null) {
                                Text(
                                    text = gradeError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (gradeLevel.isNotBlank()) {
                                Text(
                                    text = "Category: ${StudentRegistrationFormData.getOlympiadCategoryForGrade(gradeLevel)}",
                                    fontSize = 11.sp,
                                    color = PrimaryTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "Select student's current grade or class",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { gradeDropdownExpanded = true }
                            .testTag("student_grade_selector"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    DropdownMenu(
                        expanded = gradeDropdownExpanded,
                        onDismissRequest = { gradeDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        standardGradeLevels.forEach { grade ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(grade, fontWeight = FontWeight.Medium)
                                        Text(
                                            text = StudentRegistrationFormData.getOlympiadCategoryForGrade(grade),
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    gradeLevel = grade
                                    gradeDropdownExpanded = false
                                    if (formSubmittedAttempt) gradeError = validateGrade(grade)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.School, contentDescription = null, tint = PrimaryTeal)
                                }
                            )
                        }
                    }
                }

                // Quick Grade Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(standardGradeLevels) { grade ->
                        val isSelected = gradeLevel == grade
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                gradeLevel = grade
                                if (formSubmittedAttempt) gradeError = validateGrade(grade)
                            },
                            label = { Text(grade, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // FIELD 4: SCHOOL (MANDATORY)
            // -------------------------------------------------------------
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = school,
                    onValueChange = {
                        school = it
                        if (formSubmittedAttempt) {
                            schoolError = validateSchool(it)
                        }
                    },
                    label = { Text("School / Institute *") },
                    placeholder = { Text("e.g. Rajuk Uttara Model College") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationCity,
                            contentDescription = "School Icon",
                            tint = if (schoolError != null) MaterialTheme.colorScheme.error else PrimaryTeal
                        )
                    },
                    trailingIcon = {
                        if (school.isNotBlank()) {
                            if (isSchoolValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = BdjsoEmerald)
                            } else {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    isError = schoolError != null,
                    supportingText = {
                        if (schoolError != null) {
                            Text(
                                text = schoolError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Full name of school, madrasah, or college",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            submitForm()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_school_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Popular School Suggestions
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Suggestions:",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularSchools) { suggestedSchool ->
                        val isSelected = school == suggestedSchool
                        SuggestionChip(
                            onClick = {
                                school = suggestedSchool
                                if (formSubmittedAttempt) schoolError = validateSchool(suggestedSchool)
                            },
                            label = {
                                Text(
                                    suggestedSchool,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) PrimaryTealContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            // Summary Error Notification (if submit attempted with missing fields)
            AnimatedVisibility(
                visible = formSubmittedAttempt && (nameError != null || ageError != null || gradeError != null || schoolError != null),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Please correct all highlighted mandatory fields above before submitting.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Action Buttons (Submit and Reset)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { resetForm() },
                    modifier = Modifier
                        .weight(0.35f)
                        .height(48.dp)
                        .testTag("student_register_reset_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 13.sp)
                }

                Button(
                    onClick = { submitForm() },
                    modifier = Modifier
                        .weight(0.65f)
                        .height(48.dp)
                        .testTag("student_register_submit_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Register Student",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // SUCCESS CONFIRMATION DIALOG
    // -------------------------------------------------------------
    if (showSuccessDialog && submittedStudent != null) {
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_registration_success_dialog"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Celebratory Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BdjsoEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = BdjsoEmerald,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Registration Successful!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "The student has been validated and registered.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Registered Details Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DetailItem(label = "Registration ID", value = submittedStudent!!.registrationId, isHighlighted = true)
                            DetailItem(label = "Student Name", value = submittedStudent!!.name)
                            DetailItem(label = "Age", value = "${submittedStudent!!.age} Years")
                            DetailItem(label = "Grade Level", value = submittedStudent!!.gradeLevel)
                            DetailItem(label = "School", value = submittedStudent!!.school)
                            DetailItem(label = "Category", value = submittedStudent!!.category)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showSuccessDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isHighlighted) PrimaryTeal else MaterialTheme.colorScheme.onSurface
        )
    }
}
