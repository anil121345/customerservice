package com.enterprise.customerservice.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for the application
 */
object AppUtils {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    /**
     * Format timestamp to readable date
     */
    fun formatTimestamp(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Check network connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Truncate text to specified length
     */
    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length <= maxLength) {
            text
        } else {
            text.take(maxLength - 3) + "..."
        }
    }
    
    /**
     * Get confidence color resource based on score
     */
    fun getConfidenceColor(score: Float): String {
        return when {
            score >= 80f -> "#4CAF50" // Green
            score >= 60f -> "#FF9800" // Orange
            else -> "#F44336" // Red
        }
    }
    
    /**
     * Get confidence text based on score
     */
    fun getConfidenceText(score: Float): String {
        return when {
            score >= 80f -> "High Confidence"
            score >= 60f -> "Medium Confidence"
            else -> "Low Confidence"
        }
    }
    
    /**
     * Calculate time ago from timestamp
     */
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
            else -> formatTimestamp(timestamp)
        }
    }
}
