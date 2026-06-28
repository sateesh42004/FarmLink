package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.collectAsState
import com.example.data.*

// Color Constants consistent with FarmLink marketplace branding
private val FarmGreenContainer = Color(0xFFE8F5E9)
private val EarthAmberContainer = Color(0xFFFFF3E0)
private val SoftSlateBg = Color(0xFFF1F5F9)

// ==========================================
// 1. ADMIN LOGIN SCREEN
// ==========================================

@Composable
fun AdminLoginScreen(onSuccess: (Boolean) -> Unit) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin") }
    var rememberMe by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftSlateBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .testTag("admin_login_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Shield Logo
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Admin Security Shield",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FarmLink Admin Console",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Global Operations & Marketplace Command",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username Input
                OutlinedTextField(
                    value = username,
                    onValueChange = { 
                        username = it
                        showError = false 
                    },
                    label = { Text("Username / Email") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_username_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        showError = false 
                    },
                    label = { Text("Secret Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        modifier = Modifier.testTag("admin_remember_me_checkbox")
                    )
                    Text(
                        text = "Remember secret Admin session",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Invalid credentials. Authorized personnel only.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.trim() == "admin" && password.trim() == "admin") {
                            onSuccess(rememberMe)
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_login_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Authenticate & Enter Hub", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ==========================================
// 2. ADMIN MAIN CONSOLE & NAVIGATION
// ==========================================

@Composable
fun AdminDashboardHome(initialTab: String = "Dashboard", onLogout: () -> Unit) {
    var selectedSubTab by remember(initialTab) { mutableStateOf(initialTab) }
    
    val tabs = listOf(
        "Dashboard", 
        "Farmers", 
        "Buyers", 
        "Logistics", 
        "Warehouses", 
        "Crop Listings", 
        "Orders Log", 
        "Financial Ledger", 
        "Settings Hub"
    )

    val tabIndex = tabs.indexOf(selectedSubTab).coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize().background(SoftSlateBg)) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            edgePadding = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_sub_tabs"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEach { title ->
                Tab(
                    selected = selectedSubTab == title,
                    onClick = { selectedSubTab = title },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_$title")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                "Dashboard" -> AdminDashboardOverviewScreen(onLogout)
                "Farmers" -> AdminFarmersManagementScreen()
                "Buyers" -> AdminBuyersManagementScreen()
                "Logistics" -> AdminLogisticsManagementScreen()
                "Warehouses" -> AdminWarehousesManagementScreen()
                "Crop Listings" -> AdminCropManagementScreen()
                "Orders Log" -> AdminOrdersModerationView()
                "Financial Ledger" -> AdminFinancialDashboardScreen()
                "Settings Hub" -> AdminSettingsAndControlScreen()
            }
        }
    }
}

// ==========================================
// 3. ADMIN DASHBOARD OVERVIEW SCREEN
// ==========================================

@Composable
fun AdminDashboardOverviewScreen(onLogout: () -> Unit) {
    val orders by MarketplaceRepository.orders.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val buyers by MarketplaceRepository.buyers.collectAsState()
    val executives by MarketplaceRepository.pickupExecutives.collectAsState()
    val warehouses by MarketplaceRepository.warehouseManagers.collectAsState()
    val inventory by MarketplaceRepository.warehouseInventory.collectAsState()
    val activities by MarketplaceRepository.recentActivities.collectAsState()

    // Financial Metrics
    val totalRevenue = orders.filter { it.status == "Completed" }.sumOf { it.totalAmount }
    val commissionEarnings = totalRevenue * 0.02
    val systemWalletBalances = farmers.sumOf { it.walletBalance } + buyers.sumOf { it.walletBalance }

    // Logistics metrics
    val pendingPickupsCount = cropListings.count { it.status in listOf("Pickup Requested", "Executive Assigned") }
    val activeOrdersCount = orders.count { it.status != "Completed" && it.status != "Cancelled" }
    val completedOrdersCount = orders.count { it.status == "Completed" }

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Executive Command Portal",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Monitoring ${farmers.size} Farmers | ${buyers.size} Wholesalers | ${warehouses.size} Silo Stations",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showBroadcastDialog = true },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                .testTag("admin_broadcast_button")
                        ) {
                            Icon(Icons.Default.Campaign, "Broadcast Announcement", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(
                            onClick = { onLogout() },
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                                .testTag("admin_logout_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = Color.Red)
                        }
                    }
                }
            }
        }

        // Live Summary Stat Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Live Operational Indices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminSummaryStatCard(
                        title = "Platform GMV",
                        value = "₹${totalRevenue.toInt()}",
                        subtitle = "Escrows Cleared",
                        icon = Icons.Default.MonetizationOn,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminSummaryStatCard(
                        title = "Platform Cut (2%)",
                        value = "₹${commissionEarnings.toInt()}",
                        subtitle = "Treasury Accumulation",
                        icon = Icons.Default.Percent,
                        color = EarthAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminSummaryStatCard(
                        title = "Global Users",
                        value = "${farmers.size + buyers.size + executives.size}",
                        subtitle = "${farmers.size} Farm | ${buyers.size} Wholesale",
                        icon = Icons.Default.Groups,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminSummaryStatCard(
                        title = "Total Listings",
                        value = "${cropListings.size}",
                        subtitle = "${cropListings.count { it.status == "Published" }} Live on Board",
                        icon = Icons.Default.Grass,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminSummaryStatCard(
                        title = "Active Shipments",
                        value = "$activeOrdersCount Orders",
                        subtitle = "$pendingPickupsCount Awaiting Pickup",
                        icon = Icons.Default.LocalShipping,
                        color = EarthAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminSummaryStatCard(
                        title = "Silo Stock Vol",
                        value = "${inventory.sumOf { it.availableQuantity }.toInt()} Kg",
                        subtitle = "${inventory.count { it.status == "Low Stock" }} Silos Low",
                        icon = Icons.Default.Warehouse,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Analytics Canvas Graphs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Logistics volume - Active Orders vs Completed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Native Custom Canvas Line/Area Graph
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            
                            // Draw grid lines
                            for (i in 1..4) {
                                val y = height * i / 5
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.4f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            // Dynamic data points based on order volumes
                            val points = listOf(
                                Offset(width * 0.05f, height * 0.85f),
                                Offset(width * 0.2f, height * 0.65f),
                                Offset(width * 0.35f, height * 0.75f),
                                Offset(width * 0.5f, height * 0.45f),
                                Offset(width * 0.65f, height * 0.5f),
                                Offset(width * 0.8f, height * 0.25f),
                                Offset(width * 0.95f, height * 0.3f)
                            )

                            val path = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (idx in 1 until points.size) {
                                    lineTo(points[idx].x, points[idx].y)
                                }
                            }

                            // Fill Area under the graph
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(points.last().x, height)
                                lineTo(points.first().x, height)
                                close()
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(FarmGreenPrimary.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )

                            drawPath(
                                path = path,
                                color = FarmGreenPrimary,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Draw point coordinates circles
                            points.forEach { pt ->
                                drawCircle(
                                    color = FarmGreenPrimary,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mon", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Tue", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Wed", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Thu", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Fri", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Sat", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Sun (Today)", fontSize = 9.sp, color = FarmGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Admin Controls (Broadcast announcements, Reports)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Commands & Intelligence Reports",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showBroadcastDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_broadcasting_trigger"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Campaign, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Broadcast", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_reports_trigger"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Assessment, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reports Hub", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Feed Audit Log
        item {
            Text(
                text = "Live Operations Stream",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(activities.take(8)) { act ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when (act.category) {
                                    "Farmer" -> FarmGreenContainer
                                    "Buyer" -> Color(0xFFE3F2FD)
                                    "Logistics" -> EarthAmberContainer
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (act.category) {
                                "Farmer" -> Icons.Default.Agriculture
                                "Buyer" -> Icons.Default.Storefront
                                "Logistics" -> Icons.Default.LocalShipping
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when (act.category) {
                                "Farmer" -> FarmGreenPrimary
                                "Buyer" -> Color(0xFF1976D2)
                                "Logistics" -> EarthAmberPrimary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = act.description,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${act.category} • ${act.relativeTime}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    // Modal Broadcaster announcement dialog
    if (showBroadcastDialog) {
        var broadTitle by remember { mutableStateOf("System Mandi Pricing Update") }
        var broadBody by remember { mutableStateOf("Attention Farmers: New Wheat base trading range index updated in your local mandi widget.") }
        var targetRole by remember { mutableStateOf("All") }
        val roles = listOf("All", "Farmer", "Buyer", "Pickup", "Warehouse")

        Dialog(onDismissRequest = { showBroadcastDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("admin_broadcast_modal"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Broadcast System Announcement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showBroadcastDialog = false }) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }

                    OutlinedTextField(
                        value = broadTitle,
                        onValueChange = { broadTitle = it },
                        label = { Text("Announcement Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("announcement_title_field")
                    )

                    OutlinedTextField(
                        value = broadBody,
                        onValueChange = { broadBody = it },
                        label = { Text("Message Body") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("announcement_body_field")
                    )

                    Column {
                        Text("Recipient Audience", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(roles) { role ->
                                val isSelected = targetRole == role
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { targetRole = role },
                                    label = { Text(role, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("audience_$role")
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            MarketplaceRepository.addNotification(
                                userId = if (targetRole == "All") "ALL" else targetRole,
                                role = targetRole,
                                title = broadTitle,
                                body = broadBody
                            )
                            MarketplaceRepository.addActivity(
                                description = "System Announcement Broadcasted: \"$broadTitle\"",
                                category = "Admin"
                            )
                            showBroadcastDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("broadcast_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                    ) {
                        Text("Broadcast Announcement Live", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal Reports Generator
    if (showReportDialog) {
        Dialog(onDismissRequest = { showReportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Platform Analytics Reports Generator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showReportDialog = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }

                    val itemsReportList = listOf(
                        Triple("Sales Audit Report", "Aggregates trading logs & GMVs", Icons.Default.CurrencyExchange),
                        Triple("Inventory Aging Status", "Details crop moisture levels & shelf lifes", Icons.Default.Assessment),
                        Triple("Farmer Activity Metrics", "Analyzes seller logs & active wallets", Icons.Default.AccountCircle),
                        Triple("Logistics Logistics SLA", "Monitors driver dispatch speeds & delays", Icons.Default.LocalShipping),
                        Triple("Warehouse Utilization Report", "Tracks physical silos volumetrics", Icons.Default.Warehouse)
                    )

                    itemsReportList.forEach { (title, desc, icon) ->
                        var isDownloaded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDownloaded = true },
                            colors = CardDefaults.cardColors(containerColor = SoftSlateBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, null, tint = FarmGreenPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(desc, fontSize = 10.sp, color = Color.Gray)
                                }
                                if (isDownloaded) {
                                    Icon(Icons.Default.Check, "Success", tint = FarmGreenPrimary)
                                } else {
                                    Icon(Icons.Default.Download, "Download Report", tint = Color.Gray, modifier = Modifier.size(16.dp))
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
fun AdminSummaryStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

// ==========================================
// 4. FARMER MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminFarmersManagementScreen() {
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val listings by MarketplaceRepository.cropListings.collectAsState()
    var searchVal by remember { mutableStateOf("") }
    
    // Maintain a local dummy map of suspended states
    var suspendedFarmers by remember { mutableStateOf(setOf<String>()) }
    var inspectingFarmer by remember { mutableStateOf<Farmer?>(null) }

    val filteredFarmers = farmers.filter {
        it.name.contains(searchVal, ignoreCase = true) || it.village.contains(searchVal, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search & Filters Header
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            placeholder = { Text("Search Farmer Name or Village...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_farmers_search"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredFarmers) { farmer ->
                val isSuspended = suspendedFarmers.contains(farmer.id)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(farmer.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = farmer.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(farmer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Village: ${farmer.village} | Rating: ${farmer.rating} ⭐", fontSize = 11.sp, color = Color.Gray)
                            Text("Active Listings: ${listings.count { it.farmerId == farmer.id }} | Wallet: ₹${farmer.walletBalance.toInt()}", fontSize = 10.sp, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSuspended) Color.Red.copy(alpha = 0.1f) else FarmGreenContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isSuspended) "Suspended" else "Active",
                                    color = if (isSuspended) Color.Red else FarmGreenPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                IconButton(
                                    onClick = { inspectingFarmer = farmer },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, "Inspect Profile", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = {
                                        suspendedFarmers = if (isSuspended) {
                                            suspendedFarmers - farmer.id
                                        } else {
                                            suspendedFarmers + farmer.id
                                        }
                                        MarketplaceRepository.addActivity(
                                            description = if (isSuspended) "Restored farmer ${farmer.name}" else "Suspended farmer ${farmer.name}",
                                            category = "Admin"
                                        )
                                    },
                                    modifier = Modifier.size(24.dp).testTag("suspend_btn_${farmer.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isSuspended) Icons.Default.SettingsBackupRestore else Icons.Default.Block,
                                        contentDescription = "Toggle Status",
                                        tint = if (isSuspended) FarmGreenPrimary else Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Inspect profile dialog
    if (inspectingFarmer != null) {
        val farmer = inspectingFarmer!!
        Dialog(onDismissRequest = { inspectingFarmer = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("farmer_inspect_modal"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Farmer Security Credentials Inspector", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenPrimary)
                    HorizontalDivider()
                    Text("Farmer ID: ${farmer.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Legal Full Name: ${farmer.name}", fontSize = 12.sp)
                    Text("District Village: ${farmer.village}", fontSize = 12.sp)
                    Text("Secure Phone Link: ${farmer.phone}", fontSize = 12.sp)
                    Text("Official Joined: ${farmer.joinedDate}", fontSize = 12.sp)
                    Text("Wallet Escrow Capital: ₹${farmer.walletBalance.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    Text("Trust Rating Score: ${farmer.rating} / 5.0 ⭐", fontSize = 12.sp)
                    HorizontalDivider()
                    Button(
                        onClick = { inspectingFarmer = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Profile Integrity")
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. BUYER MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminBuyersManagementScreen() {
    val buyers by MarketplaceRepository.buyers.collectAsState()
    val orders by MarketplaceRepository.orders.collectAsState()
    val reviews by MarketplaceRepository.customerReviews.collectAsState()
    var searchVal by remember { mutableStateOf("") }
    var selectedBuyerDetails by remember { mutableStateOf<Buyer?>(null) }

    val filteredBuyers = buyers.filter {
        it.name.contains(searchVal, ignoreCase = true) || it.companyName.contains(searchVal, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            placeholder = { Text("Search Buyer or Company...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_buyers_search"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredBuyers) { buyer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(buyer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Company: ${buyer.companyName} | Location: ${buyer.city}", fontSize = 11.sp, color = Color.Gray)
                            Text("Orders Placed: ${orders.count { it.buyerId == buyer.id }} | Wallet: ₹${buyer.walletBalance.toInt()}", fontSize = 10.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = { selectedBuyerDetails = buyer },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("inspect_buyer_${buyer.id}")
                        ) {
                            Text("View", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (selectedBuyerDetails != null) {
        val buyer = selectedBuyerDetails!!
        val buyerOrders = orders.filter { it.buyerId == buyer.id }
        val buyerReviews = reviews.filter { it.reviewerName == buyer.name }

        Dialog(onDismissRequest = { selectedBuyerDetails = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("buyer_details_modal"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Wholesaler Profile & Order Log", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenPrimary)
                    HorizontalDivider()
                    Text("Buyer ID: ${buyer.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("Company: ${buyer.companyName}", fontSize = 12.sp)
                    Text("Contact: ${buyer.phone} | City: ${buyer.city}", fontSize = 12.sp)
                    Text("Deposit Capital: ₹${buyer.walletBalance.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    Text("Joined Date: ${buyer.joinedDate}", fontSize = 12.sp)

                    HorizontalDivider()
                    Text("Orders Completed History (${buyerOrders.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    buyerOrders.take(3).forEach { order ->
                        Text("• Order #${order.id}: ${order.cropName} (${order.quantityKg.toInt()} kg) - ₹${order.totalAmount.toInt()} [${order.status}]", fontSize = 10.sp)
                    }

                    HorizontalDivider()
                    Text("Reviews Shared (${buyerReviews.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    buyerReviews.take(2).forEach { r ->
                        Text("• \"${r.comment}\" (${r.rating}⭐)", fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { selectedBuyerDetails = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. LOGISTICS EXECUTIVE MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminLogisticsManagementScreen() {
    val executives by MarketplaceRepository.pickupExecutives.collectAsState()
    val orders by MarketplaceRepository.orders.collectAsState()
    val deliveries by MarketplaceRepository.deliveryRequests.collectAsState()
    var searchVal by remember { mutableStateOf("") }

    val filteredExecs = executives.filter {
        it.name.contains(searchVal, ignoreCase = true) || it.area.contains(searchVal, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            placeholder = { Text("Search Drivers, Vehicles or Cities...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_logistics_search"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExecs) { exec ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(EarthAmberContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalShipping, null, tint = EarthAmberPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(exec.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Logistics Zone: ${exec.area} | Vehicle: ${exec.vehicleNumber}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EarthAmberContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("SLA Verified", color = EarthAmberPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pending Pickups: ${exec.pendingPickupsCount}", fontSize = 11.sp, color = Color.Gray)
                            Text("Mobile contact: ${exec.phone}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. WAREHOUSE MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminWarehousesManagementScreen() {
    val warehouses by MarketplaceRepository.warehouseManagers.collectAsState()
    val inventory by MarketplaceRepository.warehouseInventory.collectAsState()
    val dispatches by MarketplaceRepository.warehouseDispatches.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Centralized Silo Facilities & Occupancies",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(warehouses) { wh ->
            val whInventory = inventory.filter { it.warehouseId == wh.id }
            val totalQty = whInventory.sumOf { it.availableQuantity }
            val lowStockCount = whInventory.count { it.status == "Low Stock" }
            val expiringCount = whInventory.count { it.status == "Expiring" }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warehouse, null, tint = FarmGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(wh.warehouseName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("ID: ${wh.id}", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Facility Master: ${wh.name}", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Capacity Gauge Progress
                    val capacityPercent = if (wh.capacityMetricTons > 0) {
                        (wh.filledVolumeMetricTons / wh.capacityMetricTons).toFloat()
                    } else 0f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Silo Volumetric Utilization", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${(capacityPercent * 100).toInt()}% Occupied", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { capacityPercent },
                        modifier = Modifier.fillMaxWidth(),
                        color = FarmGreenPrimary,
                        trackColor = Color.LightGray.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Red.copy(alpha = 0.08f))
                                .padding(6.dp)
                        ) {
                            Text("⚠️ $lowStockCount Low Stock Warnings", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(EarthAmberContainer)
                                .padding(6.dp)
                        ) {
                            Text("⏳ $expiringCount Expiring Stock Alerts", color = EarthAmberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Live Inventory breakdown (${whInventory.size} Crops)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    whInventory.take(3).forEach { inv ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${inv.cropName} (${inv.shelfLocation})", fontSize = 10.sp)
                            Text("${inv.availableQuantity.toInt()} Kg", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. CROP LISTINGS MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminCropManagementScreen() {
    val listings by MarketplaceRepository.cropListings.collectAsState()
    var searchVal by remember { mutableStateOf("") }
    
    // Filters
    var statusFilter by remember { mutableStateOf("All") }
    var gradeFilter by remember { mutableStateOf("All") }
    var categoryFilter by remember { mutableStateOf("All") }

    val statuses = listOf("All", "Draft", "Pickup Requested", "Approved", "Warehouse Received", "Published", "Completed", "Sold")
    val grades = listOf("All", "A", "B", "C")
    val categories = listOf("All", "Grain", "Vegetable", "Fruit", "Pulse", "Oilseed")

    val filteredListings = listings.filter {
        val matchesSearch = it.name.contains(searchVal, ignoreCase = true) || it.farmerName.contains(searchVal, ignoreCase = true)
        val matchesStatus = statusFilter == "All" || it.status == statusFilter
        val matchesGrade = gradeFilter == "All" || it.qualityGrade == gradeFilter
        val matchesCategory = categoryFilter == "All" || it.category == categoryFilter
        matchesSearch && matchesStatus && matchesGrade && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            placeholder = { Text("Search crop name or farmer...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_crops_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Filters Row
        Text("Filter Logs By Parameters", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Status Chip Dropdown/Selection
            item {
                Box {
                    AssistChip(
                        onClick = { /* Status selection managed dynamically */ },
                        label = { Text("Status: $statusFilter") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.testTag("status_filter_chip")
                    )
                }
            }

            items(statuses.filter { it != "All" }) { st ->
                val isSelected = statusFilter == st
                FilterChip(
                    selected = isSelected,
                    onClick = { statusFilter = if (isSelected) "All" else st },
                    label = { Text(st, fontSize = 10.sp) },
                    modifier = Modifier.testTag("filter_status_$st")
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(grades) { gd ->
                val isSelected = gradeFilter == gd
                FilterChip(
                    selected = isSelected,
                    onClick = { gradeFilter = gd },
                    label = { Text("Grade $gd") },
                    modifier = Modifier.testTag("filter_grade_$gd")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredListings) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Farmer: ${item.farmerName} | Cat: ${item.category}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (item.status) {
                                            "Published", "Completed" -> FarmGreenContainer
                                            "Pickup Requested", "Executive Assigned" -> EarthAmberContainer
                                            else -> Color.LightGray.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    color = when (item.status) {
                                        "Published", "Completed" -> FarmGreenPrimary
                                        "Pickup Requested", "Executive Assigned" -> EarthAmberPrimary
                                        else -> Color.DarkGray
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weight: ${item.quantityKg.toInt()} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Rate: ₹${item.pricePerKg}/kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                            Text("Grade: ${item.qualityGrade}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EarthAmberPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. ORDER MANAGEMENT SCREEN
// ==========================================

@Composable
fun AdminOrdersModerationView() {
    val orders by MarketplaceRepository.orders.collectAsState()
    var searchVal by remember { mutableStateOf("") }
    var inspectOrder by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = orders.filter {
        it.id.contains(searchVal, ignoreCase = true) || it.cropName.contains(searchVal, ignoreCase = true) || it.buyerName.contains(searchVal, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchVal,
            onValueChange = { searchVal = it },
            placeholder = { Text("Search Orders by ID, crop, wholesaler...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_orders_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredOrders) { ord ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order ID: #${ord.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (ord.status) {
                                            "Completed" -> FarmGreenContainer
                                            "Cancelled" -> Color.Red.copy(alpha = 0.1f)
                                            else -> EarthAmberContainer
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = ord.status,
                                    color = when (ord.status) {
                                        "Completed" -> FarmGreenPrimary
                                        "Cancelled" -> Color.Red
                                        else -> EarthAmberPrimary
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Commodity: ${ord.cropName} (${ord.quantityKg.toInt()} Kg) @ ₹${ord.pricePerKg}/Kg", fontSize = 12.sp)
                        Text("Wholesale Buyer: ${ord.buyerName} | Seller: ${ord.farmerName}", fontSize = 10.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Transaction: ₹${ord.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = FarmGreenPrimary, fontSize = 13.sp)
                            Button(
                                onClick = { inspectOrder = ord },
                                modifier = Modifier.testTag("inspect_order_${ord.id}"),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Inspect Timeline", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (inspectOrder != null) {
        val ord = inspectOrder!!
        Dialog(onDismissRequest = { inspectOrder = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("order_inspect_modal"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Secure Order Escrow & Logistics Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmGreenPrimary)
                    HorizontalDivider()
                    Text("Order ID reference: #${ord.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Crop listing involved: ${ord.cropName}", fontSize = 12.sp)
                    Text("Agricultural Farmer: ${ord.farmerName}", fontSize = 12.sp)
                    Text("Wholesale Buyer: ${ord.buyerName}", fontSize = 12.sp)
                    Text("Financial GMV: ₹${ord.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = FarmGreenPrimary, fontSize = 12.sp)
                    Text("Estimated Transit Completion: ${ord.estimatedDelivery}", fontSize = 12.sp)

                    HorizontalDivider()
                    Text("Operational Status Audit Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = FarmGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Funds secured in Escrow. Status: ${ord.status}", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { inspectOrder = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Timeline State")
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. FINANCIAL DASHBOARD SCREEN
// ==========================================

@Composable
fun AdminFinancialDashboardScreen() {
    val orders by MarketplaceRepository.orders.collectAsState()
    val transactions by MarketplaceRepository.walletTransactions.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()

    val totalGMV = orders.filter { it.status == "Completed" }.sumOf { it.totalAmount }
    val commissionPlatform = totalGMV * 0.02
    val logisticsCharges = orders.count { it.status == "Completed" } * 250.0
    val netFarmerSettlement = totalGMV - commissionPlatform - logisticsCharges

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Ledger & Escrow Settlement",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Auditing live platform trades, logistics charges, and treasury cuts.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Live stats summary
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminSummaryStatCard(
                    title = "Total Clearing Trade",
                    value = "₹${totalGMV.toInt()}",
                    subtitle = "Global Wholesalers Payments",
                    icon = Icons.Default.CurrencyExchange,
                    color = FarmGreenPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminSummaryStatCard(
                        title = "Platform Profits",
                        value = "₹${commissionPlatform.toInt()}",
                        subtitle = "Net commission (2%)",
                        icon = Icons.Default.Percent,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    AdminSummaryStatCard(
                        title = "Logistics Fees",
                        value = "₹${logisticsCharges.toInt()}",
                        subtitle = "₹250 per completion",
                        icon = Icons.Default.LocalShipping,
                        color = EarthAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Live Ledger Journal Updates",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(transactions.take(10)) { txn ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (txn.type == "Credit") FarmGreenContainer else Color.Red.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (txn.type == "Credit") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (txn.type == "Credit") FarmGreenPrimary else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(txn.purpose, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${txn.userRole} ID: ${txn.userId} | ${txn.timestamp}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Text(
                        text = "${if (txn.type == "Credit") "+" else "-"} ₹${txn.amount.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (txn.type == "Credit") FarmGreenPrimary else Color.Red
                    )
                }
            }
        }
    }
}

// ==========================================
// 11. CONTROLS, MANDI & SETTINGS SCREEN
// ==========================================

@Composable
fun AdminSettingsAndControlScreen() {
    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()

    // Form states for broadcasting mandi updates
    var selectMandiCrop by remember { mutableStateOf("Wheat") }
    var mandiPriceRange by remember { mutableStateOf("₹2,200 - ₹2,400 / Quintal") }
    var mandiAvgPriceText by remember { mutableStateOf("23.0") }
    var mandiTrendSelected by remember { mutableStateOf("Up") }
    var showMandiSuccess by remember { mutableStateOf(false) }

    val cropsMandiList = listOf("Wheat", "Rice (Basmati)", "Chana Dal", "Onions (Nasik)", "Mustard Seeds", "Tomatoes")
    val trends = listOf("Up", "Down", "Stable")

    // Settings Parameters
    var platformCommPercent by remember { mutableStateOf("2.0") }
    var pickupFeeAmt by remember { mutableStateOf("100.0") }
    var deliveryFeeAmt by remember { mutableStateOf("150.0") }
    var refreshIntervalMin by remember { mutableStateOf("30") }
    var currentLanguage by remember { mutableStateOf("English") }
    var showSettingsSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Broadcaster Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("mandi_broadcast_form"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Broadcast Daily Mandi Index Price",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Adjust official commodity ranges visible on buyer platforms.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    // Crop Chips Row
                    Column {
                        Text("Commodity Index", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(cropsMandiList) { crop ->
                                val isSelected = selectMandiCrop == crop
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectMandiCrop = crop },
                                    label = { Text(crop, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("mandi_crop_$crop")
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = mandiPriceRange,
                        onValueChange = { mandiPriceRange = it },
                        label = { Text("Display Price Range (per Quintal)") },
                        modifier = Modifier.fillMaxWidth().testTag("mandi_range_field"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = mandiAvgPriceText,
                            onValueChange = { mandiAvgPriceText = it },
                            label = { Text("Avg Base Rate / Kg") },
                            modifier = Modifier.weight(1f).testTag("mandi_avg_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Market Trend", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                trends.forEach { tr ->
                                    val isSelected = mandiTrendSelected == tr
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { mandiTrendSelected = tr },
                                        label = { Text(tr, fontSize = 10.sp) },
                                        modifier = Modifier.testTag("trend_$tr")
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val parsedAvg = mandiAvgPriceText.toDoubleOrNull() ?: 23.0
                            MarketplaceRepository.updateMandiPrice(
                                cropName = selectMandiCrop,
                                newRange = mandiPriceRange,
                                avgPrice = parsedAvg,
                                trend = mandiTrendSelected
                            )
                            MarketplaceRepository.addActivity(
                                description = "Mandi price index updated for $selectMandiCrop",
                                category = "Admin"
                            )
                            showMandiSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("broadcast_mandi_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                    ) {
                        Text("Broadcast Live Mandi Feed", fontWeight = FontWeight.Bold)
                    }

                    if (showMandiSuccess) {
                        Text(
                            text = "Mandi rates published and synchronized!",
                            color = FarmGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Global Parameters Config
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Global Platform Constants Config",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = platformCommPercent,
                        onValueChange = { platformCommPercent = it },
                        label = { Text("Platform Fee / Escrow Cut (%)") },
                        modifier = Modifier.fillMaxWidth().testTag("config_commission"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = pickupFeeAmt,
                            onValueChange = { pickupFeeAmt = it },
                            label = { Text("Driver Pickup Fee (₹)") },
                            modifier = Modifier.weight(1f).testTag("config_pickup"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = deliveryFeeAmt,
                            onValueChange = { deliveryFeeAmt = it },
                            label = { Text("Buyer Delivery Fee (₹)") },
                            modifier = Modifier.weight(1f).testTag("config_delivery"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = refreshIntervalMin,
                            onValueChange = { refreshIntervalMin = it },
                            label = { Text("Refresh Interval (min)") },
                            modifier = Modifier.weight(1f).testTag("config_refresh"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text("System Language", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("English", "Hindi").forEach { lng ->
                                    val isSelected = currentLanguage == lng
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { currentLanguage = lng },
                                        label = { Text(lng, fontSize = 10.sp) },
                                        modifier = Modifier.testTag("lang_$lng")
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            MarketplaceRepository.addActivity(
                                description = "Updated Global Constants (Commission: $platformCommPercent%, Delivery: ₹$deliveryFeeAmt)",
                                category = "Admin"
                            )
                            showSettingsSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_config_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Save System Settings Parameters", fontWeight = FontWeight.Bold)
                    }

                    if (showSettingsSuccess) {
                        Text(
                            text = "Platform configurations updated and persisted locally!",
                            color = FarmGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
