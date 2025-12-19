package com.enterprise.customerservice.data.local

import androidx.room.*
import com.enterprise.customerservice.data.models.CustomerQuery
import com.enterprise.customerservice.data.models.QueryStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for customer queries
 */
@Dao
interface CustomerQueryDao {
    
    @Query("SELECT * FROM customer_queries ORDER BY timestamp DESC")
    fun getAllQueries(): Flow<List<CustomerQuery>>
    
    @Query("SELECT * FROM customer_queries WHERE id = :queryId")
    suspend fun getQueryById(queryId: Long): CustomerQuery?
    
    @Query("SELECT * FROM customer_queries WHERE status = :status ORDER BY timestamp DESC")
    fun getQueriesByStatus(status: QueryStatus): Flow<List<CustomerQuery>>
    
    @Query("SELECT * FROM customer_queries WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getQueriesByCustomer(customerId: String): Flow<List<CustomerQuery>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query: CustomerQuery): Long
    
    @Update
    suspend fun updateQuery(query: CustomerQuery)
    
    @Delete
    suspend fun deleteQuery(query: CustomerQuery)
    
    @Query("UPDATE customer_queries SET status = :status WHERE id = :queryId")
    suspend fun updateQueryStatus(queryId: Long, status: QueryStatus)
}
