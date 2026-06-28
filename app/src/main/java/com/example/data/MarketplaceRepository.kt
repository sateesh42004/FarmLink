package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

// ==========================================
// DATA MODELS
// ==========================================

data class Farmer(
    val id: String,
    val name: String,
    val village: String,
    val rating: Double,
    val phone: String,
    val cropCount: Int,
    val joinedDate: String,
    val walletBalance: Double,
    val avatarColor: Long
)

data class Buyer(
    val id: String,
    val name: String,
    val city: String,
    val companyName: String,
    val phone: String,
    val walletBalance: Double,
    val joinedDate: String
)

data class PickupExecutive(
    val id: String,
    val name: String,
    val phone: String,
    val area: String,
    val vehicleNumber: String,
    val pendingPickupsCount: Int
)

data class DeliveryExecutive(
    val id: String,
    val name: String,
    val phone: String,
    val zone: String,
    val vehicleNumber: String,
    val completedDeliveriesCount: Int
)

data class WarehouseManager(
    val id: String,
    val name: String,
    val warehouseName: String,
    val capacityMetricTons: Double,
    val filledVolumeMetricTons: Double
)

data class Admin(
    val id: String,
    val name: String,
    val email: String
)

data class CropListing(
    val id: String,
    val farmerId: String,
    val farmerName: String,
    val name: String,
    val category: String, // Grain, Vegetable, Fruit, Pulse, Oilseed
    val quantityKg: Double,
    val pricePerKg: Double,
    val qualityGrade: String, // A, B, C
    val description: String,
    val status: String, // "Available", "Sold", "Awaiting Pickup"
    val listedDate: String,
    val warehouseLocation: String,
    val shelfLifeDays: Int,
    val rating: Double,
    val reviewsCount: Int,
    val pickupId: String? = null,
    val pickupDate: String? = null,
    val executiveName: String? = null,
    val vehicleNumber: String? = null,
    val pickupStatus: String? = null,
    val inspectionReport: QualityInspection? = null,
    
    // Executive Metadata (Added for logistics integration)
    val executiveId: String? = null,
    val executivePhone: String? = null,
    val pickupOtp: String? = null,
    val estimatedArrivalTime: String? = null,
    val liveLocation: String? = null,
    val executiveProfilePhoto: String? = null,

    // Warehouse Metadata (Added for warehouse management integration)
    val warehouseId: String? = null,
    val warehouseName: String? = null,
    val warehouseRackNumber: String? = null,
    val warehouseStorageDate: String? = null,
    val warehouseShelfLife: String? = null,
    val warehouseStatus: String? = null,
    val warehouseAvailableQuantity: Double? = null,
    val warehouseReservedQuantity: Double? = null
)

data class Order(
    val id: String,
    val buyerId: String,
    val buyerName: String,
    val cropListingId: String,
    val cropName: String,
    val quantityKg: Double,
    val pricePerKg: Double,
    val totalAmount: Double,
    val status: String, // "Pending Pickup", "Picked Up", "In Warehouse", "Out for Delivery", "Completed", "Cancelled"
    val orderDate: String,
    val estimatedDelivery: String,
    val farmerId: String,
    val farmerName: String,
    val pickupExecutiveId: String,
    val deliveryExecutiveId: String,
    val warehouseId: String,
    val ratingForFarmer: Int = 0,
    val reviewForFarmer: String = ""
)

data class WalletTransaction(
    val id: String,
    val userId: String,
    val userRole: String, // "Farmer", "Buyer", "Pickup", "Delivery", "Warehouse", "Admin"
    val amount: Double,
    val type: String, // "Credit", "Debit"
    val purpose: String,
    val timestamp: String
)

data class Notification(
    val id: String,
    val userId: String,
    val userRole: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean
)

data class PickupRequest(
    val id: String,
    val orderId: String,
    val farmerId: String,
    val farmerName: String,
    val farmAddress: String,
    val cropName: String,
    val quantityKg: Double,
    val pickupExecutiveId: String,
    val pickupExecutiveName: String,
    val status: String, // "Assigned", "Picked Up", "At Warehouse"
    val requestedTime: String
)

data class DeliveryRequest(
    val id: String,
    val orderId: String,
    val buyerId: String,
    val buyerName: String,
    val deliveryAddress: String,
    val cropName: String,
    val quantityKg: Double,
    val deliveryExecutiveId: String,
    val deliveryExecutiveName: String,
    val status: String, // "Assigned", "Out For Delivery", "Delivered"
    val requestedTime: String
)

data class WarehouseInventory(
    val id: String,
    val warehouseId: String = "W01",
    val warehouseName: String,
    val cropName: String,
    val quantityKg: Double,
    val shelfLocation: String,
    val qualityCheckedBy: String,
    val storageDate: String,
    val rackNumber: String = "Rack-01",
    val shelf: String = "Shelf-A",
    val bin: String = "Bin-01",
    val storageZone: String = "Zone-1",
    val availableQuantity: Double = quantityKg,
    val reservedQuantity: Double = 0.0,
    val damagedQuantity: Double = 0.0,
    val expiringQuantity: Double = 0.0,
    val shelfLifeDaysRemaining: Int = 45,
    val status: String = "Stored", // Stored, Alert, Expiring, Low Stock
    val farmerName: String = "Unknown Farmer",
    val executiveName: String = "Unknown Executive",
    val moistureLevel: Double = 12.0,
    val grade: String = "Grade A",
    val arrivalTime: String = "11:30 AM",
    val isApproved: Boolean = true
)

data class IncomingDelivery(
    val id: String,
    val cropName: String,
    val farmerName: String,
    val executiveName: String,
    val quantityKg: Double,
    val grade: String,
    val moistureLevel: Double,
    val status: String, // "Pending", "Accepted", "Rejected", "Damaged"
    val arrivalTime: String,
    val warehouseId: String,
    val damagesRecorded: String? = null,
    val receiptNumber: String? = null
)

data class WarehouseDispatch(
    val id: String,
    val orderId: String,
    val buyerName: String,
    val cropName: String,
    val quantityKg: Double,
    val status: String, // "Pending Pick", "Picked", "Packed", "Dispatched", "Completed"
    val dispatchDate: String,
    val warehouseId: String,
    val rackLocation: String = "Rack-01"
)

data class MandiPrice(
    val crop: String,
    val category: String,
    val location: String,
    val priceRange: String,
    val avgPrice: Double,
    val yesterdayPrice: Double,
    val priceDiff: Double,
    val trend: String, // "Up", "Down", "Stable"
    val lastUpdated: String
)

data class QualityInspection(
    val id: String,
    val cropListingId: String,
    val cropName: String,
    val grade: String,
    val qualityScore: Double,
    val moistureLevel: Double,
    val executiveNotes: String,
    val inspectionDate: String,
    val isApproved: Boolean,
    val verifiedBadge: Boolean = true
)

data class CustomerReview(
    val id: String,
    val cropName: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class RecentActivity(
    val id: String,
    val description: String,
    val relativeTime: String,
    val category: String // "list", "buy", "pickup", "delivery", "wallet", "mandi"
)

// ==========================================
// CENTRAL REPOSITORY
// ==========================================

object MarketplaceRepository {

    // Current logged-in context
    val currentRole = MutableStateFlow("Farmer") // Options: "Farmer", "Buyer", "Pickup", "Delivery", "Warehouse", "Admin"
    val currentUserId = MutableStateFlow("F01")   // Sync with role below
    
    // User lists
    private val _farmers = MutableStateFlow<List<Farmer>>(emptyList())
    val farmers: StateFlow<List<Farmer>> = _farmers.asStateFlow()

    private val _buyers = MutableStateFlow<List<Buyer>>(emptyList())
    val buyers: StateFlow<List<Buyer>> = _buyers.asStateFlow()

    private val _pickupExecutives = MutableStateFlow<List<PickupExecutive>>(emptyList())
    val pickupExecutives: StateFlow<List<PickupExecutive>> = _pickupExecutives.asStateFlow()

    private val _deliveryExecutives = MutableStateFlow<List<DeliveryExecutive>>(emptyList())
    val deliveryExecutives: StateFlow<List<DeliveryExecutive>> = _deliveryExecutives.asStateFlow()

    private val _warehouseManagers = MutableStateFlow<List<WarehouseManager>>(emptyList())
    val warehouseManagers: StateFlow<List<WarehouseManager>> = _warehouseManagers.asStateFlow()

    private val _admins = MutableStateFlow<List<Admin>>(emptyList())
    val admins: StateFlow<List<Admin>> = _admins.asStateFlow()

    // Business transaction lists
    private val _cropListings = MutableStateFlow<List<CropListing>>(emptyList())
    val cropListings: StateFlow<List<CropListing>> = _cropListings.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _pickupRequests = MutableStateFlow<List<PickupRequest>>(emptyList())
    val pickupRequests: StateFlow<List<PickupRequest>> = _pickupRequests.asStateFlow()

    private val _qualityInspections = MutableStateFlow<List<QualityInspection>>(emptyList())
    val qualityInspections: StateFlow<List<QualityInspection>> = _qualityInspections.asStateFlow()

    private val _deliveryRequests = MutableStateFlow<List<DeliveryRequest>>(emptyList())
    val deliveryRequests: StateFlow<List<DeliveryRequest>> = _deliveryRequests.asStateFlow()

    private val _warehouseInventory = MutableStateFlow<List<WarehouseInventory>>(emptyList())
    val warehouseInventory: StateFlow<List<WarehouseInventory>> = _warehouseInventory.asStateFlow()

    private val _incomingDeliveries = MutableStateFlow<List<IncomingDelivery>>(emptyList())
    val incomingDeliveries: StateFlow<List<IncomingDelivery>> = _incomingDeliveries.asStateFlow()

    private val _warehouseDispatches = MutableStateFlow<List<WarehouseDispatch>>(emptyList())
    val warehouseDispatches: StateFlow<List<WarehouseDispatch>> = _warehouseDispatches.asStateFlow()

    private val _mandiPrices = MutableStateFlow<List<MandiPrice>>(emptyList())
    val mandiPrices: StateFlow<List<MandiPrice>> = _mandiPrices.asStateFlow()

    private val _customerReviews = MutableStateFlow<List<CustomerReview>>(emptyList())
    val customerReviews: StateFlow<List<CustomerReview>> = _customerReviews.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<RecentActivity>>(emptyList())
    val recentActivities: StateFlow<List<RecentActivity>> = _recentActivities.asStateFlow()

    // Search and filters
    val searchQuery = MutableStateFlow("")
    val searchHistory = MutableStateFlow(listOf("Wheat Price", "Organic Onions", "Basmati Rice Grade A", "Potatoes", "Apples"))

    // Buyer specific extra states
    val wishlistItems = MutableStateFlow<Set<String>>(emptySet())
    val recentlyViewedCrops = MutableStateFlow<List<String>>(emptyList())
    val savedAddresses = MutableStateFlow<List<String>>(emptyList())
    val cartItems = MutableStateFlow<Map<String, Double>>(emptyMap()) // map of listingId to qtyKg

    // Helper functions for extra states
    fun toggleWishlist(listingId: String) {
        val current = wishlistItems.value
        if (current.contains(listingId)) {
            wishlistItems.value = current - listingId
        } else {
            wishlistItems.value = current + listingId
        }
    }

    fun addRecentlyViewed(listingId: String) {
        val current = recentlyViewedCrops.value.filter { it != listingId }
        recentlyViewedCrops.value = (listOf(listingId) + current).take(10)
    }

    fun addSavedAddress(address: String) {
        if (address.isNotBlank() && !savedAddresses.value.contains(address)) {
            savedAddresses.value = savedAddresses.value + address
        }
    }

    fun removeSavedAddress(address: String) {
        savedAddresses.value = savedAddresses.value.filter { it != address }
    }

    fun addToCart(listingId: String, qtyKg: Double) {
        val current = cartItems.value.toMutableMap()
        current[listingId] = (current[listingId] ?: 0.0) + qtyKg
        cartItems.value = current
    }

    fun updateCartQuantity(listingId: String, qtyKg: Double) {
        val current = cartItems.value.toMutableMap()
        if (qtyKg <= 0.0) {
            current.remove(listingId)
        } else {
            current[listingId] = qtyKg
        }
        cartItems.value = current
    }

    fun removeFromCart(listingId: String) {
        val current = cartItems.value.toMutableMap()
        current.remove(listingId)
        cartItems.value = current
    }

    fun clearCart() {
        cartItems.value = emptyMap()
    }

    // Submit user review
    fun submitReview(cropName: String, reviewerName: String, rating: Int, comment: String) {
        val newReview = CustomerReview(
            id = "R_${UUID.randomUUID().toString().take(5).uppercase()}",
            cropName = cropName,
            reviewerName = reviewerName,
            rating = rating,
            comment = comment,
            date = "Just now"
        )
        _customerReviews.value = listOf(newReview) + _customerReviews.value
    }

    fun checkoutCart(address: String, paymentMethod: String): List<String> {
        val buyer = _buyers.value.find { it.id == currentUserId.value } ?: return emptyList()
        val cart = cartItems.value
        if (cart.isEmpty()) return emptyList()

        val orderIds = mutableListOf<String>()

        cart.forEach { (listingId, qty) ->
            val listing = _cropListings.value.find { it.id == listingId }
            if (listing != null && listing.quantityKg >= qty) {
                val success = purchaseListing(listingId, qty)
                if (success) {
                    val latestOrder = _orders.value.firstOrNull { it.buyerId == buyer.id && it.cropListingId == listingId }
                    if (latestOrder != null) {
                        orderIds.add(latestOrder.id)
                    }
                }
            }
        }

        clearCart()
        return orderIds
    }

    init {
        generateMockData()
    }

    // Dynamic switch of active profile context
    fun switchRole(role: String) {
        currentRole.value = role
        currentUserId.value = when (role) {
            "Farmer" -> "F01"
            "Buyer" -> "B01"
            "Pickup" -> "P01"
            "Delivery" -> "D01"
            "Warehouse" -> "W01"
            "Admin" -> "A01"
            else -> "F01"
        }
    }

    // Helper to get current user balance
    fun getCurrentUserBalance(): Double {
        return when (currentRole.value) {
            "Farmer" -> _farmers.value.find { it.id == currentUserId.value }?.walletBalance ?: 0.0
            "Buyer" -> _buyers.value.find { it.id == currentUserId.value }?.walletBalance ?: 0.0
            else -> 12500.0 // Standard operational wallet for executives/managers
        }
    }

    // ==========================================
    // MUTATION / CRUD OPERATIONS
// ==========================================

    // Farmer: Create Listing
    fun createCropListing(
        name: String,
        category: String,
        quantityKg: Double,
        pricePerKg: Double,
        qualityGrade: String,
        description: String,
        warehouseLocation: String = "Central Hub Delhi",
        status: String = "Draft"
    ): Boolean {
        val farmer = _farmers.value.find { it.id == currentUserId.value } ?: return false
        val newId = "CROP_${UUID.randomUUID().toString().take(6).uppercase()}"
        
        var pickupId: String? = null
        var pickupDate: String? = null
        var pickupStatus: String? = null
        var execId: String? = null
        var execName: String? = null
        var execPhone: String? = null
        var vehNum: String? = null
        var pOtp: String? = null
        var estArrival: String? = null
        var liveLoc: String? = null
        var execPic: String? = null
        
        if (status == "Pickup Requested") {
            pickupId = "PKP_${UUID.randomUUID().toString().take(5).uppercase()}"
            pickupDate = "Tomorrow, 10:00 AM"
            pickupStatus = "Requested"
            execId = "EXE_8293"
            execName = "Satish Yadav"
            execPhone = "+91 98765 43210"
            vehNum = "DL-1L-AA-2342"
            pOtp = "4829"
            estArrival = "Tomorrow, 10:30 AM"
            liveLoc = "28.7041° N, 77.1025° E (Delhi Outer Ring Road)"
            execPic = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80"
        }

        val shelfLife = when(category) {
            "Grain" -> 180
            "Pulse" -> 240
            "Vegetable" -> 7
            "Fruit" -> 14
            else -> 30
        }

        val newListing = CropListing(
            id = newId,
            farmerId = farmer.id,
            farmerName = farmer.name,
            name = name,
            category = category,
            quantityKg = quantityKg,
            pricePerKg = pricePerKg,
            qualityGrade = qualityGrade,
            description = description,
            status = status,
            listedDate = "Today, 09:30 AM",
            warehouseLocation = warehouseLocation,
            shelfLifeDays = shelfLife,
            rating = 4.8,
            reviewsCount = 0,
            pickupId = pickupId,
            pickupDate = pickupDate,
            pickupStatus = pickupStatus,
            executiveId = execId,
            executiveName = execName,
            executivePhone = execPhone,
            vehicleNumber = vehNum,
            pickupOtp = pOtp,
            estimatedArrivalTime = estArrival,
            liveLocation = liveLoc,
            executiveProfilePhoto = execPic
        )
        _cropListings.value = listOf(newListing) + _cropListings.value
        
        // Update farmer crop count
        _farmers.value = _farmers.value.map {
            if (it.id == farmer.id) it.copy(cropCount = it.cropCount + 1) else it
        }

        addActivity("You created crop $name (${quantityKg.toInt()} kg) as $status", "list")
        if (status == "Draft") {
            addNotification(farmer.id, "Farmer", "Crop Draft Saved", "Your crop $name has been saved as a Draft. You can request pickup anytime.")
        } else {
            addNotification(farmer.id, "Farmer", "Pickup Requested Successfully", "Your crop $name is submitted and pickup request $pickupId generated.")
        }
        return true
    }

    // Farmer: Delete/Cancel Listing
    fun deleteCropListing(listingId: String): Boolean {
        val listing = _cropListings.value.find { it.id == listingId } ?: return false
        _cropListings.value = _cropListings.value.filter { it.id != listingId }
        addActivity("Listing for ${listing.name} was removed", "list")
        return true
    }

    // Farmer: Edit Crop Listing
    fun editCropListing(listingId: String, name: String, category: String, qty: Double, price: Double, grade: String, desc: String): Boolean {
        _cropListings.value = _cropListings.value.map {
            if (it.id == listingId) {
                it.copy(
                    name = name,
                    category = category,
                    quantityKg = qty,
                    pricePerKg = price,
                    qualityGrade = grade,
                    description = desc
                )
            } else it
        }
        addActivity("Listing $listingId was updated", "list")
        return true
    }

    // Farmer: Pause/Resume Listing or update status
    fun updateCropListingStatus(listingId: String, newStatus: String): Boolean {
        _cropListings.value = _cropListings.value.map {
            if (it.id == listingId) it.copy(status = newStatus) else it
        }
        addActivity("Listing $listingId set to $newStatus", "list")
        return true
    }

    // Farmer: Publish Crop and create pickup request
    fun publishFarmerCrop(
        name: String,
        category: String,
        variety: String,
        harvestDate: String,
        quantity: Double,
        unit: String,
        description: String,
        mandiPrice: Double,
        sellingPrice: Double,
        pickupDate: String,
        address: String,
        village: String,
        district: String,
        notes: String
    ): Boolean {
        val farmer = _farmers.value.find { it.id == currentUserId.value } ?: _farmers.value.find { it.id == "F01" } ?: return false
        val newId = "CROP_${UUID.randomUUID().toString().take(6).uppercase()}"
        val finalQuantityKg = if (unit == "Quintal") quantity * 100.0 else quantity
        val newListing = CropListing(
            id = newId,
            farmerId = farmer.id,
            farmerName = farmer.name,
            name = if (variety.isNotBlank()) "$name ($variety)" else name,
            category = category,
            quantityKg = finalQuantityKg,
            pricePerKg = sellingPrice,
            qualityGrade = "Grade A",
            description = if (description.isBlank()) "Freshly harvested $name from $village. Notes: $notes" else description,
            status = "Available",
            listedDate = "Today",
            warehouseLocation = "Delhi Agri-Store Hub",
            shelfLifeDays = 30,
            rating = 4.8,
            reviewsCount = 0
        )
        _cropListings.value = listOf(newListing) + _cropListings.value

        // Update farmer crop count
        _farmers.value = _farmers.value.map {
            if (it.id == farmer.id) it.copy(cropCount = it.cropCount + 1) else it
        }

        // Create Pickup Request
        val pickupId = "PKP_${UUID.randomUUID().toString().take(5).uppercase()}"
        val newPickup = PickupRequest(
            id = pickupId,
            orderId = "ORD_P_" + UUID.randomUUID().toString().take(4).uppercase(),
            farmerId = farmer.id,
            farmerName = farmer.name,
            farmAddress = "$address, $village, $district",
            cropName = name,
            quantityKg = finalQuantityKg,
            pickupExecutiveId = _pickupExecutives.value.randomOrNull()?.id ?: "PE_01",
            pickupExecutiveName = _pickupExecutives.value.randomOrNull()?.name ?: "Satish Yadav",
            status = "Pending",
            requestedTime = pickupDate
        )
        _pickupRequests.value = listOf(newPickup) + _pickupRequests.value

        addActivity("You published $quantity $unit of $name", "list")
        addNotification(farmer.id, "Farmer", "Crop Listed & Pickup Created", "Your crop $name is now live. Pickup request $pickupId generated.")
        return true
    }

    // Farmer: Update Order Status (Accept, Reject, Preparing, Ready for Pickup, Completed)
    fun updateOrderStatusFarmer(orderId: String, newStatus: String): Boolean {
        _orders.value = _orders.value.map {
            if (it.id == orderId) {
                val updated = it.copy(status = newStatus)
                addNotification(it.buyerId, "Buyer", "Order $newStatus", "Your order #${it.id} for ${it.cropName} is now '$newStatus'.")
                addNotification(it.farmerId, "Farmer", "Order Status Updated", "You updated order #${it.id} to '$newStatus'.")
                updated
            } else it
        }
        addActivity("Order $orderId updated to '$newStatus'", "buy")
        return true
    }

    // Farmer: Cancel Pickup Request
    fun cancelPickupRequest(pickupId: String): Boolean {
        _pickupRequests.value = _pickupRequests.value.map {
            if (it.id == pickupId) it.copy(status = "Cancelled") else it
        }
        addActivity("Pickup $pickupId was cancelled", "pickup")
        return true
    }

    // Farmer: Reschedule Pickup Request
    fun reschedulePickupRequest(pickupId: String, newDate: String): Boolean {
        _pickupRequests.value = _pickupRequests.value.map {
            if (it.id == pickupId) it.copy(requestedTime = newDate, status = "Rescheduled") else it
        }
        addActivity("Pickup $pickupId rescheduled to $newDate", "pickup")
        return true
    }

    // Farmer: Notifications Management
    fun markNotificationAsRead(notifId: String): Boolean {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
        return true
    }

    fun deleteNotification(notifId: String): Boolean {
        _notifications.value = _notifications.value.filter { it.id != notifId }
        return true
    }

    fun clearAllNotifications(): Boolean {
        val currentUserId = currentUserId.value
        _notifications.value = _notifications.value.filter { it.userId != currentUserId }
        return true
    }

    // Farmer: Quality Inspections helper
    fun getQualityInspection(pickupId: String): QualityInspection {
        val existing = _qualityInspections.value.find { it.id == pickupId || it.cropListingId == pickupId }
        if (existing != null) return existing
        
        val r = java.util.Random(pickupId.hashCode().toLong())
        val grades = listOf("Grade A", "Grade B", "Grade C")
        val isApproved = r.nextInt(10) > 1 // mostly approved
        return QualityInspection(
            id = "INSP_${pickupId.takeLast(4)}",
            cropListingId = pickupId,
            cropName = "Crop Verified",
            grade = grades[r.nextInt(3)],
            qualityScore = 80.0 + r.nextDouble() * 18.0,
            moistureLevel = 10.0 + r.nextDouble() * 5.0,
            executiveNotes = "Passed physical verification. Moister level matches target shelf parameters. No pests found.",
            inspectionDate = "Yesterday, 04:30 PM",
            isApproved = isApproved,
            verifiedBadge = isApproved
        )
    }

    // Buyer: Purchase Crop Listing (Creates Order, Pickup request, Deducts wallet, etc.)
    fun purchaseListing(listingId: String, qtyKg: Double): Boolean {
        val listing = _cropListings.value.find { it.id == listingId } ?: return false
        val buyer = _buyers.value.find { it.id == currentUserId.value } ?: return false
        
        val totalCost = qtyKg * listing.pricePerKg
        if (buyer.walletBalance < totalCost) {
            addNotification(buyer.id, "Buyer", "Purchase Failed", "Insufficient wallet balance. Total amount: ₹$totalCost")
            return false
        }

        // Deduct from buyer wallet
        _buyers.value = _buyers.value.map {
            if (it.id == buyer.id) it.copy(walletBalance = it.walletBalance - totalCost) else it
        }

        // Record buyer wallet transaction
        recordTransaction(buyer.id, "Buyer", totalCost, "Debit", "Purchase: ${listing.name} (${qtyKg.toInt()} kg)")

        // Add to farmer wallet (in production this might be escrowed, we credit farmer directly to see immediate update)
        _farmers.value = _farmers.value.map {
            if (it.id == listing.farmerId) it.copy(walletBalance = it.walletBalance + totalCost) else it
        }
        recordTransaction(listing.farmerId, "Farmer", totalCost, "Credit", "Sold: ${listing.name} (${qtyKg.toInt()} kg)")

        // Update listing quantity or mark as sold
        if (qtyKg >= listing.quantityKg) {
            _cropListings.value = _cropListings.value.map {
                if (it.id == listingId) it.copy(status = "Sold", quantityKg = 0.0) else it
            }
        } else {
            _cropListings.value = _cropListings.value.map {
                if (it.id == listingId) it.copy(quantityKg = it.quantityKg - qtyKg) else it
            }
        }

        // Create Order
        val orderId = "ORD_${UUID.randomUUID().toString().take(6).uppercase()}"
        val pickupExec = _pickupExecutives.value.random()
        val deliveryExec = _deliveryExecutives.value.random()
        val warehouse = _warehouseManagers.value.random()

        val newOrder = Order(
            id = orderId,
            buyerId = buyer.id,
            buyerName = buyer.name,
            cropListingId = listing.id,
            cropName = listing.name,
            quantityKg = qtyKg,
            pricePerKg = listing.pricePerKg,
            totalAmount = totalCost,
            status = "Pending Pickup",
            orderDate = "Today, 09:40 AM",
            estimatedDelivery = "In 3 Days",
            farmerId = listing.farmerId,
            farmerName = listing.farmerName,
            pickupExecutiveId = pickupExec.id,
            deliveryExecutiveId = deliveryExec.id,
            warehouseId = warehouse.id
        )

        _orders.value = listOf(newOrder) + _orders.value

        // Generate matching Pickup Request immediately
        val pickupReq = PickupRequest(
            id = "PKP_${UUID.randomUUID().toString().take(5).uppercase()}",
            orderId = orderId,
            farmerId = listing.farmerId,
            farmerName = listing.farmerName,
            farmAddress = "${listing.farmerName}'s Farm, ${listing.farmerName} Village",
            cropName = listing.name,
            quantityKg = qtyKg,
            pickupExecutiveId = pickupExec.id,
            pickupExecutiveName = pickupExec.name,
            status = "Assigned",
            requestedTime = "Today, 09:40 AM"
        )
        _pickupRequests.value = listOf(pickupReq) + _pickupRequests.value

        // Send notifications
        addNotification(buyer.id, "Buyer", "Order Placed Successfully", "Your order #$orderId for ${qtyKg.toInt()}kg ${listing.name} was successfully placed!")
        addNotification(listing.farmerId, "Farmer", "Crop Sold!", "Buyer ${buyer.name} bought ${qtyKg.toInt()}kg of ${listing.name} for ₹$totalCost.")
        addNotification(pickupExec.id, "Pickup", "New Pickup Assigned", "Pick up ${qtyKg.toInt()}kg ${listing.name} from ${listing.farmerName}.")

        addActivity("${buyer.name} purchased ${qtyKg.toInt()} kg of ${listing.name} for ₹$totalCost", "buy")
        return true
    }

    // Pickup Executive: Update Pickup Status
    fun updatePickupStatus(requestId: String, nextStatus: String): Boolean {
        val request = _pickupRequests.value.find { it.id == requestId } ?: return false
        
        // Update pickup status
        _pickupRequests.value = _pickupRequests.value.map {
            if (it.id == requestId) it.copy(status = nextStatus) else it
        }

        // Sync with related Order status
        val updatedOrderStatus = when (nextStatus) {
            "Picked Up" -> "Picked Up"
            "At Warehouse" -> "In Warehouse"
            else -> "Pending Pickup"
        }

        _orders.value = _orders.value.map {
            if (it.id == request.orderId) it.copy(status = updatedOrderStatus) else it
        }

        val order = _orders.value.find { it.id == request.orderId }

        // If arrived at warehouse, push to warehouse inventory
        if (nextStatus == "At Warehouse" && order != null) {
            val warehouse = _warehouseManagers.value.find { it.id == order.warehouseId }
            val newInventoryItem = WarehouseInventory(
                id = "INV_${UUID.randomUUID().toString().take(6).uppercase()}",
                warehouseName = warehouse?.warehouseName ?: "Delhi Agri-Store Hub",
                cropName = request.cropName,
                quantityKg = request.quantityKg,
                shelfLocation = "Row A, Shelf ${Random.nextInt(1, 10)}",
                qualityCheckedBy = warehouse?.name ?: "Vikas Sharma",
                storageDate = "Today, 11:30 AM"
            )
            _warehouseInventory.value = listOf(newInventoryItem) + _warehouseInventory.value

            // Generate delivery request automatically when in warehouse
            val delExec = _deliveryExecutives.value.find { it.id == order.deliveryExecutiveId } ?: _deliveryExecutives.value.random()
            val buyer = _buyers.value.find { it.id == order.buyerId }
            val delRequest = DeliveryRequest(
                id = "DEL_${UUID.randomUUID().toString().take(5).uppercase()}",
                orderId = order.id,
                buyerId = order.buyerId,
                buyerName = order.buyerName,
                deliveryAddress = buyer?.city ?: "Green Agro Depot, Delhi",
                cropName = order.cropName,
                quantityKg = order.quantityKg,
                deliveryExecutiveId = delExec.id,
                deliveryExecutiveName = delExec.name,
                status = "Assigned",
                requestedTime = "Today, 12:00 PM"
            )
            _deliveryRequests.value = listOf(delRequest) + _deliveryRequests.value
            
            addNotification(order.deliveryExecutiveId, "Delivery", "New Delivery Assigned", "Deliver ${order.quantityKg.toInt()}kg ${order.cropName} to ${order.buyerName}.")
        }

        addActivity("Pickup Executive updated request $requestId to '$nextStatus'", "pickup")
        return true
    }

    // Assign executive to a pickup request
    fun assignPickupExecutive(requestId: String, executiveId: String): Boolean {
        val exec = _pickupExecutives.value.find { it.id == executiveId } ?: return false
        _pickupRequests.value = _pickupRequests.value.map { req ->
            if (req.id == requestId) {
                req.copy(
                    pickupExecutiveId = exec.id,
                    pickupExecutiveName = exec.name
                )
            } else {
                req
            }
        }
        addActivity("Pickup assignment $requestId claimed by ${exec.name}", "pickup")
        return true
    }

    // Submit inspection from Pickup Executive
    fun submitQualityInspection(
        requestId: String,
        grade: String,
        qualityScore: Double,
        moistureLevel: Double,
        executiveNotes: String,
        isApproved: Boolean
    ): Boolean {
        val request = _pickupRequests.value.find { it.id == requestId } ?: return false
        
        val nextStatus = if (isApproved) "Inspection Approved" else "Inspection Rejected"
        _pickupRequests.value = _pickupRequests.value.map {
            if (it.id == requestId) it.copy(status = nextStatus) else it
        }

        val inspectionId = "INSP_${UUID.randomUUID().toString().take(5).uppercase()}"
        val inspection = QualityInspection(
            id = inspectionId,
            cropListingId = request.orderId,
            cropName = request.cropName,
            grade = grade,
            qualityScore = qualityScore,
            moistureLevel = moistureLevel,
            executiveNotes = executiveNotes,
            inspectionDate = "Today, 11:30 AM",
            isApproved = isApproved,
            verifiedBadge = isApproved
        )
        _qualityInspections.value = listOf(inspection) + _qualityInspections.value

        // Update connected crop listings
        _cropListings.value = _cropListings.value.map { listing ->
            if (listing.pickupId == requestId || listing.id == request.orderId) {
                listing.copy(
                    qualityGrade = grade,
                    inspectionReport = inspection,
                    pickupStatus = nextStatus
                )
            } else {
                listing
            }
        }

        // Update related order status
        val orderStatus = if (isApproved) "Inspection Approved" else "Inspection Rejected"
        _orders.value = _orders.value.map { ord ->
            if (ord.id == request.orderId) ord.copy(status = orderStatus) else ord
        }

        // Generate notifications
        addNotification(request.farmerId, "Farmer", "Quality Inspection Complete", "Your crop listing for ${request.cropName} was inspected. Result: $nextStatus. Grade: $grade.")
        
        val order = _orders.value.find { it.id == request.orderId }
        if (order != null) {
            addNotification(order.buyerId, "Buyer", "Quality Inspection Passed", "The crop ${order.cropName} for your order #${order.id} has successfully passed FarmLink quality inspection with Grade $grade.")
        }

        addActivity("Quality inspection complete: $nextStatus for ${request.cropName} (Score: $qualityScore)", "pickup")
        return true
    }

    // Delivery Executive: Update Delivery Status
    fun updateDeliveryStatus(requestId: String, nextStatus: String): Boolean {
        val request = _deliveryRequests.value.find { it.id == requestId } ?: return false
        _deliveryRequests.value = _deliveryRequests.value.map {
            if (it.id == requestId) it.copy(status = nextStatus) else it
        }

        val orderStatus = when (nextStatus) {
            "Out For Delivery" -> "Out for Delivery"
            "Delivered" -> "Completed"
            else -> "In Warehouse"
        }

        _orders.value = _orders.value.map {
            if (it.id == request.orderId) it.copy(status = orderStatus) else it
        }

        val order = _orders.value.find { it.id == request.orderId }
        if (nextStatus == "Delivered" && order != null) {
            addNotification(order.buyerId, "Buyer", "Order Delivered!", "Your order #${order.id} for ${order.cropName} was delivered successfully. Leave a review!")
            addNotification(order.farmerId, "Farmer", "Delivery Complete", "Your produce for order #${order.id} has reached the buyer.")
            
            // Increment executive completed deliveries
            _deliveryExecutives.value = _deliveryExecutives.value.map {
                if (it.id == request.deliveryExecutiveId) it.copy(completedDeliveriesCount = it.completedDeliveriesCount + 1) else it
            }
        }

        addActivity("Delivery Executive updated delivery $requestId to '$nextStatus'", "delivery")
        return true
    }

    // Buyer: Rate/Review Order
    fun rateOrder(orderId: String, rating: Int, review: String): Boolean {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(ratingForFarmer = rating, reviewForFarmer = review) else it
        }

        val order = _orders.value.find { it.id == orderId } ?: return false
        
        // Add to customer reviews
        val newReview = CustomerReview(
            id = UUID.randomUUID().toString().take(6),
            cropName = order.cropName,
            reviewerName = order.buyerName,
            rating = rating,
            comment = review,
            date = "Just now"
        )
        _customerReviews.value = listOf(newReview) + _customerReviews.value

        addNotification(order.farmerId, "Farmer", "New Buyer Review", "Buyer ${order.buyerName} rated your ${order.cropName} delivery: $rating Stars!")
        addActivity("${order.buyerName} gave ${order.farmerName} $rating stars for ${order.cropName}", "buy")
        return true
    }

    // Wallet: Deposit/Add Funds
    fun depositFunds(amount: Double): Boolean {
        if (amount <= 0) return false
        when (currentRole.value) {
            "Farmer" -> {
                _farmers.value = _farmers.value.map {
                    if (it.id == currentUserId.value) it.copy(walletBalance = it.walletBalance + amount) else it
                }
                recordTransaction(currentUserId.value, "Farmer", amount, "Credit", "Added cash deposit")
            }
            "Buyer" -> {
                _buyers.value = _buyers.value.map {
                    if (it.id == currentUserId.value) it.copy(walletBalance = it.walletBalance + amount) else it
                }
                recordTransaction(currentUserId.value, "Buyer", amount, "Credit", "Added credit deposit")
            }
        }
        addActivity("You deposited ₹$amount to your FarmLink wallet", "wallet")
        return true
    }

    // Wallet: Withdraw Funds
    fun withdrawFunds(amount: Double): Boolean {
        if (amount <= 0) return false
        val currentBalance = getCurrentUserBalance()
        if (currentBalance < amount) return false

        when (currentRole.value) {
            "Farmer" -> {
                _farmers.value = _farmers.value.map {
                    if (it.id == currentUserId.value) it.copy(walletBalance = it.walletBalance - amount) else it
                }
                recordTransaction(currentUserId.value, "Farmer", amount, "Debit", "Withdrawal to Bank Account")
            }
            "Buyer" -> {
                _buyers.value = _buyers.value.map {
                    if (it.id == currentUserId.value) it.copy(walletBalance = it.walletBalance - amount) else it
                }
                recordTransaction(currentUserId.value, "Buyer", amount, "Debit", "Withdrawal of refund")
            }
        }
        addActivity("You withdrew ₹$amount from your FarmLink wallet", "wallet")
        return true
    }

    // Admin: Update Mandi Prices
    fun updateMandiPrice(cropName: String, newRange: String, avgPrice: Double, trend: String) {
        _mandiPrices.value = _mandiPrices.value.map {
            if (it.crop == cropName) it.copy(priceRange = newRange, avgPrice = avgPrice, trend = trend, lastUpdated = "Today, 09:00 AM") else it
        }
        addActivity("Admin updated Mandi price for $cropName to ₹$avgPrice/kg", "mandi")
        // Notify everyone
        _farmers.value.forEach { f ->
            addNotification(f.id, "Farmer", "Mandi Prices Updated", "$cropName is now trading at average ₹$avgPrice/kg.")
        }
    }

    // Admin: Moderate Orders or Users (Mock admin command)
    fun cancelOrderAdmin(orderId: String): Boolean {
        val order = _orders.value.find { it.id == orderId } ?: return false
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(status = "Cancelled") else it
        }
        addNotification(order.buyerId, "Buyer", "Order Cancelled by Admin", "Order #$orderId has been refunded and cancelled by Admin.")
        addNotification(order.farmerId, "Farmer", "Order Cancelled by Admin", "Order #$orderId has been cancelled by Admin.")
        addActivity("Admin cancelled order $orderId", "buy")
        return true
    }

    // ==========================================
    // PRIVATE INTERNAL HELPERS
    // ==========================================

    fun addActivity(description: String, category: String) {
        val newAct = RecentActivity(
            id = UUID.randomUUID().toString().take(6),
            description = description,
            relativeTime = "Just now",
            category = category
        )
        _recentActivities.value = listOf(newAct) + _recentActivities.value.take(29)
    }

    fun addNotification(userId: String, role: String, title: String, body: String) {
        val notif = Notification(
            id = "NOT_${UUID.randomUUID().toString().take(5).uppercase()}",
            userId = userId,
            userRole = role,
            title = title,
            body = body,
            timestamp = "Just now",
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun recordTransaction(userId: String, role: String, amount: Double, type: String, purpose: String) {
        val transaction = WalletTransaction(
            id = "TXN_${UUID.randomUUID().toString().take(6).uppercase()}",
            userId = userId,
            userRole = role,
            amount = amount,
            type = type,
            purpose = purpose,
            timestamp = "Today, 09:40 AM"
        )
        _walletTransactions.value = listOf(transaction) + _walletTransactions.value
    }

    // Farmer Module Complete Business Workflow Simulation
    fun updateCropListingWorkflow(listingId: String, nextStatus: String, extraData: Map<String, Any> = emptyMap()): Boolean {
        _cropListings.value = _cropListings.value.map { item ->
            if (item.id == listingId) {
                var updated = item.copy(status = nextStatus)
                val farmerId = item.farmerId

                when (nextStatus) {
                    "Draft" -> {
                        updated = updated.copy(
                            pickupId = null,
                            pickupDate = null,
                            executiveName = null,
                            vehicleNumber = null,
                            pickupStatus = null,
                            inspectionReport = null,
                            executiveId = null,
                            executivePhone = null,
                            pickupOtp = null,
                            estimatedArrivalTime = null,
                            liveLocation = null,
                            executiveProfilePhoto = null,
                            warehouseId = null,
                            warehouseName = null,
                            warehouseRackNumber = null,
                            warehouseStorageDate = null,
                            warehouseShelfLife = null,
                            warehouseStatus = null,
                            warehouseAvailableQuantity = null,
                            warehouseReservedQuantity = null
                        )
                        addActivity("Reset ${item.name} to Draft", "list")
                    }
                    "Pickup Requested" -> {
                        val pId = "PKP_${UUID.randomUUID().toString().take(5).uppercase()}"
                        val pDate = (extraData["pickupDate"] as? String) ?: "Tomorrow, 10:00 AM"
                        updated = updated.copy(
                            pickupId = pId,
                            pickupDate = pDate,
                            pickupStatus = "Requested",
                            executiveId = "EXE_8293",
                            executiveName = "Satish Yadav",
                            executivePhone = "+91 98765 43210",
                            vehicleNumber = "DL-1L-AA-2342",
                            pickupOtp = "4829",
                            estimatedArrivalTime = "Tomorrow, 10:30 AM",
                            liveLocation = "28.7041° N, 77.1025° E (Delhi Outer Ring Road)",
                            executiveProfilePhoto = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80"
                        )
                        addNotification(farmerId, "Farmer", "Pickup Requested Successfully", "Your pickup request $pId for ${item.name} has been received.")
                        addActivity("Pickup requested for ${item.name}", "pickup")
                    }
                    "Executive Assigned" -> {
                        val execName = "Satish Yadav"
                        val vehNum = "DL-1L-AA-2342"
                        updated = updated.copy(
                            executiveId = "EXE_8293",
                            executiveName = execName,
                            executivePhone = "+91 98765 43210",
                            vehicleNumber = vehNum,
                            pickupOtp = "4829",
                            estimatedArrivalTime = "Tomorrow, 10:30 AM",
                            liveLocation = "28.7041° N, 77.1025° E (Delhi Outer Ring Road)",
                            executiveProfilePhoto = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                            pickupStatus = "Executive Assigned"
                        )
                        addNotification(farmerId, "Farmer", "Executive Assigned", "Executive $execName ($vehNum) has been assigned to your crop ${item.name} pickup.")
                        addNotification(farmerId, "Farmer", "Pickup Tomorrow", "Your pickup request for ${item.name} is scheduled for tomorrow with executive $execName.")
                        addActivity("Executive assigned for ${item.name} pickup", "pickup")
                    }
                    "Inspection Pending" -> {
                        updated = updated.copy(
                            pickupStatus = "On the way",
                            liveLocation = "28.6139° N, 77.2090° E (Near Farm Entrance)",
                            estimatedArrivalTime = "In 15 minutes"
                        )
                        addNotification(farmerId, "Farmer", "Executive Visiting Farm", "Executive Satish Yadav is arriving at your farm for quality inspection.")
                        addActivity("Executive arriving for inspection of ${item.name}", "pickup")
                    }
                    "Approved" -> {
                        val report = QualityInspection(
                            id = "INS_${UUID.randomUUID().toString().take(5).uppercase()}",
                            cropListingId = item.id,
                            cropName = item.name,
                            grade = "Grade A",
                            qualityScore = 94.5,
                            moistureLevel = 11.2,
                            executiveNotes = "Excellent grain size, optimal moisture content under 12%, highly recommended.",
                            inspectionDate = "Today",
                            isApproved = true,
                            verifiedBadge = true
                        )
                        updated = updated.copy(
                            qualityGrade = "Grade A",
                            inspectionReport = report,
                            pickupStatus = "Inspected & Approved",
                            warehouseId = "WH_DEL_04",
                            warehouseName = "Delhi Central Agri-Silo",
                            warehouseRackNumber = "Silo-B, Rack-12",
                            warehouseStorageDate = "Today, 02:30 PM",
                            warehouseShelfLife = "${item.shelfLifeDays} days remaining",
                            warehouseStatus = "Temperature Controlled Silo",
                            warehouseAvailableQuantity = item.quantityKg,
                            warehouseReservedQuantity = 0.0
                        )
                        addNotification(farmerId, "Farmer", "Crop Approved", "Great news! Your crop ${item.name} was approved with Grade A (Score: 94.5%).")
                        addActivity("Crop ${item.name} quality approved", "list")
                    }
                    "Rejected" -> {
                        val report = QualityInspection(
                            id = "INS_${UUID.randomUUID().toString().take(5).uppercase()}",
                            cropListingId = item.id,
                            cropName = item.name,
                            grade = "Grade C",
                            qualityScore = 52.0,
                            moistureLevel = 18.5,
                            executiveNotes = "High moisture content (18.5%) and visible discoloration. Does not meet standard guidelines.",
                            inspectionDate = "Today",
                            isApproved = false,
                            verifiedBadge = false
                        )
                        updated = updated.copy(
                            qualityGrade = "Grade C",
                            inspectionReport = report,
                            pickupStatus = "Inspected & Rejected",
                            warehouseId = null,
                            warehouseName = null,
                            warehouseRackNumber = null,
                            warehouseStorageDate = null,
                            warehouseShelfLife = null,
                            warehouseStatus = null,
                            warehouseAvailableQuantity = null,
                            warehouseReservedQuantity = null
                        )
                        addNotification(farmerId, "Farmer", "Crop Rejected", "Your crop ${item.name} did not pass quality inspection due to high moisture.")
                        addActivity("Crop ${item.name} was rejected", "list")
                    }
                    "Warehouse Received" -> {
                        updated = updated.copy(
                            pickupStatus = "At Warehouse",
                            warehouseLocation = "Delhi Central Agri-Silo",
                            warehouseId = "WH_DEL_04",
                            warehouseName = "Delhi Central Agri-Silo",
                            warehouseRackNumber = "Silo-B, Rack-12",
                            warehouseStorageDate = "Today, 04:15 PM",
                            warehouseShelfLife = "${item.shelfLifeDays} days remaining",
                            warehouseStatus = "Received & Safely Deposited",
                            warehouseAvailableQuantity = item.quantityKg,
                            warehouseReservedQuantity = 0.0
                        )
                        addNotification(farmerId, "Farmer", "Warehouse Received Crop", "Your approved crop ${item.name} has been safely received at Delhi Central Agri-Silo.")
                        addActivity("Crop ${item.name} stored in warehouse", "pickup")
                    }
                    "Published" -> {
                        updated = updated.copy(
                            pickupStatus = "Completed",
                            warehouseId = "WH_DEL_04",
                            warehouseName = "Delhi Central Agri-Silo",
                            warehouseRackNumber = "Silo-B, Rack-12",
                            warehouseStorageDate = "Today, 04:15 PM",
                            warehouseShelfLife = "${item.shelfLifeDays} days remaining",
                            warehouseStatus = "Published Stock Live",
                            warehouseAvailableQuantity = item.quantityKg,
                            warehouseReservedQuantity = 0.0
                        )
                        addNotification(farmerId, "Farmer", "Listing Published", "Your verified crop ${item.name} has been published and is now live on the Buyer Marketplace!")
                        addNotification(farmerId, "Farmer", "Mandi Price Increased", "Mandi price for ${item.name} increased. Your selling price is highly competitive!")
                        addActivity("Listing ${item.name} published live", "list")
                    }
                    "Completed", "Sold" -> {
                        val totalVal = item.quantityKg * item.pricePerKg
                        val commission = totalVal * 0.02
                        val deliveryFee = 250.0
                        val netSettlement = totalVal - commission - deliveryFee

                        // Credit farmer wallet
                        _farmers.value = _farmers.value.map { f ->
                            if (f.id == farmerId) f.copy(walletBalance = f.walletBalance + netSettlement) else f
                        }

                        // Add transactions
                        recordTransaction(farmerId, "Farmer", totalVal, "Credit", "Crop Sale: ${item.name} (${item.quantityKg.toInt()} kg)")
                        recordTransaction(farmerId, "Farmer", commission, "Debit", "FarmLink Escrow Commission (2%)")
                        recordTransaction(farmerId, "Farmer", deliveryFee, "Debit", "Logistics Delivery & Handling Charge")

                        // Add notifications
                        addNotification(farmerId, "Farmer", "Buyer Purchased Crop", "Congratulations! A buyer has purchased your ${item.name} listing.")
                        addNotification(farmerId, "Farmer", "Payment Released", "Escrow payment of ₹${netSettlement.toInt()} has been successfully settled to your wallet.")
                        addActivity("Sold listing ${item.name} for ₹${totalVal.toInt()}", "wallet")
                    }
                }
                updated
            } else item
        }
        return true
    }

    // ==========================================
    // MASS MOCK GENERATION (STRICT COMPLIANCE)
    // ==========================================

    private fun generateMockData() {
        val r = Random(42) // Seed for deterministic mock generation

        // Helper to format F01 style prefix + index
        fun formatId(prefix: String, index: Int): String {
            val num = index + 1
            return if (num < 10) "${prefix}0$num" else "$prefix$num"
        }

        // 1. Generate 20 Farmers
        val farmerNames = listOf(
            "Baldev Singh", "Ramesh Kumar", "Sukhdev Yadav", "Hari Prasad", "Jaspal Dhillon",
            "Manoj Patel", "Vipul Chaudhary", "Rajesh Sharma", "Kiran Gowda", "Raman Reddy",
            "Jagdish Meena", "Devendra Verma", "Anil Deshmukh", "Prakash Mishra", "Sanjeev Jha",
            "Gopal Krishnan", "Somnath Patil", "Amrik Singh", "Vijay Naidu", "Basavaraj Bommai"
        )
        val villages = listOf(
            "Nangal", "Chhatarpur", "Karnal", "Hapur", "Bhatinda",
            "Anand", "Mehsana", "Sonipat", "Mandya", "Guntur",
            "Tonk", "Hardoi", "Latur", "Basti", "Madhubani",
            "Palakkad", "Satara", "Moga", "Nellore", "Hubli"
        )

        // Re-generate farmers with safe formatting
        val formattedFarmers = farmerNames.mapIndexed { idx, name ->
            val id = if (idx == 0) "F01" else formatId("F", idx)
            Farmer(
                id = id,
                name = name,
                village = "${villages[idx % villages.size]} District",
                rating = (40 + (idx % 11)).toDouble() / 10.0,
                phone = "+91 98765 00" + (if (idx < 9) "0${idx + 1}" else "${idx + 1}"),
                cropCount = 2 + (idx % 4),
                joinedDate = "Feb 2025",
                walletBalance = 8000.0 + (idx * 1500),
                avatarColor = when (idx % 5) {
                    0 -> 0xFF2E7D32
                    1 -> 0xFFE65100
                    2 -> 0xFF0288D1
                    3 -> 0xFFD81B60
                    else -> 0xFF8D6E63
                }
            )
        }
        _farmers.value = formattedFarmers

        // 2. Generate 15 Buyers
        val buyerNames = listOf(
            "Aman Gupta", "Gaurav Grocers", "Delhi Grain Corp", "FreshMart Retail", "Shyam Traders",
            "Vikas Organic Outlet", "Agro Foods Ltd", "Swadeshi Supermarket", "Metro Whole Foods", "Sardar ji Flour Mill",
            "Kishore Agrotech", "Modern Flour Mills", "Pantry Pick Grocery", "Nirula Catering", "Organic Harvest Delhi"
        )
        val cities = listOf("Delhi", "Mumbai", "Gurugram", "Bengaluru", "Noida", "Pune", "Kolkata", "Chandigarh")
        val formattedBuyers = buyerNames.mapIndexed { idx, name ->
            val id = if (idx == 0) "B01" else formatId("B", idx)
            Buyer(
                id = id,
                name = name,
                city = cities[idx % cities.size],
                companyName = if (idx % 2 == 0) "${name.split(" ")[0]} Enterprises" else name,
                phone = "+91 99911 00" + (if (idx < 9) "0${idx + 1}" else "${idx + 1}"),
                walletBalance = 50000.0 + (idx * 12000),
                joinedDate = "Jan 2025"
            )
        }
        _buyers.value = formattedBuyers

        // 3. Generate 10 Pickup Executives
        val pickupNames = listOf(
            "Satish Yadav", "Dilip Rawat", "Jaggu Gujjar", "Kuldeep Bishnoi", "Surinder Singh",
            "Rinku Sharma", "Manish Tiwari", "Amit Kasana", "Praveen Gujjar", "Yogesh Hooda"
        )
        val vehicleNumbers = listOf(
            "DL-1L-AA-2342", "HR-26-Y-7821", "DL-3C-BF-0982", "HR-55-E-1234", "DL-8C-AA-9911",
            "HR-26-W-4432", "DL-1V-BB-9922", "UP-16-F-5521", "DL-3D-CC-7788", "HR-55-H-1902"
        )
        val formattedPickups = pickupNames.mapIndexed { idx, name ->
            PickupExecutive(
                id = formatId("P", idx),
                name = name,
                phone = "+91 88811 0000${idx + 1}",
                area = "Zone ${'A' + idx} Delhi NCR",
                vehicleNumber = vehicleNumbers[idx],
                pendingPickupsCount = idx % 3
            )
        }
        _pickupExecutives.value = formattedPickups

        // 4. Generate 5 Delivery Executives
        val deliveryNames = listOf("Rahul Dev", "Sumit Chauhan", "Harish Saini", "Joginder Pal", "Karan Johar")
        val deliveryVehicles = listOf("DL-1G-BB-8762", "HR-26-Z-1011", "DL-3M-CF-9011", "HR-55-F-5555", "DL-8S-BD-1221")
        val formattedDeliveries = deliveryNames.mapIndexed { idx, name ->
            DeliveryExecutive(
                id = formatId("D", idx),
                name = name,
                phone = "+91 77711 0000${idx + 1}",
                zone = "Region ${'X' + idx} Logistics Hub",
                vehicleNumber = deliveryVehicles[idx],
                completedDeliveriesCount = 20 + idx * 4
            )
        }
        _deliveryExecutives.value = formattedDeliveries

        // 5. Generate 3 Warehouse Managers
        val formattedManagers = listOf(
            WarehouseManager("W01", "Vikas Sharma", "Delhi Agri-Store Hub", 2000.0, 1420.5),
            WarehouseManager("W02", "Pradeep Joshi", "Sonipat Food Silo", 5000.0, 3105.0),
            WarehouseManager("W03", "Anil Deshmukh", "Hapur Cold Storage", 3000.0, 1850.0)
        )
        _warehouseManagers.value = formattedManagers

        // 6. Generate 1 Admin
        _admins.value = listOf(Admin("A01", "Lead Director FarmLink", "admin@farmlink.org"))

        // 7. Generate 50 Crop Listings
        val crops = listOf(
            Pair("Wheat (Basmati)", "Grain"),
            Pair("Rice (Pusa 1121)", "Grain"),
            Pair("Potatoes (Jyoti)", "Vegetable"),
            Pair("Onions (Nasik Red)", "Vegetable"),
            Pair("Tomatoes (Hybrid)", "Vegetable"),
            Pair("Chana Dal", "Pulse"),
            Pair("Moong Sabut", "Pulse"),
            Pair("Mustard Seeds", "Oilseed"),
            Pair("Soybeans", "Oilseed"),
            Pair("Apples (Shimla)", "Fruit"),
            Pair("Mangoes (Safeda)", "Fruit"),
            Pair("Cauliflower", "Vegetable"),
            Pair("Green Peas", "Vegetable"),
            Pair("Arhar Dal", "Pulse"),
            Pair("Basmati Rice (Organic)", "Grain")
        )
        val qualities = listOf("Grade A", "Grade B", "Grade C")
        val listedCrops = (0..64).map { idx ->
            val cropType = crops[idx % crops.size]
            val farmer = formattedFarmers[idx % formattedFarmers.size]
            val basePrice = when(cropType.second) {
                "Grain" -> 22.0 + (idx % 8) * 2.5
                "Pulse" -> 70.0 + (idx % 12) * 3.0
                "Vegetable" -> 15.0 + (idx % 5) * 4.0
                "Fruit" -> 80.0 + (idx % 10) * 5.0
                else -> 40.0
            }
            val qty = 300.0 + (idx * 150) % 2500
            
            val statusVal = when {
                idx < 50 -> "Published"
                idx < 53 -> "Draft"
                idx < 56 -> "Pickup Requested"
                idx < 61 -> "Approved"
                else -> "Rejected"
            }

            val shelfLife = when(cropType.second) {
                "Grain" -> 180
                "Pulse" -> 240
                "Vegetable" -> 7
                "Fruit" -> 14
                else -> 45
            }

            // Logistics details if requested or further
            val isPickup = statusVal in listOf("Pickup Requested", "Approved", "Published")
            val pId = if (isPickup) "PKP_M${10000 + idx}" else null
            val pDate = if (isPickup) "Listed ${idx % 3 + 1} days ago" else null
            val pStatus = when (statusVal) {
                "Pickup Requested" -> "Requested"
                "Approved" -> "Inspected & Approved"
                "Published" -> "Completed"
                else -> null
            }
            val execId = if (isPickup) "EXE_8293" else null
            val execName = if (isPickup) "Satish Yadav" else null
            val execPhone = if (isPickup) "+91 98765 43210" else null
            val vehNum = if (isPickup) "DL-1L-AA-2342" else null
            val pOtp = if (isPickup) "4829" else null
            val estArrival = if (isPickup) "Arrived" else null
            val liveLoc = if (isPickup) "Completed Destination" else null

            // Warehouse details if Approved or Published
            val isStored = statusVal in listOf("Approved", "Published")
            val whId = if (isStored) (if (idx % 2 == 0) "WH_DEL_01" else "WH_SON_02") else null
            val whName = if (isStored) (if (idx % 2 == 0) "Delhi Agri-Store Hub" else "Sonipat Food Silo") else null
            val rack = if (isStored) "Section-A, Bin-${10 + (idx % 20)}" else null
            val sDate = if (isStored) "${idx % 5 + 1} days ago" else null
            val sLife = if (isStored) "${shelfLife - (idx % 5)} days remaining" else null
            val whStatus = if (isStored) "Stored & Certified" else null
            val availQty = if (isStored) qty else null
            val resQty = if (isStored) 0.0 else null

            CropListing(
                id = formatId("CROP_", idx),
                farmerId = farmer.id,
                farmerName = farmer.name,
                name = cropType.first,
                category = cropType.second,
                quantityKg = qty,
                pricePerKg = basePrice,
                qualityGrade = qualities[idx % qualities.size],
                description = "Freshly harvested organic ${cropType.first}. High nutrition index, grown using clean sustainable methods, carefully sun-dried and ready for immediate storage.",
                status = statusVal,
                listedDate = "Listed ${idx % 12 + 1} days ago",
                warehouseLocation = whName ?: "Delhi Agri-Store Hub",
                shelfLifeDays = shelfLife,
                rating = (44 + (idx % 7)).toDouble() / 10.0,
                reviewsCount = 4 + idx % 12,
                pickupId = pId,
                pickupDate = pDate,
                pickupStatus = pStatus,
                executiveId = execId,
                executiveName = execName,
                executivePhone = execPhone,
                vehicleNumber = vehNum,
                pickupOtp = pOtp,
                estimatedArrivalTime = estArrival,
                liveLocation = liveLoc,
                warehouseId = whId,
                warehouseName = whName,
                warehouseRackNumber = rack,
                warehouseStorageDate = sDate,
                warehouseShelfLife = sLife,
                warehouseStatus = whStatus,
                warehouseAvailableQuantity = availQty,
                warehouseReservedQuantity = resQty
            )
        }
        _cropListings.value = listedCrops

        // 8. Generate 60 Orders (40 Completed, 20 Active)
        val ordersList = mutableListOf<Order>()
        for (idx in 0..59) {
            val buyer = formattedBuyers[idx % formattedBuyers.size]
            val cropType = crops[idx % crops.size]
            // F01 gets 25 orders to keep Baldev Singh's history rich
            val farmer = if (idx < 25) formattedFarmers[0] else formattedFarmers[(idx - 25) % formattedFarmers.size]
            val qty = 200.0 + (idx * 50) % 1500
            val price = 20.0 + (idx % 6) * 4
            val amt = qty * price
            
            val statusVal = if (idx < 40) {
                "Completed"
            } else {
                when (idx % 4) {
                    0 -> "Pending Pickup"
                    1 -> "Picked Up"
                    2 -> "In Warehouse"
                    else -> "Out for Delivery"
                }
            }
            
            val estDel = if (statusVal == "Completed") "Delivered" else "In ${idx % 3 + 2} Days"

            ordersList.add(
                Order(
                    id = "ORD_SYS_${100 + idx}",
                    buyerId = buyer.id,
                    buyerName = buyer.name,
                    cropListingId = "CROP_${if (idx < 9) "0${idx + 1}" else "${idx + 1}"}",
                    cropName = cropType.first,
                    quantityKg = qty,
                    pricePerKg = price,
                    totalAmount = amt,
                    status = statusVal,
                    orderDate = "${idx % 10 + 10} Jun 2026",
                    estimatedDelivery = estDel,
                    farmerId = farmer.id,
                    farmerName = farmer.name,
                    pickupExecutiveId = formattedPickups[idx % formattedPickups.size].id,
                    deliveryExecutiveId = formattedDeliveries[idx % formattedDeliveries.size].id,
                    warehouseId = if (idx % 2 == 0) "W01" else "W02",
                    ratingForFarmer = if (idx < 15) (4 + (idx % 2)) else 0,
                    reviewForFarmer = if (idx < 15) "Excellent quality produce, smooth delivery." else ""
                )
            )
        }
        _orders.value = ordersList

        // 9. Wallet Transactions: Ensure at least 25 transactions for F01
        val mockTxns = mutableListOf<WalletTransaction>()
        // First 25 credit/debit txns for F01
        for (idx in 1..25) {
            mockTxns.add(
                WalletTransaction(
                    id = "TXN_F01_${100 + idx}",
                    userId = "F01",
                    userRole = "Farmer",
                    amount = 2500.0 + idx * 350.0,
                    type = if (idx % 5 == 0) "Debit" else "Credit",
                    purpose = when (idx % 5) {
                        0 -> "Withdrawal to State Bank of India"
                        1 -> "Sold Wheat Basmati (ORD_F01_${100 + idx})"
                        2 -> "Sold Potatoes Jyoti (ORD_F01_${100 + idx})"
                        3 -> "Mandi Transportation Refund"
                        else -> "Sign-up Loyalty Bonus Credit"
                    },
                    timestamp = "${idx % 12 + 10} Jun 2026, 11:${10 + idx % 45} AM"
                )
            )
        }
        // Add buyer/other farmer transactions
        ordersList.take(15).forEachIndexed { i, ord ->
            mockTxns.add(
                WalletTransaction(
                    id = "TXN_BUY_${1000 + i}",
                    userId = ord.buyerId,
                    userRole = "Buyer",
                    amount = ord.totalAmount,
                    type = "Debit",
                    purpose = "Ordered ${ord.cropName}",
                    timestamp = ord.orderDate
                )
            )
            if (ord.farmerId != "F01") {
                mockTxns.add(
                    WalletTransaction(
                        id = "TXN_FAR_${2000 + i}",
                        userId = ord.farmerId,
                        userRole = "Farmer",
                        amount = ord.totalAmount,
                        type = "Credit",
                        purpose = "Sold ${ord.cropName}",
                        timestamp = ord.orderDate
                    )
                )
            }
        }
        _walletTransactions.value = mockTxns

        // 10. Notifications: Ensure at least 30 notifications for F01
        val mockNotifs = mutableListOf<Notification>()
        // 30 notifications specifically for F01
        for (idx in 1..30) {
            mockNotifs.add(
                Notification(
                    id = "NOT_F01_${100 + idx}",
                    userId = "F01",
                    userRole = "Farmer",
                    title = when (idx % 8) {
                        0 -> "Pickup Assigned"
                        1 -> "Crop Approved"
                        2 -> "Buyer Purchased Crop"
                        3 -> "Wallet Credited"
                        4 -> "Order Completed"
                        5 -> "Mandi Price Increased"
                        6 -> "Executive Arriving"
                        else -> "Payment Released"
                    },
                    body = when (idx % 8) {
                        0 -> "Executive Satish Yadav assigned to pickup request PKP_F01_${100 + idx}."
                        1 -> "Your listing for Basmati Rice (Grade A) passed quality check and is live."
                        2 -> "Buyer Aman Gupta bought 500 Kg of your Tomatoes for ₹12,500."
                        3 -> "₹15,400 has been credited to your wallet for order ORD_F01_${100 + idx}."
                        4 -> "Order ORD_F01_${100 + idx} has been delivered successfully."
                        5 -> "Wheat prices increased by ₹120/Quintal in your local Agra Mandi."
                        6 -> "Pickup Executive Dilip Rawat is arriving at your farm in 15 minutes."
                        else -> "Payment of ₹8,500 has been released from escrow to your wallet."
                    },
                    timestamp = "${idx % 6 + 1} days ago",
                    isRead = idx > 5
                )
            )
        }
        // Add general notifications
        mockNotifs.addAll(
            listOf(
                Notification("N_GEN_02", "B01", "Buyer", "Order Shipped", "Your order ORD_A01 for Potatoes is now Out For Delivery.", "3 hours ago", false),
                Notification("N_GEN_03", "P01", "Pickup", "New Pickup Task", "New collection request assigned from Ramesh Kumar for 850kg Rice.", "4 hours ago", false),
                Notification("N_GEN_04", "D01", "Delivery", "Deliver Ready", "You have been assigned to deliver order ORD_A02 to Aman Enterprises.", "5 hours ago", false),
                Notification("N_GEN_05", "W01", "Warehouse", "Silo Allocation", "Allocating Space for 2 Metric Tons Incoming basmati rice.", "Yesterday", true)
            )
        )
        _notifications.value = mockNotifs

        // 11. Pickup Requests: Ensure at least 40 pickup requests
        val mockPickups = mutableListOf<PickupRequest>()
        for (idx in 1..32) {
            mockPickups.add(
                PickupRequest(
                    id = "PKP_F01_${100 + idx}",
                    orderId = "ORD_F01_${100 + idx}",
                    farmerId = "F01",
                    farmerName = "Baldev Singh",
                    farmAddress = "Baldev Singh's Farm, Nangal, Nangal District",
                    cropName = when (idx % 5) {
                        0 -> "Wheat (Basmati)"
                        1 -> "Potatoes (Jyoti)"
                        2 -> "Onions (Nasik Red)"
                        3 -> "Tomatoes (Hybrid)"
                        else -> "Rice (Pusa 1121)"
                    },
                    quantityKg = 400.0 + idx * 80.0,
                    pickupExecutiveId = formattedPickups[idx % formattedPickups.size].id,
                    pickupExecutiveName = formattedPickups[idx % formattedPickups.size].name,
                    status = when (idx % 6) {
                        0 -> "Pending"
                        1 -> "Assigned"
                        2 -> "On The Way"
                        3 -> "Picked Up"
                        4 -> "Delivered to Warehouse"
                        else -> "Completed"
                    },
                    requestedTime = "2026-06-${10 + idx} at 10:00 AM"
                )
            )
        }
        // Add other farmers pickup requests to get past 40 total
        ordersList.filter { it.farmerId != "F01" && (it.status == "Pending Pickup" || it.status == "Picked Up") }.mapIndexed { i, ord ->
            val pExec = formattedPickups[i % formattedPickups.size]
            mockPickups.add(
                PickupRequest(
                    id = "PKP_OTH_${200 + i}",
                    orderId = ord.id,
                    farmerId = ord.farmerId,
                    farmerName = ord.farmerName,
                    farmAddress = "${ord.farmerName}'s Farm, near ${ord.farmerName.split(" ")[0]} village",
                    cropName = ord.cropName,
                    quantityKg = ord.quantityKg,
                    pickupExecutiveId = pExec.id,
                    pickupExecutiveName = pExec.name,
                    status = if (ord.status == "Picked Up") "Picked Up" else "Assigned",
                    requestedTime = "Today, 07:00 AM"
                )
            )
        }
        _pickupRequests.value = mockPickups

        // 11b. Quality Inspections: Ensure at least 50 inspection reports
        val mockInspections = (1..50).map { idx ->
            QualityInspection(
                id = "INSP_${700 + idx}",
                cropListingId = "CROP_${if (idx < 10) "0$idx" else idx.toString()}",
                cropName = when (idx % 5) {
                    0 -> "Banana"
                    1 -> "Tomato"
                    2 -> "Potato"
                    3 -> "Onion"
                    else -> "Rice"
                },
                grade = when (idx % 3) {
                    0 -> "Grade A"
                    1 -> "Grade B"
                    else -> "Grade C"
                },
                qualityScore = 82.0 + (idx * 3) % 17,
                moistureLevel = 11.0 + (idx * 2) % 6,
                executiveNotes = "Passed physical verification. The size is consistent, moisture level fits dry-shelf target parameters, zero pest index.",
                inspectionDate = "2026-06-${10 + (idx % 15)} at 04:00 PM",
                isApproved = idx % 5 != 0,
                verifiedBadge = idx % 5 != 0
            )
        }
        _qualityInspections.value = mockInspections

        // 12. Delivery Requests
        val mockDeliveries = ordersList.filter { it.status == "Out for Delivery" || it.status == "In Warehouse" }.mapIndexed { i, ord ->
            val dExec = formattedDeliveries[i % formattedDeliveries.size]
            val buyer = formattedBuyers.find { it.id == ord.buyerId }
            DeliveryRequest(
                id = "DEL_${400 + i}",
                orderId = ord.id,
                buyerId = ord.buyerId,
                buyerName = ord.buyerName,
                deliveryAddress = "${buyer?.companyName}, Sector 12, ${buyer?.city ?: "Delhi"}",
                cropName = ord.cropName,
                quantityKg = ord.quantityKg,
                deliveryExecutiveId = dExec.id,
                deliveryExecutiveName = dExec.name,
                status = if (ord.status == "Out for Delivery") "Out For Delivery" else "Assigned",
                requestedTime = "Today, 08:30 AM"
            )
        }
        _deliveryRequests.value = mockDeliveries

        // 13. Warehouse Inventory (Derived from listed items and orders in warehouse state)
        val cropsList = listOf(
            "Wheat (Basmati)", "Rice (Pusa 1121)", "Potatoes (Jyoti)", "Onions (Nasik Red)",
            "Tomatoes (Hybrid)", "Chana Dal", "Moong Sabut", "Mustard Seeds",
            "Soybeans", "Apples (Shimla)", "Mangoes (Safeda)", "Cauliflower",
            "Green Peas", "Arhar Dal", "Basmati Rice (Organic)"
        )

        val mockInventory = (1..100).map { i ->
            val id = "INV_${String.format("%03d", i)}"
            val whIdx = (i % 3)
            val whId = if (whIdx == 0) "W01" else if (whIdx == 1) "W02" else "W03"
            val whName = if (whIdx == 0) "Delhi Agri-Store Hub" else if (whIdx == 1) "Sonipat Food Silo" else "Hapur Cold Storage"
            val crop = cropsList[i % cropsList.size]
            val qty = if (i <= 20) {
                // Exactly 20 Low Stock items (less than 500 Kg)
                200.0 + (i * 12.5) % 250.0 // range 200 to 450
            } else {
                1500.0 + (i * 120.0) % 15000.0
            }
            val shelfLife = if (i > 20 && i <= 40) {
                // Exactly 20 Expiring items (less than 10 days)
                2 + (i % 7) // range 2 to 8 days remaining
            } else {
                25 + (i % 90) // 25 to 115 days remaining
            }
            val status = if (i <= 20) {
                "Low Stock"
            } else if (i <= 40) {
                "Expiring"
            } else {
                "Stored"
            }
            val zone = "Zone-${'A' + (i % 4)}"
            val rack = "Rack-${String.format("%02d", (i % 15) + 1)}"
            val shelf = "Shelf-${'A' + (i % 5)}"
            val bin = "Bin-${String.format("%02d", (i % 8) + 1)}"
            
            WarehouseInventory(
                id = id,
                warehouseId = whId,
                warehouseName = whName,
                cropName = crop,
                quantityKg = qty,
                shelfLocation = "$zone, $rack, $shelf",
                qualityCheckedBy = if (whIdx == 0) "Vikas Sharma" else if (whIdx == 1) "Pradeep Joshi" else "Anil Deshmukh",
                storageDate = "${(i % 28) + 1} May 2026",
                rackNumber = rack,
                shelf = shelf,
                bin = bin,
                storageZone = zone,
                availableQuantity = qty,
                reservedQuantity = if (i % 10 == 0) qty * 0.2 else 0.0,
                damagedQuantity = if (i % 15 == 0) 50.0 else 0.0,
                expiringQuantity = if (status == "Expiring") qty else 0.0,
                shelfLifeDaysRemaining = shelfLife,
                status = status,
                farmerName = "Farmer ${10 + (i % 20)}",
                executiveName = "Satish Yadav",
                moistureLevel = 11.0 + (i % 40) * 0.1,
                grade = if (i % 3 == 0) "Grade A" else if (i % 3 == 1) "Grade B" else "Grade C"
            )
        }
        _warehouseInventory.value = mockInventory

        // Generate exactly 60 Incoming Shipments
        val mockIncoming = (1..60).map { i ->
            val whIdx = (i % 3)
            val whId = if (whIdx == 0) "W01" else if (whIdx == 1) "W02" else "W03"
            val status = when (i % 5) {
                0 -> "Pending"
                1 -> "Accepted"
                2 -> "Rejected"
                3 -> "Damaged"
                else -> "Pending"
            }
            IncomingDelivery(
                id = "INC_${String.format("%03d", i)}",
                cropName = cropsList[i % cropsList.size],
                farmerName = "Farmer ${15 + (i % 15)}",
                executiveName = if (i % 2 == 0) "Satish Yadav" else "Dilip Rawat",
                quantityKg = 500.0 + (i * 50) % 2500.0,
                grade = if (i % 3 == 0) "Grade A" else if (i % 3 == 1) "Grade B" else "Grade C",
                moistureLevel = 10.5 + (i % 30) * 0.1,
                status = status,
                arrivalTime = "Today, ${String.format("%02d", 8 + (i % 10))}:${String.format("%02d", (i * 7) % 60)} ${if (i % 2 == 0) "AM" else "PM"}",
                warehouseId = whId,
                damagesRecorded = if (status == "Damaged") "Wet bags, approx ${5 + i % 15}kg ruined." else null,
                receiptNumber = if (status == "Accepted") "REC-WR-${1000 + i}" else null
            )
        }
        _incomingDeliveries.value = mockIncoming

        // Generate exactly 50 Dispatches
        val mockDispatches = (1..50).map { i ->
            val whIdx = (i % 3)
            val whId = if (whIdx == 0) "W01" else if (whIdx == 1) "W02" else "W03"
            val status = when (i % 5) {
                0 -> "Pending Pick"
                1 -> "Picked"
                2 -> "Packed"
                3 -> "Dispatched"
                else -> "Completed"
            }
            WarehouseDispatch(
                id = "DSP_${String.format("%03d", i)}",
                orderId = "ORD_W${String.format("%03d", 100 + i)}",
                buyerName = buyerNames[i % buyerNames.size],
                cropName = cropsList[(i + 5) % cropsList.size],
                quantityKg = 250.0 + (i * 75) % 2000.0,
                status = status,
                dispatchDate = "Today, 0${(i % 8) + 1}:30 PM",
                warehouseId = whId,
                rackLocation = "Zone-${'A' + (i % 3)}, Rack-${(i % 10) + 1}"
            )
        }
        _warehouseDispatches.value = mockDispatches

        // 14. Customer Reviews (Exactly 20 Reviews)
        val mockReviews = listOf(
            CustomerReview("R_01", "Wheat (Basmati)", "Aman Gupta", 5, "Outstanding quality. Very clean grains, almost zero moisture. Perfect for our retail outlet packaging.", "2 days ago"),
            CustomerReview("R_02", "Onions (Nasik Red)", "Gaurav Grocers", 4, "Great price and very fresh. Logistics team delivered in perfect timing before the rain.", "4 days ago"),
            CustomerReview("R_03", "Apples (Shimla)", "Delhi Grain Corp", 5, "Crisp, sweet, and accurately sorted by size. Will buy again from Baldev Singh.", "1 week ago"),
            CustomerReview("R_04", "Potatoes (Jyoti)", "FreshMart Retail", 4, "Excellent quality, very uniform sizes. Perfect for bulk sorting and direct retail sale.", "3 days ago"),
            CustomerReview("R_05", "Rice (Pusa 1121)", "Shyam Traders", 5, "Highly aromatic rice. Extremely long grain length. High premium quality certified by FarmLink.", "5 days ago"),
            CustomerReview("R_06", "Tomatoes (Hybrid)", "Agro Foods Ltd", 4, "Extremely firm tomatoes. Well protected during transit. No transit damage reported.", "6 days ago"),
            CustomerReview("R_07", "Chana Dal", "Swadeshi Supermarket", 5, "Perfect polishing, high dry ratio, cooks beautifully. Fully satisfied with this lot.", "1 week ago"),
            CustomerReview("R_08", "Mustard Seeds", "Sardar ji Flour Mill", 4, "Oil yield was excellent during testing. High pungency index.", "8 days ago"),
            CustomerReview("R_09", "Soybeans", "Kishore Agrotech", 5, "Premium grade soybeans with great protein parameters. Highly recommended farmer.", "10 days ago"),
            CustomerReview("R_10", "Mangoes (Safeda)", "Modern Flour Mills", 5, "Very sweet and fiberless. Best batch of mangoes we received this summer.", "12 days ago"),
            CustomerReview("R_11", "Cauliflower", "Pantry Pick Grocery", 4, "Very fresh heads, clean white curd. Minimal trim loss. Good packaging.", "2 weeks ago"),
            CustomerReview("R_12", "Green Peas", "Nirula Catering", 5, "Sweet and tender peas. Perfect moisture and great shelf life in refrigeration.", "2 weeks ago"),
            CustomerReview("R_13", "Arhar Dal", "Organic Harvest Delhi", 5, "Unpolished, fully organic flavor. Exactly what our premium health buyers want.", "3 weeks ago"),
            CustomerReview("R_14", "Basmati Rice (Organic)", "Aman Gupta", 5, "Incredibly long grain, great taste. This organic batch has a stellar premium grade.", "3 weeks ago"),
            CustomerReview("R_15", "Wheat (Basmati)", "Delhi Grain Corp", 4, "Excellent baking quality. Perfect flour yields.", "1 month ago"),
            CustomerReview("R_16", "Tomatoes (Hybrid)", "FreshMart Retail", 4, "Consistent size and uniform deep red color. Clean sorting.", "1 month ago"),
            CustomerReview("R_17", "Onions (Nasik Red)", "Shyam Traders", 5, "Very low water content, great storage life in our depot. High quality.", "1 month ago"),
            CustomerReview("R_18", "Potatoes (Jyoti)", "Gaurav Grocers", 4, "Perfect starch balance, great for chip processors.", "1 month ago"),
            CustomerReview("R_19", "Apples (Shimla)", "Agro Foods Ltd", 5, "Extremely juicy, high grade. Zero bruised pieces.", "1 month ago"),
            CustomerReview("R_20", "Moong Sabut", "Swadeshi Supermarket", 5, "Even sized grains, sprouts very fast with near 100% germination.", "1 month ago")
        )
        _customerReviews.value = mockReviews

        // 14b. Saved Addresses (Exactly 10 Addresses)
        val mockAddresses = listOf(
            "Agro-Corp Silo Block B, Sector 62, Noida, UP - 201301",
            "Delhi Grain Wholesalers, Gali No 4, Naya Bazar, Delhi - 110006",
            "Aman Enterprises Central Warehouse, Plot 42, Okhla Phase 3, Delhi - 110020",
            "FreshMart Retail Depot, Khasra 391, Alipur Mandi Road, Delhi - 110036",
            "Modern Flour Mills, Industrial Area, Lawrence Road, Delhi - 110035",
            "Gaurav Grocers Store #14, Galleria Mall Sector 28, Gurugram, Haryana - 122002",
            "Organic Harvest Delhi Main Silo, G T Karnal Road, Sonipat, Haryana - 131001",
            "Swadeshi Supermarket Storage, Sector 15, Rohini, Delhi - 110085",
            "Pantry Pick Catering Kitchen, Chhattarpur Pahadi, Mehrauli, Delhi - 110030",
            "Agro Foods South-Delhi Hub, Malviya Nagar Market, Delhi - 110017"
        )
        savedAddresses.value = mockAddresses

        // 15. Official Daily Mandi Prices
        val mockMandi = listOf(
            MandiPrice("Banana", "Fruit", "Jalgaon Mandi", "₹1,800 - ₹2,400 / Quintal", 21.0, 20.0, 1.0, "Up", "Today, 08:00 AM"),
            MandiPrice("Tomato", "Vegetable", "Nashik Mandi", "₹1,200 - ₹1,800 / Quintal", 15.0, 16.0, -1.0, "Down", "Today, 08:00 AM"),
            MandiPrice("Potato", "Vegetable", "Agra Mandi", "₹1,100 - ₹1,500 / Quintal", 13.0, 13.0, 0.0, "Stable", "Today, 08:00 AM"),
            MandiPrice("Onion", "Vegetable", "Lasalgaon Mandi", "₹2,200 - ₹2,800 / Quintal", 25.0, 23.5, 1.5, "Up", "Today, 08:00 AM"),
            MandiPrice("Rice", "Grain", "Karnal Mandi", "₹3,500 - ₹4,200 / Quintal", 38.5, 37.0, 1.5, "Up", "Today, 08:00 AM"),
            MandiPrice("Maize", "Grain", "Chhindwara Mandi", "₹1,800 - ₹2,200 / Quintal", 20.0, 19.5, 0.5, "Up", "Today, 08:00 AM"),
            MandiPrice("Cotton", "Fiber", "Rajkot Mandi", "₹6,800 - ₹7,500 / Quintal", 71.5, 72.0, -0.5, "Down", "Today, 08:00 AM"),
            MandiPrice("Groundnut", "Oilseed", "Gondal Mandi", "₹6,200 - ₹6,800 / Quintal", 65.0, 64.0, 1.0, "Up", "Today, 08:00 AM"),
            MandiPrice("Mango", "Fruit", "Ratnagiri Mandi", "₹8,000 - ₹12,000 / Quintal", 100.0, 100.0, 0.0, "Stable", "Today, 08:00 AM"),
            MandiPrice("Chilli", "Spice", "Guntur Mandi", "₹15,000 - ₹18,000 / Quintal", 165.0, 160.0, 5.0, "Up", "Today, 08:00 AM")
        )
        _mandiPrices.value = mockMandi

        // 16. Recent Activities
        val mockActivities = listOf(
            RecentActivity("A_01", "Ramesh Kumar listed 1500 kg Wheat (Basmati)", "10 mins ago", "list"),
            RecentActivity("A_02", "Delhi Grain Corp purchased 800 kg Rice from Hari Prasad", "30 mins ago", "buy"),
            RecentActivity("A_03", "Satish Yadav updated pickup PKP_201 to 'Picked Up'", "45 mins ago", "pickup"),
            RecentActivity("A_04", "Rahul Dev completed delivery of ORD_C05 to Gaurav Grocers", "1 hour ago", "delivery"),
            RecentActivity("A_05", "Wheat trade reached a premium avg of ₹23.5/kg", "2 hours ago", "mandi"),
            RecentActivity("A_06", "Aman Gupta added ₹25,000 to buyer balance", "3 hours ago", "wallet")
        )
        _recentActivities.value = mockActivities
    }

    // ==========================================
    // WAREHOUSE MODULE SERVICE FUNCTIONS
    // ==========================================

    // Accept Shipment
    fun acceptIncomingDelivery(deliveryId: String): Boolean {
        val list = _incomingDeliveries.value
        val item = list.find { it.id == deliveryId } ?: return false
        val receiptNum = "REC-WR-${1000 + kotlin.random.Random.nextInt(1000, 9999)}"
        _incomingDeliveries.value = list.map {
            if (it.id == deliveryId) it.copy(status = "Accepted", receiptNumber = receiptNum) else it
        }

        // Add to inventory automatically
        val newInv = WarehouseInventory(
            id = "INV_NEW_${UUID.randomUUID().toString().take(4).uppercase()}",
            warehouseId = item.warehouseId,
            warehouseName = if (item.warehouseId == "W01") "Delhi Agri-Store Hub" else if (item.warehouseId == "W02") "Sonipat Food Silo" else "Hapur Cold Storage",
            cropName = item.cropName,
            quantityKg = item.quantityKg,
            shelfLocation = "Zone-A, Rack-01, Shelf-A",
            qualityCheckedBy = "Warehouse System",
            storageDate = "Today, 12:00 PM",
            availableQuantity = item.quantityKg,
            farmerName = item.farmerName,
            executiveName = item.executiveName,
            moistureLevel = item.moistureLevel,
            grade = item.grade,
            arrivalTime = item.arrivalTime
        )
        _warehouseInventory.value = listOf(newInv) + _warehouseInventory.value

        addNotification("W01", "Warehouse", "Shipment Received", "Shipment of ${item.quantityKg.toInt()} Kg ${item.cropName} accepted under receipt #$receiptNum")
        addActivity("Accepted incoming delivery ${item.id} of ${item.cropName}", "pickup")
        return true
    }

    // Reject Shipment
    fun rejectIncomingDelivery(deliveryId: String): Boolean {
        val list = _incomingDeliveries.value
        val item = list.find { it.id == deliveryId } ?: return false
        _incomingDeliveries.value = list.map {
            if (it.id == deliveryId) it.copy(status = "Rejected") else it
        }
        addNotification("W01", "Warehouse", "Shipment Rejected", "Shipment of ${item.quantityKg.toInt()} Kg ${item.cropName} was rejected due to quality guidelines.")
        addActivity("Rejected incoming delivery ${item.id} of ${item.cropName}", "pickup")
        return true
    }

    // Record Damages
    fun recordIncomingDamages(deliveryId: String, damageNotes: String): Boolean {
        val list = _incomingDeliveries.value
        val item = list.find { it.id == deliveryId } ?: return false
        _incomingDeliveries.value = list.map {
            if (it.id == deliveryId) it.copy(status = "Damaged", damagesRecorded = damageNotes) else it
        }
        addNotification("W01", "Warehouse", "Damages Recorded", "Damages recorded for shipment ${item.id}: $damageNotes")
        addActivity("Recorded damages on delivery ${item.id}", "pickup")
        return true
    }

    // Manual Rack Reallocation
    fun reallocateRack(inventoryId: String, zone: String, rack: String, shelf: String, bin: String): Boolean {
        val list = _warehouseInventory.value
        val item = list.find { it.id == inventoryId } ?: return false
        _warehouseInventory.value = list.map {
            if (it.id == inventoryId) it.copy(
                storageZone = zone,
                rackNumber = rack,
                shelf = shelf,
                bin = bin,
                shelfLocation = "$zone, $rack, $shelf"
            ) else it
        }
        addActivity("Reallocated ${item.cropName} to $zone, $rack, $shelf", "pickup")
        return true
    }

    // Adjust Stock Levels
    fun adjustStockQuantity(inventoryId: String, availableQty: Double, damagedQty: Double, expiringQty: Double, notes: String): Boolean {
        val list = _warehouseInventory.value
        val item = list.find { it.id == inventoryId } ?: return false
        _warehouseInventory.value = list.map {
            if (it.id == inventoryId) {
                val newStatus = if (availableQty <= 300) "Low Stock" else if (item.shelfLifeDaysRemaining < 10) "Expiring" else "Stored"
                if (availableQty <= 300) {
                    addNotification("W01", "Warehouse", "Low Inventory", "Low inventory alert: ${item.cropName} has only ${availableQty.toInt()} Kg remaining.")
                }
                if (item.shelfLifeDaysRemaining < 10) {
                    addNotification("W01", "Warehouse", "Expiring Crops", "Expiring crops alert: ${item.cropName} stored in ${item.rackNumber} expires in ${item.shelfLifeDaysRemaining} days.")
                }
                it.copy(
                    availableQuantity = availableQty,
                    damagedQuantity = damagedQty,
                    expiringQuantity = expiringQty,
                    quantityKg = availableQty + damagedQty,
                    status = newStatus
                )
            } else it
        }
        addNotification("W01", "Warehouse", "Stock Adjusted", "Adjusted stock for ${item.cropName}: Available: ${availableQty.toInt()} Kg, Damaged: ${damagedQty.toInt()} Kg")
        addActivity("Adjusted stock levels for ${item.cropName}", "pickup")
        return true
    }

    // Reserve Stock for Order automatically
    fun reserveStockForOrder(cropName: String, qtyKg: Double): Boolean {
        val list = _warehouseInventory.value
        val item = list.find { it.cropName.contains(cropName, ignoreCase = true) && it.availableQuantity >= qtyKg }
        if (item != null) {
            _warehouseInventory.value = list.map {
                if (it.id == item.id) {
                    it.copy(
                        availableQuantity = it.availableQuantity - qtyKg,
                        reservedQuantity = it.reservedQuantity + qtyKg
                    )
                } else it
            }
            addNotification("W01", "Warehouse", "Stock Reserved", "Reserved ${qtyKg.toInt()} Kg of ${item.cropName} for pending dispatch.")
            return true
        }
        return false
    }

    // Dispatch Management Actions:
    // Pick Item
    fun pickDispatchItem(dispatchId: String): Boolean {
        val list = _warehouseDispatches.value
        val item = list.find { it.id == dispatchId } ?: return false
        _warehouseDispatches.value = list.map {
            if (it.id == dispatchId) it.copy(status = "Picked") else it
        }
        addActivity("Picked dispatch item ${item.id} from rack", "delivery")
        return true
    }

    // Pack Item
    fun packDispatchItem(dispatchId: String): Boolean {
        val list = _warehouseDispatches.value
        val item = list.find { it.id == dispatchId } ?: return false
        _warehouseDispatches.value = list.map {
            if (it.id == dispatchId) it.copy(status = "Packed") else it
        }
        addActivity("Packed dispatch item ${item.id} for shipping", "delivery")
        return true
    }

    // Dispatch Item
    fun dispatchDispatchItem(dispatchId: String): Boolean {
        val list = _warehouseDispatches.value
        val item = list.find { it.id == dispatchId } ?: return false
        _warehouseDispatches.value = list.map {
            if (it.id == dispatchId) it.copy(status = "Dispatched") else it
        }

        // Automatically update main order status to Out for Delivery
        val mainOrders = _orders.value
        val matchedOrder = mainOrders.find { it.id == item.orderId }
        if (matchedOrder != null) {
            _orders.value = mainOrders.map {
                if (it.id == matchedOrder.id) it.copy(status = "Out for Delivery") else it
            }
            // Add notification to buyer
            addNotification(matchedOrder.buyerId, "Buyer", "Order Shipped", "Your order ${matchedOrder.id} has been dispatched from warehouse.")
        }

        addNotification("W01", "Warehouse", "Stock Dispatched", "Dispatch completed for ${item.cropName}. Order #${item.orderId} is on the way.")
        addActivity("Dispatched item ${item.id} from facility", "delivery")
        return true
    }

    // Complete Dispatch
    fun completeDispatchItem(dispatchId: String): Boolean {
        val list = _warehouseDispatches.value
        val item = list.find { it.id == dispatchId } ?: return false
        _warehouseDispatches.value = list.map {
            if (it.id == dispatchId) it.copy(status = "Completed") else it
        }

        // Update warehouse managers filled capacity
        val mgrs = _warehouseManagers.value
        _warehouseManagers.value = mgrs.map {
            if (it.id == item.warehouseId) {
                it.copy(filledVolumeMetricTons = (it.filledVolumeMetricTons - (item.quantityKg / 1000.0)).coerceAtLeast(0.0))
            } else it
        }

        // Automatically update main order status to Completed
        val mainOrders = _orders.value
        val matchedOrder = mainOrders.find { it.id == item.orderId }
        if (matchedOrder != null) {
            _orders.value = mainOrders.map {
                if (it.id == matchedOrder.id) it.copy(status = "Completed") else it
            }
            // Add notification to buyer
            addNotification(matchedOrder.buyerId, "Buyer", "Order Delivered", "Your order ${matchedOrder.id} has been delivered successfully!")
            addNotification(matchedOrder.farmerId, "Farmer", "Payment Settled", "Your payment for order ${matchedOrder.id} has been credited to your wallet.")
            recordTransaction(matchedOrder.farmerId, "Farmer", matchedOrder.totalAmount, "Credit", "Payout for Order ${matchedOrder.id}")
        }

        addNotification("W01", "Warehouse", "Dispatch Completed", "Delivery of ${item.cropName} (Order #${item.orderId}) completed and logged.")
        addActivity("Completed dispatch cycle for ${item.id}", "delivery")
        return true
    }
}
