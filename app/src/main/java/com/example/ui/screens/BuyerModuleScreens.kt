@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import kotlin.math.absoluteValue

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import java.util.UUID

// ==========================================
// 1. BUYER LOGIN SCREEN (Mobile + Simulated OTP)
// ==========================================

@Composable
fun BuyerLoginScreen(onSuccess: (rememberMe: Boolean) -> Unit) {
    var mobileNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpRequested by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .testTag("buyer_login_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Brand Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(FarmGreenPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BusinessCenter,
                        contentDescription = "Buyer Portal",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Buyer Procurement Portal",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = FarmGreenDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to access verified wholesale crop lots, manage cart, track logistics, and verify warehouse certification.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mobile Number Field
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            mobileNumber = it
                        }
                    },
                    label = { Text("10-Digit Mobile Number") },
                    placeholder = { Text("Enter mobile number") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buyer_login_mobile_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                if (!isOtpRequested) {
                    Button(
                        onClick = {
                            if (mobileNumber.length == 10) {
                                isOtpRequested = true
                                errorMessage = ""
                                Toast.makeText(context, "Procurement OTP Sent: 1234", Toast.LENGTH_LONG).show()
                            } else {
                                errorMessage = "Please enter a valid 10-digit mobile number."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("buyer_login_otp_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Request Verification OTP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    // OTP Verification Field
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                otpCode = it
                            }
                        },
                        label = { Text("Simulated OTP (Code: 1234)") },
                        placeholder = { Text("Enter 4-digit code") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("buyer_login_otp_input"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            modifier = Modifier.testTag("buyer_login_remember_checkbox")
                        )
                        Text("Remember procurement session on this device", fontSize = 12.sp)
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isOtpRequested = false
                                otpCode = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Back", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                if (otpCode == "1234") {
                                    errorMessage = ""
                                    // B01 represents Aman Gupta, our premium buyer
                                    MarketplaceRepository.currentUserId.value = "B01"
                                    onSuccess(rememberMe)
                                } else {
                                    errorMessage = "Invalid OTP code. Please enter 1234 to proceed."
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("buyer_login_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Verify & Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. BUYER DASHBOARD HOME SCREEN (10 requested sections)
// ==========================================

@Composable
fun BuyerDashboardHome(
    onNavigate: (String) -> Unit,
    onSelectCrop: (String) -> Unit
) {
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val availableListings = cropListings.filter { it.status == "Published" }
    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()
    val searchHistory by MarketplaceRepository.searchHistory.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val wishlist by MarketplaceRepository.wishlistItems.collectAsState()
    val recentlyViewedIds by MarketplaceRepository.recentlyViewedCrops.collectAsState()
    val cart by MarketplaceRepository.cartItems.collectAsState()
    val buyers by MarketplaceRepository.buyers.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()

    var searchQueryState by remember { mutableStateOf("") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // SECTION 1: Welcome Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("buyer_welcome_banner"),
                colors = CardDefaults.cardColors(containerColor = FarmGreenDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome Back,",
                                fontSize = 12.sp,
                                color = LightGreenTint,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Aman Gupta",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Delhi Grain Corp Procurement",
                                fontSize = 11.sp,
                                color = LightGreenTint.copy(alpha = 0.8f)
                            )
                        }

                        // Shopping Cart Shortcut icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { onNavigate("Cart") }
                                .testTag("buyer_cart_shortcut"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            if (cart.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = cart.size.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = LightGreenTint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Wallet Balance:",
                                fontSize = 11.sp,
                                color = LightGreenTint
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val currentBalance = buyers.find { it.id == currentUserId }?.walletBalance ?: 0.0
                            Text(
                                text = "₹${String.format("%,.0f", currentBalance)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable { onNavigate("Profile") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "View Profile & Wallet",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark
                            )
                        }
                    }
                }
            }
        }

        // SECTION 2: Search Bar & History Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQueryState,
                    onValueChange = { searchQueryState = it },
                    placeholder = { Text("Search crops, grades, or farmers...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buyer_search_input"),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQueryState.isNotEmpty()) {
                                IconButton(onClick = { searchQueryState = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = { onNavigate("Marketplace") }) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = "Filter Options", tint = FarmGreenPrimary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Search history chips
                if (searchHistory.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recently Searched:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(searchHistory) { item ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LightSurfaceVariant)
                                        .clickable { searchQueryState = item }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = item, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 3: Category Highlights
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Procurement Categories",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        Triple("Grain", Icons.Default.Grass, Color(0xFFE9F5EC)),
                        Triple("Pulse", Icons.Default.EnergySavingsLeaf, Color(0xFFFDF6E2)),
                        Triple("Vegetable", Icons.Default.Eco, Color(0xFFE6F3FF)),
                        Triple("Fruit", Icons.Default.Star, Color(0xFFFFF0F0))
                    )
                    categories.forEach { (catName, icon, bgColor) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable {
                                    MarketplaceRepository.searchQuery.value = catName
                                    onNavigate("Marketplace")
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = catName,
                                tint = when(catName) {
                                    "Grain" -> FarmGreenPrimary
                                    "Pulse" -> EarthAmberPrimary
                                    "Vegetable" -> Color(0xFF0284C7)
                                    else -> Color.Red
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(catName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        // SECTION 4: Best Deals Highlights (Crops with high savings)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥 Best Deals & High Savings",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = FarmGreenDark
                    )
                    Text(
                        text = "See All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenPrimary,
                        modifier = Modifier.clickable { onNavigate("Marketplace") }
                    )
                }

                val bestDeals = availableListings.filter { it.pricePerKg < 35.0 }.take(5)
                if (bestDeals.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bestDeals) { crop ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onSelectCrop(crop.id) }
                                    .testTag("best_deal_item_${crop.id}"),
                                colors = CardDefaults.cardColors(containerColor = LightSurface),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(LightGreenTint)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Best Deal - Grade A", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("by ${crop.farmerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Farmer Price", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 15.sp, color = EarthAmberPrimary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Stock Available", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${crop.quantityKg.toInt()} kg", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("No hot deals listed today.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // SECTION 5: Fresh Harvest Section (Latest published crops)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🌱 Fresh Harvests Recently Listed",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                val freshHarvest = availableListings.sortedByDescending { it.id }.take(4)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    freshHarvest.forEach { crop ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCrop(crop.id) },
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(FarmGreenPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Eco,
                                            contentDescription = null,
                                            tint = FarmGreenPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${crop.farmerName} • Grade: ${crop.qualityGrade}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 15.sp, color = FarmGreenPrimary)
                                    Text("${crop.quantityKg.toInt()} kg", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 6: Trending Crops Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📈 Trending High-Demand Grains",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                val trendingCrops = availableListings.filter { it.rating >= 4.5 }.take(4)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingCrops) { crop ->
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .clickable { onSelectCrop(crop.id) },
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = crop.category.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = FarmGreenPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = FieldGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(crop.rating.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 14.sp, color = EarthAmberPrimary)
                            }
                        }
                    }
                }
            }
        }

        // SECTION 7: Nearby Farmers Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🧑🏽‍🌾 Nearby Verified Farmers",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(farmers) { farmer ->
                        Card(
                            modifier = Modifier
                                .width(200.dp),
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(FarmGreenPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = FarmGreenPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(farmer.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("📍 ${farmer.village}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FieldGold, modifier = Modifier.size(12.dp))
                                    Text("4.8 Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Contacting ${farmer.name}: ${farmer.phone}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Connect Direct", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 8: Official Mandi Price Highlights
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📊 Live Mandi Market Indexes",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mandiPrices) { mandi ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(mandi.crop, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Icon(
                                        imageVector = if (mandi.trend == "Up") Icons.Default.TrendingUp else if (mandi.trend == "Down") Icons.Default.TrendingDown else Icons.Default.ArrowRightAlt,
                                        contentDescription = null,
                                        tint = if (mandi.trend == "Up") FarmGreenPrimary else if (mandi.trend == "Down") Color.Red else Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(mandi.priceRange, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Avg Price", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${mandi.avgPrice}/kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 9: Recently Viewed Products (Driven by recentlyViewedCrops list)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "👁️ Recently Viewed Crops",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                val recentlyViewed = cropListings.filter { recentlyViewedIds.contains(it.id) && it.status == "Published" }
                if (recentlyViewed.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentlyViewed) { crop ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clickable { onSelectCrop(crop.id) },
                                colors = CardDefaults.cardColors(containerColor = LightSurface),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Grade ${crop.qualityGrade}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 14.sp, color = EarthAmberPrimary)
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightSurfaceVariant, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recently viewed items yet. Start exploring!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // SECTION 10: Recommended Products
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⭐ Recommended For Your Procurement Profile",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = FarmGreenDark
                )

                val recommended = availableListings.filter { it.category == "Grain" || it.qualityGrade == "Grade A" }.take(4)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recommended.forEach { crop ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCrop(crop.id) },
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Verified Stock • ${crop.farmerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(FarmGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 12.sp, color = FarmGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. BUYER MARKETPLACE VIEW (Complete Advanced Filters & Search)
// ==========================================

@Composable
fun BuyerMarketplaceView(
    onBack: () -> Unit,
    onSelectCrop: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val availableListings = cropListings.filter { it.status == "Published" }

    var searchQueryState by remember { mutableStateOf(MarketplaceRepository.searchQuery.value) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedGrade by remember { mutableStateOf("All") }
    var selectedDistrict by remember { mutableStateOf("") }
    var maxPriceLimit by remember { mutableStateOf(100f) }
    var minRatingLimit by remember { mutableStateOf(0f) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Grain", "Pulse", "Vegetable", "Fruit")
    val grades = listOf("All", "Grade A", "Grade B", "Grade C")

    val filteredListings = availableListings.filter { crop ->
        val matchesSearch = crop.name.contains(searchQueryState, ignoreCase = true) ||
                crop.farmerName.contains(searchQueryState, ignoreCase = true) ||
                crop.category.contains(searchQueryState, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || crop.category == selectedCategory
        val matchesGrade = selectedGrade == "All" || crop.qualityGrade == selectedGrade
        val matchesDistrict = selectedDistrict.isEmpty() || crop.description.contains(selectedDistrict, ignoreCase = true) || crop.farmerName.contains(selectedDistrict, ignoreCase = true)
        val matchesPrice = crop.pricePerKg <= maxPriceLimit
        val matchesRating = crop.rating >= minRatingLimit

        matchesSearch && matchesCategory && matchesGrade && matchesDistrict && matchesPrice && matchesRating
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(LightSurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Wholesale Marketplace",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = FarmGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = FarmGreenPrimary)
                    }
                }

                OutlinedTextField(
                    value = searchQueryState,
                    onValueChange = { searchQueryState = it },
                    placeholder = { Text("Search crop name, village, or farmer...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Category Chips Horizontal list
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground)
                    .padding(innerPadding)
            ) {
                if (filteredListings.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Matches Found", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                        Text("Adjust your search parameters or filter limits and retry.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredListings) { crop ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectCrop(crop.id) }
                                    .testTag("marketplace_crop_item_${crop.id}"),
                                colors = CardDefaults.cardColors(containerColor = LightSurface),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = crop.name,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 16.sp,
                                                    color = FarmGreenDark
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(FarmGreenPrimary.copy(alpha = 0.1f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(crop.qualityGrade, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Farmer: ${crop.farmerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Farmer", tint = FarmGreenPrimary, modifier = Modifier.size(14.dp))
                                            }
                                            Text("📍 Village Nangal, District Nangal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("₹${crop.pricePerKg}/kg", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EarthAmberPrimary)
                                            // Simulated comparison savings
                                            val savings = (crop.pricePerKg * 0.15).toInt()
                                            Text("Save ₹$savings/kg (15%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = crop.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Mandi Index Reference", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("₹${(crop.pricePerKg * 1.15).toInt()}/kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        }

                                        Column {
                                            Text("Available Stock", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${crop.quantityKg.toInt()} kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                        }

                                        Button(
                                            onClick = { onSelectCrop(crop.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text("View Lot details", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    // SECTION 4: Advanced Filter dialog
    if (showFilterDialog) {
        Dialog(onDismissRequest = { showFilterDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Advanced Procurement Filters", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FarmGreenDark)
                    HorizontalDivider()

                    // Grade Filter Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Quality Grade Selection", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            grades.forEach { g ->
                                FilterChip(
                                    selected = selectedGrade == g,
                                    onClick = { selectedGrade = g },
                                    label = { Text(g, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // District Input field
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = { selectedDistrict = it },
                        label = { Text("Filter by District / Location") },
                        placeholder = { Text("e.g., Nangal, Agra, Lasalgaon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Price Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Max Selling Price limit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("₹${maxPriceLimit.toInt()}/kg", fontSize = 12.sp, fontWeight = FontWeight.Black, color = EarthAmberPrimary)
                        }
                        Slider(
                            value = maxPriceLimit,
                            onValueChange = { maxPriceLimit = it },
                            valueRange = 10f..150f,
                            colors = SliderDefaults.colors(thumbColor = FarmGreenPrimary, activeTrackColor = FarmGreenPrimary)
                        )
                    }

                    // Rating Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Minimum Farmer Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", minRatingLimit)} ★", fontSize = 12.sp, fontWeight = FontWeight.Black, color = FieldGold)
                        }
                        Slider(
                            value = minRatingLimit,
                            onValueChange = { minRatingLimit = it },
                            valueRange = 0f..5f,
                            colors = SliderDefaults.colors(thumbColor = FarmGreenPrimary, activeTrackColor = FarmGreenPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedGrade = "All"
                                selectedDistrict = ""
                                maxPriceLimit = 100f
                                minRatingLimit = 0f
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset")
                        }
                        Button(
                            onClick = { showFilterDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. BUYER PRODUCT DETAILS VIEW
// ==========================================

@Composable
fun BuyerProductDetailsView(
    cropId: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val rawCrop = cropListings.find { it.id == cropId }
    val inspections by MarketplaceRepository.qualityInspections.collectAsState()
    val reviews by MarketplaceRepository.customerReviews.collectAsState()

    val context = LocalContext.current

    if (rawCrop == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Crop listing not found.")
        }
        return
    }
    val crop: CropListing = rawCrop

    // Add to recently viewed list on compose
    LaunchedEffect(cropId) {
        MarketplaceRepository.addRecentlyViewed(cropId)
    }

    var orderQty by remember { mutableStateOf(crop.quantityKg.toInt()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurement Lot details", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        MarketplaceRepository.toggleWishlist(crop.id)
                        Toast.makeText(context, "Updated Wishlist", Toast.LENGTH_SHORT).show()
                    }) {
                        val wishlist by MarketplaceRepository.wishlistItems.collectAsState()
                        Icon(
                            imageVector = if (wishlist.contains(crop.id)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (wishlist.contains(crop.id)) Color.Red else Color.DarkGray
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Add to cart quantity", fontSize = 10.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (orderQty > 100) orderQty -= 50 }) {
                                Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = null, tint = FarmGreenPrimary)
                            }
                            Text("$orderQty kg", fontWeight = FontWeight.Black, fontSize = 14.sp)
                            IconButton(onClick = { if (orderQty < crop.quantityKg) orderQty += 50 }) {
                                Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, tint = FarmGreenPrimary)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            MarketplaceRepository.addToCart(crop.id, orderQty.toDouble())
                            Toast.makeText(context, "Added $orderQty kg of ${crop.name} to Cart!", Toast.LENGTH_LONG).show()
                            onNavigate("Cart")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("add_to_cart_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Lot to Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Crop Title, Rating, and Price Info
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FarmGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(crop.qualityGrade, fontSize = 10.sp, fontWeight = FontWeight.Black, color = FarmGreenPrimary)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LightGreenTint)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.GppGood, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Verified by FarmLink", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(crop.name, fontWeight = FontWeight.Black, fontSize = 22.sp, color = FarmGreenDark)
                            Text("Wholesale Category: ${crop.category} • Certified Safe Organic Trade Lot", fontSize = 12.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Farmer Direct Price", fontSize = 11.sp, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text("₹${crop.pricePerKg}/kg", fontWeight = FontWeight.Black, fontSize = 24.sp, color = EarthAmberPrimary)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Official Mandi Price Index", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹${(crop.pricePerKg * 1.15).toInt()}/kg", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Gray)
                                    Text("Procuring this lot saves ₹${(crop.pricePerKg * 0.15).toInt()}/kg (15%)!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                }
                            }
                        }
                    }
                }

                // Description card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Procurement Lot Description", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = crop.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Farmer Information Section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(FarmGreenPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Farmer Information", fontSize = 10.sp, color = Color.Gray)
                                Text(crop.farmerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FieldGold, modifier = Modifier.size(12.dp))
                                    Text("4.9 Rating Verified • 150+ successful trades", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // Executive Inspection Report details
                item {
                    val report = inspections.find { it.cropListingId == crop.id } ?: QualityInspection(
                        id = "SYS_INSP",
                        cropListingId = crop.id,
                        cropName = crop.name,
                        grade = crop.qualityGrade,
                        qualityScore = 88.0,
                        moistureLevel = 12.0,
                        executiveNotes = "Passed full physical & moisture inspection. Grade verified under standard procurement conditions.",
                        inspectionDate = "Yesterday, 04:00 PM",
                        isApproved = true,
                        verifiedBadge = true
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔍 Logistics Quality Inspection", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FarmGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("APPROVED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = FarmGreenPrimary)
                                }
                            }
                            HorizontalDivider()

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Quality Score Index:", fontSize = 12.sp, color = Color.Gray)
                                Text("${report.qualityScore.toInt()}% / 100%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Moisture Level:", fontSize = 12.sp, color = Color.Gray)
                                Text("${report.moistureLevel}% (Safe Shelf Range)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Verified Grade:", fontSize = 12.sp, color = Color.Gray)
                                Text(report.grade, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Inspector Comments:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LightSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = report.executiveNotes,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Warehouse Details Section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🏢 Certified Warehouse Storage Info", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Stored Facility Name:", fontSize = 12.sp, color = Color.Gray)
                                Text(crop.warehouseName ?: "Delhi Agri-Store Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Silo Rack Location:", fontSize = 12.sp, color = Color.Gray)
                                Text(crop.warehouseRackNumber ?: "Section-A, Silo Bin 12", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Storage Verified Date:", fontSize = 12.sp, color = Color.Gray)
                                Text(crop.warehouseStorageDate ?: "2 days ago", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Certified Remaining Shelf Life:", fontSize = 12.sp, color = Color.Gray)
                                Text(crop.warehouseShelfLife ?: "175 days remaining", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Facility Control Status:", fontSize = 12.sp, color = Color.Gray)
                                Text(crop.warehouseStatus ?: "Certified & Moisture Managed", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                            }
                        }
                    }
                }

                // Customer Reviews Section
                item {
                    val cropReviews = reviews.filter { it.cropName == crop.name }.take(5)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("⭐ Procurement Quality Reviews (${cropReviews.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            if (cropReviews.isNotEmpty()) {
                                cropReviews.forEach { rev ->
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(rev.reviewerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Row {
                                                repeat(rev.rating) {
                                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FieldGold, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                        Text(rev.comment, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(rev.date, fontSize = 9.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                                    }
                                }
                            } else {
                                Text("No reviews for this crop yet.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    )
}

// ==========================================
// 5. BUYER SHOPPING CART VIEW
// ==========================================

@Composable
fun BuyerCartView(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val cart by MarketplaceRepository.cartItems.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()

    var showDeliveryBenefitsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cartItemsList = cart.mapNotNull { (id, qty) ->
        val crop = cropListings.find { it.id == id }
        if (crop != null) Triple(crop, qty, qty * crop.pricePerKg) else null
    }

    val subtotal = cartItemsList.sumOf { it.third }
    val deliveryCharge = if (cartItemsList.isEmpty()) 0.0 else 120.0
    val platformFee = if (cartItemsList.isEmpty()) 0.0 else 50.0
    val sharedLogisticsCost = if (cartItemsList.isEmpty()) 0.0 else 80.0
    val totalAmount = if (cartItemsList.isEmpty()) 0.0 else (subtotal + deliveryCharge + platformFee + sharedLogisticsCost)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total procurement Amount:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("₹${String.format("%,.0f", totalAmount)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = EarthAmberPrimary)
                        }

                        Button(
                            onClick = { onNavigate("Checkout") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("checkout_cart_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            if (cart.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Text("Your Shopping Cart is Empty", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                        Text("Add wholesale crop lots from the marketplace to check out.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onBack() },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                        ) {
                            Text("Explore Marketplace")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cart items list
                    items(cartItemsList) { (crop, qty, cost) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(crop.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = FarmGreenDark)
                                        Text("Farmer: ${crop.farmerName} • Grade ${crop.qualityGrade}", fontSize = 11.sp, color = Color.Gray)
                                        Text("Warehouse Hub: ${crop.warehouseLocation}", fontSize = 10.sp, color = Color.Gray)
                                    }

                                    IconButton(
                                        onClick = { MarketplaceRepository.removeFromCart(crop.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { MarketplaceRepository.updateCartQuantity(crop.id, qty - 50) }) {
                                            Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = null, tint = FarmGreenPrimary)
                                        }
                                        Text("${qty.toInt()} kg", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        IconButton(onClick = {
                                            if (qty < crop.quantityKg) {
                                                MarketplaceRepository.updateCartQuantity(crop.id, qty + 50)
                                            } else {
                                                Toast.makeText(context, "Cannot exceed available stock!", Toast.LENGTH_SHORT).show()
                                            }
                                        }) {
                                            Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, tint = FarmGreenPrimary)
                                        }
                                    }

                                    Text("₹${String.format("%,.0f", cost)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EarthAmberPrimary)
                                }
                            }
                        }
                    }

                    // Shared Logistics Benefits Alert Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightGreenTint.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDeliveryBenefitsDialog = true }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = FarmGreenPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Shared Logistics Enabled", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenDark)
                                    Text("You are benefiting from shared route transportation, reducing platform dispatch costs.", fontSize = 10.sp, color = Color.DarkGray)
                                }
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Financial summary card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Procurement Financial Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                                HorizontalDivider()

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal cost of crops:", fontSize = 12.sp, color = Color.Gray)
                                    Text("₹${String.format("%,.0f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Platform matching service fee:", fontSize = 12.sp, color = Color.Gray)
                                    Text("₹${platformFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Wholesale standard delivery:", fontSize = 12.sp, color = Color.Gray)
                                    Text("₹${deliveryCharge.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Shared Logistics Fee:", fontSize = 12.sp, color = Color.Gray)
                                    Text("₹${sharedLogisticsCost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Estimated Grand Total:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("₹${String.format("%,.0f", totalAmount)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = EarthAmberPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showDeliveryBenefitsDialog) {
        Dialog(onDismissRequest = { showDeliveryBenefitsDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Shared route Logistics benefits", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FarmGreenDark)
                    HorizontalDivider()
                    Text(
                        text = "Because you are procuring crops stored in central hubs (such as Delhi Hub or Sonipat Silos), we coordinate shipping with other nearby orders on similar dispatch routes.\n\n" +
                                "This avoids dedicated single-truck dispatches, reducing carbon footprint, lowering wear-and-tear, and passing ₹80 route discounts directly to you.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { showDeliveryBenefitsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Understood")
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. BUYER CHECKOUT SCREEN
// ==========================================

@Composable
fun BuyerCheckoutView(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val cart by MarketplaceRepository.cartItems.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val savedAddressesList by MarketplaceRepository.savedAddresses.collectAsState()

    var selectedAddressIndex by remember { mutableStateOf(0) }
    var selectedPaymentMethod by remember { mutableStateOf("Wallet") }
    var errorMessage by remember { mutableStateOf("") }

    val cartItemsList = cart.mapNotNull { (id, qty) ->
        val crop = cropListings.find { it.id == id }
        if (crop != null) Triple(crop, qty, qty * crop.pricePerKg) else null
    }

    val subtotal = cartItemsList.sumOf { it.third }
    val deliveryCharge = if (cartItemsList.isEmpty()) 0.0 else 120.0
    val platformFee = if (cartItemsList.isEmpty()) 0.0 else 50.0
    val sharedLogisticsCost = if (cartItemsList.isEmpty()) 0.0 else 80.0
    val totalAmount = if (cartItemsList.isEmpty()) 0.0 else (subtotal + deliveryCharge + platformFee + sharedLogisticsCost)

    val currentBalance = MarketplaceRepository.getCurrentUserBalance()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Procurement", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Address selection section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("📍 Choose Dispatch Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            if (savedAddressesList.isNotEmpty()) {
                                savedAddressesList.forEachIndexed { idx, address ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selectedAddressIndex == idx) LightGreenTint.copy(alpha = 0.5f) else Color.Transparent)
                                            .clickable { selectedAddressIndex = idx }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedAddressIndex == idx,
                                            onClick = { selectedAddressIndex = idx }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(address, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            } else {
                                Text("No addresses saved. Navigate to Profile to add addresses.", fontSize = 11.sp, color = Color.Red)
                            }
                        }
                    }
                }

                // Payment method section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("💳 Choose Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            val payments = listOf(
                                Pair("Wallet", "Pre-funded Escrow Wallet (Balance: ₹${String.format("%,.0f", currentBalance)})"),
                                Pair("COD", "Cash On Delivery (Wholesale COD)"),
                                Pair("UPI", "Instant UPI Payment (Simulated GPay)"),
                                Pair("Banking", "Net Banking / Corporate Wire Transfer")
                            )

                            payments.forEach { (key, title) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedPaymentMethod == key) LightGreenTint.copy(alpha = 0.5f) else Color.Transparent)
                                        .clickable { selectedPaymentMethod = key }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedPaymentMethod == key,
                                        onClick = { selectedPaymentMethod = key }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(title, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                // Financial Summary summary
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Summary of Procurement Order", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            cartItemsList.forEach { (crop, qty, cost) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${crop.name} (${qty.toInt()} kg)", fontSize = 12.sp, color = Color.Gray)
                                    Text("₹${String.format("%,.0f", cost)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform matching fee:", fontSize = 12.sp, color = Color.Gray)
                                Text("₹${platformFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Shared Logistics Fee:", fontSize = 12.sp, color = Color.Gray)
                                Text("₹${sharedLogisticsCost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total checkout Amount:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${String.format("%,.0f", totalAmount)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = EarthAmberPrimary)
                            }
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Place order CTA button
                item {
                    Button(
                        onClick = {
                            if (savedAddressesList.isEmpty()) {
                                errorMessage = "Please add a dispatch address first before checkout."
                            } else if (selectedPaymentMethod == "Wallet" && currentBalance < totalAmount) {
                                errorMessage = "Insufficient wallet balance. Total amount: ₹${String.format("%,.0f", totalAmount)} but balance is ₹${String.format("%,.0f", currentBalance)}. Please deposit funds or choose cash on delivery."
                            } else {
                                errorMessage = ""
                                val targetAddress = savedAddressesList[selectedAddressIndex]
                                val ordersPlaced = MarketplaceRepository.checkoutCart(targetAddress, selectedPaymentMethod)
                                if (ordersPlaced.isNotEmpty()) {
                                    onNavigate("OrderSuccess")
                                } else {
                                    errorMessage = "Checkout execution failed. Ensure crop quantities fit active stock limits."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_place_order_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Place Procurement Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    )
}

// ==========================================
// 7. BUYER ORDER SUCCESS SCREEN
// ==========================================

@Composable
fun BuyerOrderSuccessView(
    onTrackOrder: (String) -> Unit,
    onBackHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .testTag("order_success_card")
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(FarmGreenPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Success", tint = FarmGreenPrimary, modifier = Modifier.size(44.dp))
                }

                Text(
                    text = "Procurement Successful!",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = FarmGreenDark
                )

                Text(
                    text = "Your wholesale order has been registered on the ledger.\n" +
                            "Pre-funded payment remains secure in Escrow and will only release upon final physical warehouse transfer confirmation.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order Number Reference:", fontSize = 11.sp, color = Color.Gray)
                        Text("ORD_${UUID.randomUUID().toString().take(6).uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fulfillment Route:", fontSize = 11.sp, color = Color.Gray)
                        Text("Shared Logistics Route Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Shipping ETA:", fontSize = 11.sp, color = Color.Gray)
                        Text("In 3 Days (Moisture Safe Cargo)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onTrackOrder("") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("success_track_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Text("Track Logistics Delivery", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Return to Procurement Home", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 8. BUYER ORDERS VIEW (Workflow Status + Tracking Timeline + CRUD Reviews)
// ==========================================

@Composable
fun BuyerOrdersView(
    selectedOrderId: String?,
    onClearSelection: () -> Unit
) {
    val orders by MarketplaceRepository.orders.collectAsState()
    val buyersOrders = orders.filter { it.buyerId == "B01" }

    var selectedOrderForTracking by remember { mutableStateOf<Order?>(null) }
    var activeFilter by remember { mutableStateOf("Active") }

    // If selectedOrderId is provided externally, find and set it immediately
    LaunchedEffect(selectedOrderId, orders) {
        if (!selectedOrderId.isNullOrEmpty()) {
            val ord = orders.find { it.id == selectedOrderId }
            if (ord != null) {
                selectedOrderForTracking = ord
            }
        }
    }

    val filteredOrders = buyersOrders.filter { ord ->
        when (activeFilter) {
            "Active" -> ord.status != "Completed" && ord.status != "Cancelled"
            "Completed" -> ord.status == "Completed"
            else -> true
        }
    }

    if (selectedOrderForTracking != null) {
        BuyerOrderTrackingTimeline(
            order = selectedOrderForTracking!!,
            onBack = {
                selectedOrderForTracking = null
                onClearSelection()
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(LightSurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Your Procurement Orders",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = FarmGreenDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Active", "Completed", "All").forEach { f ->
                            FilterChip(
                                selected = activeFilter == f,
                                onClick = { activeFilter = f },
                                label = { Text("$f Orders", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .padding(innerPadding)
                ) {
                    if (filteredOrders.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Inventory, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No orders matching this category", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredOrders) { order ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedOrderForTracking = order }
                                        .testTag("buyer_order_item_${order.id}"),
                                    colors = CardDefaults.cardColors(containerColor = LightSurface),
                                    border = BorderStroke(1.dp, BorderColor)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(order.cropName, fontWeight = FontWeight.Black, fontSize = 15.sp, color = FarmGreenDark)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        when (order.status) {
                                                            "Completed" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                                            "Cancelled" -> Color.Red.copy(alpha = 0.15f)
                                                            else -> EarthAmberPrimary.copy(alpha = 0.15f)
                                                        }
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = order.status,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = when (order.status) {
                                                        "Completed" -> FarmGreenPrimary
                                                        "Cancelled" -> Color.Red
                                                        else -> EarthAmberPrimary
                                                    }
                                                )
                                            }
                                        }

                                        HorizontalDivider()

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Order Reference:", fontSize = 11.sp, color = Color.Gray)
                                            Text(order.id, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Procurement Quantity:", fontSize = 11.sp, color = Color.Gray)
                                            Text("${order.quantityKg.toInt()} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Secured Escrow Cost:", fontSize = 11.sp, color = Color.Gray)
                                            Text("₹${String.format("%,.0f", order.totalAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EarthAmberPrimary)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Trade Farmer Name:", fontSize = 11.sp, color = Color.Gray)
                                            Text(order.farmerName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { selectedOrderForTracking = order },
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(vertical = 4.dp)
                                        ) {
                                            Text("Track Shipping & Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

// ==========================================
// 8b. BUYER ORDER LOGISTICS TRACKING TIMELINE
// ==========================================

@Composable
fun BuyerOrderTrackingTimeline(
    order: Order,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Rating and review states
    var isReviewing by remember { mutableStateOf(false) }
    var ratingState by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    val timelineStages = listOf(
        Pair("Order Placed", "Your procurement has been logged in ledger."),
        Pair("Accepted", "Farmer has confirmed lot release."),
        Pair("Preparing", "Assigned logistics executive verifying quality."),
        Pair("Ready for Pickup", "Lot sealed and moisture safe recorded."),
        Pair("Out for Delivery", "Assigned delivery executive moving cargo to destination."),
        Pair("Delivered", "Crops received at your warehouse.")
    )

    // Current index calculation based on order status
    val currentStageIndex = when(order.status) {
        "Pending Pickup" -> 1
        "Picked Up" -> 2
        "In Warehouse" -> 3
        "Out for Delivery" -> 4
        "Completed" -> 5
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logistics Timeline", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Summary card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tracking Reference:", fontSize = 11.sp, color = Color.Gray)
                                Text(order.id, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Trade Crop:", fontSize = 11.sp, color = Color.Gray)
                                Text(order.cropName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Current State Status:", fontSize = 11.sp, color = Color.Gray)
                                Text(order.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EarthAmberPrimary)
                            }
                        }
                    }
                }

                // Interactive Timeline List
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Delivery Tracking Stages", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            Spacer(modifier = Modifier.height(16.dp))

                            timelineStages.forEachIndexed { idx, stage ->
                                val isCompleted = idx <= currentStageIndex
                                val isCurrent = idx == currentStageIndex

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Bullet Indicator Column
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isCompleted) FarmGreenPrimary else Color.LightGray
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isCompleted) {
                                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            }
                                        }

                                        if (idx < timelineStages.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(48.dp)
                                                    .background(if (isCompleted) FarmGreenPrimary else Color.LightGray)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Stage Details
                                    Column {
                                        Text(
                                            text = stage.first,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isCurrent) FarmGreenPrimary else if (isCompleted) Color.DarkGray else Color.Gray
                                        )
                                        Text(
                                            text = stage.second,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // ASSIGNED EXECUTIVES & RACKS (Requirement display)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📋 Logistics Fulfilment Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pickup Executive ID:", fontSize = 12.sp, color = Color.Gray)
                                Text(order.pickupExecutiveId ?: "Satish Yadav (EXE-391)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Delivery Executive ID:", fontSize = 12.sp, color = Color.Gray)
                                Text(order.deliveryExecutiveId ?: "Rahul Dev (EXE-829)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Warehouse Rack Slot:", fontSize = 12.sp, color = Color.Gray)
                                Text("Section-B, Rack Bin ${10 + (order.id.hashCode() % 15).absoluteValue}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // SUBMIT REVIEW SECTION: Only if status is Completed
                if (order.status == "Completed") {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("✍️ Rate & Review Quality", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                                HorizontalDivider()

                                if (!isReviewing) {
                                    Button(
                                        onClick = { isReviewing = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                    ) {
                                        Text("Write Review Report")
                                    }
                                } else {
                                    Text("How would you rate this lot quality?", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    // Star Selector Row
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        (1..5).forEach { star ->
                                            IconButton(onClick = { ratingState = star }) {
                                                Icon(
                                                    imageVector = if (star <= ratingState) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = FieldGold,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = reviewComment,
                                        onValueChange = { reviewComment = it },
                                        placeholder = { Text("Write comments about moisture, dry ratios, sorting cleanliness, etc.") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 4
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { isReviewing = false },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel")
                                        }

                                        Button(
                                            onClick = {
                                                if (reviewComment.isBlank()) {
                                                    Toast.makeText(context, "Please enter a comment review.", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    MarketplaceRepository.submitReview(
                                                        cropName = order.cropName,
                                                        reviewerName = "Aman Gupta",
                                                        rating = ratingState,
                                                        comment = reviewComment
                                                    )
                                                    Toast.makeText(context, "Review Published Successfully!", Toast.LENGTH_LONG).show()
                                                    isReviewing = false
                                                    reviewComment = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                            modifier = Modifier.weight(1.5f)
                                        ) {
                                            Text("Publish Review")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

// ==========================================
// 9. BUYER PROFILE & CRUD ADDRESSES
// ==========================================

@Composable
fun BuyerProfileView(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val savedAddressesList by MarketplaceRepository.savedAddresses.collectAsState()
    val notifications by MarketplaceRepository.notifications.collectAsState()
    val buyers by MarketplaceRepository.buyers.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()

    var newAddressInput by remember { mutableStateOf("") }
    val currentBalance = buyers.find { it.id == currentUserId }?.walletBalance ?: 0.0

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buyer Procurement Profile", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header profile card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FarmGreenDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Aman Gupta", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                                Text("Delhi Grain Corp • Premium Procurement Buyer", fontSize = 11.sp, color = LightGreenTint)
                                Text("Procurement Member since Jan 2025", fontSize = 10.sp, color = LightGreenTint.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                // Wallet pre-funded deposit section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.testTag("buyer_wallet_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("💳 Ledger Pre-funded Escrow Wallet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Current Available Escrow Balance", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹${String.format("%,.2f", currentBalance)}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = FarmGreenPrimary)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FarmGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Active Escrow", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                }
                            }

                            HorizontalDivider()
                            Text("Fast Funds pre-deposit (Escrow Credit)", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5000.0, 10000.0, 25000.0).forEach { amt ->
                                    Button(
                                        onClick = {
                                            MarketplaceRepository.depositFunds(amt)
                                            Toast.makeText(context, "Added ₹${String.format("%,.0f", amt)} to Escrow balance!", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("+₹${String.format("%,.0f", amt)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Saved Addresses CRUD section (Addresses management)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("📍 Manage Procurement Delivery Addresses", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            // List of addresses
                            if (savedAddressesList.isNotEmpty()) {
                                savedAddressesList.forEach { addr ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(LightSurfaceVariant, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(addr, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                        IconButton(
                                            onClick = {
                                                MarketplaceRepository.removeSavedAddress(addr)
                                                Toast.makeText(context, "Address deleted", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            } else {
                                Text("No saved addresses. Please enter a delivery location below.", fontSize = 11.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider()

                            // Input to Add address
                            OutlinedTextField(
                                value = newAddressInput,
                                onValueChange = { newAddressInput = it },
                                label = { Text("Enter New Warehouse Address") },
                                placeholder = { Text("Enter full address details") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (newAddressInput.isNotBlank()) {
                                        MarketplaceRepository.addSavedAddress(newAddressInput)
                                        Toast.makeText(context, "Address Added successfully!", Toast.LENGTH_SHORT).show()
                                        newAddressInput = ""
                                    } else {
                                        Toast.makeText(context, "Address content cannot be empty", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AddLocation, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Delivery Address")
                            }
                        }
                    }
                }

                // 30 Notifications feed
                item {
                    val buyerNotifications = notifications.take(30)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔔 Procurement Notifications & Alerts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenDark)
                            HorizontalDivider()

                            if (buyerNotifications.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 250.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(buyerNotifications) { notif ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(LightSurfaceVariant, RoundedCornerShape(8.dp))
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = FarmGreenPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FarmGreenDark)
                                                Text(notif.body, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(notif.timestamp, fontSize = 8.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("No alerts active today.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Action buttons: Logout
                item {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("buyer_logout_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out of Procurement Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}
