package com.enterprise.customerservice.rag.embedding

import com.enterprise.customerservice.data.models.EmbeddingVector

/**
 * Service for generating embeddings from text
 * In production, this would integrate with an actual embedding model API
 */
class EmbeddingService {
    
    companion object {
        private const val EMBEDDING_DIMENSIONS = 768 // Typical for BERT-based models
    }
    
    /**
     * Generate embedding vector for a given text
     * This is a mock implementation - in production, this would call an embedding API
     */
    suspend fun generateEmbedding(text: String, documentId: String): EmbeddingVector {
        // Mock implementation using simple text features
        // In production, replace with actual embedding model API call
        val normalizedText = text.lowercase().trim()
        val vector = createMockEmbedding(normalizedText)
        
        return EmbeddingVector(
            documentId = documentId,
            vector = vector
        )
    }
    
    /**
     * Batch generate embeddings for multiple texts
     */
    suspend fun generateBatchEmbeddings(texts: List<Pair<String, String>>): List<EmbeddingVector> {
        return texts.map { (text, docId) ->
            generateEmbedding(text, docId)
        }
    }
    
    /**
     * Calculate cosine similarity between two embedding vectors
     */
    fun cosineSimilarity(vector1: FloatArray, vector2: FloatArray): Float {
        require(vector1.size == vector2.size) { "Vectors must have same dimensions" }
        
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        
        for (i in vector1.indices) {
            dotProduct += vector1[i] * vector2[i]
            norm1 += vector1[i] * vector1[i]
            norm2 += vector2[i] * vector2[i]
        }
        
        return if (norm1 > 0 && norm2 > 0) {
            dotProduct / (kotlin.math.sqrt(norm1) * kotlin.math.sqrt(norm2))
        } else {
            0f
        }
    }
    
    /**
     * Mock embedding generation based on text features
     * In production, replace with actual embedding model
     */
    private fun createMockEmbedding(text: String): FloatArray {
        val vector = FloatArray(EMBEDDING_DIMENSIONS)
        
        // Simple hash-based mock embedding
        val hash = text.hashCode()
        for (i in vector.indices) {
            val seed = hash + i
            vector[i] = (seed % 1000) / 1000f - 0.5f
        }
        
        // Normalize vector
        val norm = kotlin.math.sqrt(vector.sumOf { (it * it).toDouble() }.toFloat())
        if (norm > 0) {
            for (i in vector.indices) {
                vector[i] /= norm
            }
        }
        
        return vector
    }
}
