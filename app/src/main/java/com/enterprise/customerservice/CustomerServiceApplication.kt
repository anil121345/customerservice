package com.enterprise.customerservice

import android.app.Application
import com.enterprise.customerservice.data.local.AppDatabase
import com.enterprise.customerservice.data.repository.CustomerServiceRepository
import com.enterprise.customerservice.domain.usecases.*
import com.enterprise.customerservice.rag.embedding.EmbeddingService
import com.enterprise.customerservice.rag.generation.ResponseGenerationService
import com.enterprise.customerservice.rag.retrieval.RetrievalService
import com.enterprise.customerservice.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        // Launch coroutine to ingest sample documents
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if knowledge base is empty
                val existingDocs = database.knowledgeDocumentDao()
                    .getAllActiveDocuments()
                    .first()
                
                if (existingDocs.isEmpty()) {
                    // Ingest sample documents
                    val sampleDocs = com.enterprise.customerservice.data.models.SampleKnowledgeData.getSampleDocuments()
                    ingestKnowledgeUseCase.ingestBatch(sampleDocs)
                }
            } catch (e: Exception) {
                // Log error in production
                e.printStackTrace()
            }
        }
    }
}
