package com.enterprise.customerservice.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an AI-generated response with confidence scoring
 */
@Entity(tableName = "ai_responses")
data class AIResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val queryId: Long,
    val responseText: String,
    val confidenceScore: Float, // 0.0 to 100.0
    val generatedAt: Long = System.currentTimeMillis(),
    val sourceDocuments: List<String> = emptyList(), // Document IDs used for RAG
    val isEscalated: Boolean = false
)

/**
 * Confidence level categorization
 */
enum class ConfidenceLevel {
    HIGH,      // >= 80%
    MEDIUM,    // 60-79%
    LOW;       // < 60%
    
    companion object {
        fun fromScore(score: Float): ConfidenceLevel {
            return when {
                score >= 80f -> HIGH
                score >= 60f -> MEDIUM
                else -> LOW
            }
        }
    }
}
