package com.enterprise.customerservice.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a document in the knowledge base
 */
@Entity(tableName = "knowledge_base")
data class KnowledgeDocument(
    @PrimaryKey
    val documentId: String,
    val title: String,
    val content: String,
    val category: String,
    val embedding: String, // Serialized embedding vector
    val metadata: String, // JSON metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Represents an embedding vector for semantic search
 */
data class EmbeddingVector(
    val documentId: String,
    val vector: FloatArray,
    val dimensions: Int = vector.size
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmbeddingVector

        if (documentId != other.documentId) return false
        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = documentId.hashCode()
        result = 31 * result + vector.contentHashCode()
        return result
    }
}
