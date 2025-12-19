package com.enterprise.customerservice.presentation.ui.escalation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.enterprise.customerservice.CustomerServiceApplication
import com.enterprise.customerservice.databinding.ActivityEscalationBinding
import com.enterprise.customerservice.presentation.viewmodels.EscalationViewModel
import com.enterprise.customerservice.presentation.viewmodels.ManageEscalationsUseCase

/**
 * Activity for managing escalated queries
 */
class EscalationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEscalationBinding
    private lateinit var viewModel: EscalationViewModel
    private lateinit var escalationAdapter: EscalationAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscalationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val app = application as CustomerServiceApplication
        val factory = EscalationViewModelFactory(app.manageEscalationsUseCase)
        viewModel = ViewModelProvider(this, factory)[EscalationViewModel::class.java]
    }
    
    private fun setupUI() {
        escalationAdapter = EscalationAdapter(
            onAssign = { escalationId ->
                // In production, show dialog to select agent
                val mockAgentId = "AGENT001"
                viewModel.assignEscalation(escalationId, mockAgentId)
            },
            onResolve = { escalationId ->
                viewModel.resolveEscalation(escalationId)
            }
        )
        
        binding.recyclerViewEscalations.apply {
            layoutManager = LinearLayoutManager(this@EscalationActivity)
            adapter = escalationAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.pendingEscalations.observe(this) { escalations ->
            escalationAdapter.submitList(escalations)
            binding.textEmpty?.visibility = if (escalations.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.assignmentState.observe(this) { state ->
            when (state) {
                is com.enterprise.customerservice.presentation.viewmodels.AssignmentState.Success -> {
                    Toast.makeText(this, "Escalation assigned successfully", Toast.LENGTH_SHORT).show()
                }
                is com.enterprise.customerservice.presentation.viewmodels.AssignmentState.Error -> {
                    Toast.makeText(this, "Assignment failed: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        
        viewModel.resolutionState.observe(this) { state ->
            when (state) {
                is com.enterprise.customerservice.presentation.viewmodels.ResolutionState.Success -> {
                    Toast.makeText(this, "Escalation resolved successfully", Toast.LENGTH_SHORT).show()
                }
                is com.enterprise.customerservice.presentation.viewmodels.ResolutionState.Error -> {
                    Toast.makeText(this, "Resolution failed: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}

/**
 * ViewModelFactory for EscalationViewModel
 */
class EscalationViewModelFactory(
    private val manageEscalationsUseCase: ManageEscalationsUseCase
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EscalationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EscalationViewModel(manageEscalationsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
