const emails = []

// This variable is used to generate unique IDs for each email entry
let nextEmailId = 1

/**
 * Creates a new email subscription entry
 * @param {string} email - The email address
 * @param {string} name - The subscriber's name
 * @param {boolean} marketingConsent - Whether user consented to marketing emails
 * @returns {Object} The created email subscription object
 */
function createEmailSubscription(email, name, marketingConsent = false) {
	const subscription = {
		id: nextEmailId++,
		email,
		name,
		marketingConsent,
		subscriptionDate: new Date(),
		verified: false
	}
	emails.push(subscription)
	return subscription
}

/**
 * Find email subscription by ID
 * @param {number} id - The subscription ID
 * @returns {Object|undefined} The subscription or undefined if not found
 */
function findEmailById(id) {
	return emails.find(subscription => subscription.id === id)
}

/**
 * Find email subscription by email address
 * @param {string} email - The email address
 * @returns {Object|undefined} The subscription or undefined if not found
 */
function findEmailByAddress(email) {
	return emails.find(subscription => subscription.email === email)
}

/**
 * Verify an email subscription
 * @param {number} id - The subscription ID
 * @returns {boolean} True if verification succeeded, false otherwise
 */
function verifyEmail(id) {
	const subscription = findEmailById(id)
	if (subscription) {
		subscription.verified = true
		return true
	}
	return false
}

/**
 * Get all verified email subscriptions
 * @returns {Array} Array of verified email subscriptions
 */
function getVerifiedEmails() {
	return emails.filter(subscription => subscription.verified === true)
}

module.exports = {
	createEmailSubscription,
	findEmailById,
	findEmailByAddress,
	verifyEmail,
	getVerifiedEmails
}