package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserEntity
import com.example.ui.theme.BdjsoEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OlympiadGold
import com.example.ui.theme.PrimaryDarkNavy
import com.example.ui.theme.ScienceTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BdjsoViewModel

// ==========================================
// 1. ADMIN USER SHOW SCREEN (Screenshots 1, 10, 11)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminUserShowScreen(viewModel: BdjsoViewModel) {
    val userState by viewModel.selectedUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.users.collectAsStateWithLifecycle()

    // Fallback if none selected, default to Md. Mursaline Parvez (600032)
    val user = userState ?: allUsers.find { it.username == "600032" } ?: allUsers.firstOrNull()

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No user selected")
        }
        return
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete User", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete user ${user.name} (${user.username})?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteUser(user)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Breadcrumbs & Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_USERS) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Users")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "${user.name} | ${user.username}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = PrimaryDarkNavy
                            )
                            Text(
                                text = "Dashboard / ${user.name} / Users Show",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Edit & List Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_EDIT) },
                            colors = ButtonDefaults.buttonColors(containerColor = OlympiadGold),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_USERS) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("List", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Main User Card (Avatar + Core Profile)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Profile Avatar & Main Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(ScienceTeal.copy(alpha = 0.15f))
                                .border(2.dp, ScienceTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = ScienceTeal
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = PrimaryDarkNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${user.username}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ScienceTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (user.status == 1) BdjsoEmerald.copy(alpha = 0.15f) else Color(0xFFDC2626).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (user.status == 1) "Active (Status: 1)" else "Blocked",
                                        color = if (user.status == 1) BdjsoEmerald else Color(0xFFDC2626),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (user.role.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Role: ${user.role}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OlympiadGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Detail Fields Grid (Exact fields from Screenshot 1 & 10)
                    UserDetailRow(label = "Username", value = user.username)
                    UserDetailRow(label = "Name", value = user.name)
                    UserDetailRow(label = "Email", value = user.email)
                    UserDetailRow(label = "Mobile", value = user.mobile)
                    UserDetailRow(label = "Gender", value = user.gender)
                    UserDetailRow(label = "Date Of Birth", value = user.dateOfBirth)
                    UserDetailRow(label = "Address", value = user.address.ifEmpty { "Not specified" })
                    UserDetailRow(label = "Bio", value = user.bio.ifEmpty { "No bio added" })
                    UserDetailRow(label = "Last Ip", value = user.lastIp)
                    UserDetailRow(label = "Login Count", value = "${user.loginCount}")
                    UserDetailRow(label = "Last Login", value = user.lastLogin)

                    // Password row with button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password:",
                            modifier = Modifier.width(130.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryDarkNavy
                        )
                        Button(
                            onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_CHANGE_PASSWORD) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change password", fontSize = 11.sp)
                        }
                    }

                    UserDetailRow(label = "Social", value = "No social profile added.")
                    UserDetailRow(label = "Status", value = "${user.status}")

                    // Confirmed row with reminder button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confirmed:",
                            modifier = Modifier.width(130.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryDarkNavy
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (user.isConfirmed) BdjsoEmerald.copy(alpha = 0.15f) else Color(0xFFDC2626).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (user.isConfirmed) "Confirmed" else "Not Confirmed",
                                    color = if (user.isConfirmed) BdjsoEmerald else Color(0xFFDC2626),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (!user.isConfirmed) {
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { viewModel.sendConfirmationReminder(user) },
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Send Confirmation Reminder", fontSize = 10.sp, color = ScienceTeal)
                                }
                            }
                        }
                    }

                    // Roles
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "Roles:",
                            modifier = Modifier.width(130.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryDarkNavy
                        )
                        Text(
                            text = "• ${user.role}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Permissions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "Permissions:",
                            modifier = Modifier.width(130.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryDarkNavy
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val perms = if (user.permissions.isNotBlank()) {
                                user.permissions.split(",").map { it.trim() }
                            } else {
                                listOf("view_backend", "student_access")
                            }
                            perms.forEach { perm ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryDarkNavy.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "• $perm",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = PrimaryDarkNavy
                                    )
                                }
                            }
                        }
                    }

                    UserDetailRow(label = "Created At", value = user.createdAt)
                    UserDetailRow(label = "Updated At", value = user.updatedAt)
                    UserDetailRow(label = "Deleted At", value = user.deletedAt.ifEmpty { "None" })

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Action Buttons (Screenshot 11: Block, Delete, Email Confirmation)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.blockUser(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Block", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.sendEmailConfirmation(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email Confirmation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width(130.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = PrimaryDarkNavy
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// ==========================================
// 2. ADMIN USER EDIT SCREEN (Screenshot 12)
// ==========================================
@Composable
fun AdminUserEditScreen(viewModel: BdjsoViewModel) {
    val userState by viewModel.selectedUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.users.collectAsStateWithLifecycle()
    val user = userState ?: allUsers.find { it.username == "600032" } ?: allUsers.firstOrNull()

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No user selected for edit")
        }
        return
    }

    var name by remember(user) { mutableStateOf(user.name) }
    var instituteName by remember(user) { mutableStateOf(user.instituteName) }
    var email by remember(user) { mutableStateOf(user.email) }
    var mobile by remember(user) { mutableStateOf(user.mobile) }
    var className by remember(user) { mutableStateOf(user.className) }
    var categoryName by remember(user) { mutableStateOf(user.categoryName) }
    var division by remember(user) { mutableStateOf(user.division) }
    var district by remember(user) { mutableStateOf(user.district) }
    var upazila by remember(user) { mutableStateOf(user.upazila) }
    var gender by remember(user) { mutableStateOf(user.gender) }
    var dateOfBirth by remember(user) { mutableStateOf(user.dateOfBirth) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_USER_SHOW) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "${user.name} | ${user.username} Users Edit",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = PrimaryDarkNavy
                    )
                    Text(
                        text = "Dashboard / ${user.name} / Users Edit",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Form fields matching Screenshot 12
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = instituteName,
                        onValueChange = { instituteName = it },
                        label = { Text("Institute Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class (as you were in 2025)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = division,
                        onValueChange = { division = it },
                        label = { Text("Division Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = upazila,
                        onValueChange = { upazila = it },
                        label = { Text("Upazila Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date Of Birth") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Password and Confirmed buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_CHANGE_PASSWORD) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScienceTeal),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Change password", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.sendEmailConfirmation(user) },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Send Confirmation Email", fontSize = 12.sp, color = ScienceTeal)
                        }
                    }

                    Text("Social: No social profile added.", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Save / Cancel buttons (Screenshot 12)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val updated = user.copy(
                                    name = name,
                                    instituteName = instituteName,
                                    email = email,
                                    mobile = mobile,
                                    className = className,
                                    categoryName = categoryName,
                                    division = division,
                                    district = district,
                                    upazila = upazila,
                                    gender = gender,
                                    dateOfBirth = dateOfBirth
                                )
                                viewModel.updateUser(updated)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_USER_SHOW) },
                            colors = ButtonDefaults.buttonColors(containerColor = OlympiadGold),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. ADMIN USER CHANGE PASSWORD (Screenshot 13)
// ==========================================
@Composable
fun AdminUserChangePasswordScreen(viewModel: BdjsoViewModel) {
    val userState by viewModel.selectedUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.users.collectAsStateWithLifecycle()
    val user = userState ?: allUsers.find { it.username == "600032" } ?: allUsers.firstOrNull()

    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No user selected")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_USER_SHOW) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Users Change Password",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = PrimaryDarkNavy
                    )
                    Text(
                        text = "Dashboard / ${user.name} / Change Password",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Name & Email (Readonly, matching Screenshot 13)
                    OutlinedTextField(
                        value = user.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = user.email,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorText = null
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ScienceTeal) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = passwordConfirmation,
                        onValueChange = {
                            passwordConfirmation = it
                            errorText = null
                        },
                        label = { Text("Password Confirmation") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ScienceTeal) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (errorText != null) {
                        Text(
                            text = errorText ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (password.length < 6) {
                                    errorText = "Password must be at least 6 characters."
                                } else if (password != passwordConfirmation) {
                                    errorText = "Passwords do not match."
                                } else {
                                    viewModel.changePassword(user.id, password)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BdjsoEmerald),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_USER_SHOW) },
                            colors = ButtonDefaults.buttonColors(containerColor = OlympiadGold),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
