package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeographyData
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ScienceTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegisterScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
    // Form state
    var fullName by remember { mutableStateOf("") }
    var banglaName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("2013-05-15") }
    var gender by remember { mutableStateOf("Male") }
    var bloodGroup by remember { mutableStateOf("B+") }

    var selectedClass by remember { mutableStateOf("Class 6") }
    var institutionType by remember { mutableStateOf("Bangla Medium") }
    var schoolName by remember { mutableStateOf("") }
    var studentRoll by remember { mutableStateOf("") }

    var selectedDivision by remember { mutableStateOf("Dhaka") }
    var selectedDistrict by remember { mutableStateOf("Dhaka") }
    var upazila by remember { mutableStateOf("") }
    var presentAddress by remember { mutableStateOf("") }

    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var guardianRelation by remember { mutableStateOf("Father") }
    var guardianMobile by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown states
    var genderMenuOpen by remember { mutableStateOf(false) }
    var bloodMenuOpen by remember { mutableStateOf(false) }
    var classMenuOpen by remember { mutableStateOf(false) }
    var instTypeMenuOpen by remember { mutableStateOf(false) }
    var divisionMenuOpen by remember { mutableStateOf(false) }
    var districtMenuOpen by remember { mutableStateOf(false) }
    var relationMenuOpen by remember { mutableStateOf(false) }

    val category = GeographyData.getCategoryForClass(selectedClass)
    val availableDistricts = GeographyData.divisionsWithDistricts[selectedDivision] ?: listOf(selectedDivision)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Student Registration 2026",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
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
            // Header instruction
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Official National Olympiad Entry",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Fill out all fields carefully. An official Registration ID and Admit Card will be generated.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Section 1: Personal Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Personal Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name (English) *") },
                            placeholder = { Text("e.g. Tanvir Hasan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = banglaName,
                            onValueChange = { banglaName = it },
                            label = { Text("Bangla Name (বাংলা নাম)") },
                            placeholder = { Text("e.g. তানভীর হাসান") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = dob,
                            onValueChange = { dob = it },
                            label = { Text("Date of Birth (YYYY-MM-DD) *") },
                            placeholder = { Text("2012-08-15") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Gender Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Gender") },
                                    trailingIcon = {
                                        IconButton(onClick = { genderMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = genderMenuOpen,
                                    onDismissRequest = { genderMenuOpen = false }
                                ) {
                                    listOf("Male", "Female", "Other").forEach { g ->
                                        DropdownMenuItem(
                                            text = { Text(g) },
                                            onClick = {
                                                gender = g
                                                genderMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Blood Group Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = bloodGroup,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Blood Group") },
                                    trailingIcon = {
                                        IconButton(onClick = { bloodMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = bloodMenuOpen,
                                    onDismissRequest = { bloodMenuOpen = false }
                                ) {
                                    GeographyData.bloodGroups.forEach { bg ->
                                        DropdownMenuItem(
                                            text = { Text(bg) },
                                            onClick = {
                                                bloodGroup = bg
                                                bloodMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Academic & Category
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = BdjsoEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Academic Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Class Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedClass,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Class *") },
                                    trailingIcon = {
                                        IconButton(onClick = { classMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = classMenuOpen,
                                    onDismissRequest = { classMenuOpen = false }
                                ) {
                                    GeographyData.classes.forEach { cls ->
                                        DropdownMenuItem(
                                            text = { Text(cls) },
                                            onClick = {
                                                selectedClass = cls
                                                classMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Category (Auto-assigned & locked)
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BdjsoEmerald)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("School / College Name *") },
                            placeholder = { Text("e.g. Notre Dame College") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = institutionType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Institution Type") },
                                    trailingIcon = {
                                        IconButton(onClick = { instTypeMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = instTypeMenuOpen,
                                    onDismissRequest = { instTypeMenuOpen = false }
                                ) {
                                    GeographyData.institutionTypes.forEach { inst ->
                                        DropdownMenuItem(
                                            text = { Text(inst) },
                                            onClick = {
                                                institutionType = inst
                                                instTypeMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = studentRoll,
                                onValueChange = { studentRoll = it },
                                label = { Text("Roll / ID") },
                                placeholder = { Text("12") },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Section 3: Geographical Location
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = ScienceTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Geography & Address", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Division Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedDivision,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Division *") },
                                    trailingIcon = {
                                        IconButton(onClick = { divisionMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = divisionMenuOpen,
                                    onDismissRequest = { divisionMenuOpen = false }
                                ) {
                                    GeographyData.divisions.forEach { div ->
                                        DropdownMenuItem(
                                            text = { Text(div) },
                                            onClick = {
                                                selectedDivision = div
                                                selectedDistrict = GeographyData.divisionsWithDistricts[div]?.firstOrNull() ?: div
                                                divisionMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            // District Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedDistrict,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("District *") },
                                    trailingIcon = {
                                        IconButton(onClick = { districtMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = districtMenuOpen,
                                    onDismissRequest = { districtMenuOpen = false }
                                ) {
                                    availableDistricts.forEach { dist ->
                                        DropdownMenuItem(
                                            text = { Text(dist) },
                                            onClick = {
                                                selectedDistrict = dist
                                                districtMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = upazila,
                            onValueChange = { upazila = it },
                            label = { Text("Upazila / Thana *") },
                            placeholder = { Text("e.g. Dhanmondi, Mirpur, Sadar") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = presentAddress,
                            onValueChange = { presentAddress = it },
                            label = { Text("Present Address *") },
                            placeholder = { Text("House, Road, Area") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Section 4: Contact & Guardian Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContactMail, contentDescription = null, tint = BdjsoEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("4. Contact & Guardian Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Student / Parent Mobile (11 digits) *") },
                            placeholder = { Text("01711223344") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("student@gmail.com") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        OutlinedTextField(
                            value = guardianName,
                            onValueChange = { guardianName = it },
                            label = { Text("Guardian Name *") },
                            placeholder = { Text("Parent / Legal Guardian Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = guardianRelation,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Relation") },
                                    trailingIcon = {
                                        IconButton(onClick = { relationMenuOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = relationMenuOpen,
                                    onDismissRequest = { relationMenuOpen = false }
                                ) {
                                    listOf("Father", "Mother", "Elder Brother", "Elder Sister", "Guardian").forEach { rel ->
                                        DropdownMenuItem(
                                            text = { Text(rel) },
                                            onClick = {
                                                guardianRelation = rel
                                                relationMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = guardianMobile,
                                onValueChange = { guardianMobile = it },
                                label = { Text("Guardian Phone") },
                                placeholder = { Text("01811223344") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }
                    }
                }
            }

            // Error notice
            errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            errorMessage = "Please enter student's full name"
                            return@Button
                        }
                        if (schoolName.isBlank()) {
                            errorMessage = "Please enter your school or college name"
                            return@Button
                        }
                        if (mobile.isBlank() || mobile.length < 10) {
                            errorMessage = "Please enter a valid 11-digit mobile number"
                            return@Button
                        }
                        if (guardianName.isBlank()) {
                            errorMessage = "Please provide guardian name"
                            return@Button
                        }

                        errorMessage = null
                        viewModel.registerStudent(
                            fullName = fullName.trim(),
                            banglaName = banglaName.trim(),
                            dob = dob.trim(),
                            gender = gender,
                            bloodGroup = bloodGroup,
                            className = selectedClass,
                            categoryId = category,
                            schoolName = schoolName.trim(),
                            institutionType = institutionType,
                            division = selectedDivision,
                            district = selectedDistrict,
                            upazila = upazila.ifBlank { "Sadar" },
                            studentRoll = studentRoll.ifBlank { "01" },
                            mobile = mobile.trim(),
                            email = email.ifBlank { "student@bdjso.org" },
                            guardianName = guardianName.trim(),
                            guardianRelation = guardianRelation,
                            guardianMobile = guardianMobile.ifBlank { mobile },
                            presentAddress = presentAddress.ifBlank { "$selectedDistrict, Bangladesh" }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BdjsoEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Submit & Generate Registration ID",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
