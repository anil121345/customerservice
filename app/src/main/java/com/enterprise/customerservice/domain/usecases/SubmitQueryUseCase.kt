package com.enterprise.customerservice.domain.usecases

import com.enterprise.customerservice.data.models.CustomerQuery
import com.enterprise.customerservice.data.models.QueryStatus
import com.enterprise.customerservice.data.repository.CustomerServiceRepository

/**
 * Use case for submitting and processing customer queries
 */
class SubmitQueryUseCase(
    private val repository: CustomerServiceRepository
) {
    
    suspend operator fun invoke(
        customerId: String,
        customerName: String,
        queryText: String,
        category: String? = null
    ): Result<Long> {
        return try {
            // Validate input
            if (queryText.isBlank()) {
                return Result.failure(IllegalArgumentException("Query text cannot be empty"))
            }
            
            if (customerId.isBlank()) {
                return Result.failure(IllegalArgumentException("Customer ID cannot be empty"))
            }
            
            // Create query
            val query = CustomerQuery(
                customerId = customerId,
                customerName = customerName,
                queryText = queryText,
                category = category,
                status = QueryStatus.PENDING
            )
            
            // Submit query
            val queryId = repository.submitQuery(query)
            Result.success(queryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
