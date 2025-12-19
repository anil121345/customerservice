package com.enterprise.customerservice.data.models

/**
 * Sample knowledge base documents for testing and demonstration
 */
object SampleKnowledgeData {
    
    fun getSampleDocuments(): List<KnowledgeDocument> {
        return listOf(
            KnowledgeDocument(
                documentId = "KB001",
                title = "How to Reset Your Password",
                content = """
                    To reset your password:
                    1. Go to the login page
                    2. Click on "Forgot Password" link
                    3. Enter your registered email address
                    4. Check your email for a reset link
                    5. Click the link and create a new password
                    6. Your password must be at least 8 characters with one uppercase, one lowercase, and one number
                    
                    If you don't receive the email within 5 minutes, check your spam folder or contact support.
                """.trimIndent(),
                category = "Account Management",
                embedding = "",
                metadata = """{"difficulty": "easy", "views": 1234, "helpful": 956}"""
            ),
            
            KnowledgeDocument(
                documentId = "KB002",
                title = "Billing and Payment Information",
                content = """
                    Our billing cycle:
                    - Monthly subscriptions are billed on the same day each month
                    - Annual subscriptions offer a 20% discount
                    - We accept credit cards, debit cards, and PayPal
                    - All prices are in USD unless otherwise specified
                    
                    To update payment information:
                    1. Log in to your account
                    2. Go to Settings > Billing
                    3. Click "Update Payment Method"
                    4. Enter new payment details
                    5. Save changes
                    
                    Invoices are emailed to your registered email address within 24 hours of payment.
                """.trimIndent(),
                category = "Billing",
                embedding = "",
                metadata = """{"difficulty": "medium", "views": 2341, "helpful": 1823}"""
            ),
            
            KnowledgeDocument(
                documentId = "KB003",
                title = "Product Features and Usage",
                content = """
                    Our platform offers:
                    - Real-time collaboration tools
                    - Cloud storage with 100GB on basic plan, unlimited on premium
                    - Mobile apps for iOS and Android
                    - 24/7 customer support
                    - Integration with popular tools (Slack, Teams, Google Workspace)
                    - Advanced analytics and reporting
                    - Role-based access control
                    - End-to-end encryption
                    
                    Getting started:
                    1. Create your account
                    2. Set up your workspace
                    3. Invite team members
                    4. Start collaborating
                    
                    For detailed feature documentation, visit our help center.
                """.trimIndent(),
                category = "Product Features",
                embedding = "",
                metadata = """{"difficulty": "easy", "views": 5432, "helpful": 4321}"""
            ),
            
            KnowledgeDocument(
                documentId = "KB004",
                title = "Troubleshooting Common Issues",
                content = """
                    Common issues and solutions:
                    
                    1. Login Problems:
                       - Clear browser cache and cookies
                       - Try incognito/private mode
                       - Check if Caps Lock is on
                       - Reset your password if needed
                    
                    2. Slow Performance:
                       - Close unnecessary browser tabs
                       - Check your internet connection
                       - Clear application cache
                       - Update to the latest version
                    
                    3. File Upload Failures:
                       - Ensure file size is under 50MB
                       - Check supported file formats
                       - Verify available storage space
                       - Try a different browser
                    
                    4. Email Not Received:
                       - Check spam/junk folder
                       - Verify email address is correct
                       - Add our domain to safe senders
                       - Wait 10-15 minutes for delivery
                    
                    If issues persist, contact our support team.
                """.trimIndent(),
                category = "Troubleshooting",
                embedding = "",
                metadata = """{"difficulty": "medium", "views": 8765, "helpful": 6543}"""
            ),
            
            KnowledgeDocument(
                documentId = "KB005",
                title = "Data Security and Privacy",
                content = """
                    We take security seriously:
                    
                    - All data is encrypted in transit (TLS 1.3) and at rest (AES-256)
                    - Regular security audits and penetration testing
                    - SOC 2 Type II certified
                    - GDPR and CCPA compliant
                    - Two-factor authentication available
                    - Regular automated backups
                    - Data centers in multiple regions
                    
                    Privacy commitments:
                    - We never sell your data
                    - You control your data
                    - Right to data portability
                    - Right to deletion
                    
                    For detailed security information, see our Security Whitepaper and Privacy Policy.
                """.trimIndent(),
                category = "Security",
                embedding = "",
                metadata = """{"difficulty": "advanced", "views": 3210, "helpful": 2987}"""
            ),
            
            KnowledgeDocument(
                documentId = "KB006",
                title = "Subscription Plans and Upgrades",
                content = """
                    Available plans:
                    
                    Basic Plan ($9.99/month):
                    - 100GB storage
                    - Up to 5 team members
                    - Basic features
                    - Email support
                    
                    Professional Plan ($29.99/month):
                    - 1TB storage
                    - Up to 25 team members
                    - All basic features
                    - Priority email support
                    - Advanced analytics
                    
                    Enterprise Plan (Custom pricing):
                    - Unlimited storage
                    - Unlimited team members
                    - All professional features
                    - 24/7 phone support
                    - Dedicated account manager
                    - Custom integrations
                    - SLA guarantee
                    
                    To upgrade:
                    1. Go to Settings > Subscription
                    2. Choose your plan
                    3. Review changes
                    4. Confirm upgrade
                    
                    Upgrades take effect immediately. Downgrades take effect at next billing cycle.
                """.trimIndent(),
                category = "Billing",
                embedding = "",
                metadata = """{"difficulty": "easy", "views": 4567, "helpful": 3890}"""
            )
        )
    }
}
