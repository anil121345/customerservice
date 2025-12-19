package com.enterprise.customerservice.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.customerservice.data.models.AIResponse
import com.enterprise.customerservice.data.models.CustomerQuery
import com.enterprise.customerservice.domain.usecases.ProcessQueryWithRAGUseCase
import com.enterprise.customerservice.domain.usecases.SubmitQueryUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for chat/customer query interface
 */
class ChatViewModel(
    private val submitQueryUseCase: SubmitQueryUseCase,
    private val processQueryUseCase: ProcessQueryWithRAGUseCase
) : ViewModel() {
    
    private val _queryState = MutableLiveData<QueryState>()
    val queryState: LiveData<QueryState> = _queryState
    
    private val _responseState = MutableLiveData<ResponseState>()
    val responseState: LiveData<ResponseState> = _responseState
    
    private val _chatHistory = MutableLiveData<List<ChatMessage>>()
    val chatHistory: LiveData<List<ChatMessage>> = _chatHistory
    
    init {
        _chatHistory.value = emptyList()
    }
    
    fun submitQuery(customerId: String, customerName: String, queryText: String, category: String? = null) {
        viewModelScope.launch {
            _queryState.value = QueryState.Loading
            
            // Add user message to chat
            addChatMessage(ChatMessage.UserMessage(queryText))
            
            // Submit query
            val result = submitQueryUseCase(customerId, customerName, queryText, category)
            
            result.fold(
                onSuccess = { queryId ->
                    _queryState.value = QueryState.Success(queryId)
                    processQuery(queryId)
                },
                onFailure = { error ->
                    _queryState.value = QueryState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    private fun processQuery(queryId: Long) {
        viewModelScope.launch {
            _responseState.value = ResponseState.Processing
            
            val result = processQueryUseCase(queryId)
            
            result.fold(
                onSuccess = { response ->
                    _responseState.value = ResponseState.Success(response)
                    addChatMessage(ChatMessage.AIMessage(response))
                },
                onFailure = { error ->
                    _responseState.value = ResponseState.Error(error.message ?: "Failed to generate response")
                }
            )
        }
    }
    
    private fun addChatMessage(message: ChatMessage) {
        val currentHistory = _chatHistory.value ?: emptyList()
        _chatHistory.value = currentHistory + message
    }
    
    fun clearChat() {
        _chatHistory.value = emptyList()
        _queryState.value = QueryState.Idle
        _responseState.value = ResponseState.Idle
    }
}

sealed class QueryState {
    object Idle : QueryState()
    object Loading : QueryState()
    data class Success(val queryId: Long) : QueryState()
    data class Error(val message: String) : QueryState()
}

sealed class ResponseState {
    object Idle : ResponseState()
    object Processing : ResponseState()
    data class Success(val response: AIResponse) : ResponseState()
    data class Error(val message: String) : ResponseState()
}

sealed class ChatMessage {
    data class UserMessage(val text: String, val timestamp: Long = System.currentTimeMillis()) : ChatMessage()
    data class AIMessage(val response: AIResponse, val timestamp: Long = System.currentTimeMillis()) : ChatMessage()
}
