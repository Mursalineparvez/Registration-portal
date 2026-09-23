package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StudentRegistrationFormComponent
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegisterScreen(
    viewModel: BdjsoViewModel,
    modifier: Modifier = Modifier
) {
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
            item {
                StudentRegistrationFormComponent(
                    onStudentRegistered = { data ->
                        viewModel.registerStudent(
                            fullName = data.name,
                            banglaName = data.name,
                            dob = "2012-05-15",
                            gender = "Male",
                            bloodGroup = "B+",
                            className = data.gradeLevel,
                            categoryId = data.category,
                            schoolName = data.school,
                            institutionType = "Bangla Medium",
                            division = "Dhaka",
                            district = "Dhaka",
                            upazila = "Sadar",
                            studentRoll = "01",
                            mobile = "01712345678",
                            email = "student@bdjso.org",
                            guardianName = "Guardian of ${data.name}",
                            guardianRelation = "Parent",
                            guardianMobile = "01712345678",
                            presentAddress = "Bangladesh"
                        )
                    }
                )
            }
        }
    }
}
