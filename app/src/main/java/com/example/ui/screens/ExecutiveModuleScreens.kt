@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import kotlin.math.absoluteValue
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
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

// Color constants matching current styling
val LightAmberTint = Color(0xFFFFF8E7)
val SoftGrayBackground = Color(0xFFF7F9FA)

@Composable
fun ExecutiveLoginScreen(onSuccess: (rememberMe: Boolean) -> Unit) {
    var mobileNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpRequested by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val executives by MarketplaceRepository.pickupExecutives.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Field Executive Branding
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Cargo Truck Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "FarmLink Logistics",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Field Executive Verification Console",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Authentication Gate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { 
                            mobileNumber = it
                            errorMessage = ""
                        },
                        label = { Text("Registered Mobile Number") },
                        placeholder = { Text("e.g. 88811 00001") },
                        prefix = { Text("+91 ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("executive_phone_input")
                    )
                    
                    if (isOtpRequested) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { 
                                otpCode = it
                                errorMessage = ""
                            },
                            label = { Text("6-Digit Verification PIN") },
                            placeholder = { Text("Enter OTP code sent via SMS") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("executive_otp_input")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Simulation hint: Type any 6-digit code (e.g. 123456)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text(
                            text = "Keep executive portal active on this terminal",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (mobileNumber.isEmpty() || mobileNumber.length < 10) {
                                errorMessage = "Please enter a valid registered mobile number."
                            } else if (!isOtpRequested) {
                                isOtpRequested = true
                                Toast.makeText(context, "Verification PIN dispatched to +91 $mobileNumber", Toast.LENGTH_SHORT).show()
                            } else {
                                if (otpCode.length < 4) {
                                    errorMessage = "Please enter the verification PIN."
                                } else {
                                    // Search registered executive
                                    val formattedPhone = "+91 " + mobileNumber.trim()
                                    val matchedExec = executives.find { 
                                        it.phone.endsWith(mobileNumber.takeLast(5)) || it.phone == mobileNumber 
                                    } ?: executives.firstOrNull() // Default to first if none matched
                                    
                                    if (matchedExec != null) {
                                        MarketplaceRepository.currentUserId.value = matchedExec.id
                                        MarketplaceRepository.currentRole.value = "Pickup"
                                        onSuccess(rememberMe)
                                    } else {
                                        errorMessage = "Logistics registration not found. Please select a quick demo profile."
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("executive_login_button")
                    ) {
                        Text(
                            text = if (!isOtpRequested) "Request Secure OTP" else "Secure Verify & Enter Console",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick Access Section for Testing
            Text(
                text = "Quick Select Executive Profile (QA Test Utility)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Simulate login by clicking any registered logistics agent below:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                        items(executives) { exec ->
                            Card(
                                onClick = {
                                    val last10 = exec.phone.takeLast(10)
                                    mobileNumber = last10
                                    isOtpRequested = true
                                    otpCode = "123456"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = exec.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "ID: ${exec.id} • ${exec.area} • Vehicle: ${exec.vehicleNumber}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
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
fun PickupDashboardHome(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val pickupRequests by MarketplaceRepository.pickupRequests.collectAsState()
    val inspections by MarketplaceRepository.qualityInspections.collectAsState()
    val notifications by MarketplaceRepository.notifications.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val executives by MarketplaceRepository.pickupExecutives.collectAsState()
    
    val myExecInfo = executives.find { it.id == currentUserId } ?: executives.firstOrNull() ?: PickupExecutive("P01", "Satish Yadav", "+91 88811 00001", "Zone A Delhi NCR", "DL-1L-AA-2342", 0)

    val myAssignedTasks = pickupRequests.filter { it.pickupExecutiveId == currentUserId }
    val pendingInspections = myAssignedTasks.count { it.status == "Assigned" || it.status == "On The Way" || it.status == "Reached Farm" }
    val completedInspections = myAssignedTasks.count { it.status.contains("Approved") || it.status.contains("Rejected") || it.status == "Picked Up" || it.status == "At Warehouse" }
    
    val myNotifications = notifications.filter { it.userId == currentUserId || it.userRole == "Pickup" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Profile & Vehicle Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = myExecInfo.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Primary Zone: ${myExecInfo.area}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Truck: ${myExecInfo.vehicleNumber}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                    IconButton(
                        onClick = { onNavigate("Profile") },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings Profile")
                    }
                }
            }
        }

        // Stats Row Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = myAssignedTasks.size.toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Today's Routes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pendingInspections.toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = Color(0xFFE28743)
                        )
                        Text(
                            text = "Pending Quality",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = completedInspections.toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Verified Stocks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Today's Route Schedule
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Field Route Schedule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val dummyRoute = listOf(
                        Triple("08:00 AM", "Nangal Village Farm Gate A", "Completed"),
                        Triple("11:30 AM", "Chhatarpur Seed Silo Area 3", "Pending"),
                        Triple("02:00 PM", "Hardoi Crop Mandi Yard", "Pending"),
                        Triple("05:30 PM", "Delhi Agri-Silo Central Warehouse", "Pending")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        dummyRoute.forEachIndexed { index, (time, location, routeStatus) ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (routeStatus == "Completed") Color(0xFF2E7D32) else MaterialTheme.colorScheme.primaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (routeStatus == "Completed") Icons.Default.Check else Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (routeStatus == "Completed") Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    if (index < dummyRoute.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(24.dp)
                                                .background(MaterialTheme.colorScheme.outlineVariant)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = time,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (routeStatus == "Completed") Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = location,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Executive Notifications
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Logistical Updates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${myNotifications.size} Logs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (myNotifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No recent logistical notifications.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            myNotifications.take(4).forEach { notif ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SoftGrayBackground),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = notif.body,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
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
fun PickupTasksView() {
    val pickupRequests by MarketplaceRepository.pickupRequests.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Browse Assignments, 1: My Active Logs
    var selectedTaskForConsole by remember { mutableStateOf<PickupRequest?>(null) }
    
    val browseTasks = pickupRequests.filter { 
        it.pickupExecutiveId != currentUserId && it.status == "Pending" 
    }
    
    val myActiveTasks = pickupRequests.filter { 
        it.pickupExecutiveId == currentUserId && it.status != "At Warehouse" && it.status != "Cancelled"
    }

    if (selectedTaskForConsole != null) {
        TaskManagementConsole(
            task = selectedTaskForConsole!!,
            onBack = { 
                selectedTaskForConsole = null 
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGrayBackground)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Browse Pools (${browseTasks.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Active Logs (${myActiveTasks.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }
            
            if (selectedTab == 0) {
                // Browse Assignments
                if (browseTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "All collection assignments are currently claimed.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(browseTasks) { req ->
                            // Deterministic mock distance based on ID code
                            val mockDistance = 5.0 + (req.id.hashCode().absoluteValue % 20) / 1.5
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "ID: ${req.id}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = "${String.format("%.1f", mockDistance)} km away",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = req.cropName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Farmer: ${req.farmerName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Village: ${req.farmAddress}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Cargo Size: ${req.quantityKg.toInt()} kg",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                Toast.makeText(context, "Assignment Reject recorded", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reject Pool")
                                        }
                                        
                                        Button(
                                            onClick = {
                                                // Accept assignment
                                                MarketplaceRepository.updatePickupStatus(req.id, "Assigned")
                                                // Claim the task under our ID
                                                MarketplaceRepository.assignPickupExecutive(req.id, currentUserId)
                                                Toast.makeText(context, "Cargo assignment successfully claimed!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("accept_assignment_${req.id}")
                                        ) {
                                            Text("Claim Task")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // My Active Logs
                if (myActiveTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "You do not have any active claimed tasks.\nGo to Browse Pools to claim your cargo route assignments.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(myActiveTasks) { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "ID: ${req.id}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = when(req.status) {
                                                    "Assigned" -> Color(0xFFE3F2FD)
                                                    "On The Way" -> Color(0xFFFFF3E0)
                                                    "Reached Farm" -> Color(0xFFF1F8E9)
                                                    "Inspection Approved" -> Color(0xFFE8F5E9)
                                                    "Inspection Rejected" -> Color(0xFFFFEBEE)
                                                    "Picked Up" -> Color(0xFFEDE7F6)
                                                    "Reached Warehouse" -> Color(0xFFE0F7FA)
                                                    "Stock Submitted" -> Color(0xFFFFFDE7)
                                                    else -> MaterialTheme.colorScheme.primaryContainer
                                                }
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = req.status.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = when(req.status) {
                                                    "Assigned" -> Color(0xFF1565C0)
                                                    "On The Way" -> Color(0xFFE65100)
                                                    "Reached Farm" -> Color(0xFF2E7D32)
                                                    "Inspection Approved" -> Color(0xFF2E7D32)
                                                    "Inspection Rejected" -> Color(0xFFC62828)
                                                    "Picked Up" -> Color(0xFF4527A0)
                                                    "Reached Warehouse" -> Color(0xFF00838F)
                                                    "Stock Submitted" -> Color(0xFFFBC02D)
                                                    else -> MaterialTheme.colorScheme.primary
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = req.cropName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Farmer: ${req.farmerName} • Site: ${req.farmAddress}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Cargo Size: ${req.quantityKg.toInt()} kg",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { selectedTaskForConsole = req },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("manage_task_${req.id}")
                                    ) {
                                        Icon(Icons.Default.Settings, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open Cargo Console")
                                    }
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
fun TaskManagementConsole(
    task: PickupRequest,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showInspectionForm by remember { mutableStateOf(false) }
    var showOtpVerification by remember { mutableStateOf(false) }
    
    // Track localized visual scan animation
    var isScanningCamera by remember { mutableStateOf(false) }
    
    // Inspection form fields
    var moistureInput by remember { mutableStateOf("12.5") }
    var qualityScoreInput by remember { mutableStateOf("88") }
    var selectedGrade by remember { mutableStateOf("Grade A") }
    var inspectionNotes by remember { mutableStateOf("") }
    
    // OTP verification fields
    var verificationPinInput by remember { mutableStateOf("") }
    var generatedOtpCode by remember { mutableStateOf("2309") }

    // Re-collect dynamic requests state to see updates
    val pickupRequests by MarketplaceRepository.pickupRequests.collectAsState()
    val liveTask = pickupRequests.find { it.id == task.id } ?: task

    if (showInspectionForm) {
        // Render Inspection Form Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showInspectionForm = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Quality Verification Cargo Check",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Crop Stock: ${liveTask.cropName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Logistics Unit ID: ${liveTask.id} • Farmer: ${liveTask.farmerName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scan Camera Simulation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Visual Cargo Capture (Simulation)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isScanningCamera) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Analyzing visual grain clusters...",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap Scan below to capture crop validation photographs",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            isScanningCamera = true
                            // Reset scanning state after 1.5 seconds
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isScanningCamera = false
                                Toast.makeText(context, "Cargo photos successfully captured & stored!", Toast.LENGTH_SHORT).show()
                            }, 1500)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Smart Scan Camera")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sliders & Verification Parameters Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Physical Laboratory Benchmarks",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Moisture level slider
                    var moistureVal by remember { mutableStateOf(12.5f) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Moisture Level (%)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${String.format("%.1f", moistureVal)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = moistureVal,
                        onValueChange = { 
                            moistureVal = it
                            moistureInput = String.format("%.1f", it)
                        },
                        valueRange = 5f..30f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Ideal range: 11.0% - 14.5% moisture.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quality score slider
                    var qualityVal by remember { mutableStateOf(85f) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Laboratory Quality Index", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${qualityVal.toInt()} / 100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = qualityVal,
                        onValueChange = { 
                            qualityVal = it
                            qualityScoreInput = it.toInt().toString()
                            selectedGrade = when {
                                it >= 88f -> "Grade A"
                                it >= 75f -> "Grade B"
                                else -> "Grade C"
                            }
                        },
                        valueRange = 50f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Auto-assign Quality Category: $selectedGrade",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Field notes textfield
                    OutlinedTextField(
                        value = inspectionNotes,
                        onValueChange = { inspectionNotes = it },
                        label = { Text("Quality Inspector Field Observations") },
                        placeholder = { Text("Describe color, sorting parameters, size clusters, or dry status...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Decision Blocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Submit Reject
                        MarketplaceRepository.submitQualityInspection(
                            requestId = liveTask.id,
                            grade = selectedGrade,
                            qualityScore = qualityScoreInput.toDoubleOrNull() ?: 50.0,
                            moistureLevel = moistureInput.toDoubleOrNull() ?: 15.0,
                            executiveNotes = inspectionNotes.ifEmpty { "Cargo inspection rejected. Extreme moisture variance detected." },
                            isApproved = false
                        )
                        Toast.makeText(context, "Cargo rejected. Rejection report logged.", Toast.LENGTH_LONG).show()
                        showInspectionForm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f).testTag("btn_reject_cargo")
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject Cargo")
                }
                
                Button(
                    onClick = {
                        // Submit Approve
                        MarketplaceRepository.submitQualityInspection(
                            requestId = liveTask.id,
                            grade = selectedGrade,
                            qualityScore = qualityScoreInput.toDoubleOrNull() ?: 85.0,
                            moistureLevel = moistureInput.toDoubleOrNull() ?: 12.5,
                            executiveNotes = inspectionNotes.ifEmpty { "Passed Physical Cargo Scan. Granule size uniform, moisture metrics inside safety baseline, certified dry-silo ready." },
                            isApproved = true
                        )
                        Toast.makeText(context, "Cargo certified! Verified Badge generated.", Toast.LENGTH_LONG).show()
                        showInspectionForm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1f).testTag("btn_approve_cargo")
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Certify & Approve")
                }
            }
        }
    } else if (showOtpVerification) {
        // OTP Verification Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showOtpVerification = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Farmer Pick-Up PIN Verification",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Authorized Cargo OTP",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Text(
                text = "Enter the 4-digit cargo transfer OTP generated on the farmer's mobile interface.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = verificationPinInput,
                        onValueChange = { verificationPinInput = it },
                        label = { Text("4-Digit Farmer Pin") },
                        placeholder = { Text("e.g. 2309") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("farmer_otp_verification_input")
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Farmer Authorization Code: $generatedOtpCode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (verificationPinInput == generatedOtpCode || verificationPinInput == "1234") {
                        // Mark Picked Up
                        MarketplaceRepository.updatePickupStatus(liveTask.id, "Picked Up")
                        Toast.makeText(context, "OTP verified. Cargo Loaded to Vehicle DL-1L-AA-2342", Toast.LENGTH_LONG).show()
                        showOtpVerification = false
                    } else {
                        Toast.makeText(context, "Invalid OTP pin. Farmer must provide valid code.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_cargo_otp_btn")
            ) {
                Text("Confirm Transfer & Load Cargo")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pickup Digital Receipt
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightAmberTint),
                border = BorderStroke(1.dp, Color(0xFFE28743).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Digital Collection Receipt Preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Transaction ID: RCP-PKP-${liveTask.id.takeLast(4)}", fontSize = 11.sp)
                    Text(text = "Produce: ${liveTask.cropName}", fontSize = 11.sp)
                    Text(text = "Certified Size: ${liveTask.quantityKg.toInt()} Kg", fontSize = 11.sp)
                    Text(text = "Assigned Vehicle: DL-1L-AA-2342", fontSize = 11.sp)
                }
            }
        }
    } else {
        // Main Management Console Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGrayBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Cargo Flight Console",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Console ID: ${liveTask.id}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cargo Status Roadmap Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fulfillment Progress Roadmap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val steps = listOf("Assigned", "On The Way", "Reached Farm", "Inspection Approved", "Picked Up", "Reached Warehouse", "Stock Submitted", "At Warehouse")
                    val currentStepIdx = steps.indexOf(liveTask.status).coerceAtLeast(0)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        steps.forEachIndexed { idx, step ->
                            val isCompleted = idx < currentStepIdx
                            val isActive = idx == currentStepIdx
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isActive -> MaterialTheme.colorScheme.primary
                                                isCompleted -> Color(0xFF2E7D32)
                                                else -> MaterialTheme.colorScheme.outlineVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                                    color = when {
                                        isActive -> MaterialTheme.colorScheme.primary
                                        isCompleted -> Color(0xFF2E7D32)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Farmer & Farm Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Farmer Collection Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Farmer Name: ${liveTask.farmerName}", fontSize = 12.sp)
                    Text(text = "Collection Point: ${liveTask.farmAddress}", fontSize = 12.sp)
                    Text(text = "Crop Item: ${liveTask.cropName}", fontSize = 12.sp)
                    Text(text = "Dispatched Cargo weight: ${liveTask.quantityKg.toInt()} kg", fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Console Dynamic Action Buttons
            Text(
                text = "Console Pilot Command Hub",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            when(liveTask.status) {
                "Assigned" -> {
                    Button(
                        onClick = {
                            MarketplaceRepository.updatePickupStatus(liveTask.id, "On The Way")
                            Toast.makeText(context, "Route started! Transit GPS logged.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("start_journey_btn")
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Journey to Farm")
                    }
                }
                "On The Way" -> {
                    Button(
                        onClick = {
                            MarketplaceRepository.updatePickupStatus(liveTask.id, "Reached Farm")
                            Toast.makeText(context, "Arrived at Collection Point!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("reached_farm_btn")
                    ) {
                        Icon(Icons.Default.PinDrop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Arrival at Farm")
                    }
                }
                "Reached Farm" -> {
                    Button(
                        onClick = {
                            showInspectionForm = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("begin_inspection_btn")
                    ) {
                        Icon(Icons.Default.Grading, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Begin Crop Quality Verification")
                    }
                }
                "Inspection Approved" -> {
                    Button(
                        onClick = {
                            showOtpVerification = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("proceed_pickup_btn")
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify OTP Pin & Load Cargo")
                    }
                }
                "Inspection Rejected" -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Cargo was rejected because laboratory standards were not met. Closing logs.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "Picked Up" -> {
                    Button(
                        onClick = {
                            MarketplaceRepository.updatePickupStatus(liveTask.id, "Reached Warehouse")
                            Toast.makeText(context, "Arrived at central warehouse silo!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("reached_warehouse_btn")
                    ) {
                        Icon(Icons.Default.Warehouse, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Arrival at Warehouse")
                    }
                }
                "Reached Warehouse" -> {
                    Button(
                        onClick = {
                            MarketplaceRepository.updatePickupStatus(liveTask.id, "Stock Submitted")
                            Toast.makeText(context, "Stock handoff registered. Awaiting manager approval.", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("submit_stock_btn")
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Stock Cargo")
                    }
                }
                "Stock Submitted" -> {
                    Button(
                        onClick = {
                            // Call At Warehouse status to trigger database sync
                            MarketplaceRepository.updatePickupStatus(liveTask.id, "At Warehouse")
                            Toast.makeText(context, "Warehouse handoff completed! Inventory updated.", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("finalize_warehouse_btn")
                    ) {
                        Icon(Icons.Default.FactCheck, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalize Warehouse Handoff")
                    }
                }
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = "Logistics task successfully closed & finalized on active files.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val executives by MarketplaceRepository.pickupExecutives.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val myExecInfo = executives.find { it.id == currentUserId } ?: executives.firstOrNull() ?: PickupExecutive("P01", "Satish Yadav", "+91 88811 00001", "Zone A Delhi NCR", "DL-1L-AA-2342", 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Executive Settings Portal",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = myExecInfo.name,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )
        Text(
            text = "FarmLink Logistics Employee",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Professional Logistics Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Logistics ID", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = myExecInfo.id, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Assigned Mobile", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = myExecInfo.phone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Registered Truck", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = myExecInfo.vehicleNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Security Clearances", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "LEVEL 2 LAB CERTIFIED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("executive_logout_btn")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log out of Portal", fontWeight = FontWeight.Bold)
        }
    }
}
