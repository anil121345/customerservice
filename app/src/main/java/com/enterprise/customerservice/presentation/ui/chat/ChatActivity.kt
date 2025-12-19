package com.enterprise.customerservice.presentation.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.enterprise.customerservice.CustomerServiceApplication
import com.enterprise.customerservice.R
import com.enterprise.customerservice.databinding.ActivityChatBinding
import com.enterprise.customerservice.presentation.viewmodels.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Main chat activity for customer support interface
 */
class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    
    // Mock customer data - in production, get from authentication
    private val mockCustomerId = "CUST001"
    private val mockCustomerName = "John Doe"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val app = application as CustomerServiceApplication
        val factory = ChatViewModelFactory(
            app.submitQueryUseCase,
            app.processQueryUseCase
        )
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup RecyclerView
        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
        
        // Setup send button
        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
        
        // Setup clear button
        binding.buttonClear?.setOnClickListener {
            clearChat()
        }
    }
    
    private fun observeViewModel() {
        viewModel.chatHistory.observe(this) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerViewChat.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.queryState.observe(this) { state ->
            when (state) {
                is QueryState.Loading -> {
                    binding.buttonSend.isEnabled = false
                    binding.editTextQuery.isEnabled = false
                }
                is QueryState.Success -> {
                    binding.editTextQuery.text?.clear()
                }
                is QueryState.Error -> {
                    binding.buttonSend.isEnabled = true
                    binding.editTextQuery.isEnabled = true
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.buttonSend.isEnabled = true
                    binding.editTextQuery.isEnabled = true
                }
            }
        }
        
        viewModel.responseState.observe(this) { state ->
            when (state) {
                is ResponseState.Processing -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
                is ResponseState.Success -> {
                    binding.progressBar?.visibility = View.GONE
                    binding.buttonSend.isEnabled = true
                    binding.editTextQuery.isEnabled = true
                    
                    // Show escalation dialog if needed
                    if (state.response.isEscalated) {
                        showEscalationDialog(state.response.confidenceScore)
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar?.visibility = View.GONE
                    binding.buttonSend.isEnabled = true
                    binding.editTextQuery.isEnabled = true
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar?.visibility = View.GONE
                }
            }
        }
    }
    
    private fun sendMessage() {
        val queryText = binding.editTextQuery.text?.toString()?.trim()
        
        if (queryText.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.submitQuery(
            customerId = mockCustomerId,
            customerName = mockCustomerName,
            queryText = queryText
        )
    }
    
    private fun clearChat() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear Chat")
            .setMessage("Are you sure you want to clear the chat history?")
            .setPositiveButton("Clear") { _, _ ->
                viewModel.clearChat()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEscalationDialog(confidence: Float) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Query Escalated")
            .setMessage("This query has been escalated to a human agent due to low confidence (${String.format("%.1f", confidence)}%). An agent will contact you shortly.")
            .setPositiveButton("OK", null)
            .show()
    }
}

/**
 * ViewModelFactory for ChatViewModel
 */
class ChatViewModelFactory(
    private val submitQueryUseCase: SubmitQueryUseCase,
    private val processQueryUseCase: ProcessQueryWithRAGUseCase
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(submitQueryUseCase, processQueryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
