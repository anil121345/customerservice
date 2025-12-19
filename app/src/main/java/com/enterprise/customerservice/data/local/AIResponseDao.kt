package com.enterprise.customerservice.data.local

import androidx.room.*
import com.enterprise.customerservice.data.models.AIResponse
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AI responses
 */
@Dao
interface AIResponseDao {
    
    @Query("SELECT * FROM ai_responses ORDER BY generatedAt DESC")
    fun getAllResponses(): Flow<List<AIResponse>>
    
    @Query("SELECT * FROM ai_responses WHERE queryId = :queryId")
    suspend fun getResponseByQueryId(queryId: Long): AIResponse?
    
    @Query("SELECT * FROM ai_responses WHERE confidenceScore < :threshold AND isEscalated = 0")
    fun getLowConfidenceResponses(threshold: Float = 60f): Flow<List<AIResponse>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: AIResponse): Long
    
    @Update
    suspend fun updateResponse(response: AIResponse)
    
    @Delete
    suspend fun deleteResponse(response: AIResponse)
    
    @Query("UPDATE ai_responses SET isEscalated = 1 WHERE id = :responseId")
    suspend fun markAsEscalated(responseId: Long)
}
