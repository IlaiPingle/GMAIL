const EmailModel = require('../Models/emailModel')
const { isValidEmail } = require('../utils/emailValidator')

/**
 * Handle email subscription requests
 */
exports.subscribeEmail = (req, res) => {
    const { email, name, marketingConsent } = req.body
    
    // Check if required fields are present
    if (!email || !name) {
        return res.status(400).json({ message: 'Email and name are required' })
    }
    
    // Validate email format
    if (!isValidEmail(email)) {
        return res.status(400).json({ message: 'Invalid email format' })
    }
    
    // Check if email already exists
    const existingEmail = EmailModel.findEmailByAddress(email)
    if (existingEmail) {
        return res.status(409).json({ message: 'Email already subscribed' })
    }
    
    // Create subscription
    const newSubscription = EmailModel.createEmailSubscription(
        email, 
        name, 
        marketingConsent || false
    )
    
    // Return success response
    return res.status(201).json({
        id: newSubscription.id,
        email: newSubscription.email,
        name: newSubscription.name,
        subscriptionDate: newSubscription.subscriptionDate
    })
}

/**
 * Verify an email subscription
 */
exports.verifyEmail = (req, res) => {
    const id = parseInt(req.params.id, 10)
    
    // Attempt to verify
    const verified = EmailModel.verifyEmail(id)
    
    if (verified) {
        return res.status(200).json({ message: 'Email verified successfully' })
    } else {
        return res.status(404).json({ message: 'Email subscription not found' })
    }
}

/**
 * Get all verified emails
 */
exports.getVerifiedEmails = (req, res) => {
    const verifiedEmails = EmailModel.getVerifiedEmails()
    return res.status(200).json(verifiedEmails)
}

/**
 * Unsubscribe from email list
 */
exports.unsubscribeEmail = (req, res) => {
    const id = parseInt(req.params.id, 10)
    const subscription = EmailModel.findEmailById(id)
    
    if (!subscription) {
        return res.status(404).json({ message: 'Email subscription not found' })
    }
    
    // In a real application, you'd want to actually remove the subscription
    // or mark it as unsubscribed. For this example, we'll pretend we did.
    
    return res.status(200).json({ message: 'Successfully unsubscribed' })
}