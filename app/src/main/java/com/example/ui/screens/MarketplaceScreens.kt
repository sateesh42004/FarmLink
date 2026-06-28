package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import com.example.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// PRIMARY MARKTPLACE CONTAINER & NAVIGATION
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceApp() {
    val currentRole by MarketplaceRepository.currentRole.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()

    val farmers by MarketplaceRepository.farmers.collectAsState()
    val buyers by MarketplaceRepository.buyers.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val orders by MarketplaceRepository.orders.collectAsState()
    val walletTxns by MarketplaceRepository.walletTransactions.collectAsState()
    val notifications by MarketplaceRepository.notifications.collectAsState()
    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()
    val reviews by MarketplaceRepository.customerReviews.collectAsState()
    val recentActivities by MarketplaceRepository.recentActivities.collectAsState()

    var activeTab by remember { mutableStateOf("Home") } // "Home", "Orders", "Wallet", "Intel"
    var showRoleSelector by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("farmlink_prefs", android.content.Context.MODE_PRIVATE) }
    var isFarmerLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_farmer_logged_in", false)) }
    var farmerScreen by remember { mutableStateOf("Dashboard") }
    var isBuyerLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_buyer_logged_in", false)) }
    var buyerScreen by remember { mutableStateOf("Dashboard") }
    var isExecutiveLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_executive_logged_in", false)) }
    var executiveScreen by remember { mutableStateOf("Dashboard") }
    var isWarehouseLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_warehouse_logged_in", false)) }
    var isAdminLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_admin_logged_in", false)) }
    var selectedBuyerCropId by remember { mutableStateOf<String?>(null) }
    var trackingOrderId by remember { mutableStateOf<String?>(null) }

    // Navigation options mapping based on current role for specialized actions
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_brand),
                            contentDescription = "FarmLink Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "FarmLink",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Direct-to-Market Portal",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    // Wallet Quick Action Badge
                    val currentBalance = MarketplaceRepository.getCurrentUserBalance()
                    Card(
                        onClick = { activeTab = "Wallet" },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet Balance",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹${currentBalance.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Notification quick badge
                    IconButton(onClick = { showNotificationDialog = true }) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Role Switching Button
                    Button(
                        onClick = { showRoleSelector = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch profile",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = currentRole, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "Home",
                    onClick = { activeTab = "Home" },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == "Home") Icons.Default.Dashboard else Icons.Default.Dashboard,
                            contentDescription = "Home Hub"
                        )
                    },
                    label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Medium) }
                )
                NavigationBarItem(
                    selected = activeTab == "Orders",
                    onClick = { activeTab = "Orders" },
                    icon = {
                        Icon(
                            imageVector = if (currentRole == "Buyer" || currentRole == "Farmer") Icons.Default.ShoppingBag else Icons.Default.LocalShipping,
                            contentDescription = "Orders or Tasks"
                        )
                    },
                    label = {
                        Text(
                            text = when (currentRole) {
                                "Farmer" -> "Listings & Orders"
                                "Buyer" -> "My Orders"
                                "Pickup" -> "Pickup Tasks"
                                "Delivery" -> "Deliveries"
                                "Warehouse" -> "Inventory"
                                else -> "All Orders"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
                NavigationBarItem(
                    selected = activeTab == "Wallet",
                    onClick = { activeTab = "Wallet" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet"
                        )
                    },
                    label = { Text("Wallet Ledger", fontSize = 11.sp, fontWeight = FontWeight.Medium) }
                )
                NavigationBarItem(
                    selected = activeTab == "Intel",
                    onClick = { activeTab = "Intel" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Market intelligence"
                        )
                    },
                    label = { Text("Mandi Intel", fontSize = 11.sp, fontWeight = FontWeight.Medium) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile context indicator banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val actorName = when(currentRole) {
                        "Farmer" -> farmers.find { it.id == currentUserId }?.name ?: "Baldev Singh"
                        "Buyer" -> buyers.find { it.id == currentUserId }?.name ?: "Aman Gupta"
                        "Pickup" -> MarketplaceRepository.pickupExecutives.value.find { it.id == currentUserId }?.name ?: "Satish Yadav"
                        "Delivery" -> MarketplaceRepository.deliveryExecutives.value.find { it.id == currentUserId }?.name ?: "Rahul Dev"
                        "Warehouse" -> "Vikas Sharma (Delhi Hub)"
                        "Admin" -> "Lead System Admin"
                        else -> "Visitor"
                    }
                    val actorLocation = when(currentRole) {
                        "Farmer" -> farmers.find { it.id == currentUserId }?.village ?: "Nangal"
                        "Buyer" -> buyers.find { it.id == currentUserId }?.city ?: "Delhi"
                        "Pickup" -> "NCR Logistics Area"
                        "Delivery" -> "North Region Delivery Route"
                        "Warehouse" -> "Delhi Central Agri-Silo"
                        "Admin" -> "Corporate Head Office"
                        else -> "Central Location"
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when(currentRole) {
                                "Farmer" -> Icons.Default.Agriculture
                                "Buyer" -> Icons.Default.Storefront
                                "Pickup" -> Icons.Default.LocalShipping
                                "Delivery" -> Icons.Default.DeliveryDining
                                "Warehouse" -> Icons.Default.Warehouse
                                else -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = "Role Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Acting as $currentRole: $actorName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "📍 $actorLocation",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Screen switcher
            Box(modifier = Modifier.fillMaxSize()) {
                if (currentRole == "Farmer" && !isFarmerLoggedIn) {
                    FarmerLoginScreen(onSuccess = { rememberMe ->
                        isFarmerLoggedIn = true
                        if (rememberMe) {
                            prefs.edit().putBoolean("is_farmer_logged_in", true).apply()
                        }
                    })
                } else if (currentRole == "Buyer" && !isBuyerLoggedIn) {
                    BuyerLoginScreen(onSuccess = { rememberMe ->
                        isBuyerLoggedIn = true
                        if (rememberMe) {
                            prefs.edit().putBoolean("is_buyer_logged_in", true).apply()
                        }
                    })
                } else if (currentRole == "Pickup" && !isExecutiveLoggedIn) {
                    ExecutiveLoginScreen(onSuccess = { rememberMe ->
                        isExecutiveLoggedIn = true
                        if (rememberMe) {
                            prefs.edit().putBoolean("is_executive_logged_in", true).apply()
                        }
                    })
                } else if (currentRole == "Warehouse" && !isWarehouseLoggedIn) {
                    WarehouseLoginScreen(onSuccess = { rememberMe ->
                        isWarehouseLoggedIn = true
                        if (rememberMe) {
                            prefs.edit().putBoolean("is_warehouse_logged_in", true).apply()
                        }
                    })
                } else if (currentRole == "Admin" && !isAdminLoggedIn) {
                    AdminLoginScreen(onSuccess = { rememberMe ->
                        isAdminLoggedIn = true
                        if (rememberMe) {
                            prefs.edit().putBoolean("is_admin_logged_in", true).apply()
                        }
                    })
                } else {
                    AnimatedContent(
                        targetState = Pair(activeTab, currentRole),
                        transitionSpec = {
                            fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                        },
                        label = "Tab Transition"
                    ) { (tab, role) ->
                        when (tab) {
                            "Home" -> {
                                when (role) {
                                    "Farmer" -> {
                                        when (farmerScreen) {
                                            "Dashboard" -> FarmerDashboardHome(
                                                onNavigate = { screen ->
                                                    if (screen == "MyListings" || screen == "Orders") {
                                                        activeTab = "Orders"
                                                    } else if (screen == "Wallet") {
                                                        activeTab = "Wallet"
                                                    } else if (screen == "MandiPrices") {
                                                        activeTab = "Intel"
                                                    } else {
                                                        farmerScreen = screen
                                                    }
                                                },
                                                onLogout = {
                                                    isFarmerLoggedIn = false
                                                    prefs.edit().putBoolean("is_farmer_logged_in", false).apply()
                                                    farmerScreen = "Dashboard"
                                                }
                                            )
                                            "AddCrop" -> AddCropScreen(onBack = { farmerScreen = "Dashboard" })
                                            "Notifications" -> FarmerNotificationsScreen(onBack = { farmerScreen = "Dashboard" })
                                            "Profile" -> FarmerProfileScreen(
                                                onBack = { farmerScreen = "Dashboard" },
                                                onLogout = {
                                                    isFarmerLoggedIn = false
                                                    prefs.edit().putBoolean("is_farmer_logged_in", false).apply()
                                                    farmerScreen = "Dashboard"
                                                }
                                            )
                                            "PickupRequests" -> FarmerPickupRequestsScreen(onBack = { farmerScreen = "Dashboard" })
                                            else -> FarmerDashboardHome(
                                                onNavigate = { farmerScreen = it },
                                                onLogout = {
                                                    isFarmerLoggedIn = false
                                                    prefs.edit().putBoolean("is_farmer_logged_in", false).apply()
                                                    farmerScreen = "Dashboard"
                                                }
                                            )
                                        }
                                    }
                                    "Buyer" -> {
                                        when (buyerScreen) {
                                            "Dashboard" -> BuyerDashboardHome(
                                                onNavigate = { screen ->
                                                    buyerScreen = screen
                                                },
                                                onSelectCrop = { cropId ->
                                                    selectedBuyerCropId = cropId
                                                    buyerScreen = "ProductDetails"
                                                }
                                            )
                                            "Marketplace" -> BuyerMarketplaceView(
                                                onBack = { buyerScreen = "Dashboard" },
                                                onSelectCrop = { cropId ->
                                                    selectedBuyerCropId = cropId
                                                    buyerScreen = "ProductDetails"
                                                },
                                                onNavigate = { screen ->
                                                    buyerScreen = screen
                                                }
                                            )
                                            "ProductDetails" -> BuyerProductDetailsView(
                                                cropId = selectedBuyerCropId ?: "",
                                                onBack = { buyerScreen = "Marketplace" },
                                                onNavigate = { screen ->
                                                    buyerScreen = screen
                                                }
                                            )
                                            "Cart" -> BuyerCartView(
                                                onBack = { buyerScreen = "Dashboard" },
                                                onNavigate = { screen ->
                                                    buyerScreen = screen
                                                }
                                            )
                                            "Checkout" -> BuyerCheckoutView(
                                                onBack = { buyerScreen = "Cart" },
                                                onNavigate = { screen ->
                                                    buyerScreen = screen
                                                }
                                            )
                                            "OrderSuccess" -> BuyerOrderSuccessView(
                                                onTrackOrder = { orderId ->
                                                    trackingOrderId = orderId
                                                    activeTab = "Orders"
                                                    buyerScreen = "Dashboard"
                                                },
                                                onBackHome = {
                                                    buyerScreen = "Dashboard"
                                                }
                                            )
                                            "Profile" -> BuyerProfileView(
                                                onBack = { buyerScreen = "Dashboard" },
                                                onLogout = {
                                                    isBuyerLoggedIn = false
                                                    prefs.edit().putBoolean("is_buyer_logged_in", false).apply()
                                                    buyerScreen = "Dashboard"
                                                }
                                            )
                                            else -> BuyerDashboardHome(
                                                onNavigate = { buyerScreen = it },
                                                onSelectCrop = { cropId ->
                                                    selectedBuyerCropId = cropId
                                                    buyerScreen = "ProductDetails"
                                                }
                                            )
                                        }
                                    }
                                    "Pickup" -> {
                                        when (executiveScreen) {
                                            "Dashboard" -> PickupDashboardHome(
                                                onNavigate = { executiveScreen = it },
                                                onLogout = {
                                                    isExecutiveLoggedIn = false
                                                    prefs.edit().putBoolean("is_executive_logged_in", false).apply()
                                                    executiveScreen = "Dashboard"
                                                }
                                            )
                                            "Profile" -> ExecutiveProfileScreen(
                                                onBack = { executiveScreen = "Dashboard" },
                                                onLogout = {
                                                    isExecutiveLoggedIn = false
                                                    prefs.edit().putBoolean("is_executive_logged_in", false).apply()
                                                    executiveScreen = "Dashboard"
                                                }
                                            )
                                            else -> PickupDashboardHome(
                                                onNavigate = { executiveScreen = it },
                                                onLogout = {
                                                    isExecutiveLoggedIn = false
                                                    prefs.edit().putBoolean("is_executive_logged_in", false).apply()
                                                    executiveScreen = "Dashboard"
                                                }
                                            )
                                        }
                                    }
                                    "Delivery" -> DeliveryDashboardHome()
                                    "Warehouse" -> WarehouseDashboardHome(onLogout = {
                                        isWarehouseLoggedIn = false
                                        prefs.edit().putBoolean("is_warehouse_logged_in", false).apply()
                                    })
                                    "Admin" -> AdminDashboardHome(onLogout = {
                                        isAdminLoggedIn = false
                                        prefs.edit().putBoolean("is_admin_logged_in", false).apply()
                                    })
                                }
                            }
                            "Orders" -> {
                                when (role) {
                                    "Farmer" -> FarmerListingsAndOrdersView(onBack = { activeTab = "Home"; farmerScreen = "Dashboard" })
                                    "Buyer" -> BuyerOrdersView(selectedOrderId = trackingOrderId, onClearSelection = { trackingOrderId = null })
                                    "Pickup" -> PickupTasksView()
                                    "Delivery" -> DeliveryTasksView()
                                    "Warehouse" -> WarehouseDashboardHome(initialTab = "Stock Ledger", onLogout = {
                                        isWarehouseLoggedIn = false
                                        prefs.edit().putBoolean("is_warehouse_logged_in", false).apply()
                                    })
                                    "Admin" -> AdminOrdersModerationView()
                                }
                            }
                            "Wallet" -> WalletLedgerView()
                            "Intel" -> MandiIntelView()
                        }
                    }
                }
            }
        }
    }

    // Role selection Dialog
    if (showRoleSelector) {
        Dialog(onDismissRequest = { showRoleSelector = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Switch Marketplace Persona",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Switch roles dynamically to explore the complete end-to-end supply chain marketplace experience.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val roles = listOf(
                        Triple("Farmer", "Add listings, view crops sold, manage transport", Icons.Default.Agriculture),
                        Triple("Buyer", "Browse 50+ crops, buy live, rate services", Icons.Default.Storefront),
                        Triple("Pickup", "Collect produce from farms, dispatch to hub", Icons.Default.LocalShipping),
                        Triple("Delivery", "Distribute from warehouse to buyers, log proof", Icons.Default.DeliveryDining),
                        Triple("Warehouse", "Verify quality grade, allocate silos & shelves", Icons.Default.Warehouse),
                        Triple("Admin", "Monitor GMV, set official Mandi price indices", Icons.Default.AdminPanelSettings)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        items(roles) { (r, desc, icon) ->
                            val isSelected = currentRole == r
                            Card(
                                onClick = {
                                    MarketplaceRepository.switchRole(r)
                                    showRoleSelector = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
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
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.2f
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = r,
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = r,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showRoleSelector = false }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    // Notifications Dialog
    if (showNotificationDialog) {
        Dialog(onDismissRequest = { showNotificationDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Activity Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showNotificationDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    val roleNotifs = notifications.filter { it.userRole == currentRole || it.userId == currentUserId }
                    if (roleNotifs.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = "No Alerts",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All quiet for now",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Alerts regarding orders, payments, and pick-ups will appear instantly here.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            items(roleNotifs) { alert ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = alert.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = alert.timestamp,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = alert.body,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showNotificationDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Alerts")
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: FARMER DASHBOARD HOME
// ==========================================

@Composable
fun FarmerDashboardHome(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val farmer = farmers.find { it.id == currentUserId } ?: farmers.firstOrNull() ?: Farmer("F01", "Baldev Singh", "Nangal", 4.8, "9876543210", 15, "2024-01-15", 150000.0, 0xFF4CAF50)

    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val farmerListings = cropListings.filter { it.farmerId == farmer.id }
    val activeListings = farmerListings.filter { it.status == "Published" }

    val orders by MarketplaceRepository.orders.collectAsState()
    val farmerOrders = orders.filter { it.farmerId == farmer.id }
    val completedOrders = farmerOrders.filter { it.status == "Completed" }
    val activeOrdersCount = farmerOrders.count { it.status != "Completed" && it.status != "Cancelled" && it.status != "Rejected" }
    val rejectedOrders = farmerOrders.filter { it.status == "Cancelled" || it.status == "Rejected" }

    val walletTxns by MarketplaceRepository.walletTransactions.collectAsState()
    val myTxns = walletTxns.filter { it.userId == farmer.id }

    val pickupRequests by MarketplaceRepository.pickupRequests.collectAsState()
    val farmerPickups = pickupRequests.filter { it.farmerId == farmer.id }
    val pendingPickupsCount = farmerPickups.count { it.status == "Pending" || it.status == "Assigned" || it.status == "On The Way" }

    val notifications by MarketplaceRepository.notifications.collectAsState()
    val myNotifs = notifications.filter { it.userId == farmer.id }

    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()

    val totalEarnings = completedOrders.sumOf { it.totalAmount }
    val totalVolumeSold = completedOrders.sumOf { it.quantityKg }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("welcome_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .background(Color(farmer.avatarColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farmer.name.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = farmer.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            if (farmer.rating >= 4.5) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Badge",
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Welcome back, Farmer! • ${farmer.village} Village",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Weather",
                                tint = EarthAmberPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Partly Sunny, 32°C • Today: 27 Jun 2026",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // My Business Overview Header
        item {
            Text(
                text = "My Business Overview",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 9 Stats Cards (arranged in rows of 2, with the best-selling crop as a full-width highlight card)
        item {
            val actListingsCount = farmerListings.count { it.status == "Published" || it.status == "Available" }
            val pendPickupsCount = farmerListings.count { it.status in listOf("Pickup Requested", "Executive Assigned", "Inspection Pending") }
            val rejListingsCount = farmerListings.count { it.status == "Rejected" }
            val compOrdersCount = farmerListings.count { it.status == "Completed" || it.status == "Sold" } + completedOrders.size

            val totalRevVal = completedOrders.sumOf { it.totalAmount } + farmerListings.filter { it.status == "Completed" || it.status == "Sold" }.sumOf { it.quantityKg * it.pricePerKg }
            val todayEarnVal = totalRevVal * 0.12
            val thisMonthEarnVal = totalRevVal * 0.88

            val avgRating = if (farmerListings.isNotEmpty()) {
                val rated = farmerListings.filter { it.rating > 0 }
                if (rated.isNotEmpty()) rated.map { it.rating }.average() else 4.8
            } else 4.8

            val bestCrop = farmerListings.filter { it.status in listOf("Completed", "Sold") }.groupBy { it.name }.maxByOrNull { it.value.size }?.key ?: "Wheat (Basmati)"

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardStatCard(
                        title = "Today's Earnings",
                        value = "₹${todayEarnVal.toInt()}",
                        subtitle = "Daily settlement",
                        icon = Icons.Default.MonetizationOn,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f).testTag("stat_today_earnings")
                    )
                    DashboardStatCard(
                        title = "This Month Earnings",
                        value = "₹${thisMonthEarnVal.toInt()}",
                        subtitle = "Monthly payouts",
                        icon = Icons.Default.CalendarMonth,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f).testTag("stat_month_earnings")
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardStatCard(
                        title = "Total Revenue",
                        value = "₹${totalRevVal.toInt()}",
                        subtitle = "Life-time revenue",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = ClayTertiary,
                        modifier = Modifier.weight(1f).testTag("stat_total_revenue")
                    )
                    DashboardStatCard(
                        title = "Active Listings",
                        value = "$actListingsCount",
                        subtitle = "Live on market",
                        icon = Icons.Default.Agriculture,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f).testTag("stat_active_listings")
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardStatCard(
                        title = "Pending Pickups",
                        value = "$pendPickupsCount",
                        subtitle = "Silo transport queue",
                        icon = Icons.Default.LocalShipping,
                        color = EarthAmberPrimary,
                        modifier = Modifier.weight(1f).testTag("stat_pending_pickups")
                    )
                    DashboardStatCard(
                        title = "Completed Orders",
                        value = "$compOrdersCount",
                        subtitle = "Delivered & completed",
                        icon = Icons.Default.CheckCircle,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f).testTag("stat_completed_orders")
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardStatCard(
                        title = "Rejected Listings",
                        value = "$rejListingsCount",
                        subtitle = "Failed inspection",
                        icon = Icons.Default.Cancel,
                        color = Color.Red,
                        modifier = Modifier.weight(1f).testTag("stat_rejected_listings")
                    )
                    DashboardStatCard(
                        title = "Average Crop Rating",
                        value = String.format("%.1f ★", avgRating),
                        subtitle = "From buyer feedback",
                        icon = Icons.Default.Star,
                        color = FieldGold,
                        modifier = Modifier.weight(1f).testTag("stat_avg_rating")
                    )
                }
                DashboardStatCard(
                    title = "Best Selling Crop",
                    value = bestCrop,
                    subtitle = "Highest yield & volume sold",
                    icon = Icons.Default.TrendingUp,
                    color = FarmGreenPrimary,
                    modifier = Modifier.fillMaxWidth().testTag("stat_best_crop")
                )
            }
        }

        // Quick Command Center Header & 8 Action Buttons (arranged in 2 rows of 4 buttons each)
        item {
            Text(
                text = "Quick Command Center",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val actionsRow1 = listOf(
                    Triple("Add Crop", Icons.Default.Add, "AddCrop"),
                    Triple("My Listings", Icons.Default.ListAlt, "MyListings"),
                    Triple("Sales Orders", Icons.Default.ReceiptLong, "Orders"),
                    Triple("My Wallet", Icons.Default.Wallet, "Wallet")
                )
                val actionsRow2 = listOf(
                    Triple("Notifications", Icons.Default.Notifications, "Notifications"),
                    Triple("Mandi Intel", Icons.Default.TrendingUp, "MandiPrices"),
                    Triple("Pickup Requests", Icons.Default.LocalShipping, "PickupRequests"),
                    Triple("My Profile", Icons.Default.Person, "Profile")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    actionsRow1.forEach { (label, icon, route) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), clip = false)
                                .clickable { onNavigate(route) }
                                .testTag("btn_$route"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 1)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    actionsRow2.forEach { (label, icon, route) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), clip = false)
                                .clickable { onNavigate(route) }
                                .testTag("btn_$route"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // Today's Mandi Price Index Header & List Snippet
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today's Mandi Price Index",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate("MandiPrices") }
                )
            }
        }

        items(mandiPrices.take(5)) { price ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), clip = false),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = price.crop, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = price.category, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Text(text = "Market: ${price.location}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (price.trend == "Up") Icons.Default.TrendingUp else if (price.trend == "Down") Icons.Default.TrendingDown else Icons.Default.TrendingFlat,
                                contentDescription = null,
                                tint = if (price.trend == "Up") FarmGreenPrimary else if (price.trend == "Down") Color.Red else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "₹${price.avgPrice}/kg",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(text = price.trend, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (price.trend == "Up") FarmGreenPrimary else if (price.trend == "Down") Color.Red else Color.Gray)
                    }
                }
            }
        }

        // Recent Alerts Header & List Snippet
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Recent Alerts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate("Notifications") }
                )
            }
        }

        if (myNotifs.isEmpty()) {
            item {
                Text(text = "No recent notifications", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            items(myNotifs.take(3)) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), clip = false),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = notif.timestamp, fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = notif.body, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Log out action button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().testTag("home_logout_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Farmer Session", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), clip = false),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FarmerLoginScreen(onSuccess: (rememberMe: Boolean) -> Unit) {
    var selectedFarmerId by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val farmers by MarketplaceRepository.farmers.collectAsState()
    
    LaunchedEffect(farmers) {
        if (farmers.isNotEmpty()) {
            selectedFarmerId = farmers.first().id
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp), clip = false)
                .testTag("farmer_login_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_brand),
                    contentDescription = "FarmLink Brand Logo",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                
                Text(
                    text = "Farmer Secure Portal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Select your profile and enter your 4-digit security PIN to log in.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose Farmer Profile",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(farmers) { f ->
                            val isSelected = f.id == selectedFarmerId
                            Card(
                                modifier = Modifier
                                    .clickable { selectedFarmerId = f.id }
                                    .widthIn(min = 100.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(f.avatarColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(f.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(f.name.substringBefore(" "), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
                
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 4) pinText = it },
                    label = { Text("4-Digit Security PIN") },
                    modifier = Modifier.fillMaxWidth().testTag("farmer_pin_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        modifier = Modifier.testTag("remember_me_checkbox")
                    )
                    Text("Remember me on this device", fontSize = 12.sp)
                }
                
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = {
                        if (pinText == "1234" || pinText == "0000" || pinText.length >= 4) {
                            MarketplaceRepository.currentUserId.value = selectedFarmerId
                            onSuccess(rememberMe)
                        } else {
                            errorMessage = "Invalid PIN. Try '1234' or any 4-digit PIN."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("farmer_login_submit"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Secure Login", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCropScreen(onBack: () -> Unit) {
    var cropName by remember { mutableStateOf("") }
    var cropCategory by remember { mutableStateOf("Grain") }
    var qtyText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var gradeSelected by remember { mutableStateOf("Grade A") }
    var cropDesc by remember { mutableStateOf("") }
    var showListingSuccess by remember { mutableStateOf(false) }
    var successTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val categories = listOf("Grain", "Pulse", "Vegetable", "Fruit", "Oilseed")
    val grades = listOf("Grade A", "Grade B", "Grade C")
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish Live Produce", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("add_crop_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Crop Listing Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Fill in your crop details to instantly post it to the buyers marketplace.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = cropName,
                            onValueChange = { cropName = it },
                            label = { Text("Crop Name (e.g. Organic Potatoes)") },
                            modifier = Modifier.fillMaxWidth().testTag("crop_name_input"),
                            singleLine = true
                        )

                        Column {
                            Text(
                                text = "Crop Category",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { cat ->
                                    FilterChip(
                                        selected = cropCategory == cat,
                                        onClick = { cropCategory = cat },
                                        label = { Text(cat) },
                                        modifier = Modifier.testTag("chip_$cat")
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = qtyText,
                                onValueChange = { qtyText = it },
                                label = { Text("Quantity (Kg)") },
                                modifier = Modifier.weight(1f).testTag("crop_qty_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = { priceText = it },
                                label = { Text("Price (₹ per Kg)") },
                                modifier = Modifier.weight(1f).testTag("crop_price_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        Column {
                            Text(
                                text = "Quality Grade Assessed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                grades.forEach { grade ->
                                    val isSelected = gradeSelected == grade
                                    OutlinedButton(
                                        onClick = { gradeSelected = grade },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier.weight(1f).testTag("grade_$grade")
                                    ) {
                                        Text(grade, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = cropDesc,
                            onValueChange = { cropDesc = it },
                            label = { Text("Brief description of crop (fertilizers, sun drying)") },
                            modifier = Modifier.fillMaxWidth().testTag("crop_desc_input"),
                            maxLines = 3
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val qty = qtyText.toDoubleOrNull()
                                    val prc = priceText.toDoubleOrNull()
                                    if (cropName.isBlank() || qty == null || prc == null || qty <= 0 || prc <= 0) {
                                        errorMessage = "Please enter a valid crop name, quantity, and price."
                                    } else {
                                        errorMessage = ""
                                        val success = MarketplaceRepository.createCropListing(
                                            name = cropName,
                                            category = cropCategory,
                                            quantityKg = qty,
                                            pricePerKg = prc,
                                            qualityGrade = gradeSelected,
                                            description = if (cropDesc.isBlank()) "High grade farm fresh produce ready for market." else cropDesc,
                                            status = "Draft"
                                        )
                                        if (success) {
                                            cropName = ""
                                            qtyText = ""
                                            priceText = ""
                                            cropDesc = ""
                                            focusManager.clearFocus()
                                            successTitle = "Draft Saved Successfully!"
                                            successMessage = "Your crop has been saved as a Draft. You can request pickup, reschedule transport, and manage quality verification anytime from 'My Listings'."
                                            showListingSuccess = true
                                        } else {
                                            errorMessage = "Error saving draft."
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("crop_draft_btn"),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Draft")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save as Draft")
                            }

                            Button(
                                onClick = {
                                    val qty = qtyText.toDoubleOrNull()
                                    val prc = priceText.toDoubleOrNull()
                                    if (cropName.isBlank() || qty == null || prc == null || qty <= 0 || prc <= 0) {
                                        errorMessage = "Please enter a valid crop name, quantity, and price."
                                    } else {
                                        errorMessage = ""
                                        val success = MarketplaceRepository.createCropListing(
                                            name = cropName,
                                            category = cropCategory,
                                            quantityKg = qty,
                                            pricePerKg = prc,
                                            qualityGrade = gradeSelected,
                                            description = if (cropDesc.isBlank()) "High grade farm fresh produce ready for market." else cropDesc,
                                            status = "Pickup Requested"
                                        )
                                        if (success) {
                                            cropName = ""
                                            qtyText = ""
                                            priceText = ""
                                            cropDesc = ""
                                            focusManager.clearFocus()
                                            successTitle = "Pickup Requested!"
                                            successMessage = "Your pickup request was successfully created. A dedicated FarmLink logistics executive is being assigned to visit your farm for quality inspection and transport."
                                            showListingSuccess = true
                                        } else {
                                            errorMessage = "Error requesting pickup."
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1.2f).testTag("crop_publish_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = "Request Pickup")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Request Pickup")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showListingSuccess) {
        Dialog(onDismissRequest = { showListingSuccess = false; onBack() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = successTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = successMessage,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showListingSuccess = false; onBack() },
                        modifier = Modifier.fillMaxWidth().testTag("listing_success_ok")
                    ) {
                        Text("Awesome")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerNotificationsScreen(onBack: () -> Unit) {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val notifications by MarketplaceRepository.notifications.collectAsState()
    val myNotifs = notifications.filter { it.userId == currentUserId || it.userRole == "Farmer" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Direct Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("notif_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (myNotifs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No notifications yet.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(myNotifs) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text(text = notif.timestamp, fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = notif.body, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val farmer = farmers.find { it.id == currentUserId } ?: farmers.firstOrNull() ?: Farmer("F01", "Baldev Singh", "Nangal", 4.8, "9876543210", 15, "2024-01-15", 150000.0, 0xFF4CAF50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(farmer.avatarColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = farmer.name.take(2).uppercase(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = farmer.name,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = EarthAmberPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${farmer.rating} Rated • Member Since ${farmer.joinedDate}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Farmer ID", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
                        Text(farmer.id, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Location (Village)", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
                        Text(farmer.village, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Phone Number", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
                        Text(farmer.phone, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Live Listings", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
                        Text("${farmer.cropCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Wallet Balance", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
                        Text("₹${farmer.walletBalance.toInt()}", fontWeight = FontWeight.Bold, color = FarmGreenPrimary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().testTag("profile_logout_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Farmer Session", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerPickupRequestsScreen(onBack: () -> Unit) {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val pickupRequests by MarketplaceRepository.pickupRequests.collectAsState()
    val farmerPickups = pickupRequests.filter { it.farmerId == currentUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Pickup Requests", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("pickups_back")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (farmerPickups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No transport pickup requests yet.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(farmerPickups) { req ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = req.cropName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (req.status) {
                                                "Pending" -> Color(0xFFFFA000).copy(alpha = 0.15f)
                                                "Assigned", "On The Way" -> Color(0xFF1976D2).copy(alpha = 0.15f)
                                                "Completed" -> Color(0xFF388E3C).copy(alpha = 0.15f)
                                                else -> Color.Gray.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = req.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (req.status) {
                                            "Pending" -> Color(0xFFFFA000)
                                            "Assigned", "On The Way" -> Color(0xFF1976D2)
                                            "Completed" -> Color(0xFF388E3C)
                                            else -> Color.Gray
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Quantity: ${req.quantityKg} Kg", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Location: ${req.farmAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Requested Time: ${req.requestedTime}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 2: FARMER LISTINGS & ORDERS VIEW (CRUD REMOVAL)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropListingDetailDialog(
    item: CropListing,
    onDismiss: () -> Unit
) {
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var newPickupDate by remember { mutableStateOf("Day after tomorrow, 11:00 AM") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                // Top Bar / Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Listing ID: ${item.id}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Badge Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Current Workflow Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (item.status) {
                                                "Draft" -> Color.Gray.copy(alpha = 0.15f)
                                                "Pickup Requested", "Executive Assigned", "Inspection Pending" -> EarthAmberPrimary.copy(alpha = 0.15f)
                                                "Approved", "Warehouse Received", "Published" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                                "Rejected" -> Color.Red.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.primaryContainer
                                            }
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item.status,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = when (item.status) {
                                            "Draft" -> Color.Gray
                                            "Pickup Requested", "Executive Assigned", "Inspection Pending" -> EarthAmberPrimary
                                            "Approved", "Warehouse Received", "Published" -> FarmGreenPrimary
                                            "Rejected" -> Color.Red
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Timeline Step Section
                    item {
                        Text("Crop Status Timeline", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val steps = listOf(
                                    "Crop Created (Draft)" to (item.status == "Draft" || item.status != ""),
                                    "Pickup Requested" to (item.status in listOf("Pickup Requested", "Executive Assigned", "Inspection Pending", "Approved", "Warehouse Received", "Published", "Completed", "Sold")),
                                    "Executive Assigned" to (item.status in listOf("Executive Assigned", "Inspection Pending", "Approved", "Warehouse Received", "Published", "Completed", "Sold")),
                                    "Quality Inspected" to (item.status in listOf("Approved", "Warehouse Received", "Published", "Completed", "Sold", "Rejected") && item.inspectionReport != null),
                                    "Stored in Warehouse" to (item.status in listOf("Warehouse Received", "Published", "Completed", "Sold")),
                                    "Listing Published Live" to (item.status in listOf("Published", "Completed", "Sold")),
                                    "Delivered & Settled" to (item.status in listOf("Completed", "Sold"))
                                )

                                steps.forEachIndexed { idx, step ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (step.second) Icons.Default.CheckCircle else Icons.Default.Circle,
                                            contentDescription = null,
                                            tint = if (step.second) FarmGreenPrimary else Color.LightGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = step.first,
                                            fontSize = 12.sp,
                                            fontWeight = if (step.second) FontWeight.Bold else FontWeight.Normal,
                                            color = if (step.second) MaterialTheme.colorScheme.onSurface else Color.Gray
                                        )
                                    }
                                    if (idx < steps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 9.dp)
                                                .width(2.dp)
                                                .height(10.dp)
                                                .background(if (step.second && steps[idx+1].second) FarmGreenPrimary else Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pickup Details Card
                    if (item.pickupId != null) {
                        item {
                            Text("Pickup Logistics Details", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Pickup ID", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.pickupId ?: "", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Scheduled Date", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.pickupDate ?: "", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Vehicle Number", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.vehicleNumber ?: "Awaiting Dispatch", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Logistics Status", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.pickupStatus ?: "Requested", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EarthAmberPrimary)
                                    }

                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))

                                    // Executive Info & Profile
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (item.executiveName ?: "Satish Yadav").split(" ").map { it.take(1) }.joinToString("").uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.executiveName ?: "Satish Yadav", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("ID: ${item.executiveId ?: "EXE_8293"} • Phone: ${item.executivePhone ?: "+91 98765 43210"}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }

                                    // Pickup OTP Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(EarthAmberPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EarthAmberPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Pickup Verification OTP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            text = item.pickupOtp ?: "4829",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = EarthAmberPrimary,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    // ETA & Live Location Row
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("ETA: ", fontSize = 11.sp, color = Color.Gray)
                                            Text(item.estimatedArrivalTime ?: "Tomorrow, 10:30 AM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Live Location: ", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                text = item.liveLocation ?: "Awaiting transit GPS signal...",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    // Pickup actions
                                    if (item.status in listOf("Pickup Requested", "Executive Assigned")) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    MarketplaceRepository.updateCropListingWorkflow(item.id, "Draft")
                                                },
                                                modifier = Modifier.weight(1f).testTag("cancel_pickup_btn"),
                                                border = BorderStroke(1.dp, Color.Red),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                            ) {
                                                Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Cancel Pickup", fontSize = 10.sp)
                                            }
                                            Button(
                                                onClick = { showRescheduleDialog = true },
                                                modifier = Modifier.weight(1f).testTag("reschedule_pickup_btn"),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                            ) {
                                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Reschedule", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reschedule", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Executive Quality Inspection Report Card
                    if (item.inspectionReport != null) {
                        val report = item.inspectionReport!!
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Executive Quality Inspection Report", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                if (report.verifiedBadge) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(FarmGreenPrimary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Verified by FarmLink", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Inspector", fontSize = 11.sp, color = Color.Gray)
                                        Text("Satish Yadav", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Inspection Date", fontSize = 11.sp, color = Color.Gray)
                                        Text(report.inspectionDate, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Assigned Grade", fontSize = 11.sp, color = Color.Gray)
                                        Text(report.grade, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (report.isApproved) FarmGreenPrimary else Color.Red)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Quality Score", fontSize = 11.sp, color = Color.Gray)
                                        Text("${report.qualityScore}%", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Moisture Level", fontSize = 11.sp, color = Color.Gray)
                                        Text("${report.moistureLevel}%", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text("Inspector Notes", fontSize = 10.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(report.executiveNotes, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    }

                                    // Dummy Inspection photos
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text("Inspection Photos", fontSize = 10.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Card(
                                                modifier = Modifier.size(60.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                                                    Text("Sample.jpg", fontSize = 8.sp, color = Color.Gray)
                                                }
                                            }
                                            Card(
                                                modifier = Modifier.size(60.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                                                    Text("Moisture.jpg", fontSize = 8.sp, color = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Warehouse Details Card
                    if (item.warehouseId != null) {
                        item {
                            Text("Warehouse Storage & Stock Allocation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Header with Warehouse Name & ID
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(item.warehouseName ?: "Delhi Central Agri-Silo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(item.warehouseId ?: "WH_DEL_04", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        }
                                    }

                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))

                                    // Storage Location Specs
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Allocated Rack / Bin", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.warehouseRackNumber ?: "Silo-B, Rack-12", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Storage Deposited Date", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.warehouseStorageDate ?: "Today, 02:30 PM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Calculated Shelf Life", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.warehouseShelfLife ?: "${item.shelfLifeDays} days remaining", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EarthAmberPrimary)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Depot Stock Status", fontSize = 11.sp, color = Color.Gray)
                                        Text(item.warehouseStatus ?: "Temperature Controlled Silo", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FarmGreenPrimary)
                                    }

                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))

                                    // Allocation Quantities Bar
                                    Text("Stock Allocation Summary", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Available stock block
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(FarmGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Text("Available Quantity", fontSize = 10.sp, color = Color.Gray)
                                            Text("${(item.warehouseAvailableQuantity ?: item.quantityKg).toInt()} kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FarmGreenPrimary)
                                        }
                                        
                                        // Reserved stock block
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Text("Reserved / Booked", fontSize = 10.sp, color = Color.Gray)
                                            Text("${(item.warehouseReservedQuantity ?: 0.0).toInt()} kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Business Simulator Console
                    item {
                        Text("Business Workflow Simulator Console", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Select a simulator action to advance this crop listing through the official business steps of FarmLink:", fontSize = 11.sp)
                                
                                when (item.status) {
                                    "Draft" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Pickup Requested")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_request_pickup"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Step 1: Request Pickup")
                                        }
                                    }
                                    "Pickup Requested" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Executive Assigned")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_assign_exec")
                                        ) {
                                            Text("Step 2: Assign Logistics Executive")
                                        }
                                    }
                                    "Executive Assigned" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Inspection Pending")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_visit_farm")
                                        ) {
                                            Text("Step 3: Executive Visits Farm")
                                        }
                                    }
                                    "Inspection Pending" -> {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    MarketplaceRepository.updateCropListingWorkflow(item.id, "Rejected")
                                                },
                                                modifier = Modifier.weight(1f).testTag("sim_reject_crop"),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                            ) {
                                                Text("Reject Crop")
                                            }
                                            Button(
                                                onClick = {
                                                    MarketplaceRepository.updateCropListingWorkflow(item.id, "Approved")
                                                },
                                                modifier = Modifier.weight(1.2f).testTag("sim_approve_crop"),
                                                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                            ) {
                                                Text("Approve & Grade")
                                            }
                                        }
                                    }
                                    "Approved" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Warehouse Received")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_to_warehouse"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Text("Step 5: Move to Warehouse Silo")
                                        }
                                    }
                                    "Warehouse Received" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Published")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_publish_listing"),
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                        ) {
                                            Text("Step 6: Confirm Stock & Publish Live")
                                        }
                                    }
                                    "Published" -> {
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Completed")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_buyer_buy"),
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                        ) {
                                            Text("Step 7: Simulate Buyer Purchase & Payout")
                                        }
                                    }
                                    "Completed", "Sold" -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(FarmGreenPrimary.copy(alpha = 0.1f))
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Workflow Complete! Listing sold and payment credited.", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FarmGreenPrimary)
                                        }
                                    }
                                    "Rejected" -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Red.copy(alpha = 0.1f))
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Crop failed quality guidelines.", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Red)
                                        }
                                        Button(
                                            onClick = {
                                                MarketplaceRepository.updateCropListingWorkflow(item.id, "Draft")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("sim_reset_draft")
                                        ) {
                                            Text("Reset to Draft & Try Again")
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

    if (showRescheduleDialog) {
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule Pickup") },
            text = {
                Column {
                    Text("Select a new date/time for pickup:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPickupDate,
                        onValueChange = { newPickupDate = it },
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        MarketplaceRepository.updateCropListingWorkflow(item.id, "Pickup Requested", mapOf("pickupDate" to newPickupDate))
                        showRescheduleDialog = false
                    },
                    modifier = Modifier.testTag("confirm_reschedule_btn")
                ) {
                    Text("Reschedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FarmerListingsAndOrdersView(onBack: () -> Unit = {}) {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val farmerListings = cropListings.filter { it.farmerId == currentUserId }

    val orders by MarketplaceRepository.orders.collectAsState()
    val farmerOrders = orders.filter { it.farmerId == currentUserId }

    var selectedTab by remember { mutableStateOf("Listings") } // "Listings" or "Active Orders"
    var activeListingForDetails by remember { mutableStateOf<CropListing?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("listings_orders_back")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("My Listings & Orders", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // Tab row switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = { selectedTab = "Listings" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "Listings") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedTab == "Listings") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text("My Listings (${farmerListings.size})")
            }
            Button(
                onClick = { selectedTab = "Orders" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "Orders") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedTab == "Orders") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Text("My Sales / Orders (${farmerOrders.size})")
            }
        }

        if (selectedTab == "Listings") {
            if (farmerListings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = "No crops",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No listed crops found.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Add crops using the form on the dashboard to start selling.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(farmerListings) { item ->
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
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Category: ${item.category} • Grade: ${item.qualityGrade}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (item.status) {
                                                    "Draft" -> Color.Gray.copy(alpha = 0.15f)
                                                    "Pickup Requested", "Executive Assigned", "Inspection Pending" -> EarthAmberPrimary.copy(alpha = 0.15f)
                                                    "Approved", "Warehouse Received", "Published" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                                    "Rejected" -> Color.Red.copy(alpha = 0.15f)
                                                    else -> MaterialTheme.colorScheme.primaryContainer
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.status) {
                                                "Draft" -> Color.Gray
                                                "Pickup Requested", "Executive Assigned", "Inspection Pending" -> EarthAmberPrimary
                                                "Approved", "Warehouse Received", "Published" -> FarmGreenPrimary
                                                "Rejected" -> Color.Red
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Quantity Available",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${item.quantityKg.toInt()} Kg",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Price per Kg",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "₹${item.pricePerKg}/kg",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Storage Location",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = item.warehouseLocation,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = item.description,
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
                                    OutlinedButton(
                                        onClick = { activeListingForDetails = item },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("view_workflow_btn_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = "Workflow Details",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Details & Workflow", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { MarketplaceRepository.deleteCropListing(item.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remove Listing", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // My Orders / Sales List
            if (farmerOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders placed for your crops yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(farmerOrders) { ord ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Order #${ord.id}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = ord.status.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = when(ord.status) {
                                            "Completed" -> FarmGreenPrimary
                                            "Pending Pickup" -> EarthAmberPrimary
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Produce: ${ord.quantityKg.toInt()} Kg of ${ord.cropName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Buyer: ${ord.buyerName} • Total Deal Value: ₹${ord.totalAmount.toInt()}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Order Date: ${ord.orderDate} | ${ord.estimatedDelivery}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (ord.ratingForFarmer > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Review",
                                            tint = FieldGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = "Buyer Review (${ord.ratingForFarmer} Stars)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = ord.reviewForFarmer,
                                                fontSize = 11.sp,
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

        if (activeListingForDetails != null) {
            val freshListing = cropListings.find { it.id == activeListingForDetails!!.id }
            if (freshListing != null) {
                CropListingDetailDialog(
                    item = freshListing,
                    onDismiss = { activeListingForDetails = null }
                )
            } else {
                activeListingForDetails = null
            }
        }
    }
}

// ==========================================
// SCREEN 3: BUYER DASHBOARD & CROP FEED (SEARCH & LIVE BUYING)
// ==========================================

@Composable
fun BuyerDashboardHomeOld() {
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val availableListings = cropListings.filter { it.status == "Published" }

    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()
    val searchHistory by MarketplaceRepository.searchHistory.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Grain", "Pulse", "Vegetable", "Fruit")

    // Buy dialog trigger state
    var buyTargetCrop by remember { mutableStateOf<CropListing?>(null) }
    var buyQtyString by remember { mutableStateOf("") }
    var buyErrorMessage by remember { mutableStateOf("") }
    var buySuccessMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Explore Farm Fresh Market",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Source high quality, authenticated, and sorted grades directly from 20+ verified Indian Farmers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Search Input & Search History Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    placeholder = { Text("Search crops (e.g. Rice, Potatoes)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = { searchInput = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )

                // Search history horizontal chip list
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Popular:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(searchHistory) { hist ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        searchInput = if (hist.contains(" ")) hist.split(" ")[1] else hist
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = hist, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Live Mandi Ticker Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Official Daily Mandi Price Indexes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mandiPrices) { price ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = price.crop,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Icon(
                                        imageVector = if (price.trend == "Up") Icons.Default.TrendingUp else if (price.trend == "Down") Icons.Default.TrendingDown else Icons.Default.ArrowRightAlt,
                                        contentDescription = "Trend",
                                        tint = if (price.trend == "Up") FarmGreenPrimary else if (price.trend == "Down") Color.Red else Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = price.priceRange,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Avg: ₹${price.avgPrice}/kg",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = price.location.split(" ")[0],
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Tabs
        item {
            Column {
                Text(
                    text = "Browse By Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = categoryFilter == cat,
                            onClick = { categoryFilter = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        }

        // Crop listings feed
        val filteredListings = availableListings.filter { item ->
            val matchesCategory = (categoryFilter == "All" || item.category == categoryFilter)
            val matchesSearch = (searchInput.isEmpty() || item.name.contains(searchInput, ignoreCase = true) || item.farmerName.contains(searchInput, ignoreCase = true))
            matchesCategory && matchesSearch
        }

        if (filteredListings.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No crops matching filters",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try searching for Wheat, Rice, Potatoes, or reset filters.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredListings) { crop ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = crop.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Farmer: ${crop.farmerName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = crop.qualityGrade,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Price Bubble
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${crop.pricePerKg}/kg",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EarthAmberPrimary
                                )
                                Text(
                                    text = "Listed ${crop.listedDate}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                                Text(
                                    text = "Available Stock",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${crop.quantityKg.toInt()} Kg",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "Hub Location",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = crop.warehouseLocation.split(" ")[0] + " Hub",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    buyTargetCrop = crop
                                    buyQtyString = crop.quantityKg.toInt().toString()
                                    buyErrorMessage = ""
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Buy",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buy Produce", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Purchase Dialog
    buyTargetCrop?.let { crop ->
        Dialog(onDismissRequest = { buyTargetCrop = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Confirm Marketplace Purchase",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    Text(
                        text = "Crop: ${crop.name} (${crop.qualityGrade})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Farmer: ${crop.farmerName} | Warehouse: ${crop.warehouseLocation}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = buyQtyString,
                        onValueChange = { buyQtyString = it },
                        label = { Text("Quantity to Purchase (Kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    val requestedQty = buyQtyString.toDoubleOrNull() ?: 0.0
                    val totalCost = requestedQty * crop.pricePerKg
                    val currentBuyerBalance = MarketplaceRepository.getCurrentUserBalance()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rate per Kg:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${crop.pricePerKg}/kg", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Your Wallet Balance:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${currentBuyerBalance.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Purchase Cost:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("₹${totalCost.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = EarthAmberPrimary)
                        }
                    }

                    if (buyErrorMessage.isNotEmpty()) {
                        Text(
                            text = buyErrorMessage,
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { buyTargetCrop = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (requestedQty <= 0) {
                                    buyErrorMessage = "Please enter a valid positive quantity."
                                } else if (requestedQty > crop.quantityKg) {
                                    buyErrorMessage = "Insufficient crop stock! Maximum stock: ${crop.quantityKg.toInt()} kg"
                                } else if (totalCost > currentBuyerBalance) {
                                    buyErrorMessage = "Insufficient wallet balance! Please deposit funds."
                                } else {
                                    buyErrorMessage = ""
                                    val success = MarketplaceRepository.purchaseListing(crop.id, requestedQty)
                                    if (success) {
                                        buyTargetCrop = null
                                        buySuccessMessage = true
                                    } else {
                                        buyErrorMessage = "Error completing transaction."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Confirm & Pay")
                        }
                    }
                }
            }
        }
    }

    if (buySuccessMessage) {
        Dialog(onDismissRequest = { buySuccessMessage = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Order Placed Successfully!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = FarmGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Funds have been secured in Escrow. A Pickup Executive has been assigned to transfer the produce to the central warehouse. Track shipping in your orders tab.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { buySuccessMessage = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Track Order")
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 4: BUYER ORDERS & STATUS / REVIEWS (CRUD IN ACTION)
// ==========================================

@Composable
fun BuyerOrdersViewOld() {
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val orders by MarketplaceRepository.orders.collectAsState()
    val buyerOrders = orders.filter { it.buyerId == currentUserId }

    // Rating dialog state
    var ratingTargetOrder by remember { mutableStateOf<Order?>(null) }
    var userRatingValue by remember { mutableStateOf(5) }
    var userReviewText by remember { mutableStateOf("") }

    if (buyerOrders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "No orders",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No orders placed yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(buyerOrders) { ord ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order ID: #${ord.id}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (ord.status) {
                                            "Completed" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                            "Pending Pickup" -> EarthAmberPrimary.copy(alpha = 0.15f)
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = ord.status.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (ord.status) {
                                        "Completed" -> FarmGreenPrimary
                                        "Pending Pickup" -> EarthAmberPrimary
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${ord.quantityKg.toInt()} Kg of ${ord.cropName}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Seller: Farmer ${ord.farmerName} | Price: ₹${ord.pricePerKg}/kg",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Paid Escrow",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${ord.totalAmount.toInt()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Timeline / Shipments",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ord.estimatedDelivery,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Stepper indicator for shipping status
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(modifier = Modifier.padding(bottom = 6.dp))
                        val progressValue = when(ord.status) {
                            "Pending Pickup" -> 0.25f
                            "Picked Up" -> 0.50f
                            "In Warehouse" -> 0.75f
                            "Out for Delivery" -> 0.90f
                            "Completed" -> 1.0f
                            else -> 0.0f
                        }
                        if (ord.status != "Cancelled") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LinearProgressIndicator(
                                    progress = { progressValue },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.outlineVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Order Placed", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("At Warehouse", fontSize = 9.sp, color = if (progressValue >= 0.75f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Delivered", fontSize = 9.sp, color = if (progressValue == 1.0f) FarmGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // Leave review action
                        if (ord.status == "Completed" && ord.ratingForFarmer == 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    ratingTargetOrder = ord
                                    userRatingValue = 5
                                    userReviewText = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Rate")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rate Farmer & Produce Quality", fontSize = 12.sp)
                            }
                        } else if (ord.ratingForFarmer > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Rated",
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Your Review: ${ord.ratingForFarmer} Stars",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "\"${ord.reviewForFarmer}\"",
                                        fontSize = 11.sp,
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

    // Leave review Dialog
    ratingTargetOrder?.let { ord ->
        Dialog(onDismissRequest = { ratingTargetOrder = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Review Crop Delivery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rate your experience with ${ord.farmerName}'s crop (${ord.cropName}). This helps maintain listing authentication indices.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 1-5 Star Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            val active = userRatingValue >= star
                            IconButton(onClick = { userRatingValue = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star Stars",
                                    tint = if (active) FieldGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = userReviewText,
                        onValueChange = { userReviewText = it },
                        label = { Text("Write your comment (crop size, moisture level, sorting)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { ratingTargetOrder = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                MarketplaceRepository.rateOrder(
                                    ord.id,
                                    userRatingValue,
                                    if (userReviewText.isBlank()) "Outstanding quality crops, perfectly delivered." else userReviewText
                                )
                                ratingTargetOrder = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 5: LOGISTICS PORTALS (PICKUP & DELIVERY EXECS)
// ==========================================



@Composable
fun DeliveryDashboardHome() {
    val deliveryRequests by MarketplaceRepository.deliveryRequests.collectAsState()
    val activeDeliveries = deliveryRequests.filter { it.status != "Delivered" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery Agent Terminal",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Deliver sorted crops directly to city buyers, grocery centers, or food mills. Confirm dynamic delivery proofs.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Live Delivery Routes (${activeDeliveries.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (activeDeliveries.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No shipments to deliver currently.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(activeDeliveries) { del ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ID: ${del.id} • Order #${del.orderId}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = del.status.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ship: ${del.quantityKg.toInt()} Kg of ${del.cropName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Buyer: ${del.buyerName} • ${del.deliveryAddress}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dispatched: ${del.requestedTime}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (del.status == "Assigned") {
                                Button(
                                    onClick = { MarketplaceRepository.updateDeliveryStatus(del.id, "Out For Delivery") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = "Out for delivery")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Out for Delivery", fontSize = 12.sp)
                                }
                            } else if (del.status == "Out For Delivery") {
                                Button(
                                    onClick = { MarketplaceRepository.updateDeliveryStatus(del.id, "Delivered") },
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Deliver confirmation")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Confirm Safe Delivery", fontSize = 12.sp)
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
fun DeliveryTasksView() {
    DeliveryDashboardHome() // Re-uses the clean active deliveries hub
}

// ==========================================
// Warehouse screens are fully implemented in WarehouseModuleScreens.kt
// ==========================================

// ==========================================
// SCREEN 7: ADMIN CONTROL PANEL
// ==========================================

@Composable
fun OldAdminDashboardHome() {
    val orders by MarketplaceRepository.orders.collectAsState()
    val cropListings by MarketplaceRepository.cropListings.collectAsState()
    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()

    val totalGMV = orders.filter { it.status == "Completed" }.sumOf { it.totalAmount }
    val totalPendingAmount = orders.filter { it.status != "Completed" && it.status != "Cancelled" }.sumOf { it.totalAmount }

    // Admin state for mandi prices update
    var selectMandiCrop by remember { mutableStateOf("Wheat") }
    var mandiPriceRange by remember { mutableStateOf("₹2,200 - ₹2,400 / Quintal") }
    var mandiAvgPriceText by remember { mutableStateOf("23.0") }
    var mandiTrendSelected by remember { mutableStateOf("Up") }
    var showUpdateSuccess by remember { mutableStateOf(false) }

    val cropsMandiList = listOf("Wheat", "Rice (Basmati)", "Chana Dal", "Onions (Nasik)", "Mustard Seeds", "Tomatoes")
    val trends = listOf("Up", "Down", "Stable")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Admin Operations Command",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Manage live escrows, update global mandi index pricing, and audit full supply chain logs.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Global Statistics Card Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardStatCard(
                        title = "Platform GMV (Settled)",
                        value = "₹${totalGMV.toInt()}",
                        subtitle = "Escrow Completed",
                        icon = Icons.Default.CurrencyExchange,
                        modifier = Modifier.weight(1.5f),
                        color = FarmGreenPrimary
                    )
                    DashboardStatCard(
                        title = "Escrow Locked",
                        value = "₹${totalPendingAmount.toInt()}",
                        subtitle = "Active Shipments",
                        icon = Icons.Default.Lock,
                        modifier = Modifier.weight(1f),
                        color = EarthAmberPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardStatCard(
                        title = "Total Live Grains",
                        value = "${cropListings.size} Listings",
                        subtitle = "Across All Farmers",
                        icon = Icons.Default.Grass,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    DashboardStatCard(
                        title = "Supply Chain Tasks",
                        value = "${orders.size} Orders",
                        subtitle = "${orders.count { it.status == "Completed" }} Delivered",
                        icon = Icons.Default.LocalShipping,
                        modifier = Modifier.weight(1.2f),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Update Mandi Prices form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Broadcast Daily Mandi Index Price",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Adjust the official trading ranges shown on buyer marketplaces.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Crop select dropdown replacement (lazy scroll list or chips)
                    Column {
                        Text("Select Crop Index", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            items(cropsMandiList) { c ->
                                FilterChip(
                                    selected = selectMandiCrop == c,
                                    onClick = { selectMandiCrop = c },
                                    label = { Text(c) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = mandiPriceRange,
                        onValueChange = { mandiPriceRange = it },
                        label = { Text("Trading Range (e.g. ₹2,300 - ₹2,500 / Quintal)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = mandiAvgPriceText,
                            onValueChange = { mandiAvgPriceText = it },
                            label = { Text("Avg Price (₹ per Kg)") },
                            modifier = Modifier.weight(1.2f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Market Trend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                trends.forEach { t ->
                                    val act = mandiTrendSelected == t
                                    OutlinedButton(
                                        onClick = { mandiTrendSelected = t },
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (act) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(t, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val avg = mandiAvgPriceText.toDoubleOrNull() ?: 20.0
                            MarketplaceRepository.updateMandiPrice(
                                cropName = selectMandiCrop,
                                newRange = mandiPriceRange,
                                avgPrice = avg,
                                trend = mandiTrendSelected
                            )
                            showUpdateSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = "Broadcast")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Broadcast Live Mandi Update")
                    }
                }
            }
        }
    }

    if (showUpdateSuccess) {
        Dialog(onDismissRequest = { showUpdateSuccess = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(48.dp),
                        contentDescription = "Success"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Price Index Broadcasted!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FarmGreenPrimary
                    )
                    Text(
                        text = "Mandi price changes are now synchronized live across the buyer marketplace and farmer informational widgets.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(onClick = { showUpdateSuccess = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Awesome")
                    }
                }
            }
        }
    }
}

@Composable
fun OldAdminOrdersModerationView() {
    val orders by MarketplaceRepository.orders.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Moderation System Logs (${orders.size} Total Orders)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(orders) { ord ->
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
                        Text(
                            text = "Order #${ord.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = ord.status.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (ord.status == "Cancelled") Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${ord.quantityKg.toInt()} Kg of ${ord.cropName} | Buyer: ${ord.buyerName}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Deal Escrow Amount: ₹${ord.totalAmount.toInt()} (Farmer: ${ord.farmerName})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (ord.status != "Cancelled" && ord.status != "Completed") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { MarketplaceRepository.cancelOrderAdmin(ord.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Force Refund / Cancel", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 8: WALLET HUB & TRANSACTION LEDGER
// ==========================================

@Composable
fun WalletLedgerView() {
    val currentRole by MarketplaceRepository.currentRole.collectAsState()
    val currentUserId by MarketplaceRepository.currentUserId.collectAsState()
    val walletTxns by MarketplaceRepository.walletTransactions.collectAsState()
    val farmers by MarketplaceRepository.farmers.collectAsState()
    val buyers by MarketplaceRepository.buyers.collectAsState()

    val currentBalance = when (currentRole) {
        "Farmer" -> farmers.find { it.id == currentUserId }?.walletBalance ?: 0.0
        "Buyer" -> buyers.find { it.id == currentUserId }?.walletBalance ?: 0.0
        else -> 12500.0
    }
    val myTransactions = walletTxns.filter { it.userId == currentUserId }

    var depositAmount by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("") }
    var walletErrorMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FarmLink Escrow Wallet Balance",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${currentBalance.toInt()}.00",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instant settlements secured under verified marketplace agreements. All escrowed deposits are fully insured.",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // For Farmer role, display detailed Financial Breakdown
        if (currentRole == "Farmer") {
            item {
                val cropListings by MarketplaceRepository.cropListings.collectAsState()
                val farmerListings = cropListings.filter { it.farmerId == currentUserId }
                val orders by MarketplaceRepository.orders.collectAsState()
                val farmerOrders = orders.filter { it.farmerId == currentUserId }

                val completedFarmerOrders = farmerOrders.filter { it.status == "Completed" }

                val completedPayments = completedFarmerOrders.sumOf { it.totalAmount } + farmerListings.filter { it.status == "Completed" || it.status == "Sold" }.sumOf { it.quantityKg * it.pricePerKg }
                val pendingSettlement = farmerListings.filter { it.status in listOf("Pickup Requested", "Executive Assigned", "Inspection Pending", "Approved", "Warehouse Received", "Published", "Reserved") }.sumOf { it.quantityKg * it.pricePerKg }
                val processingPayments = pendingSettlement * 0.35 // Realistic simulation of active/processing settlements
                val commissionDeduction = completedPayments * 0.02
                val deliveryChargeDeduction = (completedFarmerOrders.size + farmerListings.count { it.status == "Completed" || it.status == "Sold" }) * 250.0

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("farmer_wallet_breakdown"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Farmer Escrow Breakdown",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Available Balance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${currentBalance.toInt()}.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pending Settlement", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${pendingSettlement.toInt()}.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EarthAmberPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Processing Payments", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${processingPayments.toInt()}.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Completed Payments", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${completedPayments.toInt()}.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FarmGreenPrimary)
                        }
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Commission Deduction (2%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("- ₹${commissionDeduction.toInt()}.00", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = Color.Red)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Charge Deduction", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("- ₹${deliveryChargeDeduction.toInt()}.00", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = Color.Red)
                        }
                    }
                }
            }
        }

        // Wallet Funds addition / subtraction
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Funds Transfer & Settlements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = { depositAmount = it },
                            label = { Text("Deposit Amount (₹)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val amt = depositAmount.toDoubleOrNull() ?: 0.0
                                if (amt <= 0) {
                                    walletErrorMessage = "Enter valid deposit funds amount."
                                } else {
                                    walletErrorMessage = ""
                                    MarketplaceRepository.depositFunds(amt)
                                    depositAmount = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Deposit")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = withdrawAmount,
                            onValueChange = { withdrawAmount = it },
                            label = { Text("Withdraw Amount (₹)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                                if (amt <= 0 || amt > currentBalance) {
                                    walletErrorMessage = "Insufficient balance or invalid withdrawal amount."
                                } else {
                                    walletErrorMessage = ""
                                    MarketplaceRepository.withdrawFunds(amt)
                                    withdrawAmount = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Withdraw")
                        }
                    }

                    if (walletErrorMessage.isNotEmpty()) {
                        Text(
                            text = walletErrorMessage,
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Transaction History Section
        item {
            val transactionHistory = myTransactions.filter { !it.purpose.contains("Withdraw", ignoreCase = true) }
            Text(
                text = "Transaction History (${transactionHistory.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val transactionHistoryList = myTransactions.filter { !it.purpose.contains("Withdraw", ignoreCase = true) }
        if (transactionHistoryList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions found.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        } else {
            items(transactionHistoryList) { txn ->
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (txn.type == "Credit") FarmGreenPrimary.copy(alpha = 0.15f) else Color.Red.copy(
                                            alpha = 0.15f
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (txn.type == "Credit") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = txn.type,
                                    tint = if (txn.type == "Credit") FarmGreenPrimary else Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = txn.purpose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "TXN ID: ${txn.id} • ${txn.timestamp}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${if (txn.type == "Credit") "+" else "-"} ₹${txn.amount.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if (txn.type == "Credit") FarmGreenPrimary else Color.Red
                        )
                    }
                }
            }
        }

        // Withdrawal History Section
        item {
            val withdrawalHistory = myTransactions.filter { it.purpose.contains("Withdraw", ignoreCase = true) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Withdrawal History (${withdrawalHistory.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        val withdrawalHistoryList = myTransactions.filter { it.purpose.contains("Withdraw", ignoreCase = true) }
        if (withdrawalHistoryList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No withdrawal records found.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        } else {
            items(withdrawalHistoryList) { txn ->
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = txn.type,
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = txn.purpose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "TXN ID: ${txn.id} • ${txn.timestamp}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "- ₹${txn.amount.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 9: MANDI INDEX & CUSTOMER REVIEWS & RECENT FEED
// ==========================================

@Composable
fun MandiIntelView() {
    val mandiPrices by MarketplaceRepository.mandiPrices.collectAsState()
    val reviews by MarketplaceRepository.customerReviews.collectAsState()
    val activities by MarketplaceRepository.recentActivities.collectAsState()

    var activeSubTab by remember { mutableStateOf("Mandi Index") } // "Mandi Index", "Reviews", "Logs"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            val subTabs = listOf("Mandi Index", "Customer Reviews", "Activity Logs")
            subTabs.forEach { tabName ->
                val isSelected = activeSubTab == tabName
                Button(
                    onClick = { activeSubTab = tabName },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(tabName, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (activeSubTab) {
            "Mandi Index" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Daily Agri-Price Indices (Live Mandi)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(mandiPrices) { idx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = idx.crop,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = idx.category,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Source Market: ${idx.location}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = idx.priceRange,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (idx.trend == "Up") Icons.Default.TrendingUp else if (idx.trend == "Down") Icons.Default.TrendingDown else Icons.Default.TrendingFlat,
                                            contentDescription = idx.trend,
                                            tint = if (idx.trend == "Up") FarmGreenPrimary else if (idx.trend == "Down") Color.Red else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = idx.trend,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (idx.trend == "Up") FarmGreenPrimary else if (idx.trend == "Down") Color.Red else Color.Gray
                                        )
                                    }
                                    Text(
                                        text = "₹${idx.avgPrice}/kg",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = idx.lastUpdated,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "Customer Reviews" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Verified Buyer Reviews",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(reviews) { rev ->
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
                                    Text(
                                        text = rev.reviewerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Row {
                                        for (s in 1..5) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (rev.rating >= s) FieldGold else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Purchased: ${rev.cropName} | Date: ${rev.date}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"${rev.comment}\"",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            "Activity Logs" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Marketplace Transaction Logs (Live)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FarmGreenPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Front State Syncing",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary
                                )
                            }
                        }
                    }

                    items(activities) { act ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (act.category) {
                                            "list" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            "buy" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                            "pickup" -> EarthAmberPrimary.copy(alpha = 0.15f)
                                            else -> ClayTertiary.copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when(act.category) {
                                        "list" -> Icons.Default.Add
                                        "buy" -> Icons.Default.Storefront
                                        "pickup" -> Icons.Default.DirectionsTransit
                                        "delivery" -> Icons.Default.CheckCircle
                                        else -> Icons.Default.Timeline
                                    },
                                    contentDescription = act.category,
                                    tint = when (act.category) {
                                        "list" -> MaterialTheme.colorScheme.primary
                                        "buy" -> MaterialTheme.colorScheme.secondary
                                        "pickup" -> EarthAmberPrimary
                                        else -> ClayTertiary
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = act.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = act.relativeTime,
                                    fontSize = 9.sp,
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
