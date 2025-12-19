package com.enterprise.customerservice.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a customer query in the support system
 */
@Entity(tableName = "customer_queries")
data class CustomerQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: String,
    val customerName: String,
    val queryText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: QueryStatus = QueryStatus.PENDING,
    val category: String? = null
)

enum class QueryStatus {
    PENDING,
    PROCESSING,
    ANSWERED,
    ESCALATED,
    RESOLVED
}
