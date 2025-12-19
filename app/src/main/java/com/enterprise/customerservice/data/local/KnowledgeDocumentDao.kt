package com.enterprise.customerservice.data.local

import androidx.room.*
import com.enterprise.customerservice.data.models.KnowledgeDocument
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for knowledge base documents
 */
@Dao
interface KnowledgeDocumentDao {
    
    @Query("SELECT * FROM knowledge_base WHERE isActive = 1")
    fun getAllActiveDocuments(): Flow<List<KnowledgeDocument>>
    
    @Query("SELECT * FROM knowledge_base WHERE documentId = :documentId")
    suspend fun getDocumentById(documentId: String): KnowledgeDocument?
    
    @Query("SELECT * FROM knowledge_base WHERE category = :category AND isActive = 1")
    fun getDocumentsByCategory(category: String): Flow<List<KnowledgeDocument>>
    
    @Query("SELECT * FROM knowledge_base WHERE content LIKE '%' || :searchTerm || '%' AND isActive = 1")
    suspend fun searchDocuments(searchTerm: String): List<KnowledgeDocument>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: KnowledgeDocument)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<KnowledgeDocument>)
    
    @Update
    suspend fun updateDocument(document: KnowledgeDocument)
    
    @Delete
    suspend fun deleteDocument(document: KnowledgeDocument)
    
    @Query("UPDATE knowledge_base SET isActive = 0 WHERE documentId = :documentId")
    suspend fun deactivateDocument(documentId: String)
    
    @Query("DELETE FROM knowledge_base WHERE isActive = 0 AND lastUpdated < :timestamp")
    suspend fun cleanupOldDocuments(timestamp: Long)
}
