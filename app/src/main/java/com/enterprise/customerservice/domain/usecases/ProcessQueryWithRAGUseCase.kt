package com.enterprise.customerservice.domain.usecases

import com.enterprise.customerservice.data.models.AIResponse
import com.enterprise.customerservice.data.models.CustomerQuery
import com.enterprise.customerservice.data.repository.CustomerServiceRepository

/**
 * Use case for processing queries with RAG pipeline
 */
class ProcessQueryWithRAGUseCase(
    private val repository: CustomerServiceRepository
) {
    
    suspend operator fun invoke(queryId: Long): Result<AIResponse> {
        return try {
            // Get query
            val query = repository.getQueryById(queryId)
                ?: return Result.failure(IllegalArgumentException("Query not found"))
            
            // Process with RAG
            val response = repository.processQueryWithRAG(query)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
