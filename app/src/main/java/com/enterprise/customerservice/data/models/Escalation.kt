package com.enterprise.customerservice.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an escalated query that needs human agent attention
 */
@Entity(tableName = "escalations")
data class Escalation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val queryId: Long,
    val customerId: String,
    val customerName: String,
    val originalQuery: String,
    val aiResponse: String,
    val confidenceScore: Float,
    val contextSummary: String, // Summarized context for human agent
    val escalationReason: EscalationReason,
    val priority: Priority = Priority.MEDIUM,
    val escalatedAt: Long = System.currentTimeMillis(),
    val assignedAgent: String? = null,
    val status: EscalationStatus = EscalationStatus.PENDING,
    val resolvedAt: Long? = null
)

enum class EscalationReason {
    LOW_CONFIDENCE,
    COMPLEX_QUERY,
    SENSITIVE_ISSUE,
    CUSTOMER_REQUEST,
    TECHNICAL_ERROR
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class EscalationStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
