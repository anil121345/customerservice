package com.enterprise.customerservice.rag.generation

import com.enterprise.customerservice.data.models.AIResponse
import com.enterprise.customerservice.data.models.ConfidenceLevel
import com.enterprise.customerservice.rag.retrieval.RetrievedDocument

/**
 * Service for generating AI responses using RAG
 * Integrates retrieved context with generative AI
 */
class ResponseGenerationService {
    
    companion object {
        private const val MAX_CONTEXT_LENGTH = 2000
        private const val BASE_CONFIDENCE = 50f
    }
    
    /**
     * Generate a response based on query and retrieved context
     */
    suspend fun generateResponse(
        query: String,
        queryId: Long,
        retrievedDocuments: List<RetrievedDocument>
    ): AIResponse {
        if (retrievedDocuments.isEmpty()) {
            return generateFallbackResponse(query, queryId)
        }
        
        // Build context from retrieved documents
        val context = buildContext(retrievedDocuments)
        
        // Generate response (mock implementation)
        // In production, this would call an LLM API with the context
        val responseText = generateResponseText(query, context, retrievedDocuments)
        
        // Calculate confidence score
        val confidenceScore = calculateConfidenceScore(query, retrievedDocuments, responseText)
        
        // Extract source document IDs
        val sourceDocumentIds = retrievedDocuments.map { it.document.documentId }
        
        return AIResponse(
            queryId = queryId,
            responseText = responseText,
            confidenceScore = confidenceScore,
            sourceDocuments = sourceDocumentIds,
            isEscalated = confidenceScore < 60f
        )
    }
    
    /**
     * Build context string from retrieved documents
     */
    private fun buildContext(documents: List<RetrievedDocument>): String {
        val contextBuilder = StringBuilder()
        var currentLength = 0
        
        for (doc in documents) {
            val docText = """
                Document: ${doc.document.title}
                Category: ${doc.document.category}
                Content: ${doc.document.content}
                Relevance: ${String.format("%.2f", doc.similarityScore)}
                
                """.trimIndent()
            
            if (currentLength + docText.length <= MAX_CONTEXT_LENGTH) {
                contextBuilder.append(docText)
                currentLength += docText.length
            } else {
                break
            }
        }
        
        return contextBuilder.toString()
    }
    
    /**
     * Generate response text (mock implementation)
     * In production, replace with actual LLM API call
     */
    private fun generateResponseText(
        query: String,
        context: String,
        retrievedDocuments: List<RetrievedDocument>
    ): String {
        // Mock response generation
        val topDoc = retrievedDocuments.firstOrNull()
        
        return if (topDoc != null && topDoc.similarityScore > 0.7f) {
            """Based on our knowledge base, here's what I found regarding your query about "${query}":
                
${topDoc.document.content.take(500)}

This information is from our ${topDoc.document.category} documentation. ${if (retrievedDocuments.size > 1) "I've also found ${retrievedDocuments.size - 1} other related documents that might be helpful." else ""}

Is there anything specific you'd like me to clarify?"""
        } else {
            """I found some information that might help with your query about "${query}". 

Based on our documentation in ${topDoc?.document?.category ?: "various categories"}, here are some relevant points:

${topDoc?.document?.content?.take(300) ?: "General information available."}

However, I recommend speaking with a support specialist for a more detailed answer. Would you like me to connect you with an agent?"""
        }
    }
    
    /**
     * Calculate confidence score for the generated response
     */
    private fun calculateConfidenceScore(
        query: String,
        retrievedDocuments: List<RetrievedDocument>,
        responseText: String
    ): Float {
        if (retrievedDocuments.isEmpty()) {
            return 0f
        }
        
        // Factor 1: Top document similarity (40% weight)
        val topSimilarity = retrievedDocuments.first().similarityScore * 100f
        val similarityScore = topSimilarity * 0.4f
        
        // Factor 2: Number of relevant documents (20% weight)
        val documentCountScore = minOf(retrievedDocuments.size / 3f, 1f) * 20f
        
        // Factor 3: Average similarity of top 3 docs (30% weight)
        val topDocs = retrievedDocuments.take(3)
        val avgSimilarity = topDocs.map { it.similarityScore }.average().toFloat()
        val avgScore = avgSimilarity * 100f * 0.3f
        
        // Factor 4: Response length appropriateness (10% weight)
        val lengthScore = if (responseText.length in 100..1000) 10f else 5f
        
        val totalScore = similarityScore + documentCountScore + avgScore + lengthScore
        
        return minOf(totalScore, 100f)
    }
    
    /**
     * Generate a fallback response when no documents are retrieved
     */
    private fun generateFallbackResponse(query: String, queryId: Long): AIResponse {
        return AIResponse(
            queryId = queryId,
            responseText = """I apologize, but I couldn't find specific information in our knowledge base to answer your query about "${query}". 

This might be a unique question or require specialized assistance. I'd recommend speaking with one of our support specialists who can provide you with more detailed help.

Would you like me to escalate this to a human agent?""",
            confidenceScore = 0f,
            sourceDocuments = emptyList(),
            isEscalated = true
        )
    }
}
