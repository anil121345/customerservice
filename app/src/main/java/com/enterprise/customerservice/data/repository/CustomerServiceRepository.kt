package com.enterprise.customerservice.data.repository

import com.enterprise.customerservice.data.local.*
import com.enterprise.customerservice.data.models.*
import com.enterprise.customerservice.rag.embedding.EmbeddingService
import com.enterprise.customerservice.rag.generation.ResponseGenerationService
import com.enterprise.customerservice.rag.retrieval.RetrievalService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Main repository for customer service operations
 * Implements the RAG pipeline: Retrieval -> Augmented Generation
 */
class CustomerServiceRepository(
    private val customerQueryDao: CustomerQueryDao,
    private val aiResponseDao: AIResponseDao,
    private val knowledgeDocumentDao: KnowledgeDocumentDao,
    private val escalationDao: EscalationDao,
    private val embeddingService: EmbeddingService,
    private val retrievalService: RetrievalService,
    private val generationService: ResponseGenerationService
) {
    
    // Query operations
    fun getAllQueries(): Flow<List<CustomerQuery>> = customerQueryDao.getAllQueries()
    
    fun getQueriesByStatus(status: QueryStatus): Flow<List<CustomerQuery>> =
        customerQueryDao.getQueriesByStatus(status)
    
    suspend fun getQueryById(queryId: Long): CustomerQuery? =
        customerQueryDao.getQueryById(queryId)
    
    suspend fun submitQuery(query: CustomerQuery): Long {
        return customerQueryDao.insertQuery(query)
    }
    
    suspend fun updateQueryStatus(queryId: Long, status: QueryStatus) {
        customerQueryDao.updateQueryStatus(queryId, status)
    }
    
    // AI Response operations with RAG
    suspend fun processQueryWithRAG(query: CustomerQuery): AIResponse {
        // Update query status
        updateQueryStatus(query.id, QueryStatus.PROCESSING)
        
        // Step 1: Retrieve relevant documents
        val retrievedDocuments = retrievalService.hybridSearch(query.queryText, topK = 5)
        
        // Step 2: Generate response with context
        val aiResponse = generationService.generateResponse(
            query = query.queryText,
            queryId = query.id,
            retrievedDocuments = retrievedDocuments
        )
        
        // Step 3: Save response
        aiResponseDao.insertResponse(aiResponse)
        
        // Step 4: Check if escalation is needed
        if (shouldEscalate(aiResponse)) {
            createEscalation(query, aiResponse)
            updateQueryStatus(query.id, QueryStatus.ESCALATED)
        } else {
            updateQueryStatus(query.id, QueryStatus.ANSWERED)
        }
        
        return aiResponse
    }
    
    suspend fun getResponseByQueryId(queryId: Long): AIResponse? =
        aiResponseDao.getResponseByQueryId(queryId)
    
    // Knowledge base operations
    suspend fun ingestKnowledgeDocument(document: KnowledgeDocument) {
        // Generate embedding for the document
        val embedding = embeddingService.generateEmbedding(
            text = "${document.title} ${document.content}",
            documentId = document.documentId
        )
        
        // Serialize embedding
        val embeddingJson = com.google.gson.Gson().toJson(embedding.vector)
        
        // Save document with embedding
        val documentWithEmbedding = document.copy(embedding = embeddingJson)
        knowledgeDocumentDao.insertDocument(documentWithEmbedding)
    }
    
    suspend fun ingestKnowledgeDocuments(documents: List<KnowledgeDocument>) {
        documents.forEach { document ->
            ingestKnowledgeDocument(document)
        }
    }
    
    fun getAllActiveDocuments(): Flow<List<KnowledgeDocument>> =
        knowledgeDocumentDao.getAllActiveDocuments()
    
    // Escalation operations
    fun getAllEscalations(): Flow<List<Escalation>> = escalationDao.getAllEscalations()
    
    fun getPendingEscalations(): Flow<List<Escalation>> =
        escalationDao.getEscalationsByStatus(EscalationStatus.PENDING)
    
    suspend fun assignEscalation(escalationId: Long, agentId: String) {
        escalationDao.assignEscalation(escalationId, agentId)
    }
    
    suspend fun resolveEscalation(escalationId: Long) {
        escalationDao.resolveEscalation(escalationId)
    }
    
    // Private helper methods
    private fun shouldEscalate(response: AIResponse): Boolean {
        return response.confidenceScore < 60f || response.sourceDocuments.isEmpty()
    }
    
    private suspend fun createEscalation(query: CustomerQuery, response: AIResponse) {
        val contextSummary = buildContextSummary(query, response)
        
        val escalation = Escalation(
            queryId = query.id,
            customerId = query.customerId,
            customerName = query.customerName,
            originalQuery = query.queryText,
            aiResponse = response.responseText,
            confidenceScore = response.confidenceScore,
            contextSummary = contextSummary,
            escalationReason = determineEscalationReason(response),
            priority = determinePriority(response)
        )
        
        escalationDao.insertEscalation(escalation)
        aiResponseDao.markAsEscalated(response.id)
    }
    
    private fun buildContextSummary(query: CustomerQuery, response: AIResponse): String {
        return """
            Customer: ${query.customerName} (ID: ${query.customerId})
            Query: ${query.queryText}
            AI Confidence: ${String.format("%.1f", response.confidenceScore)}%
            Source Documents: ${response.sourceDocuments.size}
            
            AI's preliminary response:
            ${response.responseText.take(200)}...
            
            Recommendation: Manual review needed due to ${if (response.confidenceScore < 60f) "low confidence" else "complexity"}.
        """.trimIndent()
    }
    
    private fun determineEscalationReason(response: AIResponse): EscalationReason {
        return when {
            response.sourceDocuments.isEmpty() -> EscalationReason.TECHNICAL_ERROR
            response.confidenceScore < 30f -> EscalationReason.LOW_CONFIDENCE
            response.confidenceScore < 60f -> EscalationReason.COMPLEX_QUERY
            else -> EscalationReason.LOW_CONFIDENCE
        }
    }
    
    private fun determinePriority(response: AIResponse): Priority {
        return when {
            response.confidenceScore < 20f -> Priority.HIGH
            response.confidenceScore < 40f -> Priority.MEDIUM
            else -> Priority.LOW
        }
    }
}
