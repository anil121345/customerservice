# Testing Guide

## Manual Testing

### 1. Initial Setup
1. Build and install the app on an Android device or emulator (API 24+)
2. Launch the app - you should see the chat interface
3. The app will automatically initialize with sample knowledge base documents

### 2. Testing Chat Interface

#### Test Case 1: High Confidence Query
**Input:** "How do I reset my password?"
**Expected Result:**
- AI response with password reset instructions
- Confidence score: 75-95%
- Response should include step-by-step instructions
- No escalation

#### Test Case 2: Medium Confidence Query
**Input:** "What payment methods do you accept?"
**Expected Result:**
- AI response about billing and payments
- Confidence score: 60-80%
- Response mentions credit cards, PayPal, etc.
- No escalation if score >= 60%

#### Test Case 3: Low Confidence Query (Escalation)
**Input:** "Why is my custom integration not working?"
**Expected Result:**
- AI attempts to respond but has low confidence
- Confidence score: < 60%
- Escalation dialog appears
- Query is added to escalation queue

#### Test Case 4: Unrelated Query
**Input:** "What's the weather like today?"
**Expected Result:**
- Very low confidence score (0-20%)
- Fallback response indicating no information available
- Automatic escalation

### 3. Testing Escalation Management

1. From the chat screen, trigger several low-confidence queries
2. Open the escalations screen (if menu is implemented)
3. Verify escalations are displayed with:
   - Customer information
   - Original query
   - AI's preliminary response
   - Confidence score
   - Priority level (based on confidence)
   - Context summary

4. Test escalation actions:
   - Assign to agent (mock agent ID used)
   - Resolve escalation
   - Verify status updates

### 4. Testing RAG Pipeline

#### Knowledge Retrieval Test
**Queries to test:**
- "storage space" → Should retrieve KB003 (Product Features)
- "security encryption" → Should retrieve KB005 (Security)
- "billing cycle" → Should retrieve KB002, KB006 (Billing)
- "login issues" → Should retrieve KB004 (Troubleshooting)

#### Confidence Scoring Validation
Monitor confidence scores based on:
- Exact keyword matches: Higher confidence (80-95%)
- Partial matches: Medium confidence (60-80%)
- No matches: Low confidence (0-40%)

### 5. Testing Security Features

1. **Input Sanitization:**
   - Try queries with special characters: `<script>alert('test')</script>`
   - Verify characters are escaped in responses

2. **Data Persistence:**
   - Submit queries, close app
   - Reopen app
   - Verify chat history is maintained (if persistence is enabled)

3. **Encrypted Storage:**
   - Check that sensitive data uses EncryptedSharedPreferences
   - API tokens and credentials should be encrypted

## UI Testing Checklist

- [ ] Chat messages display correctly in conversation view
- [ ] User messages align to right, AI messages to left
- [ ] Confidence indicators show with appropriate colors:
  - Green: High (≥80%)
  - Orange: Medium (60-79%)
  - Red: Low (<60%)
- [ ] Escalated messages have visual indicator (orange border)
- [ ] Time stamps display correctly
- [ ] Send button enabled/disabled appropriately
- [ ] Loading indicators appear during processing
- [ ] Error messages display for failures
- [ ] Escalation cards show all information
- [ ] Priority colors display correctly:
  - Red: Urgent/High
  - Orange: High
  - Blue: Medium
  - Gray: Low

## Performance Testing

### Response Time Benchmarks
- Query submission: < 100ms
- Embedding generation: < 500ms
- Semantic search: < 1s
- Response generation: < 2s
- Total end-to-end: < 3s

### Memory Usage
- Monitor memory consumption during extended use
- Check for memory leaks after 50+ queries
- Database size should scale linearly with data

## Edge Cases to Test

1. **Empty Input:**
   - Submit empty query
   - Verify validation message appears

2. **Very Long Query:**
   - Submit 500+ character query
   - Verify it's handled gracefully
   - Check truncation in UI if needed

3. **Special Characters:**
   - Unicode characters
   - Emojis
   - HTML/XML tags
   - SQL injection attempts

4. **Network Issues:**
   - Test with no internet (if API integration exists)
   - Verify graceful error handling

5. **Rapid Submissions:**
   - Submit multiple queries quickly
   - Verify queue handling
   - Check for race conditions

## Automated Testing (Future)

### Unit Tests
```kotlin
// Example test structure
class EmbeddingServiceTest {
    @Test
    fun testEmbeddingGeneration() {
        // Test embedding vector generation
    }
    
    @Test
    fun testCosineSimilarity() {
        // Test similarity calculation
    }
}

class ResponseGenerationServiceTest {
    @Test
    fun testConfidenceScoring() {
        // Test confidence calculation
    }
    
    @Test
    fun testInputSanitization() {
        // Test sanitization of user input
    }
}
```

### Integration Tests
```kotlin
class RAGPipelineTest {
    @Test
    fun testEndToEndQueryProcessing() {
        // Test complete RAG pipeline
    }
}
```

### UI Tests (Espresso)
```kotlin
class ChatActivityTest {
    @Test
    fun testQuerySubmission() {
        // Test chat UI interactions
    }
    
    @Test
    fun testEscalationFlow() {
        // Test escalation workflow
    }
}
```

## Known Limitations (Current Implementation)

1. **Mock Embedding Service:** Uses hash-based mock embeddings instead of actual ML model
2. **Mock Response Generation:** Template-based responses instead of actual LLM API
3. **No Network Layer:** All processing is local
4. **Sample Data Only:** Uses hardcoded knowledge base
5. **No User Authentication:** Uses mock customer data

## Production Readiness Checklist

- [ ] Replace mock embedding service with actual embedding API
- [ ] Integrate with production LLM API (OpenAI, Anthropic, etc.)
- [ ] Implement real authentication system
- [ ] Add network layer with proper error handling
- [ ] Implement knowledge base management interface
- [ ] Add analytics and monitoring
- [ ] Set up CI/CD pipeline
- [ ] Conduct security audit
- [ ] Perform load testing
- [ ] Create admin dashboard
- [ ] Implement feedback system
- [ ] Add push notifications
- [ ] Create agent mobile app
- [ ] Document API endpoints
- [ ] Prepare deployment scripts

## Reporting Issues

When reporting bugs, include:
1. Device/Emulator details
2. Android version
3. Steps to reproduce
4. Expected vs actual behavior
5. Screenshots/logs
6. Query text used
7. Confidence score received
