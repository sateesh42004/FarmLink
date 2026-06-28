package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.example.data.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FarmLink", appName)
  }

  @Test
  fun `verify executive module mock data quantities`() {
    val executives = MarketplaceRepository.pickupExecutives.value
    val requests = MarketplaceRepository.pickupRequests.value
    val inspections = MarketplaceRepository.qualityInspections.value

    // Verify user requirements for mock data counts
    assertTrue("Should have at least 10 pickup executives", executives.size >= 10)
    assertTrue("Should have at least 40 pickup requests", requests.size >= 40)
    assertTrue("Should have at least 50 quality inspections", inspections.size >= 50)
  }

  @Test
  fun `verify pickup assignment claim logic`() {
    val requests = MarketplaceRepository.pickupRequests.value
    val firstPending = requests.firstOrNull { it.status == "Pending" }
    assertNotNull("At least one pending pickup request should exist in mock data", firstPending)

    val execId = "P01"
    val result = MarketplaceRepository.assignPickupExecutive(firstPending!!.id, execId)
    assertTrue("Claiming assignment should succeed", result)

    // Re-query request to check state update
    val updatedRequest = MarketplaceRepository.pickupRequests.value.find { it.id == firstPending.id }
    assertNotNull(updatedRequest)
    assertEquals("Executive ID should match the logged-in agent", execId, updatedRequest!!.pickupExecutiveId)
    assertEquals("Executive name should be Satish Yadav", "Satish Yadav", updatedRequest.pickupExecutiveName)
  }

  @Test
  fun `verify quality inspection submit transitions and report generation`() {
    val requests = MarketplaceRepository.pickupRequests.value
    val firstPending = requests.firstOrNull()
    assertNotNull(firstPending)

    val requestId = firstPending!!.id
    val grade = "Grade A"
    val score = 92.5
    val moisture = 12.0
    val notes = "Excellent premium cluster grain."

    val result = MarketplaceRepository.submitQualityInspection(
      requestId = requestId,
      grade = grade,
      qualityScore = score,
      moistureLevel = moisture,
      executiveNotes = notes,
      isApproved = true
    )
    assertTrue("Submitting quality inspection should return true", result)

    // Verify pickup request status is updated
    val updatedRequest = MarketplaceRepository.pickupRequests.value.find { it.id == requestId }
    assertNotNull(updatedRequest)
    assertEquals("Status should be Inspection Approved", "Inspection Approved", updatedRequest!!.status)

    // Verify a QualityInspection record was created
    val latestInspection = MarketplaceRepository.qualityInspections.value.firstOrNull()
    assertNotNull(latestInspection)
    assertEquals("Inspection crop name matches request crop name", firstPending.cropName, latestInspection!!.cropName)
    assertEquals("Grade matches submission", grade, latestInspection.grade)
    assertEquals("Quality score matches submission", score, latestInspection.qualityScore, 0.01)
    assertEquals("Moisture matches submission", moisture, latestInspection.moistureLevel, 0.01)
    assertTrue("Inspection isApproved should be true", latestInspection.isApproved)
  }

  @Test
  fun `verify warehouse mock data constraints`() {
    val managers = MarketplaceRepository.warehouseManagers.value
    val inventory = MarketplaceRepository.warehouseInventory.value
    val incoming = MarketplaceRepository.incomingDeliveries.value
    val dispatches = MarketplaceRepository.warehouseDispatches.value

    assertEquals("Should have exactly 3 warehouses managed", 3, managers.size)
    assertEquals("Should have exactly 100 inventory products", 100, inventory.size)
    assertEquals("Should have exactly 60 incoming deliveries", 60, incoming.size)
    assertEquals("Should have exactly 50 outgoing dispatches", 50, dispatches.size)

    val lowStockCount = inventory.count { it.status == "Low Stock" }
    assertEquals("Should have exactly 20 low stock warnings", 20, lowStockCount)

    val expiringCount = inventory.count { it.status == "Expiring" }
    assertEquals("Should have exactly 20 expiring stock alerts", 20, expiringCount)
  }

  @Test
  fun `verify incoming delivery workflow transitions`() {
    val incoming = MarketplaceRepository.incomingDeliveries.value
    val pending = incoming.firstOrNull { it.status == "Pending" }
    assertNotNull("At least one pending delivery should exist", pending)

    // Accept shipment
    val acceptResult = MarketplaceRepository.acceptIncomingDelivery(pending!!.id)
    assertTrue(acceptResult)

    val acceptedItem = MarketplaceRepository.incomingDeliveries.value.find { it.id == pending.id }
    assertNotNull(acceptedItem)
    assertEquals("Accepted", acceptedItem!!.status)
    assertNotNull(acceptedItem.receiptNumber)
    assertTrue(acceptedItem.receiptNumber!!.startsWith("REC-WR-"))

    // Verify automatically added to inventory
    val latestInv = MarketplaceRepository.warehouseInventory.value.first()
    assertEquals(pending.cropName, latestInv.cropName)
    assertEquals(pending.quantityKg, latestInv.quantityKg, 0.01)

    // Reject shipment
    val pending2 = MarketplaceRepository.incomingDeliveries.value.firstOrNull { it.status == "Pending" }
    assertNotNull(pending2)
    val rejectResult = MarketplaceRepository.rejectIncomingDelivery(pending2!!.id)
    assertTrue(rejectResult)
    assertEquals("Rejected", MarketplaceRepository.incomingDeliveries.value.find { it.id == pending2.id }?.status)

    // Record damages
    val pending3 = MarketplaceRepository.incomingDeliveries.value.firstOrNull { it.status == "Pending" }
    assertNotNull(pending3)
    val damageNotes = "Moisture dampness on 10 bags"
    val damageResult = MarketplaceRepository.recordIncomingDamages(pending3!!.id, damageNotes)
    assertTrue(damageResult)
    val damagedItem = MarketplaceRepository.incomingDeliveries.value.find { it.id == pending3.id }
    assertEquals("Damaged", damagedItem?.status)
    assertEquals(damageNotes, damagedItem?.damagesRecorded)
  }

  @Test
  fun `verify rack reallocation mapping`() {
    val inventory = MarketplaceRepository.warehouseInventory.value
    val firstItem = inventory.first()

    val result = MarketplaceRepository.reallocateRack(
      inventoryId = firstItem.id,
      zone = "Zone-X",
      rack = "Rack-99",
      shelf = "Shelf-C",
      bin = "Bin-05"
    )
    assertTrue(result)

    val updated = MarketplaceRepository.warehouseInventory.value.find { it.id == firstItem.id }
    assertNotNull(updated)
    assertEquals("Zone-X", updated!!.storageZone)
    assertEquals("Rack-99", updated.rackNumber)
    assertEquals("Shelf-C", updated.shelf)
    assertEquals("Bin-05", updated.bin)
    assertEquals("Zone-X, Rack-99, Shelf-C", updated.shelfLocation)
  }

  @Test
  fun `verify inventory audit and stock level adjustments`() {
    val inventory = MarketplaceRepository.warehouseInventory.value
    val firstItem = inventory.find { it.id == "INV_050" } // Stored state
    assertNotNull(firstItem)

    // Adjust to normal quantity
    val result = MarketplaceRepository.adjustStockQuantity(
      inventoryId = firstItem!!.id,
      availableQty = 5000.0,
      damagedQty = 150.0,
      expiringQty = 0.0,
      notes = "Calibrated physical bag balance"
    )
    assertTrue(result)

    val updated = MarketplaceRepository.warehouseInventory.value.find { it.id == firstItem.id }
    assertNotNull(updated)
    assertEquals(5000.0, updated!!.availableQuantity, 0.01)
    assertEquals(150.0, updated.damagedQuantity, 0.01)
    assertEquals("Stored", updated.status)

    // Adjust to low stock level (<= 300)
    MarketplaceRepository.adjustStockQuantity(firstItem.id, 250.0, 0.0, 0.0, "Near empty silo")
    val updatedLow = MarketplaceRepository.warehouseInventory.value.find { it.id == firstItem.id }
    assertEquals("Low Stock", updatedLow?.status)
  }

  @Test
  fun `verify dispatch workflow progression`() {
    val dispatches = MarketplaceRepository.warehouseDispatches.value
    val pendingPick = dispatches.firstOrNull { it.status == "Pending Pick" }
    assertNotNull(pendingPick)

    // 1. Pick
    assertTrue(MarketplaceRepository.pickDispatchItem(pendingPick!!.id))
    assertEquals("Picked", MarketplaceRepository.warehouseDispatches.value.find { it.id == pendingPick.id }?.status)

    // 2. Pack
    assertTrue(MarketplaceRepository.packDispatchItem(pendingPick.id))
    assertEquals("Packed", MarketplaceRepository.warehouseDispatches.value.find { it.id == pendingPick.id }?.status)

    // 3. Dispatch
    assertTrue(MarketplaceRepository.dispatchDispatchItem(pendingPick.id))
    assertEquals("Dispatched", MarketplaceRepository.warehouseDispatches.value.find { it.id == pendingPick.id }?.status)

    // 4. Complete
    assertTrue(MarketplaceRepository.completeDispatchItem(pendingPick.id))
    assertEquals("Completed", MarketplaceRepository.warehouseDispatches.value.find { it.id == pendingPick.id }?.status)
  }

  @Test
  fun `verify admin mandi price index update and broadcasting`() {
    val targetCrop = "Rice"
    val newRange = "₹2,500 - ₹2,700 / Quintal"
    val avgPrice = 26.5
    val trend = "Up"

    MarketplaceRepository.updateMandiPrice(targetCrop, newRange, avgPrice, trend)

    // Verify it is updated in the list
    val updatedItem = MarketplaceRepository.mandiPrices.value.find { it.crop == targetCrop }
    assertNotNull(updatedItem)
    assertEquals(newRange, updatedItem!!.priceRange)
    assertEquals(avgPrice, updatedItem.avgPrice, 0.01)
    assertEquals(trend, updatedItem.trend)
  }

  @Test
  fun `verify admin announcement notification broadcasting`() {
    val initialNotificationsCount = MarketplaceRepository.notifications.value.size
    val title = "Global Admin Mandi Price Alert"
    val body = "Attention: Wheat rates have hit a record high this morning."
    
    MarketplaceRepository.addNotification(userId = "ALL", role = "All", title = title, body = body)

    val updatedNotifications = MarketplaceRepository.notifications.value
    assertTrue("Notifications count should increase", updatedNotifications.size > initialNotificationsCount)
    
    val addedNotification = updatedNotifications.firstOrNull { it.title == title }
    assertNotNull(addedNotification)
    assertEquals(body, addedNotification!!.body)
    assertEquals("ALL", addedNotification.userId)
  }
}
