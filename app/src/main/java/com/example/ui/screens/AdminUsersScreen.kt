package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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

@Composable
fun AdminUsersScreen(viewModel: BdjsoViewModel) {
    val allUsers by viewModel.users.collectAsStateWithLifecycle()
    val searchQuery by viewModel.userSearchQuery.collectAsStateWithLifecycle()

    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) {
            allUsers
        } else {
            val q = searchQuery.trim().lowercase()
            allUsers.filter {
                it.name.lowercase().contains(q) ||
                        it.username.lowercase().contains(q) ||
                        it.email.lowercase().contains(q) ||
                        it.mobile.lowercase().contains(q) ||
                        it.role.lowercase().contains(q)
            }
        }
    }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete User", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete user ${userToDelete?.name} (${userToDelete?.username})?") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { viewModel.deleteUser(it) }
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header & Breadcrumbs
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Users List",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = PrimaryDarkNavy
                        )
                        Text(
                            text = "Dashboard / Users",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ScienceTeal.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Total: 11,621",
                        color = ScienceTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Search Box (Matching Screenshot 8)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setUserSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Name, Username, Email or Mobile", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ScienceTeal) },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        item {
            // Table Header Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDarkNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("USERNAME & NAME", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                    Text("CONTACT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                    Text("ACTIONS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No matching users found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Column 1: Username & Name
                            Column(modifier = Modifier.weight(1.3f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PrimaryDarkNavy.copy(alpha = 0.08f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = user.username,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ScienceTeal
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (user.role == "Manager") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(OlympiadGold.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text("Manager", color = OlympiadGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (user.instituteName.isNotBlank()) {
                                    Text(
                                        text = user.instituteName,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Column 2: Contact
                            Column(modifier = Modifier.weight(1.3f).padding(horizontal = 6.dp)) {
                                Text(
                                    text = user.email,
                                    fontSize = 11.sp,
                                    color = ScienceTeal,
                                    maxLines = 1
                                )
                                Text(
                                    text = user.mobile,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (user.className.isNotBlank()) {
                                    Text(
                                        text = "${user.className} • ${user.district}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Column 3: Action Buttons (Eye, Edit, Key, Delete) - exact match to screenshot 8
                            Row(
                                modifier = Modifier.width(130.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // View (Eye - blue)
                                IconButton(
                                    onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_SHOW) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ScienceTeal)
                                ) {
                                    Icon(
                                        Icons.Default.Visibility,
                                        contentDescription = "Show",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Edit (Pencil - cyan/teal)
                                IconButton(
                                    onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_EDIT) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricCyan)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Key (Password - yellow/orange)
                                IconButton(
                                    onClick = { viewModel.selectUser(user, AppScreen.ADMIN_USER_CHANGE_PASSWORD) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(OlympiadGold)
                                ) {
                                    Icon(
                                        Icons.Default.Key,
                                        contentDescription = "Password",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Delete (Trash - red)
                                IconButton(
                                    onClick = { userToDelete = user },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFDC2626))
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Pagination bar (Screenshot 8: 1 2 3 4 5 6 7 8 9 10 ... 3594 3595)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing 1 to ${filteredUsers.size} of 11,621 users",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )

                listOf("1", "2", "3", "4", "5", "6", "...", "3594", "3595").forEachIndexed { index, p ->
                    val isSelected = index == 0
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) ScienceTeal else MaterialTheme.colorScheme.surface)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
