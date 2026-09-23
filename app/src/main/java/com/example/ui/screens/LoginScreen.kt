package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.model.OlympiadCategory
import com.example.model.UserProfile
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@Composable
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login state
    var identifier by remember { mutableStateOf("600032") } // Pre-filled with demo user ID
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(true) }

    // Registration state
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regMobile by remember { mutableStateOf("") }
    var regInstitute by remember { mutableStateOf("") }
    var regClass by remember { mutableStateOf("Class 8") }
    var regCategory by remember { mutableStateOf(OlympiadCategory.JUNIOR) }
    var regDivision by remember { mutableStateOf("Dhaka") }
    var regDistrict by remember { mutableStateOf("Dhaka") }
    var regPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .testTag("login_screen_content"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(36.dp))

            // Both Official Logos Header (1st: BDJSO, 2nd: SPSB)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1st: BDJSO Logo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bdjso_logo),
                            contentDescription = "BDJSO Logo",
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "BDJSO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(50.dp)
                            .padding(horizontal = 8.dp),
                        color = Color(0xFFE0E0E0)
                    )

                    // 2nd: SPSB Logo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_spsb_logo),
                            contentDescription = "SPSB Logo",
                            modifier = Modifier.size(56.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SPSB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2980B9)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "BDJSO Online Portal",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDark
            )

            Text(
                text = "Joint initiative by BFF & SPSB",
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isRegisterMode) "Student Registration" else "Login with Email, Phone, or User ID",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Switch between Login and Register
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8ECE9))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isRegisterMode) Color.White else Color.Transparent)
                        .clickable { isRegisterMode = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isRegisterMode) GreenPrimary else Color.DarkGray
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRegisterMode) Color.White else Color.Transparent)
                        .clickable { isRegisterMode = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (isRegisterMode) GreenPrimary else Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Error message banner
        if (errorMessage != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC0392B))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMessage ?: "", color = Color(0xFFC0392B), fontSize = 13.sp)
                    }
                }
            }
        }

        if (!isRegisterMode) {
            // LOGIN FORM
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Account Identifier *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = identifier,
                            onValueChange = {
                                identifier = it
                                errorMessage = null
                            },
                            placeholder = { Text("Email / Phone / User ID (e.g. 600032)") },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = GreenPrimary)
                            },
                            trailingIcon = {
                                if (identifier.isNotEmpty()) {
                                    IconButton(onClick = { identifier = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Password *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            placeholder = { Text("Enter your password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                                )
                                Text("Remember me", fontSize = 12.sp, color = Color.DarkGray)
                            }

                            TextButton(onClick = {
                                errorMessage = "Default test passwords: admin123 (Manager) or student123 (Student)"
                            }) {
                                Text("Forgot Password?", fontSize = 12.sp, color = GreenPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                when (val result = AuthRepository.login(identifier, password)) {
                                    is AuthRepository.AuthResult.Success -> {
                                        onLoginSuccess(result.user)
                                    }
                                    is AuthRepository.AuthResult.Error -> {
                                        errorMessage = result.message
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Demo Login Pills
                Text(
                    text = "Quick 1-Click Demo Accounts:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            identifier = "600032"
                            password = "admin123"
                            AuthRepository.autoLoginDemo(isManager = true)
                            AuthRepository.currentUser.value?.let(onLoginSuccess)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Manager / Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("ID: 600032", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            identifier = "911621"
                            password = "student123"
                            AuthRepository.autoLoginDemo(isManager = false)
                            AuthRepository.currentUser.value?.let(onLoginSuccess)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Student Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("ID: 911621", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onContinueAsGuest) {
                    Text("Skip for now (Browse as Guest)", color = Color.Gray, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            // REGISTER FORM
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regMobile,
                            onValueChange = { regMobile = it },
                            label = { Text("Mobile / Phone Number *") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regInstitute,
                            onValueChange = { regInstitute = it },
                            label = { Text("School / Institute Name") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(OlympiadCategory.PRIMARY, OlympiadCategory.JUNIOR, OlympiadCategory.SECONDARY).forEach { cat ->
                                FilterChip(
                                    selected = regCategory == cat,
                                    onClick = { regCategory = cat },
                                    label = { Text(cat.displayName, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                when (val result = AuthRepository.register(
                                    name = regName,
                                    email = regEmail,
                                    mobile = regMobile,
                                    institute = regInstitute,
                                    classGrade = regClass,
                                    category = regCategory,
                                    division = regDivision,
                                    district = regDistrict,
                                    password = regPassword
                                )) {
                                    is AuthRepository.AuthResult.Success -> {
                                        onLoginSuccess(result.user)
                                    }
                                    is AuthRepository.AuthResult.Error -> {
                                        errorMessage = result.message
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Create BDJSO Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
