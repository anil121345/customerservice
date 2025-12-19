package com.enterprise.customerservice.data.local

import androidx.room.*
import com.enterprise.customerservice.data.models.Escalation
import com.enterprise.customerservice.data.models.EscalationStatus
import com.enterprise.customerservice.data.models.Priority
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for escalations
 */
@Dao
interface EscalationDao {
    
    @Query("SELECT * FROM escalations ORDER BY escalatedAt DESC")
    fun getAllEscalations(): Flow<List<Escalation>>
    
    @Query("SELECT * FROM escalations WHERE id = :escalationId")
    suspend fun getEscalationById(escalationId: Long): Escalation?
    
    @Query("SELECT * FROM escalations WHERE status = :status ORDER BY priority DESC, escalatedAt DESC")
    fun getEscalationsByStatus(status: EscalationStatus): Flow<List<Escalation>>
    
    @Query("SELECT * FROM escalations WHERE assignedAgent = :agentId AND status != :excludeStatus")
    fun getEscalationsByAgent(agentId: String, excludeStatus: EscalationStatus = EscalationStatus.CLOSED): Flow<List<Escalation>>
    
    @Query("SELECT * FROM escalations WHERE priority = :priority AND status = :status")
    fun getEscalationsByPriority(priority: Priority, status: EscalationStatus = EscalationStatus.PENDING): Flow<List<Escalation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscalation(escalation: Escalation): Long
    
    @Update
    suspend fun updateEscalation(escalation: Escalation)
    
    @Delete
    suspend fun deleteEscalation(escalation: Escalation)
    
    @Query("UPDATE escalations SET status = :status, assignedAgent = :agentId WHERE id = :escalationId")
    suspend fun assignEscalation(escalationId: Long, agentId: String, status: EscalationStatus = EscalationStatus.ASSIGNED)
    
    @Query("UPDATE escalations SET status = :status, resolvedAt = :resolvedAt WHERE id = :escalationId")
    suspend fun resolveEscalation(escalationId: Long, status: EscalationStatus = EscalationStatus.RESOLVED, resolvedAt: Long = System.currentTimeMillis())
}
