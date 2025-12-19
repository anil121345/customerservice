package com.enterprise.customerservice.domain.usecases

import com.enterprise.customerservice.data.models.Escalation
import com.enterprise.customerservice.data.repository.CustomerServiceRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for managing escalations
 */
class ManageEscalationsUseCase(
    private val repository: CustomerServiceRepository
) {
    
    fun getPendingEscalations(): Flow<List<Escalation>> {
        return repository.getPendingEscalations()
    }
    
    fun getAllEscalations(): Flow<List<Escalation>> {
        return repository.getAllEscalations()
    }
    
    suspend fun assignToAgent(escalationId: Long, agentId: String): Result<Unit> {
        return try {
            repository.assignEscalation(escalationId, agentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resolveEscalation(escalationId: Long): Result<Unit> {
        return try {
            repository.resolveEscalation(escalationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
