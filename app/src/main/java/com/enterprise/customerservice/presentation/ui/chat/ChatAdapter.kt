package com.enterprise.customerservice.presentation.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.enterprise.customerservice.R
import com.enterprise.customerservice.presentation.viewmodels.ChatMessage
import com.enterprise.customerservice.utils.AppUtils
import com.google.android.material.card.MaterialCardView

/**
 * RecyclerView adapter for chat messages
 */
class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatMessage.UserMessage -> VIEW_TYPE_USER
            is ChatMessage.AIMessage -> VIEW_TYPE_AI
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = inflater.inflate(R.layout.item_user_message, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_AI -> {
                val view = inflater.inflate(R.layout.item_ai_message, parent, false)
                AIMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val message = getItem(position)) {
            is ChatMessage.UserMessage -> (holder as UserMessageViewHolder).bind(message)
            is ChatMessage.AIMessage -> (holder as AIMessageViewHolder).bind(message)
        }
    }
    
    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textUserMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textUserTime)
        
        fun bind(message: ChatMessage.UserMessage) {
            textMessage.text = message.text
            textTime.text = AppUtils.getTimeAgo(message.timestamp)
        }
    }
    
    class AIMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textAIMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textAITime)
        private val textConfidence: TextView = itemView.findViewById(R.id.textConfidence)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardAIMessage)
        
        fun bind(message: ChatMessage.AIMessage) {
            textMessage.text = message.response.responseText
            textTime.text = AppUtils.getTimeAgo(message.timestamp)
            
            val confidenceScore = message.response.confidenceScore
            textConfidence.text = "${String.format("%.1f", confidenceScore)}% - ${AppUtils.getConfidenceText(confidenceScore)}"
            
            // Set confidence color
            val color = Color.parseColor(AppUtils.getConfidenceColor(confidenceScore))
            textConfidence.setTextColor(color)
            
            // Highlight escalated messages
            if (message.response.isEscalated) {
                cardView.strokeColor = Color.parseColor("#FF9800")
                cardView.strokeWidth = 4
            }
        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return when {
            oldItem is ChatMessage.UserMessage && newItem is ChatMessage.UserMessage ->
                oldItem.timestamp == newItem.timestamp
            oldItem is ChatMessage.AIMessage && newItem is ChatMessage.AIMessage ->
                oldItem.response.id == newItem.response.id
            else -> false
        }
    }
    
    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}
