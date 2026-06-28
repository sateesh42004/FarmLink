@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.util.UUID

// Colors matching original theme
val EarthAmberPrimary = Color(0xFFC67C4E)
val FarmGreenPrimary = Color(0xFF388E3C)
val SurfaceDarkBg = Color(0xFF1E293B)

@Composable
fun WarehouseLoginScreen(onSuccess: (Boolean) -> Unit) {
    var managerName by remember { mutableStateOf("Vikas Sharma") }
    var password by remember { mutableStateOf("1234") }
    var selectedWarehouse by remember { mutableStateOf("Delhi Agri-Store Hub") }
    var rememberMe by remember { mutableStateOf(true) }
    var showDropdown by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val warehouses = listOf("Delhi Agri-Store Hub", "Sonipat Food Silo", "Hapur Cold Storage")

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
                .widthIn(max = 450.dp)
                .testTag("warehouse_login_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warehouse,
                        contentDescription = "Warehouse Portal",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Warehouse Operator Login",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "FarmLink Agri-Silos and Cold Chains",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Hub Selection Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedWarehouse,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Storage Hub") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, "Hub") },
                        trailingIcon = {
                            IconButton(onClick = { showDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, "Select")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("hub_selector")
                    )
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        warehouses.forEach { wh ->
                            DropdownMenuItem(
                                text = { Text(wh) },
                                onClick = {
                                    selectedWarehouse = wh
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = managerName,
                    onValueChange = { managerName = it },
                    label = { Text("Manager / Operator Name") },
                    leadingIcon = { Icon(Icons.Default.Person, "Name") },
                    modifier = Modifier.fillMaxWidth().testTag("manager_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Operator Pin") },
                    leadingIcon = { Icon(Icons.Default.Lock, "Lock") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("operator_pin_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        modifier = Modifier.testTag("remember_me_checkbox")
                    )
                    Text(
                        text = "Remember persistent session",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (managerName.trim().isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Please complete credentials", Toast.LENGTH_SHORT).show()
                        } else {
                            // Update current logged manager ID and role
                            MarketplaceRepository.currentRole.value = "Warehouse"
                            val currentWhId = if (selectedWarehouse.contains("Delhi")) "W01" else if (selectedWarehouse.contains("Sonipat")) "W02" else "W03"
                            MarketplaceRepository.currentUserId.value = currentWhId
                            onSuccess(rememberMe)
                            Toast.makeText(context, "Welcome Back, $managerName!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_btn")
                ) {
                    Text("Secure Authorize", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WarehouseDashboardHome(initialTab: String = "Dashboard", onLogout: () -> Unit) {
    var selectedSubTab by remember(initialTab) { mutableStateOf(initialTab) }
    val tabs = listOf("Dashboard", "Incoming", "Rack Map", "Stock Ledger", "Dispatch", "System Logs")
    val tabIndex = tabs.indexOf(selectedSubTab).coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth().testTag("warehouse_sub_tabs"),
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
                    modifier = Modifier.testTag("warehouse_tab_$title")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                "Dashboard" -> WarehouseDashboardHomeContent(onLogout)
                "Incoming" -> IncomingDeliveriesScreen()
                "Rack Map" -> RackAllocationScreen()
                "Stock Ledger" -> WarehouseInventoryViewContent()
                "Dispatch" -> DispatchManagementScreen()
                "System Logs" -> WarehouseNotificationsScreen()
            }
        }
    }
}

@Composable
fun WarehouseDashboardHomeContent(onLogout: () -> Unit) {
    val inventory by MarketplaceRepository.warehouseInventory.collectAsState()
    val incoming by MarketplaceRepository.incomingDeliveries.collectAsState()
    val dispatches by MarketplaceRepository.warehouseDispatches.collectAsState()
    val managers by MarketplaceRepository.warehouseManagers.collectAsState()

    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()
    val activeManager = managers.find { it.id == currentWhId } ?: managers[0]

    // Calculate dynamic stats
    val localInventory = inventory.filter { it.warehouseId == currentWhId }
    val localIncoming = incoming.filter { it.warehouseId == currentWhId }
    val localDispatches = dispatches.filter { it.warehouseId == currentWhId }

    val totalWeightKg = localInventory.sumOf { it.availableQuantity }
    val incomingCount = localIncoming.filter { it.status == "Pending" }.size
    val outgoingCount = localDispatches.filter { it.status != "Completed" }.size

    val lowStockAlerts = localInventory.filter { it.status == "Low Stock" }.size
    val expiringStockCount = localInventory.filter { it.status == "Expiring" }.size
    val dailyDispatchedCount = localDispatches.filter { it.status == "Completed" || it.status == "Dispatched" }.size

    var showHubSelectorDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("welcome_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeManager.warehouseName,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Authorized Operator: ${activeManager.name} (ID: ${activeManager.id})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { showHubSelectorDialog = true },
                            modifier = Modifier.testTag("switch_hub_btn")
                        ) {
                            Icon(Icons.Default.SwapHoriz, "Switch Hub", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Track microclimate sensor diagnostics, manage bin rack layouts, verify grade and moisture incoming payloads, and reserve dispatches.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showHubSelectorDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warehouse, "Hubs", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Switch Hub", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f).testTag("warehouse_logout_btn")
                        ) {
                            Icon(Icons.Default.ExitToApp, "Logout", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Out Portal", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Vault Capacity Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("capacity_card"),
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
                            text = "Agri-Silo Capacity Usage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${(totalWeightKg / 1000.0).toInt()} MT / ${activeManager.capacityMetricTons.toInt()} MT Max",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val fraction = ((totalWeightKg / 1000.0) / activeManager.capacityMetricTons).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (fraction > 0.85f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${(fraction * 100).toInt()}% of dry hermetic storage vault allocated. Environmental limits are balanced automatically.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Metrics Grid (Using standard layouts)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Available Stock",
                        value = "${totalWeightKg.toInt()} Kg",
                        subtext = "${localInventory.size} Silo items",
                        icon = Icons.Default.Inventory,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Reserved Stock",
                        value = "${localInventory.sumOf { it.reservedQuantity }.toInt()} Kg",
                        subtext = "Secured for buyers",
                        icon = Icons.Default.Lock,
                        color = EarthAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Incoming Deliveries",
                        value = "$incomingCount Pending",
                        subtext = "Exec transit",
                        icon = Icons.Default.LocalShipping,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Outgoing Shipments",
                        value = "$outgoingCount Active",
                        subtext = "Buyer orders",
                        icon = Icons.Default.Send,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Low Stock Alerts",
                        value = "$lowStockAlerts Items",
                        subtext = "Needs replenishment",
                        icon = Icons.Default.Warning,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Expiring Stock",
                        value = "$expiringStockCount Items",
                        subtext = "Shelf-life < 10 days",
                        icon = Icons.Default.Timer,
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Daily Dispatches",
                        value = "$dailyDispatchedCount Orders",
                        subtext = "Out of facility",
                        icon = Icons.Default.DoneAll,
                        color = FarmGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Quick Logs
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("active_status_card"),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Storage Environmental Diagnostics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DiagnosticRow(label = "Internal Temperature", value = "21.5°C", status = "Optimal")
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    DiagnosticRow(label = "Relative Humidity", value = "42.0%", status = "Optimal")
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    DiagnosticRow(label = "Carbon Dioxide Index", value = "380 ppm", status = "Safe")
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    DiagnosticRow(label = "Hermetic Pressure Seals", value = "101.2 kPa", status = "Balanced")
                }
            }
        }
    }

    // Hub Switch Dialog
    if (showHubSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showHubSelectorDialog = false },
            title = { Text("Select Storage Hub") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    managers.forEach { wh ->
                        val isSelected = wh.id == currentWhId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    MarketplaceRepository.currentUserId.value = wh.id
                                    showHubSelectorDialog = false
                                },
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(wh.warehouseName, fontWeight = FontWeight.Bold)
                                Text("Manager: ${wh.name} | Cap: ${wh.capacityMetricTons.toInt()} MT", fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHubSelectorDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("metric_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(subtext, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(FarmGreenPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(status, fontSize = 8.sp, color = FarmGreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 3. INCOMING DELIVERIES SCREEN
@Composable
fun IncomingDeliveriesScreen() {
    val incoming by MarketplaceRepository.incomingDeliveries.collectAsState()
    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()
    val localIncoming = incoming.filter { it.warehouseId == currentWhId }

    var filterStatus by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDeliveryForAction by remember { mutableStateOf<IncomingDelivery?>(null) }
    var selectedDeliveryReceipt by remember { mutableStateOf<IncomingDelivery?>(null) }
    var damageNotes by remember { mutableStateOf("") }
    var showDamageDialog by remember { mutableStateOf(false) }

    val filteredList = localIncoming.filter {
        val matchesStatus = filterStatus == "All" || it.status == filterStatus
        val matchesSearch = it.cropName.contains(searchQuery, ignoreCase = true) ||
                it.farmerName.contains(searchQuery, ignoreCase = true) ||
                it.executiveName.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Incoming Shipments Verification",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Accept, reject, record damages, and generate receipt records for grains brought by logistics Executives.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search crop, farmer, executive...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            modifier = Modifier.fillMaxWidth().testTag("incoming_search"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Row
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Pending", "Accepted", "Rejected", "Damaged").forEach { status ->
                FilterChip(
                    selected = filterStatus == status,
                    onClick = { filterStatus = status },
                    label = { Text(status, fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_incoming_$status")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No incoming shipments found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("incoming_shipments_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { shipment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (shipment.status == "Accepted") {
                                    selectedDeliveryReceipt = shipment
                                } else if (shipment.status == "Pending") {
                                    selectedDeliveryForAction = shipment
                                }
                            }
                            .testTag("shipment_item_${shipment.id}"),
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
                                    text = "ID: ${shipment.id} • Arrival: ${shipment.arrivalTime}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (shipment.status) {
                                                "Accepted" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                                "Rejected" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                                                "Damaged" -> Color(0xFFE65100).copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = shipment.status.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (shipment.status) {
                                            "Accepted" -> FarmGreenPrimary
                                            "Rejected" -> Color(0xFFD32F2F)
                                            "Damaged" -> Color(0xFFE65100)
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${shipment.quantityKg.toInt()} Kg of ${shipment.cropName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("Farmer", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(shipment.farmerName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Executive", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(shipment.executiveName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Parameters", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${shipment.grade} • Mo: ${shipment.moistureLevel}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (shipment.damagesRecorded != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE65100).copy(alpha = 0.08f))
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                ) {
                                    Text(
                                        text = "⚠️ Damage Record: ${shipment.damagesRecorded}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFD84315),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (shipment.receiptNumber != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { selectedDeliveryReceipt = shipment },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Icon(Icons.Default.Description, "Receipt", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Receipt: ${shipment.receiptNumber}", fontSize = 11.sp)
                                }
                            }

                            if (shipment.status == "Pending") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { MarketplaceRepository.acceptIncomingDelivery(shipment.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.weight(1.2f).testTag("accept_shipment_${shipment.id}")
                                    ) {
                                        Icon(Icons.Default.CheckCircle, "Accept", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Accept", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            selectedDeliveryForAction = shipment
                                            showDamageDialog = true
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE65100)),
                                        modifier = Modifier.weight(1f).testTag("damage_shipment_${shipment.id}")
                                    ) {
                                        Icon(Icons.Default.BrokenImage, "Damages", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Damages", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { MarketplaceRepository.rejectIncomingDelivery(shipment.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                        modifier = Modifier.weight(1f).testTag("reject_shipment_${shipment.id}")
                                    ) {
                                        Icon(Icons.Default.Cancel, "Reject", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reject", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Damage notes input dialog
    if (showDamageDialog && selectedDeliveryForAction != null) {
        AlertDialog(
            onDismissRequest = { showDamageDialog = false },
            title = { Text("Record Payload Damages") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter description of damages verified for ${selectedDeliveryForAction!!.cropName} (ID: ${selectedDeliveryForAction!!.id}):")
                    OutlinedTextField(
                        value = damageNotes,
                        onValueChange = { damageNotes = it },
                        placeholder = { Text("Wet bags, moisture mold, burst stitching, etc...") },
                        modifier = Modifier.fillMaxWidth().testTag("damage_notes_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        MarketplaceRepository.recordIncomingDamages(selectedDeliveryForAction!!.id, damageNotes)
                        showDamageDialog = false
                        damageNotes = ""
                    },
                    modifier = Modifier.testTag("submit_damages_btn")
                ) {
                    Text("Log Damages")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDamageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Receipt display dialog
    if (selectedDeliveryReceipt != null) {
        val shipment = selectedDeliveryReceipt!!
        AlertDialog(
            onDismissRequest = { selectedDeliveryReceipt = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, "Receipt", tint = FarmGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Warehouse Receipt Certificate")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FARMLINK DIGITAL WAREHOUSE RECEIPT",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Receipt Number:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.receiptNumber ?: "Pending", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Facility:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Delhi Agri-Store Hub (W01)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Commodity:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.cropName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gross Net Weight:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${shipment.quantityKg.toInt()} Kg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Inspection Grade:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.grade, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Moisture Margin:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${shipment.moistureLevel}% RH", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Depositing Farmer:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.farmerName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivered By Agent:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.executiveName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Receipt Date:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Today, 12:00 PM", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedDeliveryReceipt = null }) {
                    Text("Done")
                }
            }
        )
    }
}

// 4. RACK ALLOCATION SCREEN
@Composable
fun RackAllocationScreen() {
    val inventory by MarketplaceRepository.warehouseInventory.collectAsState()
    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()
    val localInventory = inventory.filter { it.warehouseId == currentWhId }

    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForRack by remember { mutableStateOf<WarehouseInventory?>(null) }

    var inputZone by remember { mutableStateOf("Zone-A") }
    var inputRack by remember { mutableStateOf("Rack-01") }
    var inputShelf by remember { mutableStateOf("Shelf-A") }
    var inputBin by remember { mutableStateOf("Bin-01") }

    val filteredList = localInventory.filter {
        it.cropName.contains(searchQuery, ignoreCase = true) ||
                it.id.contains(searchQuery, ignoreCase = true) ||
                it.shelfLocation.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Storage Vault Layout Allocations",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Assign commodities to hermetic silos, dry zones, and specify row rack layout mapping coordinates.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search crop or location coordinates...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            modifier = Modifier.fillMaxWidth().testTag("rack_search"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No inventory items stored.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("rack_allocations_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { inv ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItemForRack = inv
                                inputZone = inv.storageZone
                                inputRack = inv.rackNumber
                                inputShelf = inv.shelf
                                inputBin = inv.bin
                            }
                            .testTag("rack_item_${inv.id}"),
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
                                    text = "INV ID: ${inv.id}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = inv.shelfLocation,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = inv.cropName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Coordinate Details", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "Zone: ${inv.storageZone} • Row: ${inv.rackNumber} • Shelf: ${inv.shelf} • Bin: ${inv.bin}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedItemForRack = inv
                                        inputZone = inv.storageZone
                                        inputRack = inv.rackNumber
                                        inputShelf = inv.shelf
                                        inputBin = inv.bin
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp).testTag("edit_rack_${inv.id}")
                                ) {
                                    Text("Reallocate", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reallocate dialog
    if (selectedItemForRack != null) {
        val item = selectedItemForRack!!
        AlertDialog(
            onDismissRequest = { selectedItemForRack = null },
            title = { Text("Reallocate Vault Coordinates") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Reallocating ${item.cropName} (ID: ${item.id}) from current coordinates: ${item.shelfLocation}")

                    OutlinedTextField(
                        value = inputZone,
                        onValueChange = { inputZone = it },
                        label = { Text("Storage Zone") },
                        modifier = Modifier.fillMaxWidth().testTag("input_zone")
                    )

                    OutlinedTextField(
                        value = inputRack,
                        onValueChange = { inputRack = it },
                        label = { Text("Rack Number") },
                        modifier = Modifier.fillMaxWidth().testTag("input_rack")
                    )

                    OutlinedTextField(
                        value = inputShelf,
                        onValueChange = { inputShelf = it },
                        label = { Text("Shelf Level") },
                        modifier = Modifier.fillMaxWidth().testTag("input_shelf")
                    )

                    OutlinedTextField(
                        value = inputBin,
                        onValueChange = { inputBin = it },
                        label = { Text("Bin Identifier") },
                        modifier = Modifier.fillMaxWidth().testTag("input_bin")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        MarketplaceRepository.reallocateRack(
                            inventoryId = item.id,
                            zone = inputZone,
                            rack = inputRack,
                            shelf = inputShelf,
                            bin = inputBin
                        )
                        selectedItemForRack = null
                    },
                    modifier = Modifier.testTag("confirm_rack_btn")
                ) {
                    Text("Confirm Storage Mapping")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForRack = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// 5. INVENTORY MANAGEMENT SCREEN
@Composable
fun WarehouseInventoryViewContent() {
    val inventory by MarketplaceRepository.warehouseInventory.collectAsState()
    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()
    val localInventory = inventory.filter { it.warehouseId == currentWhId }

    var filterStatus by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForAdjust by remember { mutableStateOf<WarehouseInventory?>(null) }

    var adjAvailable by remember { mutableStateOf("0.0") }
    var adjDamaged by remember { mutableStateOf("0.0") }
    var adjExpiring by remember { mutableStateOf("0.0") }
    var adjustmentNotes by remember { mutableStateOf("") }

    val filteredList = localInventory.filter {
        val matchesStatus = filterStatus == "All" || it.status == filterStatus
        val matchesSearch = it.cropName.contains(searchQuery, ignoreCase = true) ||
                it.shelfLocation.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Mandi Grain Silos Stock ledger",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Track, audit, and adjust dry commodities, monitor moisture safety standards, and log damage metrics.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search crop or shelf location...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            modifier = Modifier.fillMaxWidth().testTag("inventory_search"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Row
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Stored", "Low Stock", "Expiring").forEach { status ->
                FilterChip(
                    selected = filterStatus == status,
                    onClick = { filterStatus = status },
                    label = { Text(status, fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_inventory_$status")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No inventory items found matching filters.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("inventory_items_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { inv ->
                    val isLowStock = inv.status == "Low Stock"
                    val isExpiring = inv.status == "Expiring"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItemForAdjust = inv
                                adjAvailable = inv.availableQuantity.toString()
                                adjDamaged = inv.damagedQuantity.toString()
                                adjExpiring = inv.expiringQuantity.toString()
                            }
                            .testTag("inventory_card_${inv.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            if (isLowStock) Color(0xFFD32F2F) else if (isExpiring) Color(0xFFE65100) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isLowStock) Icons.Default.Warning else if (isExpiring) Icons.Default.Timer else Icons.Default.CheckCircle,
                                        contentDescription = inv.status,
                                        tint = if (isLowStock) Color(0xFFD32F2F) else if (isExpiring) Color(0xFFE65100) else FarmGreenPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ID: ${inv.id} • ${inv.shelfLocation}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = inv.status.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLowStock) Color(0xFFD32F2F) else if (isExpiring) Color(0xFFE65100) else FarmGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = inv.cropName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quantities row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Available", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${inv.availableQuantity.toInt()} Kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Reserved", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${inv.reservedQuantity.toInt()} Kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EarthAmberPrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Damaged", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${inv.damagedQuantity.toInt()} Kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                }
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text("Shelf Life Left", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${inv.shelfLifeDaysRemaining} days", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (inv.shelfLifeDaysRemaining < 10) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grade: ${inv.grade} | Moisture: ${inv.moistureLevel}%",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        selectedItemForAdjust = inv
                                        adjAvailable = inv.availableQuantity.toString()
                                        adjDamaged = inv.damagedQuantity.toString()
                                        adjExpiring = inv.expiringQuantity.toString()
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(30.dp).testTag("adjust_btn_${inv.id}")
                                ) {
                                    Text("Audit Adjust", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Adjust quantities Dialog
    if (selectedItemForAdjust != null) {
        val item = selectedItemForAdjust!!
        AlertDialog(
            onDismissRequest = { selectedItemForAdjust = null },
            title = { Text("Audit Stock adjustments") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Modify inventory metrics for ${item.cropName} (ID: ${item.id}). Automatically audits status triggers.")

                    OutlinedTextField(
                        value = adjAvailable,
                        onValueChange = { adjAvailable = it },
                        label = { Text("Available Quantity (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("adj_available_field")
                    )

                    OutlinedTextField(
                        value = adjDamaged,
                        onValueChange = { adjDamaged = it },
                        label = { Text("Damaged Quantity (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("adj_damaged_field")
                    )

                    OutlinedTextField(
                        value = adjExpiring,
                        onValueChange = { adjExpiring = it },
                        label = { Text("Expiring / Alerts Qty (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("adj_expiring_field")
                    )

                    OutlinedTextField(
                        value = adjustmentNotes,
                        onValueChange = { adjustmentNotes = it },
                        label = { Text("Audit Logs & Remarks") },
                        placeholder = { Text("Physical bag count calibration...") },
                        modifier = Modifier.fillMaxWidth().testTag("adj_notes_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val avail = adjAvailable.toDoubleOrNull() ?: item.availableQuantity
                        val dmg = adjDamaged.toDoubleOrNull() ?: item.damagedQuantity
                        val exp = adjExpiring.toDoubleOrNull() ?: item.expiringQuantity
                        MarketplaceRepository.adjustStockQuantity(item.id, avail, dmg, exp, adjustmentNotes)
                        selectedItemForAdjust = null
                        adjustmentNotes = ""
                    },
                    modifier = Modifier.testTag("submit_adjust_btn")
                ) {
                    Text("Commit Stock Adjustments")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForAdjust = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// 6. DISPATCH MANAGEMENT SCREEN
@Composable
fun DispatchManagementScreen() {
    val dispatches by MarketplaceRepository.warehouseDispatches.collectAsState()
    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()
    val localDispatches = dispatches.filter { it.warehouseId == currentWhId }

    var filterStatus by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = localDispatches.filter {
        val matchesStatus = filterStatus == "All" || it.status == filterStatus
        val matchesSearch = it.cropName.contains(searchQuery, ignoreCase = true) ||
                it.buyerName.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Buyer Orders Fulfillment Dispatch",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Transition fulfillment states from picking, packing, and dispatching to complete standard order cycles.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search buyer, crop name...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            modifier = Modifier.fillMaxWidth().testTag("dispatch_search"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Row
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Pending Pick", "Picked", "Packed", "Dispatched", "Completed").forEach { status ->
                FilterChip(
                    selected = filterStatus == status,
                    onClick = { filterStatus = status },
                    label = { Text(status, fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_dispatch_$status")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No matching dispatches found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("dispatch_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { dispatch ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dispatch_item_${dispatch.id}"),
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
                                    text = "DISPATCH: ${dispatch.id} • ${dispatch.orderId}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (dispatch.status) {
                                                "Completed" -> FarmGreenPrimary.copy(alpha = 0.15f)
                                                "Dispatched" -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = dispatch.status.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (dispatch.status) {
                                            "Completed" -> FarmGreenPrimary
                                            "Dispatched" -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${dispatch.quantityKg.toInt()} Kg of ${dispatch.cropName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Buyer: ${dispatch.buyerName} | Rack: ${dispatch.rackLocation}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Fulfillment Cycle: ${dispatch.dispatchDate}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (dispatch.status != "Completed") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    when (dispatch.status) {
                                        "Pending Pick" -> {
                                            Button(
                                                onClick = { MarketplaceRepository.pickDispatchItem(dispatch.id) },
                                                modifier = Modifier.testTag("pick_btn_${dispatch.id}")
                                            ) {
                                                Icon(Icons.Default.CropFree, "Pick", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pick Items from Rack", fontSize = 11.sp)
                                            }
                                        }
                                        "Picked" -> {
                                            Button(
                                                onClick = { MarketplaceRepository.packDispatchItem(dispatch.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                modifier = Modifier.testTag("pack_btn_${dispatch.id}")
                                            ) {
                                                Icon(Icons.Default.Inventory2, "Pack", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pack & Check Batch", fontSize = 11.sp)
                                            }
                                        }
                                        "Packed" -> {
                                            Button(
                                                onClick = { MarketplaceRepository.dispatchDispatchItem(dispatch.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = EarthAmberPrimary),
                                                modifier = Modifier.testTag("dispatch_btn_${dispatch.id}")
                                            ) {
                                                Icon(Icons.Default.LocalShipping, "Dispatch", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Ship from Loading Dock", fontSize = 11.sp)
                                            }
                                        }
                                        "Dispatched" -> {
                                            Button(
                                                onClick = { MarketplaceRepository.completeDispatchItem(dispatch.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                                modifier = Modifier.testTag("complete_btn_${dispatch.id}")
                                            ) {
                                                Icon(Icons.Default.CheckCircle, "Complete", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Log & Complete Cycle", fontSize = 11.sp)
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
    }
}

// 7. WAREHOUSE NOTIFICATIONS SCREEN
@Composable
fun WarehouseNotificationsScreen() {
    val notifications by MarketplaceRepository.notifications.collectAsState()
    val currentWhId by MarketplaceRepository.currentUserId.collectAsState()

    // Filter system logs for Warehouse role
    val whNotifications = notifications.filter { it.userRole == "Warehouse" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Warehouse System Alert & Audit Logs",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Real-time records of receipts, low stock warnings, crop shelf-life expirations, and order dispatch events.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (whNotifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No active notifications or logs.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("warehouse_notif_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(whNotifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("notif_${notif.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            notif.title.contains("Low", ignoreCase = true) -> Color(0xFFD32F2F).copy(alpha = 0.12f)
                                            notif.title.contains("Expiring", ignoreCase = true) -> Color(0xFFE65100).copy(alpha = 0.12f)
                                            notif.title.contains("Reserved", ignoreCase = true) -> EarthAmberPrimary.copy(alpha = 0.12f)
                                            else -> FarmGreenPrimary.copy(alpha = 0.12f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        notif.title.contains("Low", ignoreCase = true) -> Icons.Default.Warning
                                        notif.title.contains("Expiring", ignoreCase = true) -> Icons.Default.Timer
                                        notif.title.contains("Reserved", ignoreCase = true) -> Icons.Default.Lock
                                        else -> Icons.Default.NotificationImportant
                                    },
                                    contentDescription = "Notif Icon",
                                    tint = when {
                                        notif.title.contains("Low", ignoreCase = true) -> Color(0xFFD32F2F)
                                        notif.title.contains("Expiring", ignoreCase = true) -> Color(0xFFE65100)
                                        notif.title.contains("Reserved", ignoreCase = true) -> EarthAmberPrimary
                                        else -> FarmGreenPrimary
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = notif.timestamp,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.body,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
