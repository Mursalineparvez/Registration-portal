package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.data.BDJSOData
import com.example.model.OlympiadCategory
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToQuiz: (OlympiadCategory) -> Unit,
    onNavigateToQuestionBank: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToDataVisualization: () -> Unit = {}
) {
    val currentUser by AuthRepository.currentUser.collectAsState()
    var selectedCategory by remember { mutableStateOf(OlympiadCategory.JUNIOR) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundClean)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Welcome Card ("আপনাকে স্বাগতম 👋" - matching reference design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentUser != null) "স্বাগতম, ${currentUser?.name?.substringBefore(" ")} 👋" else "আপনাকে স্বাগতম 👋",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryTealDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "বাংলাদেশ জুনিয়র সায়েন্স অলিম্পিয়াড (BDJSO) ও SPSB-এর অফিসিয়াল প্ল্যাটফর্মে আপনাকে স্বাগতম।",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Category Badge
                        Surface(
                            color = ServiceGreenBg,
                            border = BorderStroke(1.dp, ServiceGreenBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = selectedCategory.displayName,
                                color = PrimaryTealDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Pill Switcher (Primary, Junior, Secondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            OlympiadCategory.PRIMARY,
                            OlympiadCategory.JUNIOR,
                            OlympiadCategory.SECONDARY
                        ).forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryTeal else Color.Transparent)
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Start Preparation Button (`পরবর্তী →` / `অনুশীলন শুরু করুন →`)
                    Button(
                        onClick = { onNavigateToQuiz(selectedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "মক টেস্ট শুরু করুন (${selectedCategory.displayName})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Section Header: সেবা ও একাডেমিক মডিউল (Service Modules)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সেবা ও একাডেমি সমূহ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTealDark
                )
                Text(
                    text = "সকল সার্ভিস",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTeal
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 3. Service Cards (Matching the reference design list with colored boxes and right arrow)
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 0: শিক্ষার্থী নিবন্ধন ও রেজিস্ট্রেশন ২০২৬ (Local Room Database Persistence)
                ServiceListCard(
                    title = "শিক্ষার্থী নিবন্ধন (Room DB রেজিস্ট্রেশন)",
                    subtitle = "ডিভাইসের লোকাল ডাটাবেসে নতুন শিক্ষার্থী তথ্য সংরক্ষণ করুন",
                    icon = Icons.Default.AppRegistration,
                    iconBgColor = Color(0xFFE0F2FE),
                    iconBorderColor = Color(0xFFBAE6FD),
                    iconTint = Color(0xFF0284C7),
                    onClick = onNavigateToRegister
                )

                // Card 1: কুইজ ও মক টেস্ট (Green Accent)
                ServiceListCard(
                    title = "মক টেস্ট ও অলিম্পিয়াড কুইজ",
                    subtitle = "পদার্থ, রসায়ন ও জীববিজ্ঞানের টাইমারসহ অনুশীলন",
                    icon = Icons.Default.Quiz,
                    iconBgColor = ServiceGreenBg,
                    iconBorderColor = ServiceGreenBorder,
                    iconTint = ServiceGreen,
                    onClick = { onNavigateToQuiz(selectedCategory) }
                )

                // Card 2: বিগত বছরের প্রশ্ন (Purple Accent)
                ServiceListCard(
                    title = "বিগত বছরের প্রশ্নব্যাংক",
                    subtitle = "২০২০ থেকে ২০২৫ সালের আঞ্চলিক ও জাতীয় প্রশ্ন",
                    icon = Icons.Default.MenuBook,
                    iconBgColor = ServicePurpleBg,
                    iconBorderColor = ServicePurpleBorder,
                    iconTint = ServicePurple,
                    onClick = onNavigateToQuestionBank
                )

                // Card 3: সিলেবাস ও ল্যাব (Amber Accent)
                ServiceListCard(
                    title = "সিলেবাস ও সায়েন্স ল্যাব গাইড",
                    subtitle = "বিজ্ঞানের মূল সূত্র, কনসেপ্ট ও অলিম্পিয়াড ট্রিকস",
                    icon = Icons.Default.Science,
                    iconBgColor = ServiceAmberBg,
                    iconBorderColor = ServiceAmberBorder,
                    iconTint = ServiceAmber,
                    onClick = onNavigateToSyllabus
                )

                // Card 4: মেধা তালিকা ও রেজাল্ট (Blue Accent)
                ServiceListCard(
                    title = "ফলাফল ও জাতীয় মেধা তালিকা",
                    subtitle = "রোল বা নাম দিয়ে ফলাফল ও ক্যাম্প সিলেকশন যাচাই",
                    icon = Icons.Default.Verified,
                    iconBgColor = ServiceBlueBg,
                    iconBorderColor = ServiceBlueBorder,
                    iconTint = ServiceBlue,
                    onClick = onNavigateToResults
                )

                // Card 5: গাইডলাইন ও নিয়মাবলী (Teal Accent)
                ServiceListCard(
                    title = "অলিম্পিয়াড নিয়মাবলী ও তথ্য গাইড",
                    subtitle = "আঞ্চলিক ও জাতীয় পর্ব থেকে IJSO দল নির্বাচনের ধাপ",
                    icon = Icons.Default.HelpOutline,
                    iconBgColor = ServiceTealBg,
                    iconBorderColor = ServiceTealBorder,
                    iconTint = ServiceTeal,
                    onClick = onNavigateToGuide
                )

                // Card 6: এডমিন ও ম্যানেজমেন্ট প্যানেল (Admin Control & Management Portal)
                ServiceListCard(
                    title = "এডমিন ও ম্যানেজমেন্ট প্যানেল (Admin Portal)",
                    subtitle = "শিক্ষার্থী ডাটাবেস, প্রশ্নব্যাংক, পরীক্ষা ও ফলাফল সম্পূর্ণ নিয়ন্ত্রণ",
                    icon = Icons.Default.AdminPanelSettings,
                    iconBgColor = Color(0xFFFEF3C7),
                    iconBorderColor = Color(0xFFFDE68A),
                    iconTint = Color(0xFFD97706),
                    onClick = onNavigateToAdmin
                )

                // Card 7: ডাটা ভিজ্যুয়ালাইজেশন ও ট্রেন্ড ড্যাশবোর্ড (Recharts Trends)
                ServiceListCard(
                    title = "ডাটা ভিজ্যুয়ালাইজেশন ড্যাশবোর্ড (Analytics)",
                    subtitle = "অংশগ্রহণ ট্রেন্ড ও বিষয়ভিত্তিক পরীক্ষার পারফরম্যান্স গ্রাফ (Recharts)",
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = Color(0xFFE0F2FE),
                    iconBorderColor = Color(0xFFBAE6FD),
                    iconTint = Color(0xFF0284C7),
                    onClick = onNavigateToDataVisualization
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 4. "কীভাবে কাজ করবেন" / How It Works (Step-by-Step Flow matching reference design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "কীভাবে কাজ করবেন (ধাপসমূহ)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTealDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepItem(stepNumber = "১", label = "তথ্য ও লগইন", icon = Icons.Default.AccountCircle)
                        Text("• • →", fontSize = 11.sp, color = TextMuted)
                        StepItem(stepNumber = "২", label = "প্রশ্নব্যাংক", icon = Icons.Default.MenuBook)
                        Text("• • →", fontSize = 11.sp, color = TextMuted)
                        StepItem(stepNumber = "৩", label = "মক টেস্ট", icon = Icons.Default.Timer)
                        Text("• • →", fontSize = 11.sp, color = TextMuted)
                        StepItem(stepNumber = "৪", label = "ক্যাম্প ও টিম", icon = Icons.Default.EmojiEvents)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Trust & Information Badges (Grid of 4 items matching reference footer section)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrustBadgeCard(
                    title = "তথ্য সুরক্ষিত",
                    subtitle = "অফিসিয়াল বিডিজেএসও পোর্টাল",
                    icon = Icons.Default.Lock,
                    iconTint = ServiceGreen,
                    modifier = Modifier.weight(1f)
                )

                TrustBadgeCard(
                    title = "বিনামূল্যে সেবা",
                    subtitle = "সকল অংশগ্রহণকারীর জন্য উন্মুক্ত",
                    icon = Icons.Default.VolunteerActivism,
                    iconTint = ServiceBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrustBadgeCard(
                    title = "সময় সাশ্রয়ী",
                    subtitle = "মোবাইল ও অনলাইনে দ্রুত পরীক্ষা",
                    icon = Icons.Default.Schedule,
                    iconTint = ServiceAmber,
                    modifier = Modifier.weight(1f)
                )

                TrustBadgeCard(
                    title = "৬৪ জেলা",
                    subtitle = "সারা দেশের শিক্ষার্থীদের জন্য",
                    icon = Icons.Default.Public,
                    iconTint = ServiceTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. Official Organizing Bodies (BDJSO 1st, SPSB 2nd)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আয়োজক প্রতিষ্ঠান",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryTealDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .height(44.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BDJSO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = PrimaryTealDark
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(horizontal = 8.dp),
                            color = BorderLight
                        )

                        // 2nd: SPSB Logo
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_spsb_logo),
                                contentDescription = "SPSB Logo",
                                modifier = Modifier.size(44.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SPSB",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ServiceBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "যৌথভাবে আয়োজনে: বাংলাদেশ ফ্রিডম ফাউন্ডেশন (BFF) ও বাংলাদেশ বিজ্ঞান জনপ্রিয়করণ সমিতি (SPSB)।",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 7. Announcements / নোটিশ বোর্ড
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "জরুরি নোটিশ ও আপডেট",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTealDark
                )
                Text(
                    text = "সকল নোটিশ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTeal
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(BDJSOData.announcements) { announcement ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (announcement.isImportant) ServiceRedBg else ServiceGreenBg,
                            border = BorderStroke(1.dp, if (announcement.isImportant) ServiceRedBorder else ServiceGreenBorder),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = announcement.tag,
                                color = if (announcement.isImportant) ServiceRed else PrimaryTealDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = announcement.date,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = announcement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = announcement.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Service Card item styled exactly like the reference image:
 * Soft tinted rounded square on left + bold title + subtitle + arrow on right
 */
@Composable
fun ServiceListCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconBorderColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded square icon container with soft border & tint
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor)
                    .border(1.dp, iconBorderColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right arrow matching the reference design `>`
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StepItem(
    stepNumber: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ServiceGreenBg)
                .border(1.dp, ServiceGreenBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun TrustBadgeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = TextMuted,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
