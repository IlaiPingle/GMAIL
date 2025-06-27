const express = require('express')
const router = express.Router()
const emailController = require('../Controllers/emailController')

// Route to subscribe a new email
router.post('/api/emails/subscribe', emailController.subscribeEmail)

// Route to verify an email subscription
router.post('/api/emails/verify/:id', emailController.verifyEmail)

// Route to get all verified emails (admin endpoint)
router.get('/api/emails/verified', emailController.getVerifiedEmails)

// Route to unsubscribe
router.delete('/api/emails/unsubscribe/:id', emailController.unsubscribeEmail)

module.exports = router