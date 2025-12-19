package com.enterprise.customerservice.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.enterprise.customerservice.data.models.*

/**
 * Main Room database for the application
 */
@Database(
    entities = [
        CustomerQuery::class,
        AIResponse::class,
        KnowledgeDocument::class,
        Escalation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun customerQueryDao(): CustomerQueryDao
    abstract fun aiResponseDao(): AIResponseDao
    abstract fun knowledgeDocumentDao(): KnowledgeDocumentDao
    abstract fun escalationDao(): EscalationDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "customer_service_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
