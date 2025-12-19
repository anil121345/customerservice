package com.enterprise.customerservice.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.enterprise.customerservice.data.models.Escalation
import com.enterprise.customerservice.domain.usecases.ManageEscalationsUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for escalation management interface
 */
class EscalationViewModel(
    private val manageEscalationsUseCase: ManageEscalationsUseCase
) : ViewModel() {
    
    val pendingEscalations: LiveData<List<Escalation>> = 
        manageEscalationsUseCase.getPendingEscalations().asLiveData()
    
    val allEscalations: LiveData<List<Escalation>> =
        manageEscalationsUseCase.getAllEscalations().asLiveData()
    
    private val _assignmentState = MutableLiveData<AssignmentState>()
    val assignmentState: LiveData<AssignmentState> = _assignmentState
    
    private val _resolutionState = MutableLiveData<ResolutionState>()
    val resolutionState: LiveData<ResolutionState> = _resolutionState
    
    fun assignEscalation(escalationId: Long, agentId: String) {
        viewModelScope.launch {
            _assignmentState.value = AssignmentState.Loading
            
            val result = manageEscalationsUseCase.assignToAgent(escalationId, agentId)
            
            result.fold(
                onSuccess = {
                    _assignmentState.value = AssignmentState.Success
                },
                onFailure = { error ->
                    _assignmentState.value = AssignmentState.Error(error.message ?: "Assignment failed")
                }
            )
        }
    }
    
    fun resolveEscalation(escalationId: Long) {
        viewModelScope.launch {
            _resolutionState.value = ResolutionState.Loading
            
            val result = manageEscalationsUseCase.resolveEscalation(escalationId)
            
            result.fold(
                onSuccess = {
                    _resolutionState.value = ResolutionState.Success
                },
                onFailure = { error ->
                    _resolutionState.value = ResolutionState.Error(error.message ?: "Resolution failed")
                }
            )
        }
    }
}

sealed class AssignmentState {
    object Idle : AssignmentState()
    object Loading : AssignmentState()
    object Success : AssignmentState()
    data class Error(val message: String) : AssignmentState()
}

sealed class ResolutionState {
    object Idle : ResolutionState()
    object Loading : ResolutionState()
    object Success : ResolutionState()
    data class Error(val message: String) : ResolutionState()
}
