package com.enterprise.customerservice

import android.app.Application
import com.enterprise.customerservice.data.local.AppDatabase
import com.enterprise.customerservice.data.repository.CustomerServiceRepository
import com.enterprise.customerservice.domain.usecases.*
import com.enterprise.customerservice.rag.embedding.EmbeddingService
import com.enterprise.customerservice.rag.generation.ResponseGenerationService
import com.enterprise.customerservice.rag.retrieval.RetrievalService
import com.enterprise.customerservice.security.SecurityManager

/**
 * Main application class
 * Initializes dependencies and manages app lifecycle
 */
class CustomerServiceApplication : Application() {
    
    // Database
    private val database by lazy { AppDatabase.getDatabase(this) }
    
    // Security
    val securityManager by lazy { SecurityManager(this) }
    
    // RAG Services
    private val embeddingService by lazy { EmbeddingService() }
    private val retrievalService by lazy { 
        RetrievalService(database.knowledgeDocumentDao(), embeddingService)
    }
    private val generationService by lazy { ResponseGenerationService() }
    
    // Repository
    val repository by lazy {
        CustomerServiceRepository(
            customerQueryDao = database.customerQueryDao(),
            aiResponseDao = database.aiResponseDao(),
            knowledgeDocumentDao = database.knowledgeDocumentDao(),
            escalationDao = database.escalationDao(),
            embeddingService = embeddingService,
            retrievalService = retrievalService,
            generationService = generationService
        )
    }
    
    // Use Cases
    val submitQueryUseCase by lazy { SubmitQueryUseCase(repository) }
    val processQueryUseCase by lazy { ProcessQueryWithRAGUseCase(repository) }
    val ingestKnowledgeUseCase by lazy { IngestKnowledgeUseCase(repository) }
    val manageEscalationsUseCase by lazy { ManageEscalationsUseCase(repository) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app-wide configurations
        initializeSampleKnowledgeBase()
    }
    
    /**
     * Initialize sample knowledge base for demo purposes
     */
    private fun initializeSampleKnowledgeBase() {
        // This would be done asynchronously in production
        // Sample data for demonstration
    }
}
