package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.AuthRepository
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.RedAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${user.name} | ${user.username}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Log out", tint = RedAccent)
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
                .testTag("profile_content"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Profile Card Header (Matching Page 1 / 10 from BDJSO portal)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2980B9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "User ID: ${user.username}",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = if (user.role == UserRole.MANAGER || user.role == UserRole.ADMIN) Color(0xFFFEF9E7) else Color(0xFFE8F8F5),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (user.role == UserRole.MANAGER) "Manager" else "Student Participant",
                                    color = if (user.role == UserRole.MANAGER) Color(0xFFB7950B) else GreenPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = Color(0xFFE8F4FD),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Status: Confirmed",
                                    color = Color(0xFF1B4F72),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (user.role == UserRole.MANAGER || user.role == UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToAdminDashboard,
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Dashboard, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Admin Dashboard & Stats")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // User Info Details Table (exact fields from BDJSO portal)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Account Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(label = "Email", value = user.email)
                        DetailRow(label = "Mobile", value = user.mobile)
                        DetailRow(label = "Institute", value = user.instituteName.ifEmpty { "Not specified" })
                        DetailRow(label = "Class Grade", value = user.classGrade.ifEmpty { "Class 8" })
                        DetailRow(label = "Olympiad Category", value = user.category.displayName)
                        DetailRow(label = "Division & District", value = "${user.district}, ${user.division}")
                        DetailRow(label = "Gender", value = user.gender)
                        DetailRow(label = "Date of Birth", value = user.dateOfBirth)
                        DetailRow(label = "Login Count", value = "${user.loginCount} times")

                        if (user.permissions.isNotEmpty()) {
                            DetailRow(label = "Permissions", value = user.permissions.joinToString(", "))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPasswordDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Change Password", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Change Password Dialog (Screenshot Page 13)
        if (showPasswordDialog) {
            var newPass by remember { mutableStateOf("") }
            var confirmPass by remember { mutableStateOf("") }
            var passError by remember { mutableStateOf<String?>(null) }
            var passSuccess by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Change Password") },
                text = {
                    Column {
                        Text("Update password for ${user.email}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newPass,
                            onValueChange = { newPass = it; passError = null },
                            label = { Text("New Password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPass,
                            onValueChange = { confirmPass = it; passError = null },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (passError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(passError ?: "", color = RedAccent, fontSize = 12.sp)
                        }

                        if (passSuccess) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Password updated successfully!", color = GreenPrimary, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPass.length < 4) {
                                passError = "Password must be at least 4 characters"
                            } else if (newPass != confirmPass) {
                                passError = "Passwords do not match"
                            } else {
                                AuthRepository.changePassword(user.username, newPass)
                                passSuccess = true
                            }
                        }
                    ) {
                        Text("Save Password")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Edit Profile Dialog (Screenshot Page 12)
        if (showEditDialog) {
            var editName by remember { mutableStateOf(user.name) }
            var editEmail by remember { mutableStateOf(user.email) }
            var editMobile by remember { mutableStateOf(user.mobile) }
            var editInstitute by remember { mutableStateOf(user.instituteName) }
            var editDistrict by remember { mutableStateOf(user.district) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit User Profile") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editMobile,
                            onValueChange = { editMobile = it },
                            label = { Text("Mobile") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editInstitute,
                            onValueChange = { editInstitute = it },
                            label = { Text("Institute Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val updated = user.copy(
                                name = editName,
                                email = editEmail,
                                mobile = editMobile,
                                instituteName = editInstitute,
                                district = editDistrict
                            )
                            AuthRepository.updateUser(updated)
                            showEditDialog = false
                        }
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }
        Divider(color = Color(0xFFF0F0F0), thickness = 0.8.dp, modifier = Modifier.padding(top = 4.dp))
    }
}
