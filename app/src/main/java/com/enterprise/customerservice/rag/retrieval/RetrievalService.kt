package com.enterprise.customerservice.rag.retrieval

import com.enterprise.customerservice.data.local.KnowledgeDocumentDao
import com.enterprise.customerservice.data.models.EmbeddingVector
import com.enterprise.customerservice.data.models.KnowledgeDocument
import com.enterprise.customerservice.rag.embedding.EmbeddingService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

/**
 * Service for retrieving relevant documents from knowledge base
 * Implements semantic search using embeddings
 */
class RetrievalService(
    private val knowledgeDocumentDao: KnowledgeDocumentDao,
    private val embeddingService: EmbeddingService
) {
    
    private val gson = Gson()
    
    companion object {
        private const val DEFAULT_TOP_K = 5
        private const val SIMILARITY_THRESHOLD = 0.3f
    }
    
    /**
     * Retrieve most relevant documents for a query
     */
    suspend fun retrieveRelevantDocuments(
        query: String,
        topK: Int = DEFAULT_TOP_K,
        similarityThreshold: Float = SIMILARITY_THRESHOLD
    ): List<RetrievedDocument> {
        // Generate embedding for the query
        val queryEmbedding = embeddingService.generateEmbedding(query, "query")
        
        // Get all active documents
        val allDocuments = knowledgeDocumentDao.getAllActiveDocuments().first()
        
        // Calculate similarity scores
        val scoredDocuments = allDocuments.mapNotNull { doc ->
            try {
                val docEmbedding = deserializeEmbedding(doc.embedding)
                val similarity = embeddingService.cosineSimilarity(
                    queryEmbedding.vector,
                    docEmbedding
                )
                
                if (similarity >= similarityThreshold) {
                    RetrievedDocument(
                        document = doc,
                        similarityScore = similarity
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null // Skip documents with invalid embeddings
            }
        }
        
        // Sort by similarity and return top K
        return scoredDocuments
            .sortedByDescending { it.similarityScore }
            .take(topK)
    }
    
    /**
     * Hybrid search combining semantic and keyword-based retrieval
     */
    suspend fun hybridSearch(
        query: String,
        topK: Int = DEFAULT_TOP_K
    ): List<RetrievedDocument> {
        // Semantic search
        val semanticResults = retrieveRelevantDocuments(query, topK)
        
        // Keyword search
        val keywordResults = knowledgeDocumentDao.searchDocuments(query)
        
        // Combine and re-rank results
        val combinedResults = mutableMapOf<String, RetrievedDocument>()
        
        semanticResults.forEach { result ->
            combinedResults[result.document.documentId] = result
        }
        
        keywordResults.forEach { doc ->
            val existing = combinedResults[doc.documentId]
            if (existing != null) {
                // Boost score if found in both searches
                combinedResults[doc.documentId] = existing.copy(
                    similarityScore = existing.similarityScore * 1.2f
                )
            } else {
                // Add with moderate score
                combinedResults[doc.documentId] = RetrievedDocument(
                    document = doc,
                    similarityScore = 0.5f
                )
            }
        }
        
        return combinedResults.values
            .sortedByDescending { it.similarityScore }
            .take(topK)
    }
    
    /**
     * Deserialize embedding from string format
     */
    private fun deserializeEmbedding(embeddingStr: String): FloatArray {
        return gson.fromJson(embeddingStr, FloatArray::class.java)
    }
}

/**
 * Represents a retrieved document with its similarity score
 */
data class RetrievedDocument(
    val document: KnowledgeDocument,
    val similarityScore: Float
)
