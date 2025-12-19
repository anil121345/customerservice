# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Customer Support App                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Presentation Layer (UI)                    │
│  ┌──────────────────┐        ┌──────────────────────┐      │
│  │  ChatActivity    │        │ EscalationActivity   │      │
│  │  - Customer UI   │        │  - Agent Dashboard   │      │
│  └──────────────────┘        └──────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   ViewModel Layer (MVVM)                     │
│  ┌──────────────────┐        ┌──────────────────────┐      │
│  │  ChatViewModel   │        │ EscalationViewModel  │      │
│  └──────────────────┘        └──────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer (Use Cases)                  │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │SubmitQuery   │  │ProcessRAG   │  │ManageEscalations │  │
│  │UseCase       │  │UseCase      │  │UseCase           │  │
│  └──────────────┘  └─────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              Data Layer (Repository Pattern)                 │
│           CustomerServiceRepository                          │
│                      │                                       │
│       ┌──────────────┼──────────────┐                       │
│       ▼              ▼              ▼                       │
│  ┌────────┐   ┌──────────┐   ┌──────────┐                 │
│  │ Local  │   │   RAG    │   │ Security │                 │
│  │  DB    │   │ Pipeline │   │ Manager  │                 │
│  └────────┘   └──────────┘   └──────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## RAG Pipeline Flow

```
1. Customer Query
        │
        ▼
2. Embedding Generation (768-dim vector)
        │
        ▼
3. Semantic Search (Cosine Similarity)
        │
        ▼
4. Document Retrieval (Top 5 documents)
        │
        ▼
5. Context Augmentation
        │
        ▼
6. Response Generation
        │
        ▼
7. Confidence Scoring (0-100%)
        │
        ├─[Score >= 60%]──► Direct Response
        │
        └─[Score < 60%]───► Escalation to Agent
```

## Data Flow

```
User Input → Sanitization → Query Submission
                                    │
                                    ▼
                            RAG Processing
                                    │
                    ┌───────────────┴────────────┐
                    ▼                            ▼
            High Confidence              Low Confidence
                    │                            │
                    ▼                            ▼
            AI Response                  Create Escalation
                    │                            │
                    ▼                            ▼
            Update Status               Notify Agent
```

## Security Layers

1. **Transport Security**: HTTPS-only communication
2. **Data Encryption**: AES-256-GCM for sensitive data
3. **Storage Security**: EncryptedSharedPreferences
4. **Input Validation**: Sanitization at all entry points
5. **ProGuard**: Code obfuscation in release builds

## Database Schema

### CustomerQuery
- id (PK)
- customerId
- customerName
- queryText
- timestamp
- status (PENDING, PROCESSING, ANSWERED, ESCALATED, RESOLVED)
- category

### AIResponse
- id (PK)
- queryId (FK)
- responseText
- confidenceScore
- generatedAt
- sourceDocuments[]
- isEscalated

### KnowledgeDocument
- documentId (PK)
- title
- content
- category
- embedding (serialized vector)
- metadata
- lastUpdated
- isActive

### Escalation
- id (PK)
- queryId (FK)
- customerId
- customerName
- originalQuery
- aiResponse
- confidenceScore
- contextSummary
- escalationReason
- priority
- escalatedAt
- assignedAgent
- status
- resolvedAt
