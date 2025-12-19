package com.enterprise.customerservice.presentation.ui.escalation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.enterprise.customerservice.R
import com.enterprise.customerservice.data.models.Escalation
import com.enterprise.customerservice.data.models.Priority
import com.enterprise.customerservice.utils.AppUtils
import com.google.android.material.card.MaterialCardView

/**
 * RecyclerView adapter for escalations
 */
class EscalationAdapter(
    private val onAssign: (Long) -> Unit,
    private val onResolve: (Long) -> Unit
) : ListAdapter<Escalation, EscalationAdapter.EscalationViewHolder>(EscalationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscalationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_escalation, parent, false)
        return EscalationViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EscalationViewHolder, position: Int) {
        holder.bind(getItem(position), onAssign, onResolve)
    }
    
    class EscalationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardEscalation)
        private val textCustomer: TextView = itemView.findViewById(R.id.textCustomerName)
        private val textQuery: TextView = itemView.findViewById(R.id.textQuery)
        private val textConfidence: TextView = itemView.findViewById(R.id.textConfidenceScore)
        private val textPriority: TextView = itemView.findViewById(R.id.textPriority)
        private val textTime: TextView = itemView.findViewById(R.id.textEscalationTime)
        private val textContext: TextView = itemView.findViewById(R.id.textContext)
        private val buttonAssign: Button = itemView.findViewById(R.id.buttonAssign)
        private val buttonResolve: Button = itemView.findViewById(R.id.buttonResolve)
        
        fun bind(escalation: Escalation, onAssign: (Long) -> Unit, onResolve: (Long) -> Unit) {
            textCustomer.text = "${escalation.customerName} (${escalation.customerId})"
            textQuery.text = escalation.originalQuery
            textConfidence.text = "AI Confidence: ${String.format("%.1f", escalation.confidenceScore)}%"
            textPriority.text = "Priority: ${escalation.priority.name}"
            textTime.text = AppUtils.getTimeAgo(escalation.escalatedAt)
            textContext.text = escalation.contextSummary
            
            // Set priority color
            when (escalation.priority) {
                Priority.URGENT -> {
                    cardView.strokeColor = Color.RED
                    textPriority.setTextColor(Color.RED)
                }
                Priority.HIGH -> {
                    cardView.strokeColor = Color.parseColor("#FF9800")
                    textPriority.setTextColor(Color.parseColor("#FF9800"))
                }
                Priority.MEDIUM -> {
                    cardView.strokeColor = Color.parseColor("#2196F3")
                    textPriority.setTextColor(Color.parseColor("#2196F3"))
                }
                Priority.LOW -> {
                    cardView.strokeColor = Color.GRAY
                    textPriority.setTextColor(Color.GRAY)
                }
            }
            
            // Setup buttons
            buttonAssign.setOnClickListener { onAssign(escalation.id) }
            buttonResolve.setOnClickListener { onResolve(escalation.id) }
            
            // Show/hide buttons based on status
            when (escalation.status) {
                com.enterprise.customerservice.data.models.EscalationStatus.PENDING -> {
                    buttonAssign.visibility = View.VISIBLE
                    buttonResolve.visibility = View.GONE
                }
                com.enterprise.customerservice.data.models.EscalationStatus.ASSIGNED,
                com.enterprise.customerservice.data.models.EscalationStatus.IN_PROGRESS -> {
                    buttonAssign.visibility = View.GONE
                    buttonResolve.visibility = View.VISIBLE
                }
                else -> {
                    buttonAssign.visibility = View.GONE
                    buttonResolve.visibility = View.GONE
                }
            }
        }
    }
}

class EscalationDiffCallback : DiffUtil.ItemCallback<Escalation>() {
    override fun areItemsTheSame(oldItem: Escalation, newItem: Escalation): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Escalation, newItem: Escalation): Boolean {
        return oldItem == newItem
    }
}
